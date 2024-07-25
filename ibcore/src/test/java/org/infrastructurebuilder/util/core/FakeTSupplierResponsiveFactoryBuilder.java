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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FakeTSupplierResponsiveFactoryBuilder
    extends AbstractTSupplierResponsiveFactoryBuilder<String, String, String> {

  private Map<String, String> map;

  public FakeTSupplierResponsiveFactoryBuilder(Map<String, String> map) {
    this.map = new HashMap<>();
    this.map.putAll(map);
  }

  @Override
  public Optional<TSupplier<String>> build(String resp) {
    return Optional.ofNullable(getConfig()).flatMap(c -> {
      return TSupplier.getNullableTSupplier(this.map.get(c));
    });
  }

  @Override
  public int respondsTo(String input) {
    return Objects.requireNonNull(input).startsWith("X") && this.map.keySet().contains(input) ? 0 : -1;
  }

}
