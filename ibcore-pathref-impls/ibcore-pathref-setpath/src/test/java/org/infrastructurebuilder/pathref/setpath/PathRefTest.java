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
package org.infrastructurebuilder.pathref.setpath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefFactory;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.setpath.SetValuePathRefSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathRefTest {

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
  private SetValuePathRefSupplier h;
  private Path tp;

  @BeforeEach
  void setUp() throws Exception {
    tps = new TestingPathSupplier();
    tp = tps.get();
    h = new SetValuePathRefSupplier().withPath(tp);
    rrp = new PathRefFactory(Set.of(this.h));
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testRR() {
    PathRef rr = rrp.get(SetValuePathRefSupplier.NAME).get();
    assertEquals(tp, rr.getPath().get());
  }

  @Test
  void testNoReset() {
    Path p2 = tps.get();
    SetValuePathRefSupplier s2 = new SetValuePathRefSupplier().withPath(tp);
    assertEquals(tp, s2.getProperty().map(Paths::get).get());
    s2 = s2.withPath(p2);
    assertNotEquals(p2, s2.getProperty().map(Paths::get).get());
  }

  @Test
  void testWithPath() {
    var v = new SetValuePathRefSupplier().withPath(tp);
    assertNotNull(v);
    Optional<Path> q = v.with(null).flatMap(PathRef::getPath);
    assertEquals(tp, q.get());
  }

}
