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
package org.infrastructurebuilder.pathref.urifactory;

import static org.infrastructurebuilder.pathref.urifactory.ZipFilePathRefProducer.NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Set;

import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefFactory;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.urifactory.ZipFilePathRefProducer;
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
  private ZipFilePathRefProducer h;
  private Path tp;

  @BeforeEach
  void setUp() throws Exception {
    tps = new TestingPathSupplier();
    tp = tps.get();
    h = new ZipFilePathRefProducer();
    rrp = new PathRefFactory(Set.of(this.h));
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testRR() throws MalformedURLException {
    var q = rrp.get(NAME, tp.toUri().toURL());
    assertNotNull(q);
  }

  @Test
  void testNoReset() throws MalformedURLException {
  }

  @Test
  void testWithPath() throws MalformedURLException {
  }

}
