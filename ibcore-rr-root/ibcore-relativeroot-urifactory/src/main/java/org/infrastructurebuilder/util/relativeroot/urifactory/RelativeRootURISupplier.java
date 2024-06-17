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
package org.infrastructurebuilder.util.relativeroot.urifactory;

import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.AbstractBaseRelativeRoot;
import org.infrastructurebuilder.pathref.RelativeRoot;
import org.infrastructurebuilder.pathref.RelativeRootSupplier;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.relativeroot.base.AbstractRelativeRootBasicPathPropertiesSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is (mostly) a testing instance of RelativeRootSupplier, allowing for the creation of RelativeRootSupplier
 * instances in the same manner as the Named instances
 */
@Named(RelativeRootURISupplier.NAME)
// NOT a singleton
public class RelativeRootURISupplier implements RelativeRootSupplier {
  private static final Logger log = LoggerFactory.getLogger(RelativeRootURISupplier.class);
  public static final String NAME = "uri-supplier";
  private final AtomicReference<String> path = new AtomicReference<>();
  private AtomicReference<Path> localPath = new AtomicReference<>();

  public String getName() {
    return NAME;
  }

  /**
   * Get a new one every time
   */
  @Inject
  public RelativeRootURISupplier() {
  }

  public RelativeRootURISupplier(URL p) {
    this();
    this.withPath(p);
  }

  public final RelativeRootURISupplier withPath(URL p) {

    Objects.requireNonNull(p, "Mapping URL");
    log.info("Mapping " + p.toExternalForm());
    final String[] uset = new String[2];
    String uu = p.toExternalForm();
    if (uu.contains("!")) {
      String[] xx = uu.split("!");
      uset[0] = xx[0];
      uset[1] = xx[1];
    } else {
      uset[0] = uu;
      uset[1] = "/";
    }
    URI ru = URI.create(uu);
    Path pathToIBDataXml;
    if (uset[0].startsWith("jar") || uset[0].startsWith("zip")) {
      log.info("Reading from archive " + uset[0]);
      uset[0] += "!/"; // FIXME somehow we need to make this work without "/" althought that's correct

      URI optFsURI = Optional.ofNullable(IBUtils.translateToWorkableArchiveURL(uset[0]))
          .map(ur -> cet.returns(() -> ur.toURI())).orElseThrow(() -> new IBException("No URI for " + uset[0]));

      try (FileSystem optFs = cet.returns(() -> FileSystems.newFileSystem(optFsURI, Collections.emptyMap()))) {
        localPath.compareAndSet(null, optFs.getPath(optFs.getSeparator()));
        pathToIBDataXml = optFs.getPath(uset[1]);
      } catch (IOException e) {
        throw new IBException("No filesystem allowed us to load data for zip filesystem", e);
      }
    } else if (uset[0].startsWith("file")) {
      log.info("Reading from file location " + uset[0]);
      pathToIBDataXml = cet.returns(() -> Paths.get(p.toURI()));
      localPath.compareAndSet(null, pathToIBDataXml.getParent().getParent());
//        left = cet.returns(() -> datasetRoot.toUri().toURL());
    } else
      throw new IBException("Unrecognized URL for mapping to data set " + uset[0]);

    this.path.compareAndSet(null, this.localPath.get().toString());
    return this;
  }

  public Optional<RelativeRoot> getRelativeRoot(Object data) {
    if (data instanceof URL x) {
      withPath(x);
      return getRelativeRoot();
    } else if (data instanceof String s) {
      withPath(cet.returns(() -> new URL(s)));
      return getRelativeRoot();
    } else {
      log.error("Data must be a URL " + data);
      return Optional.empty();
    }
  }

  protected Logger getLog() {
    return log;
  }

  public final static class RelativeRootURI extends AbstractBaseRelativeRoot {

    protected RelativeRootURI(String u) {
      super(u);
    }

    @Override
    public RelativeRoot extendAsNewRoot(Path newPath) {
      return Optional.ofNullable(newPath).map(p -> {
        if (p.isAbsolute())
          throw new IBException("Unable to extend existing RR with " + p);
        return this;
      }).orElseThrow(() -> new IBException("null extension"));
    }

    @Override
    public Optional<InputStream> getInputStreamFromExtendedPath(String path) {
      // TODO Auto-generated method stub
      return Optional.empty();
    }

  }

  @Override
  public Optional<RelativeRoot> getRelativeRoot() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

}
