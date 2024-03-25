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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AbstractTSupplierFactoryTest {

  private static final String VAL = "VAL";
  private static final String VAL5 = "VAL5";
  private static final String VAL4 = "VAL4";
  private static final String VAL3 = "VAL3";
  private static final String DESC = "desc";
  private static final String VAL2 = "VAL2";
  private static final int WEIGHT = -1;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private AbstractTSupplierFactory<String, String> tsf;

  @BeforeEach
  void setUp() throws Exception {
    this.tsf = new AbstractTSupplierFactory<String, String>() {
      private final Logger log = LoggerFactory.getLogger(AbstractTSupplierFactoryTest.class);

      @Override
      public Logger getLog() {
        return log;
      }

      @Override
      public Optional<TSupplier<String>> build() {
        TSupplier<String> ts = new TSupplier<String>();
        ts.setT(VAL);
        return Optional.of(ts);
      }
    }
        //
        .withWeight(WEIGHT) // weight
        .withDescription(DESC) // desc
        .withDisplayName(VAL3) // displayName
        .withHint(VAL4) // hint
        .withName(VAL5).withConfig(VAL2) // confiug

    ;
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testBuild() {
    TSupplier<String> v1 = this.tsf.get();
    assertNotNull(v1);
    assertEquals(VAL, v1.get());
    assertEquals(this.tsf.getConfig(), VAL2);
    assertEquals(this.tsf.getWeight(), WEIGHT);
    assertEquals(this.tsf.getDescription().get(), DESC);
    assertEquals(this.tsf.getDisplayName().get(), VAL3);
    assertEquals(this.tsf.getHint(), VAL4);
    assertEquals(this.tsf.getName(), VAL5);
  }

}
