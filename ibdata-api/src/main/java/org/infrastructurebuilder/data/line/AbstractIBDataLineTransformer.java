/**
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
package org.infrastructurebuilder.data.line;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.PropertiesSupplier;

abstract public class AbstractIBDataLineTransformer<I, O> implements IBDataLineTransformer<I, O> {

  private final Properties config = new Properties();

  public AbstractIBDataLineTransformer(PropertiesSupplier ps) {
    Optional.ofNullable(ps).ifPresent(configSupplier -> {
      Map<String, String> q = Objects.requireNonNull(configSupplier).get().entrySet().stream()
          .filter(e -> e.getKey().toString().startsWith(getConfigurationPrefix())).collect(Collectors.toMap(
              k -> k.getKey().toString().substring(getConfigurationPrefix().length()), v -> v.getValue().toString()));
      q.forEach((k, v) -> {
        this.config.setProperty(k.startsWith(".") ? k.substring(1) : k, v);
      });
    });
  }

  abstract public String getConfigurationPrefix();

  @Override
  public String getName() {
    return getConfigurationPrefix();
  }

  @Override
  public final Optional<String> getConfiguration(String key) {
    return getConfiguration(key, null);
  }

  @Override
  public final Optional<String> getConfiguration(String key, String defaultValue) {
    return Optional.ofNullable(this.config.getProperty(key, defaultValue));
  }

}