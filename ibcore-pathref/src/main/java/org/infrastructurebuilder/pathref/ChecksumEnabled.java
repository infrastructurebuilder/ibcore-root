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
package org.infrastructurebuilder.pathref;

import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public interface ChecksumEnabled {
  String CHECKSUM = "checksum";
  /**
   * This seems weird here but it's super-useful
   */
  public final static Function<String, Optional<UUID>> safeMapUUID = (s) -> cet
      .returns(() -> ofNullable(s).map(UUID::fromString));
  /**
   * Also seems weird to be here, but again super useful
   */
  public final static Function<String, UUID> nullableSafeMapUUID = uuid -> {
    return safeMapUUID.apply(uuid).orElse(null);
  };

  default Checksum asChecksum() {
    return getChecksumBuilder().asChecksum();
  }

  ChecksumBuilder getChecksumBuilder();
}
