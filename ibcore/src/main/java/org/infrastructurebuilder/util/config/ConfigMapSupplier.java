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

import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

public interface ConfigMapSupplier extends Supplier<Map<String, String>> {
  public static final String MAVEN = "maven"; // This one is implemented in ibcore-plexus

  ConfigMapSupplier addConfiguration(Map<String, String> add);

  ConfigMapSupplier addConfiguration(Properties add);

  ConfigMapSupplier addValue(String key, String value);

  /**
   * The contract for ConfigMapSupplier is that "OVERRIDES" should <code>return this;</code>
   * @param over
   * @return
   */
  ConfigMapSupplier overrideConfiguration(Map<String, String> over);

  ConfigMapSupplier overrideConfiguration(Properties over);

  ConfigMapSupplier overrideValue(String key, String value);

  ConfigMapSupplier overrideValueDefault(String key, String value, String defaultValue);

  ConfigMapSupplier overrideValueDefaultBlank(String key, String value);

}
