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

import static java.util.Objects.requireNonNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ActivationFileProxy {
  private final Optional<Path> exists, missing;

  public ActivationFileProxy(Optional<Path> exists, Optional<Path> missing) {
    this.exists = requireNonNull(exists);
    this.missing = requireNonNull(missing);
  }

  /**
   * Get the name of the file that should exist to activate a
   * profile.
   *
   * @return String
   */
  Optional<Path> getExists() {
    return this.exists;
  }

  /**
   * Get the name of the file that should be missing to activate
   * a
   *             profile.
   *
   * @return String
   */
  Optional<Path> getMissing() {
    return this.missing;
  }

  public boolean isActive() {
    return getExists().map(Files::exists).orElse(true) && getMissing().map(Files::exists).map(b -> !b).orElse(true);
  }
}
