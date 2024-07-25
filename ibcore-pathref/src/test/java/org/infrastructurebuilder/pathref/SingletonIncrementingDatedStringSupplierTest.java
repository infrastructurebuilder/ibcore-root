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
package org.infrastructurebuilder.pathref;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.infrastructurebuilder.api.base.IdentifierSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SingletonIncrementingDatedStringSupplierTest {

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private IdentifierSupplier s;

  @BeforeEach
  public void setUp() throws Exception {
    s = new SingletonIncrementingDatedStringSupplier();
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testGet() {
    String x = s.get();
    assertTrue(x.endsWith("001"));
    assertTrue(s.get().endsWith("002"));
  }

}
