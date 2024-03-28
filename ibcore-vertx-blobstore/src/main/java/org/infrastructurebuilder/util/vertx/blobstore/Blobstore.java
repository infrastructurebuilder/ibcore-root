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
package org.infrastructurebuilder.util.vertx.blobstore;

import static io.vertx.core.Future.succeededFuture;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.util.core.LoggerEnabled;
import org.infrastructurebuilder.util.readdetect.base.IBResource;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

public interface Blobstore<T> extends LoggerEnabled {

  Future<Buffer> getBlob(String id);

  Future<String> putBlob(String blobname, @Nullable String description, Future<Buffer> b, Instant createDate,
      Instant lastUpdated, Optional<Properties> addlProps);

  Future<String> putBlob(String blobname, @Nullable String description, Path p, Optional<Properties> addlProps);

  Future<Void> removeBlob(String id);

  default Future<String> putBlob(String blobname, @Nullable String description, Path p) {
    return putBlob(blobname, description, p, Optional.empty());
  }

  Future<IBResource> getMetadata(String id);

  default Future<Instant> getCreateDate(String id) {
    return getMetadata(id)
        .compose(md -> md.getCreateDate().map(Future::succeededFuture).orElse(Future.failedFuture("no create date")));
  }

  default Future<Instant> getLastUpdated(String id) {
    return getMetadata(id).compose(
        md -> md.getLastUpdateDate().map(Future::succeededFuture).orElse(Future.failedFuture("no last update date")));
  }

  default Future<String> getName(String id) {
    return getMetadata(id).compose(md -> succeededFuture(md.getName()));
  }

  default Future<String> getDescription(String id) {
    return getMetadata(id).compose(md -> succeededFuture(md.getDescription().orElse(null)));
  }

}
