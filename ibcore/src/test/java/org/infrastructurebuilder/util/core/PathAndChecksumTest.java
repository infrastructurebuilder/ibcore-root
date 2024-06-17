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

import static org.infrastructurebuilder.pathref.IBChecksumUtils.copy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;

import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathAndChecksumTest {
  private static final String CSUM = "5cd814bd44716a73c2e380c443f573aa3f0a4aaf881f70810ec9c9552035433f6cca4c64161ce460e97db0089eefb1aad4d09e7151c330afc7a4caf528a6f475";
  private static final String B_XML = "b.xml";
  private final static TestingPathSupplier tps = new TestingPathSupplier();
  private final static Path wp = tps.get();
  private final static Path tRoot = tps.getTestClasses();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    tps.finalize();
  }

  private Path target;

  @BeforeEach
  void setUp() throws Exception {
    target = wp.resolve(B_XML);
    copy(tRoot.resolve(B_XML), target);
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testGetAttributes() {
    PathAndChecksum pandc = new DefaultPathAndChecksum(target);
    var checksum = pandc.asChecksum();
    assertEquals(CSUM, checksum.toString());
    var a = pandc.getAttributes().get();
    assertEquals(204L, a.size());
    assertEquals(204L, pandc.size().get());
  }

  @Test
  void testAsOptStream() throws IOException {
    PathAndChecksum pandc = new DefaultPathAndChecksum(target);
    assertNotNull(pandc.asOptStream());
  }

}
