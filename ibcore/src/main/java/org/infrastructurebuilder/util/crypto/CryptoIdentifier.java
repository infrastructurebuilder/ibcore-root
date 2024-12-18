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
package org.infrastructurebuilder.util.crypto;

import java.util.Optional;
import java.util.SortedSet;

import org.infrastructurebuilder.pathref.JSONAndChecksumEnabled;

public interface CryptoIdentifier extends JSONAndChecksumEnabled {

  default boolean absolutelyMatch(final CryptoIdentifier i) {
    return i == null ? false : matches(i) && i.getIdentifiers().containsAll(getIdentifiers());
  }

  SortedSet<String> getIdentifiers();

  String getId();

  String getType();

  Optional<String> getValidationIdentifier();

  default boolean matches(final CryptoIdentifier i) {
    return i != null ?

        i.getType().equals(getType())

            && getIdentifiers().containsAll(i.getIdentifiers())

        :

        false;
  }
}
