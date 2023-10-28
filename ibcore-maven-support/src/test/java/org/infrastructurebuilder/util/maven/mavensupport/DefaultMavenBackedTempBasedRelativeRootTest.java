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
package org.infrastructurebuilder.util.maven.mavensupport;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultMavenBackedTempBasedRelativeRootTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private MavenBackedRelativeRoot mbrr;
  private Path path;

  @BeforeEach
  void setUp() throws Exception {
    path = Paths.get(".").toAbsolutePath();
    mbrr = new DefaultMavenBackedTempBasedRelativeRoot(() -> path);
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testGetName() {
    assertEquals(DefaultMavenBackedTempBasedRelativeRoot.NAME, mbrr.getName());
  }

  @Test
  void testGet() {
    assertEquals(path, mbrr.get().get().getPath().get().getParent()); // LOL!
  }

}
