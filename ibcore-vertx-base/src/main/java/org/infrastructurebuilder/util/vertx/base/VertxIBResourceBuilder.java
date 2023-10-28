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

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceException;
import org.json.JSONObject;

import io.vertx.core.Future;
import io.vertx.core.file.AsyncFile;

public interface VertxIBResourceBuilder {

  VertxIBResourceBuilder fromJSON(JSONObject j);

  VertxIBResourceBuilder withChecksum(Checksum csum);

  VertxIBResourceBuilder from(Path path);

  VertxIBResourceBuilder withFilePath(String path);

  VertxIBResourceBuilder cached(boolean cached);

  VertxIBResourceBuilder withName(String name);

  VertxIBResourceBuilder withDescription(String desc);

  VertxIBResourceBuilder withType(String type);

  VertxIBResourceBuilder withType(Optional<String> type);

  VertxIBResourceBuilder withAdditionalProperties(Properties p);

  VertxIBResourceBuilder withLastUpdated(Instant last);

  VertxIBResourceBuilder withSource(String source);

  VertxIBResourceBuilder withCreateDate(Instant create);

  VertxIBResourceBuilder withSize(long size);

  VertxIBResourceBuilder withMostRecentAccess(Instant access);

  VertxIBResourceBuilder movedTo(Path path);

  /**
   * validate checks the values provided so far and throws IBResourceException if anything is off.
   *
   * You can call validate whenever you set any value and if it returns your data is still possibly OK
   *
   * @param hard if true, then assume nothing and re-validate the existence and checksums of the paths and sources
   * @throws IBResourceException if validation fails
   * @return this builder
   */
  VertxIBResourceBuilder validate(boolean hard);

  Future<VertxIBResource> build(boolean hard);

  default Future<VertxIBResource> build() {
    return build(false);
  }

}