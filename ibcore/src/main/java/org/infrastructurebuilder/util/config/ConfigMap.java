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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

public class ConfigMap {
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

  public final String get(String key) {
    return (String) getObject(key);
  }

  public final Object getObject(String key) {
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
    return config.getOrDefault(key, defaultValue).toString();
  }
}
