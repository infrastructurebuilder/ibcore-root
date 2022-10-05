/*
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
 */
package org.infrastructurebuilder.util.core;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.*;

class RelativeRootTest {

  private final static Logger log = LoggerFactory.getLogger(RelativeRootTest.class);
  private static TestingPathSupplier tps;


  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    tps = new TestingPathSupplier();
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    tps.finalize();
  }

  private RelativeRoot rr;
  private Path tp;
  private Path t1;
  private Path r2;
  private String s;

  @BeforeEach
  void setUp() throws Exception {
    tp = tps.get();
    rr = new RelativeRoot(tp);
    s = UUID.randomUUID().toString();
    t1 = Paths.get(s);
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testPathFrom() {
    r2 = RelativeRoot.pathFrom(Optional.of(rr),t1);
    assertEquals(r2, tp.resolve(s));
  }

  @Test
  void testRelativeRootPathString() {
  }

  @Test
  void testRelativeRootPathURL() {
  }

  @Test
  void testRelativeRootPath() {
  }

  @Test
  void testRelativeRootString() {
  }

  @Test
  void testRelativeRootURL() {
  }

  @Test
  void testGetPath() {
  }

  @Test
  void testGetUrl() {
  }

  @Test
  void testGetURLAsString() {
    RelativeRoot rt = new RelativeRoot(".");
    assertEquals(".", rt.getURLAsString().get());
  }

  @Test
  void testToString() {
  }

  @Test
  void testRelativizePath() {
  }

  @Test
  void testResolve() {
  }

  @Test
  void testRelativizeURL() {
  }

  @Test
  void testRelativizeString() {
  }
}
