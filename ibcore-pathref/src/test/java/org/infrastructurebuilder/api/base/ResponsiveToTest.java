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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponsiveToTest {

  private static ResponsiveToString rt;
  private static ResponsiveTo<String> ru;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    rt = new ResponsiveToString() {

    };
    ru = new ResponsiveTo<String>() {
      @Override
      public int respondsTo(String input) {
        return input == null ? 100 : -1;
      }
    };
  }

  @BeforeEach
  void setUp() throws Exception {
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testRespondsTo() {
    assertEquals(-1, rt.respondsTo("a"));
    assertEquals(-1, rt.respondsTo("b"));
    assertEquals(-1, rt.respondsTo(null));
    assertEquals(100, ru.respondsTo(null));
    assertEquals(-1, ru.respondsTo("a"));
  }

}
