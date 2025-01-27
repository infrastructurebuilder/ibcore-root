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
package org.infrastructurebuilder.pathref.urifactory;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.AbstractBasePathRef;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This producer uses a ZipFileSystem to retrieve items stored within an archive file.
 *
 * There are multiple ways to do this, including expanding the file for direct access to a local directory, etc. That
 * would obviously increase the access time and reduce the initial retrieval time.
 *
 */
@Named(ZipFilePathRefProducer.NAME)
// NOT a singleton
public class ZipFilePathRefProducer implements PathRefProducer {
  private static final Logger log = LoggerFactory.getLogger(ZipFilePathRefProducer.class);
  public static final String NAME = "uri-supplier";
  private final AtomicReference<String> path = new AtomicReference<>();
  private final AtomicReference<Path> localPath = new AtomicReference<>();

  public String getName() {
    return NAME;
  }

  /**
   * Get a new one every time
   */
  @Inject
  public ZipFilePathRefProducer() {
  }

  protected Logger getLog() {
    return log;
  }

  public Optional<PathRef> with(String data) {

    return Optional.ofNullable(data).map(s -> {
      var lsc = s.toLowerCase();
      if (!lsc.endsWith(".zip") && !lsc.endsWith(".jar"))
        return null;
      PathRef n;
      try {
        n = new ZipFilePathRef(s);
      } catch (Throwable t) {
        log.error(String.format("Error creating ZipFilePathRef from {}", data), t);
        n = null;
      }
      return n;
    });
  }

  public final static class ZipFilePathRef extends AbstractBasePathRef {

    private FileSystem fs;

    protected ZipFilePathRef(String u) {
      super(u);
      URI uri = URI.create(u);

      Map<String, ?> env = new HashMap<>();
      try {

        fs = FileSystems.newFileSystem(uri, env);
      } catch (IOException e) {
        log.error("Error making filesystem", e);
        throw new IBException(e);
      }
    }

    @Override
    public Optional<InputStream> getInputStreamFrom(String path) {
      Path p = fs.getPath(path);
      try {
        return of(Files.newInputStream(p, StandardOpenOption.READ));
      } catch (IOException e) {
        log.error("Error opening path {} ", path, e);
        return empty();
      }
    }

    @Override
    public void close() throws Exception {
      fs.close();
      super.close();
    }

    @Override
    public Optional<PathRef> extendAsPathRef(Path newPath) {
      // Zip files are their own root and that's that
      return Optional.empty();
    }
  }


}
