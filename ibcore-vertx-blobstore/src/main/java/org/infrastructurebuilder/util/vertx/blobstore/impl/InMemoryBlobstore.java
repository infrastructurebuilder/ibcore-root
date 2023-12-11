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
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.NOT_FOUND;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.impl.IBResourceInMemoryDelegated;
import org.infrastructurebuilder.util.vertx.blobstore.Blobstore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

public class InMemoryBlobstore implements Blobstore<InputStream> {

  public static final String ERROR_GETTING_ATTRIBUTES = "error.getting.attributes";

  private final static FileSystem fs = Vertx.vertx().fileSystem();

  private final static Collector<Entry<String, IBResource<InputStream>>, ?, ImmutableSortedMap<Instant, String>> oldestCollector = ImmutableSortedMap
      .toImmutableSortedMap(Ordering.natural().reversed(), (e) -> {
        return ((Entry<String, IBResource<InputStream>>) e).getValue().getLastUpdateDate().orElse(null);
      }, Entry::getKey);

  private final static Logger log = LoggerFactory.getLogger(InMemoryBlobstore.class);
  private final Map<String, IBResource<InputStream>> resources = new ConcurrentHashMap<>();
  private final Map<String, byte[]> blobs = new ConcurrentHashMap<>();

  private long maxBytes;

  public InMemoryBlobstore() {
    this.maxBytes = 2 >> 16;
  }

  public Optional<String> getOldest() {
    return Optional.ofNullable(resources.entrySet().stream().collect(oldestCollector).firstEntry())
        .map(e -> e.getValue());
  }

  public Long size() {
    return resources.values().parallelStream().collect(Collectors.summingLong(e -> e.size()));
  }

  @Override
  public Future<Void> removeBlob(String id) {
    blobs.remove(id);
    resources.remove(id);
    return Future.succeededFuture();
  }

  public void evict() {
    while (size() > maxBytes) {
      getLog().warn("Evicting due to size constraint");
      // FIXME this could be problematic and lead to issues
      getOldest().ifPresent(this::removeBlob);
    }
  }

  @Override
  public Future<IBResource<InputStream>> getMetadata(String id) {
    return ofNullable(resources.get(id)).map(Future::succeededFuture).orElse(Future.failedFuture(NOT_FOUND));
  }

  @Override
  public Future<Buffer> getBlob(String id) {
    return ofNullable(blobs.get(id)).map(ba -> Buffer.buffer(ba)).map(Future::succeededFuture)
        .orElse(failedFuture(NOT_FOUND));
  }

  @Override
  public Future<Instant> getCreateDate(String id) {
    return ofNullable(resources.get(id)).flatMap(IBResource::getCreateDate).map(Future::succeededFuture)
        .orElse(failedFuture(NOT_FOUND));
  }

  @Override
  public Future<Instant> getLastUpdated(String id) {
    return ofNullable(resources.get(id)).flatMap(IBResource::getLastUpdateDate).map(Future::succeededFuture)
        .orElse(failedFuture(NOT_FOUND));
  }

  @Override
  public Future<String> putBlob(String blobname, String desc, Future<Buffer> b, Instant createDate, Instant lastUpdated,
      Optional<Properties> addlProps) {
    return b
//        .onFailure(t -> {
//      log.error("--------------------------------------------------------  Error puttign blob", t);
//    })
        .compose(bb -> {
          byte[] bytes = bb.getBytes();
          IBResource<InputStream> r = new IBResourceInMemoryDelegated(bytes, blobname, desc, createDate, lastUpdated,
              addlProps);
          String id = r.getChecksum().asUUID().get().toString();
          this.blobs.putIfAbsent(id, bytes);
          this.resources.putIfAbsent(id, r);
          return succeededFuture(id);
        });
  }

  @Override
  public Future<String> putBlob(String blobname, @Nullable String description, Path p, Optional<Properties> addlProps) {
    log.info("N: {} , D: {} P: {}", blobname, description, p);
    BasicFileAttributes bfa = IBResourceBuilderFactory.getAttributes.apply(requireNonNull(p)).get();
    Future<Buffer> rf = fs.readFile(p.toAbsolutePath().toString());

//    var test = rf.result(); // FIXME Remove later
    return rf.compose(bb -> {
      byte[] bytes = bb.getBytes();
      IBResource<InputStream> r = new IBResourceInMemoryDelegated(bytes, blobname, description,
          bfa.creationTime().toInstant(), bfa.lastModifiedTime().toInstant(), requireNonNull(addlProps));
      String id = r.getChecksum().asUUID().get().toString();
      this.blobs.putIfAbsent(id, bytes);
      this.resources.putIfAbsent(id, r);
      return succeededFuture(id);
    });
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
    return ofNullable(resources.get(id)).map(IBResource::getName).map(Future::succeededFuture)
        .orElse(failedFuture(NOT_FOUND));
  }

  @Override
  public Future<String> getDescription(String id) {
    return ofNullable(resources.get(id)).map(IBResource::getDescription).map(Optional::get).map(Future::succeededFuture)
        .orElse(failedFuture(NOT_FOUND));
  }

}
