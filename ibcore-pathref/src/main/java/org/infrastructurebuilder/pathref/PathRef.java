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
package org.infrastructurebuilder.pathref;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.pathref.IBChecksumUtils.stripTrailingSlash;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PathRef is an object that holds some base location as the base location of filesystem or filesystem-like
 * collection.
 *
 * Some persisted things, like ChecksumBuilder and JSONBuilder, accept a PathRef and strip off the prefix from absolute
 * paths so that the persisted values remain portable.
 *
 * A PathRef's job is to ensure that files may be <i>read</i> from within the context of the object using only a Path.
 *
 *
 * The PathRef helps to solve a problem of checksumming values that have paths baked into them. It allows us to manage
 * any number of filesystem objects as relative paths from some PathRef, and thus as long as the root is set to the same
 * value those streams should be the same streams in the future. So if the path of an object is part of its checksum,
 * then we can just relativize the path everything off the PathRef.<br/>
 * <br/>
 * It is possible that at some point we will make PathRef values into a Java FileSystem object, allowing us to treat
 * everything as 'absolute' within the root area.<br/>
 * Uses:
 * <ul>
 * <li>First and foremost, a PathRef is a pointer to some named tree of information.</li>
 * <li>A PathRef may be used to acquire an InputStream or a Reader using a <i>RELATIVE</i>
 * <code>java.nio.Path</code></li>
 * <li>Sometimes, a PathRef can be used to get a permanant or temporary file</li>
 * <li>Sometimes, a PathRef can be used as the root to a FileSystem-like construct.</li>
 * </ul>
 *
 * Requirements:
 * <ol>
 * <li>A PathRef must be constructed from a string.</li>
 *
 * <li>A root <i>cannot be <code>null</code></i>. A null value is not applicable as a root. However, most RRs is viewed
 * as an Optional, in which case the consumer is expected to accept that there actually is no root</li>
 * <ol>
 * </ol>
 * <li>A <code>PathRef</code> may point to a read-only location</li>
 * <ol>
 * <li>Not all PathRef instances are locations for writing.</li>
 * <li>Not all writeable PathRefs would be writable using the PathRef itself.</li>
 * </ol>
 * <li>A root <b>may</b> be a <code>Path</code> <i>to a filesystem directory</i></li>
 * <ol>
 * <li>The Path must be an absolute path (i.e. <code>Path.toAbsolutePath()</code>)</li>
 * <li>Any relativization or resolution must be <b><i>within the path</i></b>.</li>
 * <li>Relative links </i><b>must not</b> travel outside the root path</i>.<br/>
 * This means that any item within a relative root may not pass above the "root base" and must never refer to anything
 * outside of the defined root path.</li>
 * </ol>
 * <li>A root <b>may</b> be a <code>Path</code> <i>to a filesystem-backed archive file</i></li>
 * <ol>
 * <li>The file must be an <i>absolute path</i> to a ZipFileSystem-readable archive</li>
 * <li>Such an archive is generally going to be considered read-only (at least for now></li>
 * <li>All data will be read from the archive as if it were a ZipFilesystem item (i.e
 * <code>/path/to/filename.zip!some/path/inside.txt</code>)</li>
 * </ol>
 * <li>A root may be a <code>java.net.URI</code></li>
 * <ol>
 * <li>URI and URL might be used interchangeably in this documentation, but they're not identical.</li>
 * <li>File paths are commuted to their URI equivalent and then externalized.</li>
 * <li>This actually means that all Path objects are really URI-backed, for reading purposes.</li>
 * <li>Since a URI is sometimes more "outside of the control" of the local system (usually), it is possible that some
 * relocation might be problematic.</li>
 * <li>Since URL (thus URI) <code>scheme</code>'s are loaded at runtime, it is possible that a reconstituted runtime
 * would have different schemes available to it. For instance, one might have a <code>java.net.URLStreamHandler</code>
 * for S3, such that <code>s3://blahblahblah</code> pointed to a viable URL location. When that is persisted, but then
 * later the runtime didn't have access to that <code>URLStreamHandler</code> for whatever reason, the underlying
 * system's integrity would not be damaged but access definitely would be.</li>
 * </ol>
 * <li>A root may be a URL-like string, such as a cloud provider blobstore endpoint url
 * <ol>
 * <li>A "URL-like" means that the implementation of whatever IB-based code exists is going to handle the transfers for
 * us, much in the manner of a <code>URLStreamHandler</code>.</li>
 * <li>However, unlike above, there must be application logic that interprets the string so that configuration is
 * simpler.</li>
 * <li>Either way has advantages. It is expected that most PathRefs will be filesystem paths or paths to URLs that have
 * stream handlers associated with them. Cloud provided blobstores are the most likely targets in that case</li>
 * </ol>
 * </ol>
 *
 * Generally, the URL or the Path implementation is the correct one. Those are loaded at runtime by default, and thus
 * always available.
 *
 */
public interface PathRef extends JSONAndChecksumEnabled {
  final static String RELATIVE_ROOT_URLLIKE = "URL";
  final static Logger log = LoggerFactory.getLogger(PathRef.class);

  default ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilderFactory.newAlternateInstanceWithRelativeRoot(Optional.of(this));
  }

  default boolean isPath() {
    return getPath().isPresent();
  }

  default boolean isURL() {
    return getUrl().isPresent();
  }

  default boolean isURLLike() {
    return !(isPath() || isURL());
  }

  default Optional<Path> getPath() {
    return empty();
  }

  default Optional<URL> getUrl() {
    return empty();
  }

  default Optional<String> getUrlLike() {
    return empty();
  }

  default boolean isParentOf(PathRef otherRoot) {

    var sr = stripTrailingSlash.apply(toString());
    var or = stripTrailingSlash.apply(requireNonNull(otherRoot).toString());

    return or.startsWith(sr) && !(or.equals(sr));
  }

  default boolean isParentOf(Path p) {
    if (p.isAbsolute())
      return getPath().map(path -> {
        return p.startsWith(path);
      }).orElse(false);
    return false;
  }

  /**
   * Resolving a path may not always has a result, because although stringroot always exists, some PathRef
   * implementations may return empty for absolute paths because they always expect relative paths.
   *
   * @param p
   * @return
   */
  Optional<String> resolvePath(Path p);

  default Optional<Path> toResolvedPath(Path p) {
    if (p == null || p.isAbsolute())
      return empty();
    return toResolvedPath(p.toString());
  }

  default Optional<Path> toResolvedPath(String p) {
    return getPath().flatMap(thisPath -> {
      return ofNullable(p).flatMap(pStr -> {
        Path v = null;
        try {
          Path resPath = Paths.get(pStr);
          if (!resPath.isAbsolute())
            v = thisPath.resolve(resPath);
        } catch (Throwable t) {
          // Do nothing
          log.warn("{} not a path", pStr);
        }
        return ofNullable(v);
      });
    });
  }

  /**
   * Produce a <code>Path</code> object from a reference location. The location must be "within" the scope of a given
   * PathRef. A given PathRef must determine (by type) if the supplied object is viable and if it's possible to
   * relativize it.
   *
   * @param p Some object that is somehow referencable within the scope of this PathRef
   * @return a Path (that can be used to reference the Object
   */
  Optional<Path> relativize(Object p);

  /**
   * Create a new PathRef by appending the provided path to the existing path of the PathRef. The new PathRef is not
   * guaranteed to be of the same type as this PathRef, but probably is.
   *
   * The result is a PathRef, so if the extension causes the URI of the PathRef to point to a "regular" file, it is
   * expected that this file will be an ARCHIVE and will have a <code>!/</code> at the end so that values can be read
   * from it directly. IF THAT FILE IS NOT AN ARCHIVE, the extension will STILL WORK!
   *
   * @param newPath <i>RELATIVE</i> <code>java.nio.Path</code> instance
   * @return a new PathRef if possible or <code>empty()</code> if not possible/newPath was absolute
   */
  Optional<PathRef> extendAsPathRef(Path newPath);

  /**
   * PathRefs frequently point to ReadOnly locations, or at least ones that are difficult to write to
   *
   * @return
   */
  default boolean isReadOnly() {
    return true;
  }

  /**
   * Creates a temp file (that can then be opened) that will be deleted upon jvm exit. This is a relative path to the
   * root.
   *
   * @param prefix
   * @param suffix
   * @return Path if created or empty if it cannot be created
   */
  default Optional<Path> createTemporaryFile(String prefix, String suffix) {
    return empty();
  }

  /**
   * Creates a temp file (that can then be opened) that will be deleted upon jvm exit This is a relative path to the
   * root.
   *
   * @param relativePath
   * @param prefix
   * @param suffix
   * @return Path if created or empty if it cannot be created
   */
  default Optional<Path> createTemporaryFile(Path relativePath, String prefix, String suffix) {
    return empty();
  }

  /**
   * Creates a file that will not be deleted on exit This is a relative path to the root.
   *
   * @param prefix
   * @param suffix
   * @return Path if created or empty if it cannot be created
   */
  default Optional<Path> createPermanantFile(String prefix, String suffix) {
    return empty();
  }

  /**
   * Creates a file that will not be deleted on exit This is a relative path to the root.
   *
   * @param relativePath
   * @param prefix
   * @param suffix
   * @return Path if created or empty if it cannot be created
   */
  default Optional<Path> createPermanantFile(Path relativePath, String prefix, String suffix) {
    return empty();
  }

//  Optional<Path> extendPath(Path p);

  Optional<InputStream> getInputStreamFrom(String path);
}
