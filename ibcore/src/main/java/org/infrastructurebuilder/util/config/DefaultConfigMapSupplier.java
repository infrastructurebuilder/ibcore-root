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
package org.infrastructurebuilder.util.config;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultConfigMapSupplier implements ConfigMapSupplier {

  private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
  private final Optional<ConfigMapSupplier> init;

  public DefaultConfigMapSupplier() {
    this(null);
  }

  public DefaultConfigMapSupplier(ConfigMapSupplier init) {
    this.init = Optional.ofNullable(init);
    this.init.ifPresent(i -> map.putAll(i.get()));
  }

  @Override
  public ConfigMapSupplier addConfiguration(final Map<String, String> add) {
    Objects.requireNonNull(add).forEach((k, v) -> {
      addValue(k, v);
    });
    return this;
  }

  @Override
  public ConfigMapSupplier addConfiguration(final Properties add) {
    Collections.list(Objects.requireNonNull(add).propertyNames()).forEach(n -> {
      addValue(n.toString(), add.getProperty(n.toString()));
    });
    return this;
  }

  @Override
  public ConfigMapSupplier addValue(final String key, final String value) {
    synchronized (map) {
      if (!map.containsKey(key)) {
        map.put(key, value);
      }
    }
    return this;
  }

  @Override
  public Map<String, String> get() {
    return Collections.unmodifiableMap(map);
  }

  @Override
  public ConfigMapSupplier overrideConfiguration(final Map<String, String> over) {
    Objects.requireNonNull(over).forEach((k, v) -> {
      overrideValue(k, v);
    });
    return this;
  }

  @Override
  public ConfigMapSupplier overrideConfiguration(final Properties over) {
    Collections.list(Objects.requireNonNull(over).propertyNames()).forEach(n -> {
      overrideValue(n.toString(), over.getProperty(n.toString()));
    });
    return this;
  }

  @Override
  public ConfigMapSupplier overrideValue(final String key, final String value) {
    synchronized (map) {
      map.put(key, value);
    }
    return this;
  }

  @Override
  public ConfigMapSupplier overrideValueDefault(final String key, final String value, final String defaultValue) {
    synchronized (map) {
      map.put(key, Optional.ofNullable(value).orElse(defaultValue));
    }
    return this;
  }

  @Override
  public ConfigMapSupplier overrideValueDefaultBlank(final String key, final String value) {
    overrideValueDefault(key, value, "");
    return this;
  }

  @Override
  public String toString() {
    return "DefaultConfigMapSupplier [map=" + map + "]";
  }

}
