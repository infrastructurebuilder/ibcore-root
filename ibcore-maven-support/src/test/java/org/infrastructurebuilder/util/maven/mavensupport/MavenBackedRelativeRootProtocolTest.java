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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MavenBackedRelativeRootProtocolTest {

  public final static TestingPathSupplier tps = new TestingPathSupplier();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    tps.finalize();
  }

  MavenProject project;
  MavenBackedRelativeRootSupplier p;
  MavenBackedTempRelativeRootSupplier t;
  Path target;

  @BeforeEach
  void setUp() throws Exception {
    target = tps.get();
    project = new MavenProject();
    Build b = new Build();
    b.setOutputDirectory(target.toString());
    project.setBuild(b);
    MavenProjectSupplier p2 = new MavenProjectSupplier(project);
    p = new MavenBackedRelativeRootSupplier(p2);
    t = new MavenBackedTempRelativeRootSupplier(p2);
  }

  @Test
  void test() {
    RelativeRoot rr = p.getRelativeRoot().get();
    assertTrue(rr.isPath());
    assertEquals(target, rr.getPath().get());
    RelativeRoot rr2 = t.getRelativeRoot().get();
    assertTrue(rr2.isPath());
    assertEquals(target, rr2.getPath().get().getParent());
    assertTrue(Files.isDirectory(rr2.getPath().get()));
  }

}
