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
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.summingLong;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.constants.IBConstants.BLOBSTORE_MAXBYTES;
import static org.infrastructurebuilder.util.constants.IBConstants.BLOBSTORE_NO_MAXBYTES;
import static org.infrastructurebuilder.util.constants.IBConstants.BLOBSTORE_ROOT;
import static org.infrastructurebuilder.util.constants.IBConstants.METADATA_DIR_NAME;
import static org.infrastructurebuilder.util.core.Checksum.ofPath;
import static org.infrastructurebuilder.util.readdetect.IBResourceCacheFactory.getAttributes;
import static org.infrastructurebuilder.util.readdetect.IBResourceFactory.from;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceCacheFactory;
import org.infrastructurebuilder.util.readdetect.IBResourceCacheFactorySupplier;
import org.infrastructurebuilder.util.readdetect.IBResourceFactory;
import org.infrastructurebuilder.util.vertx.blobstore.Blobstore;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;

public class FilesystemBlobstore implements Blobstore {

  private final static Logger log = LoggerFactory.getLogger(FilesystemBlobstore.class);
  private final static FileSystem fs = Vertx.vertx().fileSystem();

  private final RelativeRoot root;
  private final Path metadata;

  private final AtomicLong size = new AtomicLong(0);
  private final Set<String> blobs; // = new ConcurrentHashSet<>();
  private final long maxBytes;
  private IBResourceCacheFactory rcf;

  public FilesystemBlobstore(JsonObject config) {
    this(Paths.get(config.getString(BLOBSTORE_ROOT)),
        Optional.ofNullable(config.getLong(BLOBSTORE_MAXBYTES)).orElse(BLOBSTORE_NO_MAXBYTES));
  }

  public FilesystemBlobstore(Path root, Long size) {
    this.root = RelativeRoot.from(requireNonNull(root));
    this.rcf = new IBResourceCacheFactorySupplier(() -> root).get();
    this.metadata = root.resolve(METADATA_DIR_NAME).toAbsolutePath();
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
        Optional<IBResource> res = this.rcf.fromJSONString(new JsonObject(readString(p)).toString());
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

        .map(f -> cet.returns(() -> readString(f))).map(JSONObject::new)

        .map(this.rcf::fromJSON)

        .flatMap(Optional::stream)

        .collect(summingLong(IBResource::size));
  }

  public RelativeRoot getRelativeRoot() {
    return this.root;
  }

  @Override
  public Future<Buffer> getBlob(String id) {
    return fs.readFile(getPath(id).toString());
  }

  @Override
  public Future<Instant> getCreateDate(String id) {
    return getMetadata(id).compose(md -> succeededFuture(md.getCreateDate()));
  }

  @Override
  public Future<Instant> getLastUpdated(String id) {
    return getMetadata(id).compose(md -> succeededFuture(md.getLastUpdateDate()));
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
      Path blobFile = Files.createTempFile(this.root.getPath().get(), "temp", ".blob").toAbsolutePath();
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
    getLog().error("writeMetadata {}, {}, desc {}, {}, {}, {}", root.relativize(blob).get(), originalName, description,
        createDate, lastUpdated, addlProps);
//    IBResource bsm = from(blob.toAbsolutePath(), of(originalName), description, createDate, lastUpdated, addlProps);
    var bsm = rcf.builderFromPath(blob)
        .withName(originalName)
        .withDescription(description.orElse(null))
        .withLastUpdated(lastUpdated)
        .withCreateDate(createDate)

        .build();

    getLog().error("bsm is {}", bsm.asJSON().toString());
    var id = bsm.getChecksum().asUUID().get().toString();
    getLog().error("Writing metadata for {}", id);
    return writeFile(getMetadataPath(id),
        // Pull the buffer out of JSON
        Buffer.buffer(new JsonObject(bsm.asJSON().toString()).encodePrettily()))

        .compose(v -> {
          log.info("Wrote metadata ");
          return succeededFuture(id);
        });
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
    return getMetadata(id).compose(md -> succeededFuture(md.getName().get()));
  }

  @Override
  public Future<String> getDescription(String id) {
    return getMetadata(id).compose(md -> succeededFuture(md.getDescription().orElse(null)));
  }

  public final Future<IBResource> getMetadata(String id) {
    return fs

        .readFile(getMetadataPath(id).toString())

        .compose(b -> {
          var q = this.rcf.fromJSONString(new JsonObject(b).toString());
          return q.isPresent() ? succeededFuture(q.get()) : Future.failedFuture("could.not.deserialize");
        });
  }

  private Path getMetadataPath(String id) {
    var p = metadata.resolve(requireNonNull(id)).toAbsolutePath();
    log.debug("Metadata path for {} is {}", id, p);
    return p;
  }

  private Path getPath(String id) {
    return root.resolve(requireNonNull(id)).map(Path::toAbsolutePath).orElseThrow(() -> new IBException());
  }

  @Override
  public Future<Void> removeBlob(String id) {
    blobs.remove(id);
    var mdPath = getMetadataPath(id);
    var res = this.rcf.fromJSON(new JSONObject(cet.returns(() -> Files.readString(mdPath))));
    res.ifPresent(r -> {
      r.getPath().ifPresent(rPath -> {
        cet.translate(() -> Files.delete(rPath));
      });
      cet.translate(() -> Files.delete(mdPath));
    });
    return succeededFuture(); // FIXME
  }

}
