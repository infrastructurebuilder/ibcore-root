/*
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

public class RelativeRoot {
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

  private final Path path;
  private final URL url;
  private final String urlE;

  public RelativeRoot(Path p, String url) {
    this.path = requireNonNull(p).toAbsolutePath();
    this.urlE = url;
    this.url = fromString(url);

  }

  public RelativeRoot(Path p, URL u) {
    this(requireNonNull(p), requireNonNull(u).toExternalForm());
  }

  public RelativeRoot(Path p) {
    this.path = requireNonNull(p).toAbsolutePath();
    this.url = cet.returns(() -> this.path.toUri().toURL());
    this.urlE = this.url.toExternalForm();
  }

  public RelativeRoot(String u) {
    this.url = fromString(requireNonNull(u));
    this.path = getUrl().map(u1 -> u1.getPath().equals("file") ? Paths.get(u1.getPath()) : null).orElse(null);
    this.urlE = requireNonNull(u);
  }

  public RelativeRoot(URL u) {
    this(requireNonNull(u).toExternalForm());
  }

  public Optional<Path> getPath() {
    return ofNullable(path);
  }

  public Optional<URL> getUrl() {
    return ofNullable(url);
  }

  public Optional<String> getURLAsString() {
    return ofNullable(urlE);
  }

  @Override
  public String toString() {
    return getPath().map(Path::toString).orElse(this.urlE);
  }

  public final Path relativize(Path p) {
    return getPath().map(root -> root.relativize(p)).orElse(p);
  }

  public final String relativize(URL p) {
    return relativize(requireNonNull(p).toExternalForm());
  }

  public final String relativize(String pext) {
    return pext.startsWith(urlE) ? pext.substring(urlE.length()) : pext;
  }

}
