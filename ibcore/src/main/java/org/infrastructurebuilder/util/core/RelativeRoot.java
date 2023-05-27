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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;

public class RelativeRoot {
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

  private final String stringRoot; // Not nullalbe

  private final Path path; // Nullable for certain cases
  private final URL url; // Nullable (for certain cases)

  private RelativeRoot(String u) {
    this.stringRoot = (!requireNonNull(u).endsWith("/")) ? u + "/" : u;
    this.url = fromString(requireNonNull(u));
    this.path = (this.url.getProtocol().equals("file")) ? Paths.get(cet.returns(() -> this.url.toURI())) : null;
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
    if (requireNonNull(p).isAbsolute())
      return Optional.of(p);
    else
      return getPath().map(r -> r.resolve(p));
  }

  public final Optional<Path> resolve(String p) {
    return resolve(Path.of(p));
  }

  public final Optional<Path> relativize(Path p) {
    if (requireNonNull(p).isAbsolute())
      return getPath().map(r -> r.relativize(p));
    else
      return Optional.of(p);
  }

  public final String relativize(URL p) {
    return relativize(requireNonNull(p).toExternalForm());
  }

  public final String relativize(String pext) {
    String r = pext.startsWith(stringRoot) ? pext.substring(stringRoot.length()) : pext;
    return (r.startsWith("/")) ? r.substring(1) : r;
  }

}
