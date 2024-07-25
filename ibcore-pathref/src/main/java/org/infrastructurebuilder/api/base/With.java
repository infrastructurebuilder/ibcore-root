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
package org.infrastructurebuilder.api.base;

import java.util.Optional;

import javax.annotation.Nullable;

public interface With<T, R> {
  /**
   * Returns an optional item if the object supplied (or null) can produce an instance of that object
   *
   * @param t An object (or null) that is passed into the call to configure or otherwise be used to provide the
   *          resulting object
   * @return an optional instance of R
   */
  Optional<R> with(@Nullable Object t);

  /**
   * This is the class that the object supplied in the with call above is to be supplied. By convention, it should be
   * Object.class if there is no specific object type to supply
   *
   * @return
   */
  Class<? extends T> withClass();
}
