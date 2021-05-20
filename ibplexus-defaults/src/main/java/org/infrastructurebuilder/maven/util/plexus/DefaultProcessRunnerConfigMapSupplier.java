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
package org.infrastructurebuilder.maven.util.plexus;

import static org.infrastructurebuilder.util.executor.plexus.ProcessRunnerSupplier.PROCESS_NAMESPACE;

import java.util.Collections;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.DefaultConfigMapSupplier;

@Named
public class DefaultProcessRunnerConfigMapSupplier extends DefaultConfigMapSupplier {

  public static final String PROCESSRUNNERCONFIG = "processrunnerconfig";
  private final ConfigMap configMap;

  public DefaultProcessRunnerConfigMapSupplier(final ConfigMapSupplier cms) {
    final ConfigMap tempMap = cms.get();
    configMap = new ConfigMap(
        Collections.unmodifiableMap(tempMap.entrySet().stream().filter(e -> e.getKey().startsWith(PROCESS_NAMESPACE))
            .collect(Collectors.toMap(k -> k.getKey().toString(), v -> v.getValue()))));
  }

  @Override
  public ConfigMap get() {
    return configMap;
  }
}
