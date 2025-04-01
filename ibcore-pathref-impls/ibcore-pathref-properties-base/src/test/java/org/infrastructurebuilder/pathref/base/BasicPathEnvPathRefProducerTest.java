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
package org.infrastructurebuilder.pathref.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.fs.PathRefFactory;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BasicPathEnvPathRefProducerTest {

  private static final String HOME = "HOME";
  private final static Logger log = LoggerFactory.getLogger(BasicPathEnvPathRefProducerTest.class);
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
  private BasicPathEnvPathRefProducer h;

  @BeforeEach
  void setUp() throws Exception {
    tps = new TestingPathSupplier();
    h = new BasicPathEnvPathRefProducer(HOME) {
      @Override
      public Optional<String> getProperty() {
        return super.getProperty().map(Paths::get).map(Path::toUri).map(URI::toString);
      }
    }; // Maybe doesn't test on windows
    rrp = new PathRefFactory(Set.of(this.h));
  }

  @AfterEach
  void tearDown() throws Exception {
    tps.finalize();
  }

  @Test
  void testBasicConstruct() {
    assertNotNull(new BasicPathEnvPathRefProducer());
  }

  @Test
  void testClasspath() throws IOException {
     PathRefFileSystem home = rrp.getPathRef(HOME);
    assertNotNull(home);
    Path home2 = home.getRoot().toRealPath();
    assertTrue(home2.isAbsolute());
    assertTrue(Files.isDirectory(home.getRoot()));
    assertEquals(HOME, h.getName());
  }

}
