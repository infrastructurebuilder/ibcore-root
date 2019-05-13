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
import java.util.SortedSet;

import org.infrastructurebuilder.util.artifacts.JSONAndChecksumEnabled;

public interface EncryptionIdentifier extends JSONAndChecksumEnabled {

  default boolean absolutelyMatch(final EncryptionIdentifier i) {
    return i == null ? false : matches(i) && i.getEncryptionIdentifiers().containsAll(getEncryptionIdentifiers());
  }

  SortedSet<String> getEncryptionIdentifiers();

  String getId();

  String getType();

  Optional<String> getValidationIdentifier();

  default boolean matches(final EncryptionIdentifier i) {
    return i == null ? false
        : i.getType().equals(getType()) && getEncryptionIdentifiers().containsAll(i.getEncryptionIdentifiers());
  }
}
