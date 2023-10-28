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
package org.infrastructurebuilder.util.core;

import java.util.Collections;
import java.util.List;

public interface RequiresArtifacts {
  /**
   * Return a list of coordinates that must be in the classpath in order to load the driver
   *
   * @return List of GAV items in order of required insertion into the (new) classpath that the driver will be created
   *         from
   */
  List<GAV> getRequiredArtifacts();

  /**
   * As above, but optional.  By convention, the optional artifacts would be added
   * to the classpath BEFORE the required artifacts.
   * @return
   */
  default List<GAV> getOptionalArtifacts() {
    return Collections.emptyList();
  }
}
