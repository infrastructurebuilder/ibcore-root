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
package org.infrastructurebuilder.util.readdetect;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.util.core.Checksum;
import org.json.JSONObject;

public interface IBResourceBuilder {

  IBResourceBuilder fromJSON(JSONObject j);

  IBResourceBuilder withChecksum(Checksum csum);

  IBResourceBuilder from(Path path);

  IBResourceBuilder withFilePath(String path);

  IBResourceBuilder cached(boolean cached);

  IBResourceBuilder withName(String name);

  IBResourceBuilder withDescription(String desc);

  IBResourceBuilder withType(String type);

  IBResourceBuilder withType(Optional<String> type);

  IBResourceBuilder withAdditionalProperties(Properties p);

  IBResourceBuilder withLastUpdated(Instant last);

  IBResourceBuilder withSource(String source);

  IBResourceBuilder withCreateDate(Instant create);

  IBResourceBuilder withSize(long size);

  IBResourceBuilder withMostRecentAccess(Instant access);

  IBResourceBuilder movedTo(Path path);

  /**
   * validate checks the values provided so far and throws IBResourceException if anything is off.
   *
   * You can call validate whenever you set any value and if it returns your data is still possibly OK
   *
   * @param hard if true, then assume nothing and re-validate the existence and checksums of the paths and sources
   * @throws IBResourceException if validation fails
   * @return this builder
   */
  IBResourceBuilder validate(boolean hard);

  Optional<IBResource> build(boolean hard);

  default Optional<IBResource> build() {
    return build(false);
  }

}
