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

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AbstractTSupplierResponsiveFactoryBuilderTest {
  private final static Logger log = LoggerFactory.getLogger(AbstractTSupplierResponsiveFactoryBuilder.class);

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private Map<String, String> map;
  private FakeTSupplierResponsiveFactoryBuilder fake;

  @BeforeEach
  void setUp() throws Exception {
    this.map = Map.of("X", "Y", "Z", "A", "XX", "YY");

    this.fake = (FakeTSupplierResponsiveFactoryBuilder) new FakeTSupplierResponsiveFactoryBuilder(map).withHint("fake")
        .withLogger(log).withConfig("X");
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void test() {
    assertEquals("fake", this.fake.getHint());
    assertTrue(this.fake.respondsTo("X"));
    assertFalse(this.fake.respondsTo("Y"));
    assertEquals(this.fake.get("X").get(), "Y");
  }

}
