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
package org.infrastructurebuilder.util;

import java.util.Optional;

public class ActivationOSProxy {
  private final Optional<String> arch;
  private final Optional<String> family;
  private final Optional<String> name;
  private final Optional<String> version;


  /**
   * @param arch
   * @param family
   * @param name
   * @param version
   */
  public ActivationOSProxy(Optional<String> arch, Optional<String> family, Optional<String> name,
      Optional<String> version) {
    this.arch = arch;
    this.family = family;
    this.name = name;
    this.version = version;
  }

  /**
   * Get the architecture of the operating system to be used to
   * activate the
   *           profile.
   *
   * @return String
   */
  Optional<String> getArch() {
    return this.arch;
  }

  /**
   * Get the general family of the OS to be used to activate the
   * profile, such as
   *             <code>windows</code> or <code>unix</code>.
   *
   * @return String
   */
  Optional<String> getFamily() {
    return this.family;
  }

  /**
   * Get the name of the operating system to be used to activate
   * the profile. This must be an exact match
   *             of the <code>${os.name}</code> Java property,
   * such as <code>Windows XP</code>.
   *
   * @return String
   */
  Optional<String> getName() {
    return this.name;
  }

  /**
   * Get the version of the operating system to be used to
   * activate the
   *           profile.
   *
   * @return String
   */
  Optional<String> getVersion() {
    return this.version;
  }
}
