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
package org.infrastructurebuilder.util.config.factory;

import java.util.Optional;

/**
 * Used for matching types to their processors. The "processable type" is
 * generally the name of the class that a given processor will handle.
 *
 * @author mykel.alvis
 *
 */
public interface ProcessableTyped<T> {

  /**
   * Get the "processable type", which is generally a fully qualified class name
   * of the thing being processed.
   *
   * @return non-null String
   */
  String getProcessableType();

  /**
   * If a specific processor is desired, then this value will be compared to the
   * component identifier for processing.
   *
   * @return name of required component processor, if available, else
   *         {@code Optional#empty()}
   */
  Optional<String> getSpecificProcessor();

  /**
   * Some IB processors imply a direction (inbound or outbound)
   * @return
   */
  boolean isInbound();

  T getValue();
}
