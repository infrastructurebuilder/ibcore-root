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
package org.infrastructurebuilder.util.artifacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

public class IdentifiedAndWeightedComparatorTest {

  private IdentifiedAndWeighted i1, i2, i3;
  private Comparator<IdentifiedAndWeighted> c;
  private IdentifiedAndWeighted i4;

  @Before
  public void setUp() throws Exception {
    i1 = new IC2("A", 1);
    i2 = new IC2("B", 1);
    i3 = new IC2("A", 2);
    i4 = new IC3();
    c = IdentifiedAndWeighted.comparator();
  }

  @Test
  public void testCompare() {
    assertEquals(0, c.compare(i1, i1));
    assertEquals(0, c.compare(i2, i2));
    assertTrue(c.compare(i1, i2) < 0);
    assertTrue(c.compare(i2, i1) > 0);
    assertTrue(c.compare(i1, i3) < 0);
    assertTrue(c.compare(i3, i1) > 0);
    assertEquals(Integer.valueOf(2), i3.getWeight());
    assertEquals("A", i3.getId());
    assertEquals(Integer.valueOf(0),i4.getWeight());
    assertNotNull(i4.getId());

  }

  public final static class IC2 implements IdentifiedAndWeighted {

    private final String id;
    private final Integer weight;

    public IC2(String id, int weight) {
      this.id = id;
      this.weight = weight;
    }

    @Override
    public String getId() {
      return this.id;
    }

    @Override
    public Integer getWeight() {
      return this.weight;
    }

  }

  public final static class IC3 implements IdentifiedAndWeighted {

  }
}
