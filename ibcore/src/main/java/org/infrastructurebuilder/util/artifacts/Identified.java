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
package org.infrastructurebuilder.util.artifacts;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

/**
 * REQUIREMENT: getId() must return a unique id for a given component. If the
 * component is not a singleton, it's id must be different from others,
 * including other singletons, of any of its types
 *
 * @author mykel.alvis
 *
 */
public interface Identified {
  public static java.util.Comparator<Identified> comparator() {
    return new java.util.Comparator<Identified>() {
      @Override
      public int compare(Identified o1, Identified o2) {
        return requireNonNull(o1, "Identified o1").getId().compareTo(requireNonNull(o2, "Identified o2").getId());
      }
    };
  };

  /**
   * Return non-null value for id
   *
   * @return
   */
  default String getId() {
    return UUID.randomUUID().toString();
  }
}
