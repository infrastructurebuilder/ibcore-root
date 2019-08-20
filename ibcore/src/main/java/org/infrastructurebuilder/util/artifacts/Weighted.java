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

/**
 * Allows us to know the relative weight of a given component within a set.
 * The requirement is HIGHER IS MORE IMPORTANT
 *
 * Default value is expected to be 0, and most impls should PROBABLY use 0
 *
 * @author mykel.alvis
 *
 */
public interface Weighted {
  /**
   * Return non-null value for weight
   */
  default Integer getWeight() {
    return 0;
  }
}
