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
package org.infrastructurebuilder.util.vertx.blobstore.impl;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.nio.file.Files.readString;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.constants.IBConstants.BLOBSTORE_NO_MAXBYTES;
import static org.infrastructurebuilder.util.constants.IBConstants.METADATA_DIR_NAME;
import static org.infrastructurebuilder.util.core.Checksum.ofPath;
import static org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory.getAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.RelativeRootFactory;
import org.infrastructurebuilder.util.core.RelativeRootSupplier;
import org.infrastructurebuilder.util.readdetect.DefaultIBResourceBuilderFactorySupplier;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.IBResourceException;
import org.infrastructurebuilder.util.vertx.blobstore.Blobstore;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;

public class FilesystemBlobstore implements Blobstore<InputStream> {

  private final static Logger log = LoggerFactory.getLogger(FilesystemBlobstore.class);
  private final static FileSystem fs = Vertx.vertx().fileSystem();

  private final RelativeRootSupplier root;
  private final Path metadata;

  private final AtomicLong size = new AtomicLong(0);
  private final Set<String> blobs; // = new ConcurrentHashSet<>();
  private final long maxBytes;
  private IBResourceBuilderFactory<Optional<IBResource<InputStream>>> rcf;

  public FilesystemBlobstore(RelativeRootSupplier rrs, Long size) {
    this.root = requireNonNull(rrs);
    this.rcf = new DefaultIBResourceBuilderFactorySupplier(new RelativeRootFactory(Set.of(this.root))).get(rrs.getName())
        .get();
    this.metadata = getRelativeRoot().resolvePath(METADATA_DIR_NAME).map(Path::toAbsolutePath)
        .orElseThrow(() -> new IBResourceException("No path"));
    try {
      Files.createDirectories(this.metadata);
    } catch (IOException e) {
      log.error("Could not create {}", this.metadata.toAbsolutePath().toString(), e);
      throw new RuntimeException(e);
    }
    blobs = scanMetadata();
    maxBytes = requireNonNull(size);
  }

  private Set<String> scanMetadata() {
    Set<String> resources = new ConcurrentHashSet<>();
    cet.translate(() -> Files.newDirectoryStream(metadata).forEach(p -> {
      try {
        UUID u = UUID.fromString(p.getFileName().toString());
        Optional<IBResource<InputStream>> res = this.rcf.fromJSONString(new JsonObject(readString(p)).toString())
            .flatMap(IBResourceBuilder::build);
        res.ifPresent(r -> {
          resources.add(u.toString());
          size.addAndGet(r.size());
        });
      } catch (Throwable e) {
        getLog().error("{} was not a metadata file {}", p, e);
      }
    }));
    return resources;
  }

  public Long scanSize() {
    return blobs.stream()

        .map(this::getMetadataPath)

        .map(f -> cet.returns(() -> readString(f)))

        .map(s -> new JSONObject(s))

        .map(this.rcf::fromJSON)

        .flatMap(Optional::stream)

        .map(k -> k.build())

        .flatMap(Optional::stream)

        .collect(Collectors.summingLong(IBResource::size));
  }

  public RelativeRoot getRelativeRoot() {
    return this.root.get().orElseThrow(() -> new IBResourceException("No root"));
  }

  @Override
  public Future<Buffer> getBlob(String id) {
    return fs.readFile(getPath(id).toString());
  }

  @Override
  public Future<Instant> getCreateDate(String id) {
    return getMetadata(id).compose(md ->md.getCreateDate().map(Future::succeededFuture).orElse(Future.failedFuture("no create date")));
  }

  @Override
  public Future<Instant> getLastUpdated(String id) {
    return getMetadata(id).compose(md -> md.getLastUpdateDate().map(Future::succeededFuture).orElse(Future.failedFuture("no last updated date")));
  }

  @Override
  public Future<String> putBlob(String originalFilename, String desc, Future<Buffer> b, Instant cDate, Instant uDate,
      Optional<Properties> addlProps) {
    if (maxBytes != BLOBSTORE_NO_MAXBYTES && size.get() > maxBytes) {
      getLog().warn("Local size ({}) appears to have exceeded maxSize ({})", size.get(), maxBytes);
      getLog().error("Should we do something else?"); // TODO
    }
    try {
      // Not really a temp file
      Path blobFile = Files
          .createTempFile(this.getRelativeRoot().getPath().orElseThrow(() -> new IBResourceException("No path")),
              "temp", ".blob")
          .toAbsolutePath();
      return requireNonNull(b)
          // write the blob
          .compose(buffer -> writeFile(blobFile, buffer))
          // File written
          .compose(v -> readChecksum(blobFile))
          // Checksum read
          .compose(csum -> writeMetadata(blobFile, originalFilename, ofNullable(desc), cDate, uDate, addlProps));
    } catch (IOException e) {
      log.error("Error creating temp file in {}", this.root, e);
      return failedFuture("error.creating.tempfile.for.putblob");
    } catch (Throwable t) {
      log.error("Error attempting to work with temp file in {}", this.root, t);
      return failedFuture("error.working.with.blob");
    }
  }

  private Future<Checksum> readChecksum(Path p) {
    getLog().error("Starting to write metadata");
    Optional<Checksum> csum = ofPath.apply(p);
    return csum.isPresent() ?

        succeededFuture(csum.get())

        : failedFuture("unable.to.read.checksum");
  }

  private Future<String> writeMetadata(Path blob, String originalName, Optional<String> description, Instant createDate,
      Instant lastUpdated, Optional<Properties> addlProps) {
    getLog().error("writeMetadata {}, {}, desc {}, {}, {}, {}", getRelativeRoot().relativize(blob).get(), originalName,
        description, createDate, lastUpdated, addlProps);
    return rcf.fromPath(blob, IBConstants.JSON_TYPE).map(builder -> {

      return builder

          .withName(originalName)

          .withDescription(description.orElse(null))

          .withLastUpdated(lastUpdated)

          .withCreateDate(createDate)

          .build().map(bsm -> {
            getLog().error("bsm is {}", bsm.asJSON().toString());
            var id = bsm.getChecksum().asUUID().get().toString();
            getLog().error("Writing metadata for {}", id);
            return writeFile(getMetadataPath(id),
                // Pull the buffer out of JSON
                Buffer.buffer(new JsonObject(bsm.asJSON().toString()).encodePrettily())).compose(v -> {
                  log.info("Wrote metadata ");
                  return succeededFuture(id);
                });
          }).orElse(Future.failedFuture("Could not build IBResource"));
    }).orElse(Future.failedFuture("Could not build IBResourceBuilder"));
  }

  private Future<Void> writeFile(Path blobFile, Buffer buffer) {
    log.debug("writing buffer to {}", blobFile);
    return fs.writeFile(blobFile.toString(), buffer);
  }

  @Override
  public Future<String> putBlob(String blobname, @Nullable String description, Path p, Optional<Properties> addlProps) {
    AtomicReference<Instant> c = new AtomicReference<>(Instant.now());
    AtomicReference<Instant> m = new AtomicReference<>(Instant.now());
    getAttributes.apply(p).ifPresentOrElse(attr -> {
      c.set(attr.creationTime().toInstant());
      m.set(attr.lastModifiedTime().toInstant());
    }, () -> getLog().error("Error getting attributes of {}", p.toAbsolutePath()));
    log.debug("File {} to buffer {} ", blobname, p);
    return putBlob(blobname, description, fs.readFile(p.toAbsolutePath().toString()), c.get(), m.get(), empty());
  }

  @Override
  public Future<String> putBlob(String blobname, String description, Path p) {
    return putBlob(blobname, description, p, empty());
  }

  @Override
  public Logger getLog() {
    return log;
  }

  @Override
  public Future<String> getName(String id) {
    return getMetadata(id).compose(md -> succeededFuture(md.getName()));
  }

  @Override
  public Future<String> getDescription(String id) {
    return getMetadata(id).compose(md -> succeededFuture(md.getDescription().orElse(null)));
  }

  public final Future<IBResource<InputStream>> getMetadata(String id) {
    return fs

        .readFile(getMetadataPath(id).toString())

        .compose(b -> {
          log.debug("Metadata json is {} ", new JsonObject(b).encodePrettily());
          var q = this.rcf.fromJSONString(new JsonObject(b).toString())

              .flatMap(IBResourceBuilder::build);
          return q.isPresent() ?

              succeededFuture(q.get())

              : Future.failedFuture("could.not.deserialize");
        });
  }

  private Path getMetadataPath(String id) {
    var p = metadata.resolve(requireNonNull(id)).toAbsolutePath();
    log.debug("Metadata path for {} is {}", id, p);
    return p;
  }

  private Path getPath(String id) {
    return this.getRelativeRoot().resolvePath(requireNonNull(id)).map(Path::toAbsolutePath)
        .orElseThrow(() -> new IBException());
  }

  @Override
  public Future<Void> removeBlob(String id) {
    blobs.remove(id);
    var mdPath = getMetadataPath(id);
    this.rcf.fromJSON(new JSONObject(cet.returns(() -> readString(mdPath))))

        .ifPresent(r2 -> {
          r2.build().ifPresent(r -> {
            r.getPath().ifPresent(rPath -> {
              cet.translate(() -> Files.delete(rPath));
            });
            cet.translate(() -> Files.delete(mdPath));
          });
        });
    return succeededFuture(); // FIXME
  }

}
