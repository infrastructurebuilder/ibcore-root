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
package org.infrastructurebuilder.util.readdetect.base;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.Objects.hash;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.util.constants.IBConstants.CREATE_DATE;
import static org.infrastructurebuilder.util.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.util.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH_CHECKSUM;
import static org.infrastructurebuilder.util.constants.IBConstants.SIZE;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_NAME;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.util.constants.IBConstants.UPDATE_DATE;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.ChecksumBuilder;
import org.infrastructurebuilder.util.core.ChecksumEnabled;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.infrastructurebuilder.util.core.JSONOutputEnabled;
import org.infrastructurebuilder.util.core.Modeled;
import org.infrastructurebuilder.util.core.NameDescribed;
import org.infrastructurebuilder.util.core.OptStream;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.base.impls.IBURLPlexusIOResource;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;

/**
 *
 * An IBResource is a representation of a stream of bytes on some bytestream source somewhere.
 *
 * It may be possible for a consumer to read that stream, if the IBResource is "realized". It may be possible to acquire
 * the stream from a URL if the type of the URL has a protocol that can be read from the running JVM.
 *
 * Realized IBResource instances are <u>always</u> stored under a RelativeRoot (hereafter "the relroot") on the local
 * filesystem.
 *
 * All IBResource instances must have a source that is expected to be the location from which the stream would or did
 * originate.
 *
 * A valid IBResource has a bytestream whose SHA-512 checksum matches that of the IBResource.getChecksum(). Any other
 * situation, included a resource that cannot be validated because it has no local representation, is considered
 * invalid.
 *
 * The values of IBResource items are considered to be fairly constant. However, by default, local files in the relroot
 * are NOT considered immutable. Cached-object persistence, cached-object stability, immutability, and implied
 * immutability through SLAs and assurances, is outside the scope of the ibcore-root/ibcore-read-detect module. These
 * attributes can be managed through infrastructure and process, but IB projects themselves are (like essentially all
 * software) intrinsically unable to provide those assurances.
 *
 * For these elements, look at the IBData projects and their infrastructure requirements.
 *
 * There are multiple ways to acquire an IBResource:
 * <ol>
 * <li>From a file on the filesystem
 * <ul>
 * <li>This original file may be copied to the relroot if it exists</li>
 * <li>The size is read from the original file</li>
 * <li>The checksum is computed from the original file. This is used to identify the file inside the relroot.</li>
 * <li>The MIME type of the file is computed from the original file unless supplied at creation time.</li>
 * <li>If the MIME type is supplied, the model assumes that the type is correct and does no validation.</li>
 * <li>The properties/attributes of the original file are placed in the IBResource model, if they can be read.</li>
 * <li>The properties are never updated unless the IBResource value is recreated</li>
 * </ul>
 * <li>From a remote resource at the end of some URL-like. If realized, this will be copied to an available relroot like
 * above, or not cached and read to a temp file to be deleted on exit
 * <ul>
 * <li>This stream is copied to the relroot, if present.</li>
 * <li>If the stream is not or cannot be copied, this is considered a "reference" resource.</li>
 * <li>The file size is read from the copied file.</li>
 * <li>The checksum is computed from the copied file.</li>
 * <li>The MIME type is read from headers, if available, unless supplied (as above). If not available, the type is
 * considered to be <code>application/octet-stream</code>.</li>
 * <li>The <code>Last-Modified</code> header is used as the lastUpdateDate, if available. <code>null</code>
 * otherwise.</li>
 * <li>Other properties/attributes of the original file are NOT placed in the IBResource model.</li>
 * <li>As above, the properties are not updated unless the IBResource value is recreated</li>
 * </ul>
 * </li>
 * <li>As a reference to a known remote value, also at the end of some URL-like. This will not be realized, thus not
 * readable, and will only be useful as a reference. Validation of a strict reference is not necessarily possible.</li>
 * <ul>
 * <li>Reference resources are only created when the remote resource is not copied, either due to inability to read the
 * remote bytestream or with a no-copy flag being set at IBResource creation time.</li>
 * <li>Reference resources can be used to recreate another resource.</li>
 * </ul>
 * </ol>
 *
 * As changes allow, IBResource implementations will attempt to expand on the model information available to them.
 *
 * If an IBResource exists pointing to a Path on the filesystem and that entire filesystem is moved elsewhere,
 * theoretically only the relroot value needs to be changed to point to its new location.
 *
 * @author mykelalvis
 *
 */

public interface IBResource extends JSONOutputEnabled, ChecksumEnabled, NameDescribed, Modeled {
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
    return hash(t.getChecksum(), t.getPath(), t.getSourceName(), t.getSourceURL(), t.getType());
  }

  public static String defaultToString(IBResource t) {
    StringJoiner sj = new StringJoiner("|") //
        .add(t.getTChecksum().asUUID().get().toString()) // Checksum
        .add(t.getType()) // type
        .add(t.getPath().toString()); // Path
    t.getSourceURL().ifPresent(u -> sj.add(u.toExternalForm()));
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
          && Objects.equals(t.getPath(), other.getPath()) // path TODO Maybe not?
          && Objects.equals(t.getSourceName(), other.getSourceName()) // source
          && Objects.equals(t.getSourceURL(), other.getSourceURL()) // sourceURL
          && Objects.equals(t.getType(), other.getType()); // Type
    }
    return false;
  }

  public static Path requireRelativePath(Path p) {
    if (Objects.requireNonNull(p).isAbsolute())
      throw new IBResourceException("Path " + p + " must be relative");
    return p;
  }

  OptStream get();

  /**
   * @return Non-null Optional Path to this result. If <code>empty()</code>, this is considered a reference resource.
   */
  Optional<Path> getPath();

  /**
   * @return Non-null. By contract, this is the calculated Checksum of the contents of the InputStream supplied by get()
   */
  Checksum getTChecksum();

  /**
   * @return Non-null. This is the calculated Checksum of the entire model (not just the bytestream)
   */
  Checksum getChecksum();

  /**
   * By contract, an unknown or unknowable type should be 'application/octet-stream' Otherwise, it should be assumed
   * that this type is a valid MIME type for the byte stream
   *
   * @return Non-null MIME type for the byte stream (i.e. the file at getPath())
   */
  String getType();

  /**
   * Sub-types may, at their discretion, return a {@link Instant} of the most recent "get()" call. The generated
   * IBResourceModel does not because it is merely a persistence mechanism and that value isn't relevant.
   *
   *
   * @return most recent read time or null
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
   * @return create date
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
   * @return last file update or null
   */
  Optional<Instant> getLastUpdateDate();

  Optional<URL> getSourceURL();

  Optional<String> getSourceName();

  JSONObject getMetadata();

  Optional<Long> size();

  default JSONObject asJSON() {
    return new JSONBuilder(getRelativeRoot().flatMap(RelativeRoot::getPath))

        .addChecksum(PATH_CHECKSUM, getTChecksum())

        .addChecksum(CHECKSUM, getChecksum())

        .addInstant(CREATE_DATE, getCreateDate())

        .addInstant(UPDATE_DATE, getLastUpdateDate())

        .addInstant(MOST_RECENT_READ_TIME, getMostRecentReadTime())

        .addString(SOURCE_NAME, getSourceName())

        .addString(SOURCE_URL, getSourceURL().map(java.net.URL::toExternalForm))

        .addString(MIME_TYPE, getType())

        .addPath(PATH, getPath())

//        .addString(ORIGINAL_PATH, getOriginalPath().toString())

        .addLong(SIZE, size()) // -1L means unknowable and missing means unknown

        .addString(DESCRIPTION, getDescription())

        .addJSONObject(IBConstants.METADATA, getMetadata())

        .asJSON();
  }

//  Path getOriginalPath();

  default Optional<BasicFileAttributes> getBasicFileAttributes() {
    return getPath().flatMap(path -> IBUtils.getAttributes.apply(path));
  }

  /**
   * @return true if this file was cached, which means the path is based on a RelativeRoot
   */
  default boolean isCached() {
    return false;
  }

  default Optional<RelativeRoot> getRelativeRoot() {
    return Optional.empty();
  }

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

  default ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilder.newInstance(this.getRelativeRoot())
        .addChecksum(new Checksum(copyModel().getStreamChecksum()));
  }

  default PlexusIoResource asPlexusIOResource() {
    return new IBURLPlexusIOResource(this);
  }

}
