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

import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

public interface ConfigMapSupplier extends Supplier<ConfigMap> {
  public static final String MAVEN = "maven"; // This one is implemented in ibcore-plexus
  public static final String IB_DATA_WORKING_DIR = "IBDataWorkingDir"; // This is also in ibcore-plexus
  public static final String MAVEN_WITH_SERVERS = "maven-with-servers";
  public static final String MAVEN_SETTINGS_SERVER_NAMESPACE = "maven.settings.server.";

//  ConfigMapSupplier addConfiguration(ConfigMapSupplier add);

  ConfigMapSupplier addConfiguration(Map<String, Object> add);
  ConfigMapSupplier addConfiguration(ConfigMap add);

  ConfigMapSupplier addConfiguration(Properties add);

  ConfigMapSupplier addValue(String key, Object  value);

  /**
   * The contract for ConfigMapSupplier is that "OVERRIDES" should <code>return this;</code>
   * @param over
   * @return
   */
  ConfigMapSupplier overrideConfiguration(Map<String, Object> over);

  ConfigMapSupplier overrideConfiguration(Properties over);

  ConfigMapSupplier overrideValue(String key, Object value);

  ConfigMapSupplier overrideValueDefault(String key, Object  value, String defaultValue);

  ConfigMapSupplier overrideValueDefaultBlank(String key, Object  value);

  /** Transfer map. You shouldn't use this method unless there's a need */
  Map<String,Object> asMap();

  ConfigMapSupplier overrideConfigurationString(Map<String, String> over);

  ConfigMapSupplier overrideConfiguration(ConfigMap over);
}
