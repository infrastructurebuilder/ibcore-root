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
package org.infrastructurebuilder.util.credentials.basic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import org.infrastructurebuilder.util.core.Memoize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultMemoizerTest {

  private Function<Integer, Integer> m;

  @BeforeEach
  public void setUp() throws Exception {
    final Function<Integer, Integer> f = this::func;
    m = Memoize.memoize(f);
  }

  @Test
  public void testMemoize() {
    Instant start = Instant.now();
    assertTrue(m.apply(1) == 2);
    assertTrue(Duration.between(start, Instant.now()).toMillis() > 1000);
    start = Instant.now();
    assertTrue(m.apply(1) == 2);
    assertTrue(Duration.between(start, Instant.now()).toMillis() < 10);

  }

  Integer func(final Integer i) {
    try {
      Thread.sleep(3000);
    } catch (final InterruptedException e) {
    }
    return i + 1;
  }

}
