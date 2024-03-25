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
package org.infrastructurebuilder.util.readdetect.base.impls;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.infrastructurebuilder.util.core.JSONBuilder;
import org.infrastructurebuilder.util.readdetect.base.IBResourceSource;
import org.json.JSONObject;

abstract public class DefaultIBResourceSource implements IBResourceSource {

  private final String name;
  private final String desc;
  private final Integer weight;

  DefaultIBResourceSource(String name, Optional<String> desc, Optional<Integer> weight) {
    this.name = requireNonNull(name);
    this.desc = requireNonNull(desc).orElse(null);
    this.weight = requireNonNull(weight).orElse(0);
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Optional<String> getDescription() {
    return ofNullable(this.desc);
  }

  @Override
  public Integer getWeight() {
    return this.weight;
  }

  abstract protected JSONBuilder getJsonBuilder();// JSONBuilder.newInstance(getRelativeRoot().flatMap(RelativeRoot::getPath))

  @Override
  public JSONObject asJSON() {
    return getJsonBuilder().asJSON();
  }

}
