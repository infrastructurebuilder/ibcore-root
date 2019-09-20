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
package org.infrastructurebuilder.data;

import java.util.Optional;

public interface IBDataTransformer {
  /**
   * Override to return true where a transformer responds to an datastream of type "i"
   * @param i
   * @return
   */
  default boolean respondsTo(IBDataStream i) {
    return false;
  }
  /**
   * Call onl if respondsTo(i)
   * @param stream
   * @return
   */
  default Optional<IBTransformationResult> transform(IBDataStream stream) {
    return Optional.empty();
  }
}
