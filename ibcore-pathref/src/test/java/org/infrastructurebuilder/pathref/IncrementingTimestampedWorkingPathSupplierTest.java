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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IncrementingTimestampedWorkingPathSupplierTest {

  IncrementingTimestampedStringSupplier ids = new SingletonIncrementingTimestampedStringSupplier();
  WorkingPathSupplier wps;
  Path p;
  Path p2;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
    wps = new IncrementingDatedWorkingPathSupplier(ids);
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testIncrementingDatedWorkingPathSupplier() throws InterruptedException, IOException {
    p = wps.get(); // Exercised but not validated
    assertTrue(Files.isDirectory(p));
    Thread.sleep(500L);
    p2 = wps.get();
    assertTrue(Files.isDirectory(p2));
    assertNotEquals(p,p2);
    Files.delete(p);
    Files.delete(p2);
  }

}
