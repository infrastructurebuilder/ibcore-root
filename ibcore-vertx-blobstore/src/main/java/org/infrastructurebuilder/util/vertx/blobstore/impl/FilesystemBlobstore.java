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
package org.infrastructurebuilder.util.vertx.blobstore.impl;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.*;
import static org.infrastructurebuilder.util.core.Checksum.ofPath;
import static org.infrastructurebuilder.util.readdetect.IBResourceFactory.getAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceFactory;
import org.infrastructurebuilder.util.vertx.blobstore.Blobstore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;

public class FilesystemBlobstore implements Blobstore {

  private final static Logger log = LoggerFactory.getLogger(FilesystemBlobstore.class);
  private final static FileSystem fs = Vertx.vertx().fileSystem();

  private final RelativeRoot root;
  private final Path metadata;

  public FilesystemBlobstore(JsonObject config) {
    this(Paths.get(config.getString(BLOBSTORE_ROOT)));
  }

  public FilesystemBlobstore(Path root) {
    this.root = RelativeRoot.from(requireNonNull(root));
    this.metadata = root.resolve(METADATA_DIR_NAME).toAbsolutePath();
    try {
      Files.createDirectories(this.metadata);
    } catch (IOException e) {
      log.error("Could not create {}", this.metadata.toAbsolutePath().toString(), e);
      throw new RuntimeException(e);
    }
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
    try {
      // Not really a temp file
      Path blobFile = Files.createTempFile(this.root.getPath().get(), "temp", ".blob").toAbsolutePath();
      return requireNonNull(b).compose(buffer -> fs.writeFile(blobFile.toString(), buffer))
          // File written
          .compose(v -> {
            Promise<String> p = Promise.promise();
            ofPath.apply(blobFile).ifPresentOrElse(csum -> {
              writeMetadata(blobFile, originalFilename, ofNullable(desc), cDate, uDate, addlProps)
                  // Fail to write metadata for some reason
                  .onFailure(t -> p.fail(t))
                  // Wrote metadata
                  .onSuccess(id -> p.complete(id));
            }, () -> {
              p.fail("unable.to.read.checksum");
            });
            return p.future();
          });
    } catch (IOException e) {
      log.error("Error creating temp file in {}", this.root, e);
      return failedFuture("error.creating.tempfile.for.putblob");
    } catch (Throwable t) {
      log.error("Error attempting to work with temp file in {}", this.root, t);
      return failedFuture("error.working.with.blob");
    }
  }

  @Override
  public Future<String> putBlob(String blobname, String description, Path p) {
    AtomicReference<Instant> c = new AtomicReference<>(Instant.now());
    AtomicReference<Instant> m = new AtomicReference<>(Instant.now());
    getAttributes.apply(p).ifPresentOrElse(attr -> {
      c.set(attr.creationTime().toInstant());
      m.set(attr.lastModifiedTime().toInstant());
    }, () -> getLog().error("Error getting attributes of {}", p.toAbsolutePath()));
    return putBlob(blobname, description, fs.readFile(p.toAbsolutePath().toString()), c.get(), m.get(), empty());
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
    return fs.readFile(getMetadataPath(id).toString())
        .compose(b -> succeededFuture(IBResourceFactory.from(new JsonObject(b).toString())));
  }

  private Future<String> writeMetadata(Path blob, String name, Optional<String> description, Instant createDate,
      Instant lastUpdated, Optional<Properties> addlProps) {
    IBResource bsm = IBResourceFactory.from(root.relativize(blob).get(), of(name), description, createDate, lastUpdated,
        addlProps);
    var id = bsm.getChecksum().asUUID().get().toString();
    var p = getMetadataPath(id).toString();
    var mdj = new JsonObject(bsm.asJSON().toString());
    var md = Buffer.buffer(mdj.encodePrettily());
    return fs.writeFile(p, md).compose(v -> {
      log.info("Wrote metadata {}", p);
      return succeededFuture(id);
    });
  }

  private Path getMetadataPath(String id) {
    return metadata.resolve(requireNonNull(id)).toAbsolutePath();
  }

  private Path getPath(String id) {
    return root.resolve(requireNonNull(id)).map(Path::toAbsolutePath).orElseThrow(() -> new IBException());
  }

  @Override
  public Future<String> putBlob(String blobname, @Nullable String description, Path p, Optional<Properties> addlProps) {
    // TODO Auto-generated method stub
    return putBlob(blobname, description, p, empty());
  }

//  public final static class BSMetadata implements JsonOutputEnabled {
//    private final AtomicReference<IBResource> resource = new AtomicReference<>();
//
//    public BSMetadata(JsonObject j) {
//      this(IBResourceFactory.from(j.toString()));
//    }
//
//    public BSMetadata(IBResource r) {
//      this.resource.compareAndSet(null, requireNonNull(r));
//    }
//
//    public BSMetadata(Path p) {
//      this(p, of(nameMapper.apply(p, empty())), empty());
//    }
//
//    public BSMetadata(Path p, Optional<String> name, Optional<String> desc) {
//      this(IBResourceFactory.from(p,name,desc));
//    }
//
//
//    public Instant getCreateDate() {
//      return resource.get().getCreateDate().get();
//    }
//
//    public String getDescription() {
//      return re
//    }
//
//    public String getName() {
//      return name;
//    }
//
//    @Override
//    public JsonObject toJson() {
//      return new JsonBuilder(getRelativeRoot()).addString(NAME, getName())
//
//          .addInstant(CREATE_DATE, getCreateDate())
//
//          .addInstant(UPDATE_DATE, getLastUpdated()).addString(DESCRIPTION, getDescription())
//
//          .addChecksum(CHECKSUM, getChecksum()).toJson();
//    }
//
//    public Optional<Checksum> getChecksum() {
//      return ofNullable(this.checksum);
//    }
//
//    public Instant getLastUpdated() {
//      return lastUpdated;
//    }
//
//  }

}
