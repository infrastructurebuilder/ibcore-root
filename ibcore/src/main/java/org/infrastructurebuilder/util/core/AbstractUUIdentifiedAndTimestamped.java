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

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;

abstract public class AbstractUUIdentifiedAndTimestamped extends AbstractUUIdentified
    implements UUIdentifiedAndTimestamped {
  private final Instant timestamp;

  protected AbstractUUIdentifiedAndTimestamped(UUID id, Instant timestamp) {
    super(id);
    this.timestamp = Optional.ofNullable(timestamp).orElse(Instant.now());
  }

  public AbstractUUIdentifiedAndTimestamped() {
    this(null);
  }

  protected AbstractUUIdentifiedAndTimestamped(Instant timestamp) {
    this(null, timestamp);
  }

  @Override
  public Instant getTimestamp() {
    return this.timestamp;
  }

  public JSONObject getLocalJSON() {
    return JSONBuilder.newInstance()
        .addString(ID, getId().toString())
        .addInstant(TIMESTAMP, getTimestamp())
        .asJSON();
  }

}
