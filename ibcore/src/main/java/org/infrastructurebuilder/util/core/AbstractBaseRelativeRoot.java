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
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractBaseRelativeRoot implements RelativeRoot {

  private final static Logger log = LoggerFactory.getLogger(AbstractBaseRelativeRoot.class);

  public final static Optional<Path> checkAbsolute(Path p) {
    return ofNullable(requireNonNull(p).isAbsolute() ? p : null);
  }

  public final static Optional<URL> fromString(String url) {
    URL u = null;
    if (url != null)
      try {
        u = requireNonNull(new URL(url));
      } catch (MalformedURLException e) {
        log.warn(String.format("Tried fromString with %s", url), e);
        // return value already set to null
      }
    return Optional.ofNullable(u);
  }

  private final String stringRoot; // Required
  protected transient final URL url; // Nullable (for certain cases)

  protected AbstractBaseRelativeRoot(String u) {
    AtomicReference<String> aref = new AtomicReference<>("/");
    this.url = fromString(requireNonNull(u)).orElse(null);

    // Actually ensures that the RR dirs are available for a "path"/"file"
    getPath().ifPresent(p -> {
      boolean exists = Files.exists(p, LinkOption.NOFOLLOW_LINKS);
      boolean dir = Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS);
      if (exists) {
        if (!dir) {
          // This is a file, probably an archive of some sort.
          aref.set("!");
        }
      } else
        cet.translate(() -> Files.createDirectories(p));
    });
    this.stringRoot = (!requireNonNull(u).endsWith(aref.get())) ? u + aref.get() : u;

  }

  public Optional<Path> getPath() {
    return ofNullable((this.url != null)
        ? (this.url.getProtocol().equals("file")) ? Paths.get(cet.returns(() -> this.url.toURI())) : null
        : null);
  }

  public Optional<URL> getUrl() {
    return ofNullable(url);
  }

  @Override
  public String toString() {
    return this.stringRoot;
  }

  public final Optional<Path> resolve(Path p) {
    log.debug("Resolve {} from {}", p, getPath());
    return (requireNonNull(p).isAbsolute()) ? of(p) : getPath().map(r -> r.resolve(p));
  }

  /**
   * Resolving a path usually always has a result, because stringroot always exists But some implemenatins may not allow
   * absolute paths to be passed in and return empty when doing so, rather than throwing a runtime exception
   *
   * @param p
   * @return
   */
  public final Optional<String> resolvePath(Path p) {
    log.debug("Resolve {} from {}/{}/{}", p, getPath(), getUrl(), this.stringRoot);
    return of((requireNonNull(p).isAbsolute()) ? p.toString() : String.format("%s%s", this.stringRoot, p.toString()));
  }

  public final Optional<Path> relativize(Path p) {
    log.debug("Relativize {} from {}", p, getPath());
    if (requireNonNull(p).isAbsolute())
      return getPath().map(r -> r.relativize(p));
    else
      return of(p);
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
    return new JSONBuilder(empty()).addString(RelativeRoot.RELATIVE_ROOT_URLLIKE, this.stringRoot) // required
        .asJSON();
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilder.newInstance().addString(this.stringRoot); // Only the stringroot actually matters
  }

  private Optional<Path> makeAFile(Path relativePath, String prefix, String suffix, boolean temp) {
    if (relativePath != null && relativePath.isAbsolute())
      return Optional.empty();
    Optional<Path> thePath = (relativePath == null) ? getPath() : getPath().map(p -> p.resolve(relativePath));
    return thePath.map(location -> {
      Path f;
      try {
        Files.createDirectories(location);
        f = Files.createTempFile(location, prefix, suffix);
        f.toFile().deleteOnExit();
        return relativize(f).get();
      } catch (IOException e) {
        return null;
      }
    });
  }

  @Override
  public Optional<Path> getTemporaryPath(String prefix, String suffix) {
    return makeAFile(null, prefix, suffix, true);
  }

  @Override
  public Optional<Path> getPermanantPath(String prefix, String suffix) {
    return makeAFile(null, prefix, suffix, false);
  }

  @Override
  public Optional<Path> getTemporaryPath(Path relativePath, String prefix, String suffix) {
    return makeAFile(relativePath, prefix, suffix, true);
  }

  @Override
  public Optional<Path> getPermanantPath(Path relativePath, String prefix, String suffix) {
    return makeAFile(relativePath, prefix, suffix, false);
  }

  @Override
  public Optional<Path> extendPath(Path p) {
    return getPath().map(p1 -> p1.resolve(requireNonNull(p)));
  }

}
