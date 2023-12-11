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
package org.infrastructurebuilder.util.core;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RelativeRoot ("root") is an object that holds some base location as the base location of filesystem or
 * filesystem-like collection.
 *
 * Some persisted things, like ChecksumBuilder and JSONBuilder, accept a RelativeRoot and strip off the prefix from
 * absolute paths so that the persisted values remain portable.
 *
 * The RelativeRoot helps to solve a problem of checksumming values that have paths baked into them. It allows us to
 * manage any number of filesystem objects as relative paths from some RelativeRoot, and thus as long as the root is set
 * to the same value those streams should be the same streams in the future. So if the path of an object is part of its
 * checksum, then we can just relativize the path everything off the RelativeRoot.
 *
 * It is possible that at some point we will make RelativeRoot values into a Java FileSystem object, allowing us to
 * treat everything as 'absolute' within the root area.
 *
 * Requirements:
 * <ol>
 * <li>A root cannot be null. A null value is not applicable as a root. However, most RR is views as an Optional, in
 * which case the consumer is expected to accept that there is no root</li>
 * <li>A root may be a <code>Path</code></li>
 * <ol>
 * <li>The Path must be an absolute path (i.e. <code>Path.toAbsolutePath()</code>)</li>
 * <li>Any relativization or resolution must be <b><i>within the path</i></b>. Relative links cannot travel outside the
 * root path.
 * </ol>
 * <li>A root may be a <code>java.net.URL</code></li>
 * <ol>
 * <li>File paths are commuted to their URI equivalent and then externalized, so all Path objects are really
 * URL-backed.</li>
 * <li>Since a URL is more "outside of the control" of the local system (usually), it is possible that some relocation
 * might be problematic.</li>
 * <li>Since URL <code>scheme</code>'s are loaded at runtime, it is possible that a reconstituted runtime would have
 * different schemes available to it. For instance, one might have a <code>java.net.URLStreamHandler</code> for S3, such
 * that <code>s3://blahblahblah</code> pointed to a viable URL location. When that is persisted, but then later the
 * runtime didn't have access to that <code>URLStreamHandler</code> for whatever reason, the underlying system's
 * integrity would not be damaged but access definitely would be.</li>
 * </ol>
 * <li>A root may be a URL-like string, such as a cloud provider blobstore endpoint url
 * <ol>
 * <li>A "URL-like" means that the implementation of whatever IB-based code exists is going to handle the transfers for
 * us, much in the manner of a <code>URLStreamHandler</code>.</li>
 * <li>However, unlike above, there must be application logic that interprets the string so that configuration is
 * simpler.</li>
 * <li>Either way has advantages. It is expected that most RRs will be filesystem paths or paths to URLs that have
 * stream handlers associated with them. Cloud provided blobstores are the most likely targets in that case</li>
 * </ol>
 * </ol>
 *
 * Generally, the URL or the Path implementation is the correct one. Those are loaded at runtime by default, and thus
 * always available.
 *
 */
public interface RelativeRoot extends JSONAndChecksumEnabled {
  final static String RELATIVE_ROOT_URLLIKE = "URL";
  final static Logger log = LoggerFactory.getLogger(RelativeRoot.class);

  default boolean isPath() {
    return getPath().isPresent();
  }

  default boolean isURL() {
    return getUrl().isPresent();
  }

  default boolean isURLLike() {
    return !(isPath() || isURL());
  }

  Optional<Path> getPath();

  Optional<URL> getUrl();

  /**
   * Resolving a path always has a result, because stringroot always exists
   *
   * @param p
   * @return
   */
  String resolvePath(Path p);

  default Optional<Path> resolvePath(String p) {
    return getPath().flatMap(thisPath -> {
      return Optional.ofNullable(p).flatMap(pStr -> {
        Path v = null;
        try {
          Path resPath = Paths.get(pStr);
          if (!resPath.isAbsolute())
            v = thisPath.resolve(resPath);
        } catch (Throwable t) {
          // Do nothing
          log.warn("{} not a path", pStr);
        }
        return Optional.ofNullable(v);
      });

    });
  }

  default Checksum asChecksum() {
    return getChecksumBuilder().asChecksum();
  }

  Optional<Path> relativize(Path p);

  Optional<String> relativize(URL p);

  String relativize(String pext);
  
  RelativeRoot extend(String newPath);

}
