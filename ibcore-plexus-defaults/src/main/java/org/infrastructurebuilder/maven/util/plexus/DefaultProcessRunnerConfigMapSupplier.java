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
package org.infrastructurebuilder.maven.util.plexus;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;

import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.ConfigMapBuilder;
import org.infrastructurebuilder.util.config.ConfigMapBuilderSupplier;
import org.infrastructurebuilder.util.config.impl.DefaultConfigMapBuilderSupplier;
import org.infrastructurebuilder.util.executor.ProcessRunnerSupplier;

@Deprecated
public class DefaultProcessRunnerConfigMapSupplier extends DefaultConfigMapBuilderSupplier {

  public static final String PROCESSRUNNERCONFIG = "processrunnerconfig";
  private final ConfigMapBuilder configMap;

  public DefaultProcessRunnerConfigMapSupplier(final ConfigMapBuilderSupplier cms) {
    this.configMap = cms.get(); 
    final ConfigMap tempMap = this.configMap.get(); // Interim values

    Map<String, String> q = tempMap.keySet().stream()

        .filter(e -> e.startsWith(ProcessRunnerSupplier.PROCESS_NAMESPACE))

        .collect(toMap(Function.identity(), v -> tempMap.get(v).toString()));
    this.configMap.withMapStringString(q);
  }
  
  @Override
  public ConfigMapBuilder get() {
    return configMap;
  }
  
  @Override
  public String getName() {
    return PROCESSRUNNERCONFIG;
  }
}
