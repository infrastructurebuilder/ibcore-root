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

import java.util.function.Supplier;

import org.infrastructurebuilder.util.config.impl.DefaultConfigMapBuilderSupplier;

public interface ConfigMapBuilderSupplier extends Supplier<ConfigMapBuilder> {
  public static final String MAVEN = "maven"; // This one is implemented in ibcore-plexus
  public static final String IB_DATA_WORKING_DIR = "IBDataWorkingDir"; // This is also in ibcore-plexus
  public static final String MAVEN_WITH_SERVERS = "maven-with-servers";
  public static final String MAVEN_SETTINGS_SERVER_NAMESPACE = "maven.settings.server.";

  public static ConfigMapBuilder defaultBuilder() {
    return new DefaultConfigMapBuilderSupplier().get();
  }

}
