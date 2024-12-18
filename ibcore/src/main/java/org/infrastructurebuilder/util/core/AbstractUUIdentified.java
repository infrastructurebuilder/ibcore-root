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
package org.infrastructurebuilder.util.core;

import static java.util.Optional.ofNullable;

import java.util.UUID;

import org.infrastructurebuilder.pathref.JSONBuilder;
import org.json.JSONObject;

public class AbstractUUIdentified implements UUIdentified {
  private UUID id;

  public AbstractUUIdentified() {
    this(null);
  }

  public AbstractUUIdentified(UUID id2) {
    this.id = ofNullable(id2).orElse(UUID.randomUUID());
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  public JSONObject getLocalJSON() {
    return JSONBuilder.newInstance().addString(ID, getId().toString()).asJSON();
  }

}
