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
package org.infrastructurebuilder.util.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.infrastructurebuilder.util.core.Weighted;
import org.infrastructurebuilder.util.core.WeightedComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WeightedComparatorTest {

  private Weighted w1, w2;

  @BeforeEach
  public void setUp() throws Exception {
    w1 = new Weighted() {
    };
    w2 = new Weighted() {
      @Override
      public Integer getWeight() {
        return 2;
      }
    };
  }


  @Test
  public void testCompare() {
    WeightedComparator c = new WeightedComparator();
    assertEquals(0, c.compare(w1, w1));
    assertTrue(c.compare(w1, w2) < 0);
  }

}
