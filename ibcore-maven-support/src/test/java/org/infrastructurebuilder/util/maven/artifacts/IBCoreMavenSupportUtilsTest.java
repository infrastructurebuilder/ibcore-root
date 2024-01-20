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
package org.infrastructurebuilder.util.maven.artifacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.infrastructurebuilder.util.versions.DefaultGAVBasic;
import org.infrastructurebuilder.util.versions.GAVBasic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IBCoreMavenSupportUtilsTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testGav2Artifact() {
    GAVBasic g = new DefaultGAVBasic("a:b:1.0.0");
    ArtifactHandler a = new DefaultArtifactHandler();
    Optional<Artifact> b = IBCoreMavenSupportUtils.gav2Artifact.apply(g, null);
    Optional<Artifact> c = IBCoreMavenSupportUtils.gav2Artifact.apply(g, a);

    assertFalse(b.isPresent());
    assertTrue(c.isPresent());
    Artifact d = c.get();
    assertEquals(d.getArtifactHandler(), a);
    assertEquals(d.getGroupId(), "a");
    assertEquals(d.getVersion(), "1.0.0");
    assertEquals(GAVBasic.BASIC_PACKAGING, d.getType());
  }

}
