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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NameDescribedTest {

  private static final String DESC2 = "DESC";
  private static final String DISP2 = "DISP";
  private static final String NAME2 = "NAME";
  private NameDescribed n, m;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
    n = new NameDescribed() {

      @Override
      public String getName() {
        return NAME2;
      }

      @Override
      public Optional<String> getDisplayName() {
        return Optional.of(DISP2);
      }
    };
    m = new NameDescribed() {

      @Override
      public String getName() {
        return NAME2;
      }

      @Override
      public Optional<String> getDescription() {
        return Optional.of(DESC2);
      }

      @Override
      public Optional<String> getDisplayName() {
        return Optional.of(DISP2);
      }
    };

  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testGetName() {
    assertEquals(NAME2, n.getName());
    assertEquals(NAME2, m.getName());
  }

  @Test
  void testGetDisplayName() {
    assertFalse(n.getDisplayName().isEmpty());
    assertEquals(DISP2, n.getDisplayName().get());
    assertEquals(DISP2, m.getDisplayName().get());
  }

  @Test
  void testGetDescription() {
    assertEquals(DISP2, n.getDescription().get());
    assertEquals(DESC2, m.getDescription().get());
    assertTrue(new NameDescribed() {
      @Override
      public String getName() {
        // TODO Auto-generated method stub
        return null;
      }
    }.getDisplayName().isEmpty());
  }

}
