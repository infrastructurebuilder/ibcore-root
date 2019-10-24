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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

public class ConfigMap implements Map<String,Object> {
  private final Map<String, Object> config;

  public ConfigMap(Map<String, Object> c) {
    this.config = Optional.ofNullable(c).orElse(new HashMap<>());
  }

  public ConfigMap() {
    this(new HashMap<>());
  }

  public ConfigMap(Properties p) {
    this.config = new HashMap<>();
    p.stringPropertyNames().forEach(k -> this.config.put(k,  p.getProperty(k)));
  }

  public ConfigMap(ConfigMap configuration) {
    config = new HashMap<>();
    config.putAll(configuration.config);
  }

  public final String getString(String key) {
    return Optional.ofNullable(get(key)).map(Object::toString).orElse(null);
  }

  public final Object get(String key) {
    return this.config.get(key);
  }

  public final Set<String> keySet() {
    return config.keySet();
  }

  public final int size() {
    return config.size();
  }

  public Set<Entry<String, Object>> entrySet() {
    return config.entrySet();
  }

  public boolean containsKey(String key) {
    return config.containsKey(key);
  }

  public String getOrDefault(String key, String defaultValue) {
    return getObjectOrDefault(key, defaultValue).toString();
  }

  public Object getObjectOrDefault(String key, Object defaultValue) {
    return config.getOrDefault(key, defaultValue);
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return config.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    // TODO Auto-generated method stub
    return config.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return config.containsValue(value);
  }

  @Override
  public Object get(Object key) {
    return config.get(key);
  }

  @Override
  public Object put(String key, Object value) {
    return config.put(key, value);
  }

  @Override
  public Object remove(Object key) {
    return config.remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m) {
    config.putAll(m);
  }

  @Override
  public void clear() {
    config.clear();
  }

  @Override
  public Collection<Object> values() {
    return config.values();
  }
}