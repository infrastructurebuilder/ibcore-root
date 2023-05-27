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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LastUpdatedTest {

  private final static Logger log = LoggerFactory.getLogger(LastUpdatedTest.class);

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private LastUpdated lu1, lu2;
  private Instant now, then;

  @BeforeEach
  void setUp() throws Exception {
    now = Instant.now();
    Thread.sleep(10);
    then = Instant.now();
    lu1 = new FakeLastUpdated(now);
    lu2 = new FakeLastUpdated(then);
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testGetLastUpdated() {
    assertEquals(now, lu1.getLastUpdated());
    assertEquals(LastUpdated.lastupdatedComparator.compare(lu1, lu1), 0);
    assertTrue(LastUpdated.lastupdatedComparator.compare(lu2, lu1) > 0);
    assertTrue(LastUpdated.lastupdatedComparator.compare(lu2, null) > 0);
    assertFalse(LastUpdated.lastupdatedComparator.compare(null, lu1) >= 0);
  }
}
