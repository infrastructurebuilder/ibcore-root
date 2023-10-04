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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RelativeRoot ("root") is an object that holds some base location as the base location of filesystem or
 * filesystem-like collection.  Some persisted things, like ChecksumBuilder and JSONBuilder, accept a RelativeRoot and strip off
 * the prefix from an absolute value.
 *
 * The RelativeRoot solves a problem of checksumming values that have paths baked into them.  It allows us to manage
 * any number of filesystem objects as relative paths from some RelativeRoot, and thus as long as the root is set to the same
 * value those streams should be the same streams in the future.  So if the path of an object is part of its checksum, then
 * we can just path everything off the RelativeRoot.
 *
 * It is possible that at some point we will make RelativeRoot values into a Java FileSystem object, allowing us to treat everything
 * as 'absolute' within the root area.
 *
 * Requirements:
 * <ol>
 * <li>A root cannot be null.  A null value is not applicable as a root.</li>
 * <li>A root may be a <code>Path</code></li>
 *  <ol>
 *    <li>The Path must be absolute</li>
 *    <li>Any relativization or resolution must be <b><i>within the path</i></b>.  Relative links cannot travel outside the root path.
 *  </ol>
 * <li>A root may be a <code>java.net.URL</code></li>
 *  <ol>
 *    <li>File paths are commuted to their URI equivalent and then externalized, so all Path objects are really URL-backed.</li>
 *    <li>Since a URL is more "outside of the control" of the local system (usually), it is possible that some relocation might be problematic.</li>
 *    <li>Since URL <code>scheme</code>'s are loaded at runtime, it is possible that a reconstituted runtime would have different schemes
 *      available to it.  For instance, one might have a <code>java.net.URLStreamHandler</code> for S3, such that <code>s3://blahblahblah</code> pointed to a viable
 *      URL location.  When that is persisted, but then later the runtime didn't have access to that <code>URLStreamHandler</code> for whatever reason,
 *      the underlying system's integrity would not be damaged but access definitely would be.</li>
 *  </ol>
 * <li>A root may be a URL-like string, such as an S3 url
 *  <ol>
 *    <li>A "URL-like" means that the implementation of whatever IB-based code exists is going to handle the transfers for us, much in the manner
 *    of a <code>URLStreamHandler</code>.</li>
 *    <li>However, unlike above, there will be application logic that interprets the string so that configuration is simpler.</li>
 *    <li>Either way has advantages.</li>
 *  </ol>
 * </ol>
 *
 * Generally, the URL or the Path implementation is the correct one.  Those are loaded at runtime by default, and thus always available.
 *
 */
public class RelativeRoot implements JSONAndChecksumEnabled {
  private static final String URL2 = "URL";

  private static final String PATH = "PATH";

  private static final String STRING_ROOT = "STRING_ROOT";

  private final static Logger log = LoggerFactory.getLogger(RelativeRoot.class);

  public final static Path checkRelative(Path p) {
    if (requireNonNull(p).isAbsolute())
      throw new IBException("must.be.absolute");
    return p;
  }

  public final static Path checkAbsolute(Path p) {
    if (!requireNonNull(p).isAbsolute())
      throw new IBException("must.be.absolute");
    return p;
  }

  public final static Path pathFrom(Optional<RelativeRoot> r1, Path path) {
    return (requireNonNull(path).isAbsolute()) ?
    // If absolute
        path
        // If relative -- works quite poorly on OS's with crappy filesystems.
        : requireNonNull(r1).map(r -> Path.of(r.toString(), path.toString()))
            .orElse(Path.of(Path.of("").toAbsolutePath().toString(), path.toString()));
  }

  private final static URL fromString(String url) {
    URL u = null;
    if (url != null)
      try {
        u = requireNonNull(new URL(url));
      } catch (MalformedURLException e) {
        // return value already set to null
      }
    return u;
  }
  public final static RelativeRoot from(String s) {
    return new RelativeRoot(s);
  }

  public final static RelativeRoot from(URL u) {
    return from(requireNonNull(u).toExternalForm());
  }

  public final static RelativeRoot from(Path p) {
    return from(cet.returns(() -> checkAbsolute(p).toUri().toURL().toExternalForm()));
  }

  public final static RelativeRoot from(JSONObject j) {
    return new RelativeRoot(j.getString(STRING_ROOT));
  }

  private final String stringRoot; // Not nullable

  private final Path path; // Nullable for certain cases
  private final URL url; // Nullable (for certain cases)

  private RelativeRoot(String u) {
    this.stringRoot = (!requireNonNull(u).endsWith("/")) ? u + "/" : u;
    this.url = fromString(requireNonNull(u));

    this.path = (this.url != null)
        ? (this.url.getProtocol().equals("file")) ? Paths.get(cet.returns(() -> this.url.toURI())) : null
        : null;
    getPath().ifPresent(p -> cet.translate(() -> Files.createDirectories(p)));
  }

  public boolean isPath() {
    return this.path != null;
  }

  public boolean isURL() {
    return this.url != null;
  }

  public boolean isURLLike() {
    return !(isPath() || isURL());
  }

  public Optional<Path> getPath() {
    return ofNullable(path);
  }

  public Optional<URL> getUrl() {
    return ofNullable(url);
  }

  @Override
  public String toString() {
    return this.stringRoot;
  }

  public final Optional<Path> resolveAsPath(Path p) {
    log.debug("Resolve {} from {}", p, getPath());
    return (requireNonNull(p).isAbsolute()) ? Optional.of(p) : getPath().map(r -> r.resolve(p));
  }

  /**
   * Resolving a path always has a result, because stringroot always exists
   *
   * @param p
   * @return
   */
  public final String resolvePath(Path p) {
    log.debug("Resolve {} from {}/{}/{}", p, getPath(), getUrl(), this.stringRoot);
    return (requireNonNull(p).isAbsolute()) ? p.toString() : String.format("%s%s", this.stringRoot, p.toString());
  }

  public final Optional<Path> resolvePath(String p) {
    log.debug("Resolve {} from {}", p, this.stringRoot);
    try {
      return Optional.of(Path.of(resolvePath(Path.of(p))));

    } catch (InvalidPathException e) {
      return Optional.empty();
    }
  }

  public final Optional<Path> relativize(Path p) {
    log.debug("Relativize {} from {}", p, getPath());
    if (requireNonNull(p).isAbsolute())
      return getPath().map(r -> r.relativize(p));
    else
      return Optional.of(p);
  }

  public final Optional<String> relativize(URL p) {
    log.debug("Relativize URL {} from {}", p, this.url);
    return ofNullable(isURL() ? relativize(requireNonNull(p).toExternalForm()) : null);

  }

  public final String relativize(String pext) {
    log.debug("Relativize String {} from {}", pext, this.stringRoot);
    String r = pext.startsWith(stringRoot) ? pext.substring(stringRoot.length()) : pext;
    return (r.startsWith("/")) ? r.substring(1) : r;
  }

  @Override
  public JSONObject asJSON() {
    return new JSONBuilder(Optional.empty())
        .addString(STRING_ROOT, this.stringRoot) // required
        .asJSON();
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilder.newInstance().addString(this.stringRoot); // Only the stringroot actually matters
  }

}
