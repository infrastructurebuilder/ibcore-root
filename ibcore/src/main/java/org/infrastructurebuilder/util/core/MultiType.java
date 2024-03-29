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

import static java.util.Optional.ofNullable;

import java.util.Optional;

public class MultiType<T, E extends Throwable> {

  final Optional<E> e;
  final Optional<T> t;

  public MultiType(final E thrown) {
    this(null, thrown);
  }

  public MultiType(final T typed) {
    this(typed, null);
  }

  public MultiType(final T typed, final E thrown) {
    this.t = ofNullable(typed);
    this.e = ofNullable(thrown);
  }

  public Optional<E> getException() {
    return e;
  }

  /**
   * Use getT() instead
   *
   * @return
   */
  @Deprecated
  public Optional<T> getReturnedType() {
    return t;
  }

  public Optional<T> getT() {
    return getReturnedType();
  }

}
