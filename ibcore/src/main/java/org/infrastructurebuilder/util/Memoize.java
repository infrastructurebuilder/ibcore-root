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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Memoize<T, U> {
  public static <T, U> Function<T, U> memoize(final Function<T, U> function) {
    return new Memoize<T, U>().doMemoize(function);
  }

  private final Map<T, U> cache = new ConcurrentHashMap<>();

  private Memoize() {
  }

  private Function<T, U> doMemoize(final Function<T, U> function) {
    return input -> cache.computeIfAbsent(input, function::apply);
  }
}
