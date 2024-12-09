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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefFactory;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.urifactory.ZipFilePathRefProducer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZipFilePathRefTest {

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
  private Path zipfile;

  @BeforeEach
  void setUp() throws Exception {
    tps = new TestingPathSupplier();
    tp = tps.get();
    h = new ZipFilePathRefProducer();
    rrp = new PathRefFactory(Set.of(this.h));
   zipfile = tps.getTestClasses().resolve("X.zip");
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testRR() throws MalformedURLException {
    var q = rrp.get(NAME, tp.toUri().toURL().toExternalForm());
    assertNotNull(q);
  }

  @Test
  void testNoReset() throws MalformedURLException {
  }

  @Test
  void testWithPath() throws Exception {
    try (PathRef q = h.with(zipfile.toAbsolutePath().toString()).get()) {
      Optional<InputStream> r = q.getInputStreamFrom("X/Y/rick.jpg");
      assertTrue(r.isPresent());
      Checksum c = new Checksum(r.get());
      assertEquals("abc", c.toString());
    }
  }

}
