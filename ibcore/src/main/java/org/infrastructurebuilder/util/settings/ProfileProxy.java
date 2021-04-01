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
package org.infrastructurebuilder.util.settings;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ProfileProxy {
  private final String id;
  private final boolean active;
  private final Optional<ActivationProxy> activation;
  private final List<RepositoryProxy> pluginRepositories;
  private final Properties properties;
  private final List<RepositoryProxy> repositories;


  /**
   * @param id
   * @param active
   * @param activation
   * @param pluginRepositories
   * @param properties
   * @param repositories
   */
  public ProfileProxy(String id, boolean active, Optional<ActivationProxy> activation,
      List<RepositoryProxy> pluginRepositories, Properties properties, List<RepositoryProxy> repositories) {
    this.id = id;
    this.active = active;
    this.activation = activation;
    this.pluginRepositories = pluginRepositories;
    this.properties = properties;
    this.repositories = repositories;
  }

  String getId() {
    return this.id;
  }

  boolean isActive() {
    return this.active;
  }

  /**
   * Get the conditional logic which will automatically
   *             trigger the inclusion of this profile.
   *
   * @return Activation
   */
  Optional<ActivationProxy> getActivation() {
    return this.activation;
  }

  /**
   * Method getPluginRepositories.
   *
   * @return List
   */
  java.util.List<RepositoryProxy> getPluginRepositories() {
    return this.pluginRepositories;
  }

  /**
   * Method getProperties.
   *
   * @return Properties
   */
  java.util.Properties getProperties() {
    return this.properties;
  }

  /**
   * Method getRepositories.
   *
   * @return List
   */
  java.util.List<RepositoryProxy> getRepositories() {
    return this.repositories;
  }
}
