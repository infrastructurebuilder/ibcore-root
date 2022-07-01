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
package org.infrastructurebuilder.util.core;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.UUID;

public class AbstractUUIdentifiedAndWeighted implements UUIdentifiedAndWeighted {
  private UUID id;
  private Integer weight = 0;

  protected AbstractUUIdentifiedAndWeighted(UUID id, int timestamp) {
    this.id = requireNonNull(id);
    this.weight = requireNonNull(timestamp);
  }

  protected AbstractUUIdentifiedAndWeighted(int timestamp) {
    this.id = UUID.randomUUID();
    this.weight = requireNonNull(timestamp);
  }

  @Override
  public UUID getId() {
    if (this.id == null)
      this.id = UUID.randomUUID();
    return this.id;
  }

  public Integer getWeight() {
    if (this.weight == null)
      this.weight = 0;
    return weight;
  }

}
