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
package org.infrastructurebuilder.pathref.basicpathproperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.util.Set;

import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefFactory;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.basicpathproperties.BasicPathPropertiesPathRefSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RelativeRootTest {

  private static TestingPathSupplier tps;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    tps = new TestingPathSupplier();
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    tps.finalize();
  }

  private PathRefFactory rrp;
  private BasicPathPropertiesPathRefSupplier h;
  private Path p;

  @BeforeEach
  void setUp() throws Exception {
    tps = new TestingPathSupplier();
    p = tps.get();
    h = new BasicPathPropertiesPathRefSupplier();
    System.setProperty(h.getPropertyName(), p.toString());
    rrp = new PathRefFactory(Set.of(this.h));
  }

  @AfterEach
  void tearDown() throws Exception {
    tps.finalize();
  }

  @Test
  void testUserHome() {
    assertNotNull(h.getLog());
    Path root = tps.getRoot();
    PathRef t = rrp.get(h.getName()).get();
    assertEquals(root, t.getPath().get().getParent());
  }

}