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

import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * ConfigMaps have been deprecated in favor of either:
 * <ol>
 * <li>explicit DAO objects that are keyed specifically to the config consumer</li>
 * <li><code>JSONObject</code> instances</li>
 * </ol>
 */
@Deprecated(forRemoval = true, since = "0.21.2")
public interface OldConfigMapSupplier extends Supplier<OldConfigMap> {
  public static final String MAVEN = "maven"; // This one is implemented in ibcore-plexus
  public static final String IB_DATA_WORKING_DIR = "IBDataWorkingDir"; // This is also in ibcore-plexus
  public static final String MAVEN_WITH_SERVERS = "maven-with-servers";
  public static final String MAVEN_SETTINGS_SERVER_NAMESPACE = "maven.settings.server.";

//  OldConfigMapSupplier addConfiguration(OldConfigMapSupplier add);

  OldConfigMapSupplier addConfiguration(Map<String, Object> add);

  OldConfigMapSupplier addConfiguration(OldConfigMap add);

  OldConfigMapSupplier addConfiguration(Properties add);

  OldConfigMapSupplier addValue(String key, Object value);

  /**
   * The contract for OldConfigMapSupplier is that "OVERRIDES" should <code>return this;</code>
   *
   * @param over
   * @return
   */
  OldConfigMapSupplier overrideConfiguration(Map<String, Object> over);

  OldConfigMapSupplier overrideConfiguration(Properties over);

  OldConfigMapSupplier overrideValue(String key, Object value);

  OldConfigMapSupplier overrideValueDefault(String key, Object value, String defaultValue);

  OldConfigMapSupplier overrideValueDefaultBlank(String key, Object value);

  /** Transfer map. You shouldn't use this method unless there's a need */
  Map<String, Object> asMap();

  OldConfigMapSupplier overrideConfigurationString(Map<String, String> over);

  OldConfigMapSupplier overrideConfiguration(OldConfigMap over);
}
