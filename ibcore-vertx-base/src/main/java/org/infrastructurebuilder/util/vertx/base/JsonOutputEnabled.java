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
package org.infrastructurebuilder.util.vertx.base;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.infrastructurebuilder.pathref.PathRef;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface JsonOutputEnabled {
  /**
   * Deprecating in favor of toJson (for DataObject use)
   *
   * @return
   */
  @Deprecated
  default JsonObject asJson() {
    return toJson();
  }

  /**
   * DataObject convenience method.
   *
   * @return
   */
  JsonObject toJson();

  default Optional<PathRef> getJsonRelativeRoot() {
    return Optional.empty();
  }

  default Future<JsonObject> toFutureJson() {
    return Future.succeededFuture(toJson());
  }

  static JsonObject serialize(JsonOutputEnabled f) {
    return requireNonNull(f).toJson();
  }

}
