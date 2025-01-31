/*
 * @formatter:off
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @formatter:on
 */
package org.infrastructurebuilder.util.readdetect.api;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.Objects.hash;
import static org.infrastructurebuilder.constants.IBConstants.*;
import static org.infrastructurebuilder.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.constants.IBConstants.PATH_CHECKSUM;
import static org.infrastructurebuilder.constants.IBConstants.SIZE;
import static org.infrastructurebuilder.constants.IBConstants.SOURCE_NAME;
import static org.infrastructurebuilder.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.constants.IBConstants.UPDATE_DATE;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.ChecksumBuilder;
import org.infrastructurebuilder.pathref.ChecksumBuilderFactory;
import org.infrastructurebuilder.pathref.ChecksumEnabled;
import org.infrastructurebuilder.pathref.JSONBuilderBaseFactory;
import org.infrastructurebuilder.pathref.JSONBuilderFactory;
import org.infrastructurebuilder.pathref.JSONOutputEnabled;
import org.infrastructurebuilder.pathref.api.Modeled;
import org.infrastructurebuilder.pathref.api.base.NameDescribed;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.pathref.fs.attribute.PathRefFileAttributes;
import org.infrastructurebuilder.pathref.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.core.IBUtils;
import org.json.JSONObject;

/**
 *
 * <p>
 * An IBResource is a persistable representation of the metadata of a <b><i>stream of bytes</i></b> on some nominal
 * "source" somewhere, and a reference to possibly obtain the stream itself. As a rule, an IBResource is most useful
 * when its source is readable and the bytestream is immutable, or at least consistently and reliably reproducible.
 * </p>
 *
 * <p>
 * The immutability of an IBResource is an aspect of it being treated as a released Maven artifact. Per the default
 * artifact lifecycle managed by Maven's release plugin, once released an artifact should never be released again. Any
 * subsequent releases are considered new versions, and thus a different artifact.
 * </p>
 *
 * <p>
 * An IBResource can be realized or unrealized. A realized IBResource is one that may be expected to be capable of
 * producing the bytestream and is accessible from a JVM. An unrealized IBResource may still be used, but only as an
 * element of metadata around the unrealized target data stream. An IBResource becomes realized when its bytestream is
 * readable.
 * </p>
 *
 * <p>
 * IBResources are instantiated from a URI that points to some FileSystemProvider. If a provider for a URI is
 * unavailable, then the IBResource cannot be realized.
 * </p>
 *
 * <p>
 * Realized IBResource instances are <u>always</u> stored as a {@code PathRefPath}. Note that this implies that there is
 * a FileSystem provider for the PathRefFileSystem to use as a target for the URI of the underlying resource. Due to the
 * way that the PathRefFileSystemProvider constructs PathRefFileSystems, the resulting PathRefFileSystem URI that points
 * to the actual resource will be very different from the original URI. The correct initial configuration (or
 * reconfiguration at a later date) of the PathRefFileSystem is essential to the correct operation of the IBResource.
 * </p>
 *
 * <p>
 * An IBResource may be purely synthetic. In this case, the IBResource is a reference to a resource that is possibly, or
 * even probably, not consistent over time. Such an IBResource is generally only useful once it has been realized, and
 * potentially had its output persisted to a realized IBResource.
 * </p>
 *
 * <p>
 * For instance, the contents of a live database table may be represented by an IBResource, if a URI could be
 * constructed to be read as a bytestream. However, the contents of the table is likely to change over time. As
 * IBResource elements are most useful when immutable, one operation would be to copy the contents of the table
 * (probably as a database dump of INSERT statements and the like) to a file, and then create a new IBResource from that
 * file. This new IBResource could be considered immutable, although someone could still change the contents of the
 * file, but that would invalidate the use-case of the IBResource.
 * </p>
 *
 * <p>
 * An IBResource may be validated, unvalidated, or erroneous. A validated IBResource is one whose metadata has been
 * shown to match the actual stream of bytes. An unvalidated IBResource <u><i>might</i></u> ostensibly be "valid", but
 * has not been checked. Any use of the metadata of an unvalidated IBRresource is optimistic and should be considered
 * suspect. An erroneous IBResource is one that has been demonstrated to contain false metadata. Use of an erroneous
 * IBResource breaks the contract and is not recommended.
 * </p>
 *
 * <p>
 * Across IBResource instances, the following attributes must be true to conform to the contract:
 * <ol>
 * <li>An IBResource has a byteStreamChecksum property which is a SHA-512 checksum meant to match that of the actual
 * bytestream. If the checksum does not match, the IBResource is not valid. Any other situation, included a resource
 * that cannot be validated because it has no local representation, does not conform to the specification and cannot
 * reasonably be expected to work within the IBResource ecosystem.</li>
 *
 * <li>An IBResource has a type, which is a string representation of the MIME type of the byte stream. If the type of
 * the data does not match the IBResource type, then the IBResource is erroneous.</li>
 * </p>
 *
 * <p>
 * Equality is a special case among IBResource items. In general, there may be many different subclasses of IBResource.
 * However, the default implementation implies that the only underlying relevant value for equality is the stream
 * checksum. As an IBResource is meant to represent an immutable stream of bytes, which therefore generally implies an
 * <i>archived</i> stream of bytes, other aspects of the IBResource are not considered relevant for equality. Size, for
 * instance, typically gets subsumed in Checksum. Dates, while relevant for various purposes, do not apply. The same
 * goes for type and source. Two identical byte streams are, effectively, interchangeable within the IBResource
 * ecosystem.<br/>
 * <br/>
 * Obviously, the other metadata will be relevant for other purposes, such as query, display, or targeted
 * computation.<br/>
 * <br/>
 * It should be noted that while <b><i><u>astronomically improbable</u></i></b>, two very different inputs could produce
 * the same checksum. The only sure method would be to compare all the bytes of the two streams. This is not done by the
 * IBResource framework as a means to conserve processing time and speed on arbitrarily large datasets.
 * </p>
 * <p>
 * The values of IBResource items are meant to be immutably accessible. However, by default, local files are NOT
 * considered immutable, nor are other types of data. The reliability of an IBResource's immutability is tied to
 * the manner in which it was produced.
 * </p>
 *
 * <p>
 * Subtypes of IBResource items may be available. Cached-object persistence, cached-object stability, immutability, and
 * implied immutability through SLAs and assurances, is outside the scope of basic IBResource implementation, although
 * providers for such things can be produced. These attributes can be managed through infrastructure and process, but IB
 * projects themselves are (like essentially all software) intrinsically unable to provide those assurances.
 * </p>
 *
 *
 * @author mykelalvis
 *
 */

public interface IBResource extends JSONOutputEnabled, ChecksumEnabled, NameDescribed, Modeled {
  public static final String ACQUIRE_DATE = "acquireDate";
  public static final String PERMISSIONS = "permissions";
  public static final String GROUP = "group";
  public static final String OWNER = "owner";
  public final static OpenOption[] ZIP_OPTIONS = {
      READ
  };
  public final static OpenOption[] OPTIONS = {
      READ, NOFOLLOW_LINKS
  };

  public static Path requireAbsolutePath(Path p) {
    if (!Objects.requireNonNull(p).isAbsolute())
      throw new IBResourceException("Path " + p + " must be absolute");
    return p;
  }

  public static int defaultHashCode(IBResource t) {
    return hash(t.getChecksum(), t.getPath(), t.getSourceName(), t.getSourceURI(), t.getType());
  }

  public static String defaultToString(IBResource t) {
    StringJoiner sj = new StringJoiner("|") //
        .add(t.getByteStreamChecksum().asUUID().get().toString()) // Checksum
        .add(t.getType()) // type
    ;
    t.getPath().map(p -> sj.add(p.toString())).orElseGet(() -> sj.add("_No_Path_"));
    sj.add(t.getSourceURI().toString());
    t.getSourceName().ifPresent(sj::add);
    return sj.toString();
  }

  public static boolean defaultEquals(IBResource t, Object obj) {
    if (t == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if ((obj instanceof IBResource)) {
      IBResource other = (IBResource) obj;
      return Objects.equals(t.getChecksum(), other.getChecksum()) // checksum
          && Objects.equals(t.getType(), other.getType()); // Type
    }
    return false;
  }

  PathRefPath get();

  /**
   * @return Non-null Optional Path to this result. If <code>empty()</code>, this is useful only as a reference
   *         resource.
   */
  default Optional<PathRefPath> getPath() {
    return PathRefPath.fromUri(getSourceURI());
  }

  /**
   * By contract, this is the calculated Checksum of the contents of the InputStream supplied by get()
   *
   * It may seem odd that a checksum must exist when the bytes might not. But an IBResource is backed by a URI-based
   * FileSystem, and thus it is possible that the actual data is remote and currently inaccessible. However, the
   * Checksum of the data is always expected to be known and accurate. If it is not, the IBResource is erroneous.
   *
   * @return Non-null Checksum
   *
   *
   */
  Checksum getByteStreamChecksum();

  /**
   * @return Non-null. This is the calculated Checksum of the entire model (not just the bytestream)
   */
  Checksum getChecksum();

  /**
   * By contract, an unknown or unknowable type should be 'application/octet-stream' Otherwise, it should be assumed
   * that this type is a valid MIME type for the byte stream
   *
   * @return Non-null MIME type for the byte stream (for instance, the file at getPath())
   */
  String getType();

  /**
   * Sub-types may, at their discretion, return a {@link Instant} of the most recent "get()" call. The generated
   * IBResourceModel does not because it is merely a persistence mechanism and that value isn't relevant.
   *
   * However, the transformation capabilities of IBData require knowledge of access times, and this is a convenient way
   * to get that information.
   *
   *
   * @return most recent read time or empty if the inforamtion is unavailable
   */
  Optional<Instant> getMostRecentReadTime();

  /**
   * Create date. If possible, this should be the create instant of the T item from {@link IBResource#get()} above. For
   * instance, if T is an {@link InputStream} that is acquired from a {@link Path}, then this should be the create date
   * of that original Path. On the other hand, if the stream is from an SQL statements output, then this value should be
   * the same as {@link IBResource#getAcquireDate()} below.
   *
   * If the values for these are not immediately obvious, or violate either of the examples above, then they should
   * probably be explained in the Description of the IBResource.
   *
   * @return create date or empty() if unknown
   */
  Optional<Instant> getCreateDate();

  /**
   * This is the moment at which the acquisition of the objects returned above are read. Sometimes, this is the same as
   * {@link IBResource#getCreateDate()} and other times it is the time at which a value was read from an outside source.
   *
   * @return
   */
  Optional<Instant> getAcquireDate();

  /**
   * @return last file update or empty if unknown.  This is provider-specific information and might not be available.
   */
  Optional<Instant> getLastUpdateDate();

  /**
   * The original query that produced this bytestream.  As a rule, IBResources are backed entirely by
   * PathRefPathFileSystem instances, so the data read might be represented as a base path off the "root"
   * location of this value.
   *
   * Default implementations of IBResource instances have a value mapped directly to the PathRefPath of the filesystem
   * produced by the PathRefFileSystemProvider.  This is the "path" of the IBResource.
   * @return  Non-null URI of the original source of the bytestream.
   */
  URI getSourceURI();

  /**
   * This conforms to the "filename" of the source.  Per the {@link Path} contract, this is the last element of the
   * path.
   * @return
   */
  Optional<String> getSourceName();

  /**
   * Arbitrary metadata applied to this IBResource.  This is a JSON object that can be used for any purpose, but
   * is occasionally used by subtypes for persisting subtype-specific information.
   * @return non-null but possible empty {@link JSONObject}
   */
  JSONObject getMetadata();

  /// Follows are file attributes that may or may not be available

  Optional<Long> size();

  Optional<String> getOwner();

  Optional<String> getGroup();

  Optional<String> getPermissionsAsString();

  default Optional<Set<PosixFilePermission>> getPermissions() {
    return this.getPermissionsAsString().map(PosixFilePermissions::fromString);
  }

  /**
   * This is the calculated checksum of the entire model per {@link IBResource#getChecksum()}
   */
  @Override
  default Checksum asChecksum() {
    return getChecksum();
  }

  /**
   * The "default" implemetation of asJSON is generally correct.  However, subtypes may wish to override this method and
   * replace or inject information here.
   *
   * Note that overriding any of the information below effectively invalidates the IBResource instance.
   */
  default JSONObject asJSON() {
    return JSONBuilderFactory.newInstanceFromRelativeRoot(getRelativeRoot())

        .addString(NAME, getName())

        .addString(DESCRIPTION, getDescription())

        .addString(DISPLAYNAME, getDisplayName())

        .addString(Modeled.MODEL_VERSION, getModelVersion())

        .addModeled(Modeled.MODEL, this) // This does duplicate model version. Maybe fix it?

        .addChecksum(PATH_CHECKSUM, getByteStreamChecksum())

        .addChecksum(CHECKSUM, getChecksum())

        .addInstant(CREATE_DATE, getCreateDate())

        .addInstant(UPDATE_DATE, getLastUpdateDate())

        .addInstant(MOST_RECENT_READ_TIME, getMostRecentReadTime())

        .addString(SOURCE_NAME, getSourceName())

        .addString(SOURCE_URL, getSourceURI().toString())

        .addString(MIME_TYPE, getType())

        .addInstant(ACQUIRE_DATE, getAcquireDate())

        .addString(PATH, getPath().map(PathRefPath::toFullString))

        .addLong(SIZE, size()) // -1L means unknowable and missing means unknown

        .addString(DESCRIPTION, getDescription())

        .addJSONObject(IBConstants.METADATA, getMetadata())

        .addString(OWNER, getOwner())

        .addString(GROUP, getGroup())

        .addString(PERMISSIONS, getPermissionsAsString())

        .asJSON();
  }

  default Optional<PathRefFileAttributes> getFileAttributes() {
    return getPath().flatMap(path -> IBUtils.getAttributes.apply(path));
  }

  /**
   * @return true if this file was cached, which <b>probably</b> means the path is based on a PathRef
   */
  default boolean isCached() {
    return false;
  }

  /**
   * The root filesystem for an IBResource should be configured prior to utilizing any resources out
   * of the library.  As a rule, all filesystems are persisted by a 'pathref:key:SOME_KEY' URI that may
   * be used to reference an existing PathRefFileSystem.  If such a filesystem is not available, then,
   * it must be created prior to using the IBResource.
   * @return
   */
  PathRefFileSystem getRelativeRoot();

  /**
   * Validate current IBResource.
   *
   * @param hard if true, then everything must be correct and missing items will be queried (potentially an expensive
   *             operation). If false, then only things that are obviously incorrect will be reported.
   * @return true if validation was successful
   */
  boolean validate(boolean hard);

  default boolean validate() {
    return validate(true);
  }

  /**
   * Produce a new copy of the underlying model of this resource
   *
   * @return
   */
  IBResourceModel copyModel();

  default Optional<ChecksumBuilder> getChecksumBuilder() {
    return Optional.of(ChecksumBuilderFactory.newInstance(this.getRelativeRoot())
        .addChecksum(new Checksum(copyModel().getStreamChecksum())));
  }

  /**
   *
   * @return An instance of a {@link org.codehaus.plexus.components.io.resources.PlexusIoResource} if possible
   */
  Optional<Object> asPlexusIOResource();
}
