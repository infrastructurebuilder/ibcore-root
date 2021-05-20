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
package org.infrastructurebuilder.util.config;

import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.infrastructurebuilder.exceptions.IBException;

public class ConfigMap implements Map<String, Object> {
  private final Map<String, Object> config;

  public ConfigMap(Map<String, Object> c) {
    this.config = ofNullable(c).orElse(new HashMap<>());
  }

  public ConfigMap() {
    this(new HashMap<>());
  }

  public ConfigMap(Properties p) {
    this.config = new HashMap<>();
    p.stringPropertyNames().forEach(k -> this.config.put(k, p.getProperty(k)));
  }

  public ConfigMap(ConfigMap configuration) {
    config = new HashMap<>();
    config.putAll(configuration.config);
  }

  public final String getString(String key) {
    return ofNullable(get(key)).map(Object::toString).orElse(null);
  }

  public final boolean getParsedBoolean(String key, boolean defaultValue) {
    return ofNullable(getString(key)).map(Boolean::parseBoolean).orElse(defaultValue);
  }

  @SuppressWarnings("unchecked")
  public final <T> T get(String key) {
    return (T) this.config.get(key);
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

  @SuppressWarnings("unchecked")
  public <T> T getOrDefault(String key, T defaultValue) {
    return (T) config.getOrDefault(key, defaultValue);
  }

  public Optional<String> getOptionalString(String key) {
    return ofNullable(getOrDefault(key, null));
  }

  @SuppressWarnings("unchecked")
  public <T> T getRequired(String key) {
    return (T) ofNullable(config.getOrDefault(key, null))
        .orElseThrow(() -> new IBException("Value " + key + " not available"));
  }

  @Override
  public boolean isEmpty() {
    return config.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
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
