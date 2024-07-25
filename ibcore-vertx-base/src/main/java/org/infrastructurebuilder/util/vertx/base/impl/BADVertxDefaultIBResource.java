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
///*
// * @formatter:off
// * Copyright © 2019 admin (admin@infrastructurebuilder.org)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * @formatter:on
// */
//package org.infrastructurebuilder.util.vertx.base.impl;
//
//import static io.vertx.core.Future.succeededFuture;
//import static java.util.Objects.requireNonNull;
//import static java.util.Optional.empty;
//import static java.util.Optional.ofNullable;
//import static org.infrastructurebuilder.exceptions.IBException.cet;
//import static org.infrastructurebuilder.util.constants.IBConstants.CREATE_DATE;
//import static org.infrastructurebuilder.util.constants.IBConstants.DESCRIPTION;
//import static org.infrastructurebuilder.util.constants.IBConstants.MIME_TYPE;
//import static org.infrastructurebuilder.util.constants.IBConstants.MOST_RECENT_READ_TIME;
//import static org.infrastructurebuilder.util.constants.IBConstants.NAME;
//import static org.infrastructurebuilder.util.constants.IBConstants.NO_PATH_SUPPLIED;
//import static org.infrastructurebuilder.util.constants.IBConstants.PATH;
//import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_URL;
//import static org.infrastructurebuilder.util.constants.IBConstants.UPDATE_DATE;
//import static org.infrastructurebuilder.util.core.ChecksumEnabled.CHECKSUM;
//import static org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory.getAttributes;
//import static org.infrastructurebuilder.util.vertx.base.VertxChecksumFactory.checksumFrom;
//
//import java.io.IOException;
//import java.io.Reader;
//import java.net.URL;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.Instant;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.function.Function;
//
//import org.apache.tika.Tika;
//import org.apache.tika.metadata.TikaCoreProperties;
//import org.infrastructurebuilder.exceptions.IBException;
//import org.infrastructurebuilder.util.constants.IBConstants;
//import org.infrastructurebuilder.util.core.Checksum;
//import org.infrastructurebuilder.util.core.IBUtils;
//import org.infrastructurebuilder.util.core.PathRef;
//import org.infrastructurebuilder.util.readdetect.IBResource;
//import org.infrastructurebuilder.util.readdetect.impl.AbsolutePathIBResource;
//import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
//import org.infrastructurebuilder.util.vertx.base.IBResourceVertx;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import io.vertx.core.CompositeFuture;
//import io.vertx.core.Future;
//import io.vertx.core.Handler;
//import io.vertx.core.Promise;
//import io.vertx.core.Vertx;
//import io.vertx.core.buffer.Buffer;
//import io.vertx.core.json.JsonObject;
//
//public class BADVertxDefaultIBResource implements IBResourceVertx {
//
//  private final static Logger log = LoggerFactory.getLogger(VertxDefaultIBResource.class);
//
//  private final static Tika tika = new Tika();
//  private final IBResourceModel m;
//
//  private final Vertx vertx;
//
//  private Path p;
//  private Path originalPath;
//
//  private final PathRef root;
//
//  public Optional<PathRef> getRoot() {
//    return Optional.of(this.root);
//  }
//
//  private final static Function<Path, Handler<Promise<String>>> ss = (path) -> {
//    return new Handler<Promise<String>>() {
//      @Override
//      public void handle(Promise<String> event) {
//        synchronized (tika) {
//          log.debug("Detecting path " + path);
//          org.apache.tika.metadata.Metadata md = new org.apache.tika.metadata.Metadata();
//          md.set(TikaCoreProperties.RESOURCE_NAME_KEY, path.toAbsolutePath().toString());
//          try (Reader p = tika.parse(path, md)) {
//            log.debug(" Metadata is {}", md);
//            event.complete(tika.detect(path));
//          } catch (IOException e) {
//            log.error("Failed during attempt to get tika type", e);
//            event.complete(IBConstants.APPLICATION_OCTET_STREAM);
//          }
//        }
//      }
//
//    };
//  };
//
//  public final static Function<Path, Future<String>> toType = (path) -> {
//    return Vertx.vertx().executeBlocking(ss.apply(path));
//  };
//
//  public BADVertxDefaultIBResource(Optional<PathRef> root) {
//    this(root, new IBResourceModel());
//  }
//
////  public AbsolutePathIBResource(
////      // Path
////      java.nio.file.Path path
////      // Checksum
////      , org.infrastructurebuilder.util.core.Checksum checksum
////      // Type
////      , String type
////      // Source
////      , java.util.Optional<java.net.URL> sourceURL
////      // Name
////      , java.util.Optional<String> name, java.util.Optional<java.util.Date> readDate) {
////    this.m = new IBResourceModel();
////    m.setFilePath(requireNonNull(path).toAbsolutePath().toString());
////    m.setFileChecksum(requireNonNull(checksum).toString());
////    m.setType(type);
////    m.setSource(requireNonNull(sourceURL).map(java.net.URL::toExternalForm).orElse(null));
////    m.setName(requireNonNull(name).orElse(null));
////    m.setMostRecentReadTime(readDate.orElse(null));
////  }
//
////  AbsolutePathIBResource(String filePath, String checksum, String type, String source, String name, Date mostRecentRead) {
////    this.m = new IBResourceModel();
////    m.setFilePath(filePath);
////    m.setFileChecksum(checksum);
////    m.setType(type);
////    m.setSource(source);
////    m.setName(name);
////    m.setMostRecentReadTime(mostRecentRead);
////  }
////
//  public BADVertxDefaultIBResource(Optional<PathRef> root, IBResourceModel m) {
//    this.root = requireNonNull(root).orElse(null);
//    this.m = requireNonNull(m);
//    this.vertx = Vertx.vertx();
//
//  }
//
//  /**
//   * Magic deserializer :)
//   *
//   * @param j JSONObject produced by IBResource#asJSON
//   */
//  public BADVertxDefaultIBResource(JsonObject j) {
//    this.root = null;
//    this.vertx = Vertx.vertx();
//
//    // FIXME Some of these not being present should produce a runtime failure
//    m = new IBResourceModel();
//    m.setCreated(j.getString(CREATE_DATE));
//    m.setFileChecksum(j.getString(CHECKSUM));
//    m.setFilePath(j.getString(PATH));
//    m.setLastUpdate(j.getString(UPDATE_DATE));
////    m.setModelEncoding("UTF-8");
//    m.setMostRecentReadTime(j.getString(MOST_RECENT_READ_TIME));
//    m.setName(j.getString(NAME));
//    m.setSource(j.getString(SOURCE_URL));
//    m.setType(j.getString(MIME_TYPE));
//    m.setDescription(j.getString(DESCRIPTION));
//
//    String x = ofNullable(requireNonNull(j).getString(IBConstants.PATH))
//        .orElseThrow(() -> new RuntimeException(NO_PATH_SUPPLIED));
//    try {
//      Path p = Paths.get(cet.returns(() -> cet.returns(() -> new URL(x).toURI())));
//      this.p = p;
//    } catch (Throwable t) {
//      log.error("Error converting to path", t);
//      throw t;
//    }
//  }
//
//  public final static Future<IBResourceVertx> copyToTempChecksumAndPath(Vertx vertx, Path targetDir, final Path source,
//      final Optional<String> oSource, final String pString) throws IOException {
//    return copyToTempChecksumAndPath(vertx, targetDir, source).compose(d -> {
//      requireNonNull(oSource).ifPresent(o -> {
//        ((VertxDefaultIBResource) d).setSource(o + "!/" + pString);
//      });
//      return succeededFuture(d);
//    });
//  }
//
//  public void setSource(String source) {
//    this.m.setSource(requireNonNull(source));
//  }
//
//  @Override
//  public Vertx vertx() {
//    return vertx;
//  }
//
//  public final static Future<IBResourceVertx> copyToTempChecksumAndPath(Vertx vertx, Path targetDir, final Path source)
//      throws IOException {
//    return Future.all(toType.apply(requireNonNull(source)), checksumFrom(vertx, source))
//        // Got a type and a checksum
//        .compose(f -> {
//          String localType = f.resultAt(0);
//          Checksum cSum = f.resultAt(1);
//          Path newTarget = targetDir.resolve(cSum.asUUID().get().toString());
//          return vertx.fileSystem().copy(source.toString(), newTarget.toString()).compose(v -> {
//            return Future.succeededFuture(new VertxDefaultIBResource(Optional.empty(),newTarget, cSum, Optional.of(localType)));
//          });
//        });
//  }
//
////  public final static IBResourceVertx copyToDeletedOnExitTempChecksumAndPath(Path targetDir, String prefix,
////      String suffix, final InputStream source) {
////    return cet.returns(() -> {
////      Path target = createTempFile(requireNonNull(targetDir), prefix, suffix);
////      try (OutputStream outs = Files.newOutputStream(target)) {
////        copy(source, outs);
////      } finally {
////        source.close();
////      }
////      return copyToTempChecksumAndPath(targetDir, target);
////    });
////  }
//
//  public final static IBResourceVertx from(Path p, Checksum c, String type) {
//    IBResourceModel m = new IBResourceModel();
//    m.setFilePath(requireNonNull(p).toAbsolutePath().toString());
//    m.setFileChecksum(c.toString());
//    m.setType(type);
//    return new VertxDefaultIBResource(Optional.empty(),p, c, Optional.of(type));
//  }
//
//  public BADVertxDefaultIBResource(Optional<PathRef> root, Path path, Checksum checksum, Optional<String> type) {
//    this(root);
//    this.originalPath = requireNonNull(path);
//    m.setFilePath(this.originalPath.toAbsolutePath().toString());
//    m.setFileChecksum(requireNonNull(checksum).toString());
//    getAttributes.apply(path).ifPresent(bfa -> {
//      this.m.setCreated(bfa.creationTime().toInstant().toString());
//      this.m.setLastUpdate(bfa.lastModifiedTime().toInstant().toString());
//      this.m.setMostRecentReadTime(bfa.lastAccessTime().toInstant().toString());
//    });
//
//    requireNonNull(type).ifPresent(t -> m.setType(t));
//  }
//
//  public BADVertxDefaultIBResource(Optional<PathRef> root, Path path, Checksum checksum) {
//    this(root, path, checksum, Optional.empty());
//  }
//
//  public final static IBResource fromPath(Path path) {
//    return new AbsolutePathIBResource(Optional.empty(),path, new Checksum(path), empty(), empty());
//  }
//
//  @Override
//  public Future<Checksum> getChecksum() {
//    return Optional.ofNullable(m.getFileChecksum()).map(c -> succeededFuture(new Checksum(c))).orElseGet(() -> {
//      // Read the file checksum
//      return checksumFrom(vertx, getPath()).compose(c -> {
//        m.setFileChecksum(c.toString());
//        return succeededFuture(c);
//      });
//    });
//  }
//
//  @Override
//  public Future<String> getType() {
//    return Optional.ofNullable(m.getType()).map(Future::succeededFuture)
//        .orElse(toType.apply(getPath()).compose(type -> {
//          m.setType(type);
//          return succeededFuture(type);
//        }));
//  }
//
//  @Override
//  public Future<IBResourceVertx> moveTo(Path target) throws IOException {
//    IBUtils.moveAtomic(getPath(), target);
//    IBResourceModel m2 = m.clone();
//    m2.setFilePath(target.toAbsolutePath().toString());
//    return succeededFuture(new VertxDefaultIBResource(getRoot(),m2));
//  }
//
//  @Override
//  public Future<Buffer> get() {
//    m.setMostRecentReadTime(Instant.now().toString());
//    return IBResourceVertx.super.get();
//  }
//
//  private int defaultHashCode() {
//    return Objects.hash(getChecksum().result(), getPath(), getSourceName(), getSourceURL(), getType().result());
//  }
//
//  @Override
//  public int hashCode() {
//    return defaultHashCode();
//  }
//
//  private boolean defaultEquals(Object obj) {
//    if (this == obj) {
//      return true;
//    }
//    if (obj == null) {
//      return false;
//    }
//    if ((obj instanceof IBResourceVertx other)) {
//      return Future.all(getChecksum(), other.getChecksum(), getType(), other.getType())
//          .compose(cf -> succeededFuture(
//              // All checks
//              Objects.equals(cf.resultAt(0), cf.resultAt(1)) // checksum
//                  && Objects.equals(getPath(), other.getPath()) // path
//                  && Objects.equals(getSourceName(), other.getSourceName()) // source
//                  && Objects.equals(getSourceURL(), other.getSourceURL()) // sourceURL
//                  && Objects.equals(cf.resultAt(2), cf.resultAt(3))))
//          .result(); // Type
//    }
//    return false;
//  }
//
//  @Override
//  public boolean equals(Object obj) {
//    return defaultEquals(obj);
//  }
//
//  @Override
//  public String toString() {
//    return defaultToString().result();
//  }
//
//  @Override
//  public Optional<URL> getSourceURL() {
//    return ofNullable(m.getSource()).map(u -> IBUtils.translateToWorkableArchiveURL(u));
//  }
//
//  @Override
//  public Path getPath() {
//    if (this.p == null && m.getFilePath() != null) {
//      this.p = java.nio.file.Paths.get(m.getFilePath());
//    }
//    if (this.p == null && getSourceURL().isPresent()) {
//      this.p = Paths.get(cet.returns(() -> getSourceURL().get().toURI()));
//    }
//    return ofNullable(this.p).orElseThrow(() -> new IBException("No available path"));
//  }
//
//  @Override
//  public Optional<String> getSourceName() {
//    return ofNullable(m.getName());
//  }
//
//  @Override
//  public Optional<Instant> getMostRecentReadTime() {
//    return ofNullable(this.m.getMostRecentReadTime()).map(Instant::parse);
//  }
//
//  @Override
//  public Optional<Instant> getCreateDate() {
//    return ofNullable(this.m.getCreated()).map(Instant::parse);
//  }
//
//  @Override
//  public Optional<Instant> getLateUpdateDate() {
//    return ofNullable(this.m.getLastUpdate()).map(Instant::parse);
//  }
//
//  @Override
//  public Optional<String> getName() {
//    return ofNullable(this.m.getName());
//  }
//
//  @Override
//  public Optional<String> getDescription() {
//    return ofNullable(this.m.getDescription());
//  }
//
//  @Override
//  public Path getOriginalPath() {
//    return originalPath;
//  }
//
//  @Override
//  public JsonObject toJson() {
//    return toFutureJson().result();
//  }
//
//}
