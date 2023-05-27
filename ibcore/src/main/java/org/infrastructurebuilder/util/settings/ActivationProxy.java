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
package org.infrastructurebuilder.util.settings;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

public class ActivationProxy {
  private final boolean activeByDefault;
  private final Optional<ActivationPropertyProxy> property;
  private final Optional<ActivationOSProxy> os;
  private final Optional<String> jdk;
  private final Optional<ActivationFileProxy> file;

  public ActivationProxy(boolean activeByDefault, Optional<ActivationFileProxy> file, Optional<String> jdk,
      Optional<ActivationOSProxy> os, Optional<ActivationPropertyProxy> property) {
    this.activeByDefault = activeByDefault;
    this.file = requireNonNull(file);
    this.jdk = requireNonNull(jdk);
    this.os = requireNonNull(os);
    this.property = requireNonNull(property);
  }

  /**
   * Get specifies that this profile will be activated based on
   * existence of a file.
   *
   * @return ActivationFile
   */
  Optional<ActivationFileProxy> getFile() {
    return this.file;
  }

  /**
   * Get specifies that this profile will be activated when a
   * matching JDK is detected.
   *
   * @return String
   */
  Optional<String> getJdk() {
    return this.jdk;
  }

  /**
   * Get specifies that this profile will be activated when
   * matching OS attributes are detected.
   *
   * @return ActivationOS
   */
  Optional<ActivationOSProxy> getOs() {
    return this.os;
  }

  /**
   * Get specifies that this profile will be activated when this
   * System property is specified.
   *
   * @return ActivationProperty
   */
  Optional<ActivationPropertyProxy> getProperty() {
    return this.property;
  }

  /**
   * Get flag specifying whether this profile is active as a
   * default.
   *
   * @return boolean
   */
  boolean isActiveByDefault() {
    return this.activeByDefault;
  }
}
