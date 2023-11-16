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

abstract public class AbstractBaseRelativeRoot implements RelativeRoot {

  private final static Logger log = LoggerFactory.getLogger(AbstractBaseRelativeRoot.class);

  public final static Optional<Path> checkAbsolute(Path p) {
    return Optional.ofNullable(requireNonNull(p).isAbsolute() ? p : null);
  }

  public final static URL fromString(String url) {
    URL u = null;
    if (url != null)
      try {
        u = requireNonNull(new URL(url));
      } catch (MalformedURLException e) {
        // return value already set to null
      }
    return u;
  }

  private final String stringRoot; // Not nullable
  private final Path path; // Nullable for certain cases
  private final URL url; // Nullable (for certain cases)

  protected AbstractBaseRelativeRoot(String u) {
    this.stringRoot = (!requireNonNull(u).endsWith("/")) ? u + "/" : u;

    this.url = fromString(requireNonNull(u));

    this.path = (this.url != null)
        ? (this.url.getProtocol().equals("file")) ? Paths.get(cet.returns(() -> this.url.toURI())) : null
        : null;
    // Actually ensures that the RR dirs are available for a "path"/"file"
    getPath().ifPresent(p -> cet.translate(() -> Files.createDirectories(p)));
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

  public final Optional<Path> resolve(Path p) {
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
    return new JSONBuilder(Optional.empty()).addString(RelativeRoot.RELATIVE_ROOT_URLLIKE, this.stringRoot) // required
        .asJSON();
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilder.newInstance().addString(this.stringRoot); // Only the stringroot actually matters
  }

}
