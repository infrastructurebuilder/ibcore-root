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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RelativeRootTest {

  private static final String URLROOT = "https://someserver.com/somepath";
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

  private RelativeRootProvider rrp;
  private RelativeRoot prr, hrr;
  private Path tp;
  private Path ssPath;
  private Path r2;
  private String ss;
  private Path aPath;
  private URL ssURL;

  @BeforeEach
  void setUp() throws Exception {
    tp = tps.get();
    rrp = new RelativeRootProvider(Collections.emptySet());

    prr = RelativeRoot.from(tp);
    hrr = RelativeRoot.from(new URL(URLROOT));
    ss = UUID.randomUUID().toString();
    ssPath = Paths.get(ss);
    ssURL = new URL(URLROOT + "/" + ss);
    aPath = tp.resolve(ssPath).toAbsolutePath();
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testPathFrom() {
    assertTrue(prr.getPath().isPresent());
    assertFalse(hrr.getPath().isPresent());
  }

  @Test
  void testAbsolute() {
    Optional<Path> q = hrr.relativize(aPath);
    assertFalse(q.isPresent());
    Optional<Path> r = prr.relativize(aPath);
    assertTrue(r.isPresent());
    // Relativized paths are not the same as the original
    assertNotEquals(aPath, r.get());
    // Resolving an absolute path returns that path
    assertEquals(aPath, prr.resolve(aPath).get());
  }

  @Test
  void testURLStuff() {
    String v = hrr.relativize(ssURL);
    assertEquals(ss, v);
  }

}
