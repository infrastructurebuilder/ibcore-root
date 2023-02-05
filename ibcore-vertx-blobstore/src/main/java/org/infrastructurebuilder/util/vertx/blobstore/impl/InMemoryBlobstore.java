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
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.NOT_FOUND;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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

public class InMemoryBlobstore implements Blobstore {

  private static final String ERROR_GETTING_ATTRIBUTES = "error.getting.attributes";

  private final static FileSystem fs = Vertx.vertx().fileSystem();

  private final static Logger log = LoggerFactory.getLogger(InMemoryBlobstore.class);
  private final Map<String, IBResource> resources = new ConcurrentHashMap<>();
  private final Map<String, byte[]> blobs = new ConcurrentHashMap<>();

  @Override
  public Future<IBResource> getMetadata(String id) {
    return ofNullable(resources.get(id)).map(Future::succeededFuture).orElse(Future.failedFuture(NOT_FOUND));
  }

  @Override
  public Future<Buffer> getBlob(String id) {
    return ofNullable(blobs.get(id)).map(ba -> Buffer.buffer(ba)).map(Future::succeededFuture)
        .orElse(failedFuture(NOT_FOUND));
  }

  @Override
  public Future<Instant> getCreateDate(String id) {
    return ofNullable(resources.get(id)).map(IBResource::getCreateDate).map(Future::succeededFuture)
        .orElse(failedFuture(NOT_FOUND));
  }

  @Override
  public Future<Instant> getLastUpdated(String id) {
    return ofNullable(resources.get(id)).map(IBResource::getLastUpdateDate)
        .map(Future::succeededFuture).orElse(failedFuture(NOT_FOUND));
  }

  @Override
  public Future<String> putBlob(String blobname, String description2, Future<Buffer> b, Instant createDate,
      Instant lastUpdated, Optional<Properties> addlProps) {
    return b
//        .onFailure(t -> {
//      log.error("--------------------------------------------------------  Error puttign blob", t);
//    })
        .compose(bb -> {
          byte[] bytes = bb.getBytes();
          IBResource r = new IMDelegatedIBResource(bytes, blobname, description2, createDate, lastUpdated, addlProps);
          String id = r.getChecksum().asUUID().get().toString();
          this.blobs.putIfAbsent(id, bytes);
          this.resources.putIfAbsent(id, r);
          return succeededFuture(id);
        });
  }

  @Override
  public Future<String> putBlob(String blobname, String description, Path p) {
    log.info("N: {} , D: {} P: {}", blobname, description, p);
    BasicFileAttributes bfa = IBResourceFactory.getAttributes.apply(requireNonNull(p)).get();
    Future<Buffer> rf = fs.readFile(p.toAbsolutePath().toString());

    var test = rf.result();
    return rf.compose(bb -> {
      byte[] bytes = bb.getBytes();
      IBResource r = new IMDelegatedIBResource(bytes, blobname, description, bfa.creationTime().toInstant(),
          bfa.lastModifiedTime().toInstant(), empty());
      String id = r.getChecksum().asUUID().get().toString();
      this.blobs.putIfAbsent(id, bytes);
      this.resources.putIfAbsent(id, r);
      return succeededFuture(id);
    });
  }

  @Override
  public Logger getLog() {
    return log;
  }

  @Override
  public Future<String> getName(String id) {
    return ofNullable(resources.get(id)).map(IBResource::getName).map(Optional::get).map(Future::succeededFuture)
        .orElse(failedFuture(NOT_FOUND));
  }

  @Override
  public Future<String> getDescription(String id) {
    return ofNullable(resources.get(id)).map(IBResource::getDescription).map(Optional::get).map(Future::succeededFuture)
        .orElse(failedFuture(NOT_FOUND));
  }

  @Override
  public Future<String> putBlob(String blobname, @Nullable String description, Path p, Optional<Properties> addlProps) {
    return putBlob(blobname, description, p, empty());
  }
}
