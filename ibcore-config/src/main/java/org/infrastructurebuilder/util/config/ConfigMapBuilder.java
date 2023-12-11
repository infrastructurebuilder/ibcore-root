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
package org.infrastructurebuilder.util.config;

import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import org.json.JSONObject;

public interface ConfigMapBuilder extends Supplier<ConfigMap> {

  ConfigMapBuilder withProperties(Properties p);

  /**
   * Loads file of properties. If optional, then missing/unloadable file is not a failure.
   * 
   * @param file
   * @param optional
   * @return
   */

  ConfigMapBuilder withPropertiesResource(String file, boolean optional);

  ConfigMapBuilder withPropertiesFile(Path file, boolean optional);

  ConfigMapBuilder withMapStringString(Map<String, String> m);

  ConfigMapBuilder withJSONObject(JSONObject j);

  ConfigMapBuilder withJSONFile(Path j, boolean optional);

  ConfigMapBuilder withJSONResource(String j, boolean optional);

  ConfigMapBuilder withKeyValue(String key, Object value);

  ConfigMapBuilder withJSONObjectFacade(ConfigMap j);

}
