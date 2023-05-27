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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractUUIdentifiedAndTimestampedTest {

  private AbstractUUIdentifiedAndTimestamped w1, w2, s1, s2, s3;

  @BeforeEach
  public void setUp() throws Exception {
    var sameId = UUID.randomUUID();
    var sameInstant = Instant.now();
    try {
      Thread.sleep(4L);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    var nextInstant = Instant.now();
    w1 = new AbstractUUIdentifiedAndTimestamped(sameInstant) {
    };
    w2 = new AbstractUUIdentifiedAndTimestamped(nextInstant) {
    };
    s1 = new AbstractUUIdentifiedAndTimestamped(sameId, sameInstant) {
    };

    s2 = new AbstractUUIdentifiedAndTimestamped(sameId, sameInstant) {
    };

    s3 = new AbstractUUIdentifiedAndTimestamped(sameInstant) {
    };
  }

  @Test
  public void testCompare() {
    var c = UUIdentifiedAndTimestamped.comparator;
    assertEquals(0, c.compare(w1, w1));
    assertTrue(c.compare(w2, w1) < 0);
    assertEquals(0, c.compare(s1, s2));
  }

  @Test
  public void testCompareSameTime() {
    var c = UUIdentifiedAndTimestamped.comparator;
    assertFalse(s2.compareTo(s3) == 0);
  }

}
