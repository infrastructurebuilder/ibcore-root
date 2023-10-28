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

import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.util.constants.IBConstants.UTF_8;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.extensionmapper.basic.DefaultTypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.IBResourceException;
import org.infrastructurebuilder.util.readdetect.IBResourceRelativeRootSupplier;
import org.infrastructurebuilder.util.readdetect.PathBackedIBResourceRelativeRootSupplier;
import org.infrastructurebuilder.util.readdetect.model.IBResourceCache;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.infrastructurebuilder.util.vertx.base.VertxIBResourceBuilder;
import org.infrastructurebuilder.util.vertx.base.VertxIBResourceBuilderFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.streams.Pump;

@Named
public class VertxIBResourceBuilderFactoryImpl implements VertxIBResourceBuilderFactory {
  private final static Logger log = LoggerFactory.getLogger(VertxIBResourceBuilderFactoryImpl.class);

  private RelativeRoot root;
  private final IBResourceCache cache;

  private Supplier<VertxIBResourceBuilder> builder;

  private final Vertx vertx;

  private final TypeToExtensionMapper typeMapper;

  @Inject
  public VertxIBResourceBuilderFactoryImpl(Vertx vertx, IBResourceRelativeRootSupplier relRootSupplier,
      TypeToExtensionMapper typeMapper)
  {
    this.typeMapper = Objects.requireNonNull(typeMapper);
    this.vertx = Objects.requireNonNull(vertx);
    this.root = requireNonNull(relRootSupplier).get();
    // Delivers a new builder from the relative root each time
    this.builder = () -> new DefaultVertxIBResourceBuilder(this.vertx, relRootSupplier);
    log.debug("Root is {}", this.root);
    this.cache = new IBResourceCache();

    cache.setModelEncoding(UTF_8);
    cache.setRoot(this.root.getPath().map(Path::toAbsolutePath).map(Path::toString).orElse(null));
  }

  public VertxIBResourceBuilderFactoryImpl() {
    this(Vertx.vertx(), () -> null, new DefaultTypeToExtensionMapper()); // No cache
  }

  public VertxIBResourceBuilderFactoryImpl(Path p) {
    this(Vertx.vertx(), new PathBackedIBResourceRelativeRootSupplier(p), new DefaultTypeToExtensionMapper());
  }

  @Override
  public final Optional<RelativeRoot> getRoot() {
    return ofNullable(this.root);
  }

  @Override
  public Future<VertxIBResourceBuilder> fromPath(Path p, String type) {
    log.debug("Getting stream from {}", p);
    return getRoot()

        .map(rr -> cacheIt(p, type))

        .orElseGet(() -> readIt(p, type)).compose(builder -> {
          return getRoot().map(root -> {
            return succeededFuture(builder);
          }).orElseGet(() -> {
            return succeededFuture(builder);
          });
        });
    // FIXME the following doesn't follow the API, mykel!
    // r.ifPresent(rr -> this.cache.addResource(rr.build().get().copyModel())); // FIXME? builds the model
  }

  private Optional<RelativeRoot> getRelativeRoot() {
    return Optional.ofNullable(this.root);
  }

  private Future<VertxIBResourceBuilder> readIt(Path p, String type) {
    log.info("Reading from {}", p);
    return Checksum.ofPath.apply(p).map(checksum -> {
      VertxIBResourceBuilder m = builderFromPathAndChecksum(p, checksum);
      Optional.ofNullable(type).ifPresent(t -> m.withType(t));
      return succeededFuture(m);
    }).orElse(Future.failedFuture("Could not perform readIt"));
  }

  private Future<VertxIBResourceBuilder> cacheIt(Path p, String type) {
    log.info("Cacheing from {}", p);
    var rootPath = getRoot().flatMap(RelativeRoot::getPath);

    if (rootPath.isEmpty()) {
      log.error("no.root.path");
      return Future.failedFuture("no.root.path");
    }
    var r = rootPath.get().toAbsolutePath().toString() + File.separatorChar + "vibrtf";
    FileSystem fs = this.vertx.fileSystem();

    var tempopts = new OpenOptions().setCreate(true).setWrite(true);
    var readopts = new OpenOptions().setCreate(false).setWrite(false).setRead(true);
    Future<AsyncFile> inbound = fs.open(p.toAbsolutePath().toString(), readopts);
    Future<String> copied = fs.createTempFile(r, this.typeMapper.getExtensionForType(type))
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
      VertxIBResourceBuilder builder = new DefaultVertxIBResourceBuilder(this.vertx, () -> getRelativeRoot().get())
          // From the file
          .from(target);
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

  @Override
  public Future<VertxIBResourceBuilder> fromJSON(JSONObject json) {
    return succeededFuture(this.builder.get().fromJSON(json));
  }

  @Override
  public Future<VertxIBResourceBuilder> fromURLLike(String u, String type) {
    // TODO Auto-generated method stub
    return Future.failedFuture("unimplemented");
  }

  // Package private
  @Override
  public VertxIBResourceBuilder builderFromPathAndChecksum(Path p, Checksum checksum) {
    // We have a checksum, so we can read it, etc.
    Optional<BasicFileAttributes> bfa = IBResourceBuilderFactory.getAttributes.apply(p);
    var m = this.builder.get()

        .from(p) // sets filepath and name (and source?)

        .withChecksum(checksum)

        .withType(IBResourceBuilderFactory.toOptionalType.apply(p).orElse(APPLICATION_OCTET_STREAM))

        .withCreateDate(bfa.map(attrib -> attrib.creationTime()).map(FileTime::toInstant).orElse(null))

        .withLastUpdated(bfa.map(attrib -> attrib.lastModifiedTime()).map(FileTime::toInstant).orElse(null))

        .withSize(bfa.map(attrib -> attrib.size()).orElse(-1L))

        .withMostRecentAccess(bfa.map(attrib -> attrib.lastAccessTime()).map(FileTime::toInstant).orElse(null))

    ;

    return m;
  }

  @Override
  public Future<VertxIBResourceBuilder> fromModel(IBResourceModel model) {
//    var m = Objects.requireNonNull(model);
    // TODO
    return Future.failedFuture("unimplemented");

  }

}
