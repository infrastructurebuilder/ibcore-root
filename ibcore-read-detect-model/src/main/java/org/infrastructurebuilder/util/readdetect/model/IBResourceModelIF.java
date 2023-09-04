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
package org.infrastructurebuilder.util.readdetect.model;

import static java.time.Instant.parse;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

public interface IBResourceModelIF {

  /**
   * Returns a parsed Instant from a String, if possible. Returns Optional.empty() otherwise.
   */
  final static Function<String, Optional<Instant>> parseInstant = (i) -> {
    try {
      return of(parse(i));
    } catch (Throwable t) {
      return empty();
    }
  };

  /**
   * An IBResource must have an optional moment of realization. This is an DateTimeFormatter.ISO_INSTANT format string
   *
   * @return
   */
  String getRealized();

  /**
   * @return
   */
  String getLastUpdate();

  /**
   * @return
   */
  String getCreated();

  /**
   *
   * @return Instant that this resource was realized, or empty()
   */
  default Optional<Instant> getRealizedInstant() {
    return parseInstant.apply(getRealized());
  }

  /**
   *
   * @return Instant that this resource was realized, or empty()
   */
  default Optional<Instant> getLastUpdateInstant() {
    return parseInstant.apply(getLastUpdate());
  }

  /**
   *
   * @return Instant that this resource was realized, or empty()
   */
  default Optional<Instant> getCreatedInstant() {
    return parseInstant.apply(getCreated());
  }

  /**
   * @return true if the resource is considered realized.
   */
  default boolean isRealized() {
    return getRealizedInstant().isPresent();
  }

}
