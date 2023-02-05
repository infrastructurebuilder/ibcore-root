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
package org.infrastructurebuilder.util.core;

import static java.util.Optional.empty;

import java.util.Optional;
import java.util.SortedSet;

public interface TypeToExtensionMapper {

  /**
   * Returns the file extension for a given MIME string
   *
   * @param type String version of MIME type
   * @return non-null extension of type, mapping to a default type if unknown
   */
  String getExtensionForType(String type);

  /**
   * Returns a set of types that are associated with a given extension
   *
   * @param extension
   * @return
   */
  SortedSet<String> reverseMapFromExtension(String extension);

  /**
   * Denotes that
   *
   * @param type
   * @return
   */
  default Optional<String> getStructuredSupplyTypeClassName(String type) {
    return empty();
  }

}
