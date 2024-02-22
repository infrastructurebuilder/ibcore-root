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
package org.infrastructurebuilder.util.vertx.base.impl;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_OCTET_STREAM;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.DefaultPathAndChecksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.PathAndChecksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.RelativeRootSupplier;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.AbstractIBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.IBResourceException;
import org.infrastructurebuilder.util.readdetect.IBResourceIS;
import org.infrastructurebuilder.util.readdetect.impl.RelativePathIBResourceBuilder;
import org.infrastructurebuilder.util.vertx.base.VertxIBResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.streams.Pump;

@Named(VertxIBResourceBuilderFactoryImpl.NAME)
public class VertxIBResourceBuilderFactoryImpl extends AbstractIBResourceBuilderFactory<Future<VertxIBResource>> {
  static final String NAME = "vertx-ibresource-builder-factory";

  private static final long serialVersionUID = 1159380327029586147L;

  private final static Logger log = LoggerFactory.getLogger(VertxIBResourceBuilderFactoryImpl.class);

  private Supplier<IBResourceBuilder<Future<VertxIBResource>>> builder;

  private final Vertx vertx;

  private final TypeToExtensionMapper typeMapper;

  @Inject
  public VertxIBResourceBuilderFactoryImpl(Vertx vertx, RelativeRootSupplier relRootSupplier,
      TypeToExtensionMapper typeMapper)
  {
    super(requireNonNull(relRootSupplier).get().orElseThrow(() -> new IBResourceException("No root")));
    this.typeMapper = Objects.requireNonNull(typeMapper);
    this.vertx = Objects.requireNonNull(vertx);
    // Delivers a new builder from the relative root each time
    this.builder = () -> new DefaultVertxIBResourceBuilder(this.vertx, getRelativeRoot());
    this.setRoot(this.getRelativeRoot().getPath().map(Path::toAbsolutePath).map(Path::toString).orElse(null));
  }

  public String getName() {
    return NAME;
  }

  @Override
  protected Supplier<IBResourceBuilder<Future<VertxIBResource>>> getBuilder() {
    // Delivers a new builder from the relative root each time
    return () -> null; // new RelativePathIBResourceBuilder(getRelativeRoot());
  }

  @Override
  public Optional<IBResourceBuilder<Future<VertxIBResource>>> fromPathAndChecksum(PathAndChecksum p) {
    return Optional.of(getBuilder().get().fromPathAndChecksum(p));
//    log.debug("Getting stream from {}", p);
//
//    var c = getRelativeRoot()
//
//        .map(rr -> cacheIt(p, type))
//
//        .orElseGet(() -> readIt(p, type));
//
//    return Optional.ofNullable(c.result());
  }

  private Future<IBResourceBuilder<Future<VertxIBResource>>> readIt(Path p, String type) {
    log.info("Reading from {}", p);
    return Checksum.ofPath.apply(p).map(checksum -> {
      var qq = builderFromPathAndChecksum(p, checksum).get(); // FIXME this might fail!

      IBResourceBuilder<Future<VertxIBResource>> m = qq;
      Optional.ofNullable(type).ifPresent(t -> m.withType(t));
      return succeededFuture(m);
    }).orElse(failedFuture("Could not perform readIt"));
  }

  private Future<IBResourceBuilder<Future<VertxIBResource>>> cacheIt(Path p, String type) {
    log.info("Cacheing from {}", p);
    var rootPath = getRelativeRoot().getPath();
//
//    if (rootPath.isEmpty()) {
//      log.error("no.root.path");
//      return Optional.empty();
//    }
    var r = rootPath.get().toAbsolutePath().toString() + File.separatorChar + "vibrtf";
    FileSystem fs = this.vertx.fileSystem();

    var tempopts = new OpenOptions().setCreate(true).setWrite(true);
    var readopts = new OpenOptions().setCreate(false).setWrite(false).setRead(true);
    Future<AsyncFile> inbound = fs.open(p.toAbsolutePath().toString(), readopts);
    Future<String> copied = fs.createTempFile(r, "." + this.typeMapper.getExtensionForType(type))
        // Set temp file to deleteOnExit
        .compose(tempfilename -> {
          Path f = Paths.get(tempfilename);
          f.toFile().deleteOnExit();
          return succeededFuture(tempfilename);
        });
    Future<AsyncFile> outbound = copied
        // Open temp file for writing
        .compose(tfn -> fs.open(tfn, tempopts));

    return Future.all(inbound, outbound).compose(cf -> {
      List<AsyncFile> retvals = cf.list();
      AsyncFile in = retvals.get(0);
      AsyncFile out = retvals.get(1);
      Pump pump = Pump.pump(retvals.get(0), retvals.get(1)).start();
      return succeededFuture(out);
    }).compose(outfile -> {
      Path target = Paths.get(copied.result()).toAbsolutePath();
      // Get path to copied file
      // File is copied?
      IBResourceBuilder<Future<VertxIBResource>> builder = new DefaultVertxIBResourceBuilder(this.vertx,
          this.getRelativeRoot())
          // From the file
          .fromPath(target);
      return succeededFuture(builder);
    });

//    var ffff = ff.compose(f2 -> {
//
//      return Future.succeededFuture(null);
//    });
//      try (
//          // File to read
//          InputStream ins = Files.newInputStream(p);
//          // Temp file to write
//          OutputStream out = Files.newOutputStream(f)) {
//        // Copy the file and get it's Checksum at the same time. Slightly efficient.
//        Checksum d = IBUtils.copyAndDigest(ins, out);
//
//        Path landing = r.relativize(IBUtils.moveFileToNewIdPath(f, d));
//
//        var builder = builderFromPathAndChecksum(p, d)
//
//            .withFilePath(landing.toString())
//
//            .cached(true);
//        Optional.ofNullable(type).ifPresent(t -> builder.withType(t));
//        return Optional.of(builder);
//      } catch (IOException | NoSuchAlgorithmException e) {
//        return empty();
//      }
//
//    });

  }

  // Package private
  public Optional<IBResourceBuilder<Future<VertxIBResource>>> builderFromPathAndChecksum(Path p, Checksum checksum) {
    // We have a checksum, so we can read it, etc.
    Optional<BasicFileAttributes> bfa = IBUtils.getAttributes.apply(p);
    var pandc = new DefaultPathAndChecksum(p, checksum);
    var m = this.builder.get()

        .fromPathAndChecksum(pandc) // sets filepath and name (and source?)

        .withChecksum(checksum)

        .withType(IBResourceBuilderFactory.toOptionalType.apply(p).orElse(APPLICATION_OCTET_STREAM))

        .withCreateDate(bfa.map(attrib -> attrib.creationTime()).map(FileTime::toInstant).orElse(null))

        .withLastUpdated(bfa.map(attrib -> attrib.lastModifiedTime()).map(FileTime::toInstant).orElse(null))

        .withSize(bfa.map(attrib -> attrib.size()).orElse(-1L))

        .withMostRecentAccess(bfa.map(attrib -> attrib.lastAccessTime()).map(FileTime::toInstant).orElse(null))

    ;

    return Optional.of(m);
  }

  @Override
  public Optional<IBResourceBuilder<Future<VertxIBResource>>> fromURL(String u) {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

}
