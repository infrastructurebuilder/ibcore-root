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
package org.infrastructurebuilder.util.config.old;

import static java.util.Collections.list;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class OldDefaultConfigMapSupplier implements OldConfigMapSupplier {

  private final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
  private final Optional<OldConfigMap> init;

  public OldDefaultConfigMapSupplier() {
    this.init = empty();
  }

  public OldDefaultConfigMapSupplier(OldConfigMapSupplier init) {
    this.init = ofNullable(init).map(OldConfigMapSupplier::get);
  }

  public OldDefaultConfigMapSupplier(OldConfigMap init) {
    this.init = ofNullable(init);
  }

  @Override
  public OldConfigMapSupplier addConfiguration(final Map<String, Object> add) {
    requireNonNull(add).forEach((k, v) -> {
      addValue(k, v);
    });
    return this;
  }

  @Override
  public OldConfigMapSupplier addConfiguration(OldConfigMap add) {
    return addConfiguration(add.keySet().stream().collect(toMap(k -> k, v -> add.get(v))));
  }

  @Override
  public OldConfigMapSupplier addConfiguration(final Properties add) {
    for (String n : requireNonNull(add).stringPropertyNames())
      addValue(n.toString(), add.getProperty(n.toString()));
    return this;
  }

  @Override
  public OldConfigMapSupplier addValue(final String key, final Object value) {
    synchronized (map) {
      if (!map.containsKey(key)) {
        map.put(key, value);
      }
    }
    return this;
  }

  public final Map<String, Object> asMap() {
    Map<String, Object> o = new ConcurrentHashMap<>();
    if (init.isPresent()) {
      o.putAll(init.get());
    }
    o.putAll(map);
    return o;
  }

  @Override
  public OldConfigMap get() {
    return new OldConfigMap(asMap());
  }

  @Override
  public OldConfigMapSupplier overrideConfigurationString(final Map<String, String> over) {
    return overrideConfiguration(over.entrySet().stream().collect(toMap(k -> k.getKey(), v -> v.getValue())));
  }

  @Override
  public OldConfigMapSupplier overrideConfiguration(final Map<String, Object> over) {
    requireNonNull(over).forEach((k, v) -> {
      overrideValue(k, v);
    });
    return this;
  }

  @Override
  public OldConfigMapSupplier overrideConfiguration(final OldConfigMap over) {
    return overrideConfiguration(
        requireNonNull(over).entrySet().stream().collect(toMap(k -> k.getKey(), v -> v.getValue())));
  }

  @Override
  public OldConfigMapSupplier overrideConfiguration(final Properties over) {
    list(requireNonNull(over).propertyNames()).forEach(n -> {
      overrideValue(n.toString(), over.getProperty(n.toString()));
    });
    return this;
  }

  @Override
  public OldConfigMapSupplier overrideValue(final String key, final Object value) {
    synchronized (map) {
      map.put(key, value);
    }
    return this;
  }

  @Override
  public OldConfigMapSupplier overrideValueDefault(final String key, final Object value, final String defaultValue) {
    synchronized (map) {
      map.put(key, ofNullable(value).orElse(defaultValue));
    }
    return this;
  }

  @Override
  public OldConfigMapSupplier overrideValueDefaultBlank(final String key, final Object value) {
    overrideValueDefault(key, value, "");
    return this;
  }

  @Override
  public String toString() {
    return "OldDefaultConfigMapSupplier [map=" + map + "]";
  }

}
