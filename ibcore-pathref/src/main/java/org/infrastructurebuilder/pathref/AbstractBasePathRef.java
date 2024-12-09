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
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.client.utils.URIBuilder;
import org.infrastructurebuilder.exceptions.IBException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractBasePathRef<T> implements PathRef {

  private static final String URI_INVALID = "URI {} is invalid";
  private final static Logger log = LoggerFactory.getLogger(AbstractBasePathRef.class);

  public final static Optional<Path> pathFromURI(URI i) {
    Path p = null;
    try {
      p = Paths.get(i);
    } catch (Throwable t) {
      log.debug(String.format("Error trying to getPath from {}", i), t);
    }
    return ofNullable(p);

  }

  public final static Optional<Path> checkAbsolute(Path p) {
    return ofNullable(requireNonNull(p).isAbsolute() ? p : null);
  }

  public final static Optional<URI> fromString(String uri) {
    URI _uri = null;
    try {
      _uri = requireNonNull(URI.create(uri));
    } catch (NullPointerException | IllegalArgumentException e) {
      log.warn(String.format("Tried fromString with %s", uri), e);
      // return value already set to null
    }
    return Optional.ofNullable(_uri);
  }

  public final static Optional<URL> fromURI(URI u) {
    URL _url = null;
    try {
      _url = requireNonNull(u).toURL();
    } catch (NullPointerException | MalformedURLException e) {
      log.warn(String.format("Tried fromString with %s", u), e);
    }
    return Optional.ofNullable(_url);
  }

  private final URI uri;
  private final Proxy proxy;
  private final AtomicReference<String> aref = new AtomicReference<>("/");

  protected AbstractBasePathRef(URI uri) {
    this(uri.toString(), null);
    log.debug("From uri {} ", uri);
  }

  private AbstractBasePathRef(URI uri, Proxy proxy) {
    requireNonNull(uri, "AbsoluteBasePathRef constructor uri null");
    Optional<Path> path = pathFromURI(uri);
    boolean validPath = path.map(p -> {
      if (!p.isAbsolute())
        return false;
      // Actually ensures that the dirs are available for a "path"/"file"
      boolean exists = Files.exists(p, LinkOption.NOFOLLOW_LINKS);
      boolean dir = Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS);
      if (exists) {
        if (!dir) {
          // This is a file, expected to be an archive of some sort.
          aref.set("!/");
        }
      } else
        cet.translate(() -> Files.createDirectories(p));
      return true;
    }).orElse(uri.isAbsolute());
    if (!validPath) {
      String err = String.format(URI_INVALID, uri.toString());
      log.error(err);
      throw new IBException(err);
    }
    URIBuilder b = new URIBuilder(uri);
    var p = uri.getPath();
    if (!p.endsWith(aref.get())) {
      p = p + aref.get();
      b.setPath(p);
    }
    this.uri = cet.returns(() -> b.build());
    this.proxy = proxy;
  }

  protected AbstractBasePathRef(String u) {
    this(fromString(u).get(), null);

  }

  protected AbstractBasePathRef(String u, Proxy p) {
    this(fromString(u).get(), p);

  }

  protected final URI getUri() {
    return uri;
  }

  public Optional<Path> getPath() {
    return pathFromURI(getUri());
  }

  public Optional<URL> getUrl() {
    URL p = null;
    try {
      p = getUri().toURL();
    } catch (Throwable t) {
      log.error(String.format("Error trying to getUrl from {}", getUri()), t);
    }
    return ofNullable(p);
  }

  @Override
  public String toString() {
    return this.uri.toString();
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
    if (p == null || p.isAbsolute())
      return empty();
    log.debug("Resolve {} from {}/{}/{}", p, getPath(), getUrl(), this.uri.toString());
    return of(String.format("%s%s", this.uri.toString(), p.toString()));
  }

  @Override
  public Optional<Path> relativize(Object p) {
    log.debug("Relativize {} from {}", p, toString());
    URI target;
    String thisString = null;
    Path p2 = null;
    try {
      if (p instanceof String s) {
        p = new URI(s);
      }
      if (p instanceof File file)
        p = file.toPath();
      if (p instanceof Path path) {
        p = path.toUri();
      }
      if (p instanceof URL url) {
        target = url.toURI();
      }
      if (p instanceof URI uri) {
        target = uri;
        String s = target.toString();
        // Fix for https://www.rfc-editor.org/rfc/rfc8089#appendix-A
        if (s.startsWith("file:///")) {
          s = s.replaceFirst("file:///", "file:/");
          target = new URI(s);
        }
      } else {
        log.warn("Cannot relativize {} from {}", thisString, this.uri.toString());
        return empty();
      }
      thisString = target.toString();
      var stringRoot = this.uri.toString();
      try {
        Path thisPath = Paths.get(this.uri); // Produces arbitary results for a non-absolute path
        Path relPath = Paths.get(target);
        p2 = thisPath.relativize(relPath);
      } catch (Throwable t1) {
        if (thisString.startsWith(stringRoot)) {
          String[] splitted = thisString.substring(stringRoot.length()).split("/");
          p2 = Paths.get("", splitted);
        } else
          log.warn("Cannot relativize {} from {}", thisString, stringRoot);
      }
    } catch (Throwable t) {

    }
    return Optional.ofNullable(p2);
  }

  @Override
  public JSONObject asJSON() {
    return new JSONBuilder(empty()).addString(PathRef.RELATIVE_ROOT_URLLIKE, this.uri.toString()) // required
        .asJSON();
  }

//  @Override
//  public ChecksumBuilder getChecksumBuilder() {
//    return ChecksumBuilderImpl.newInstance().addString(this.stringRoot); // Only the stringroot actually matters
//  }

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
  public Optional<Path> createTemporaryFile(String prefix, String suffix) {
    return makeAFile(null, prefix, suffix, true);
  }

  @Override
  public Optional<Path> createPermanantFile(String prefix, String suffix) {
    return makeAFile(null, prefix, suffix, false);
  }

  @Override
  public Optional<Path> createTemporaryFile(Path relativePath, String prefix, String suffix) {
    return makeAFile(relativePath, prefix, suffix, true);
  }

  @Override
  public Optional<Path> createPermanantFile(Path relativePath, String prefix, String suffix) {
    return makeAFile(relativePath, prefix, suffix, false);
  }

  @Override
  public Optional<InputStream> getInputStreamFrom(String path) {
    URI u;
    try {
      u = URI.create(path);
      if (u.isOpaque()) {
        log.error(String.format("Cannot extend with opaque URI from {}", path));
        return empty();
      }
      if (u.isAbsolute()) {
        log.error(String.format("Cannot extend with absolute URI from {}", path));
        return empty();
      }
    } catch (Throwable t) {
      log.error(String.format("URI error from {}", path), t);
      return empty();
    }
    try {
      URI target = this.uri.resolve(u);
      URL url = target.toURL();
      return Optional
          .ofNullable((this.proxy == null ? url.openConnection() : url.openConnection(this.proxy)).getInputStream());
    } catch (Throwable t) {
      log.error(String.format("Cannot resolve from {} to {}", this.uri, u), t);
      return empty();
    }
  }

  @Override
  public Optional<PathRef> extendAsPathRef(Path p) {
    if (p == null)
      return empty();
    if (p.isAbsolute()) {
      log.error("Path extension {} is not relative", p);
      return empty();
    }
    String newPath = this.uri.getPath();

    String target = newPath + (newPath.endsWith("/") ? "" : "/") + p.toString();
    URIBuilder u = new URIBuilder(this.uri);
    u.setPath(target);
    try {
      URI uri2 = u.build();
      return Optional.ofNullable(new AbstractBasePathRef<String>(uri2, proxy) {
      });
    } catch (Throwable t) {
      log.error(String.format("Error creating PathRef from {}", target), t);
      return empty();
    }
  }
}
