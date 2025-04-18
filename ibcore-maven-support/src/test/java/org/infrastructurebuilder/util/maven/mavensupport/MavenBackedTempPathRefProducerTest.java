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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.pathref.IBChecksumUtils;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MavenBackedTempPathRefProducerTest {

  private static final String XTXT = "X.txt";
  public final static TestingPathSupplier tps = new TestingPathSupplier();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    tps.finalize();
  }

  MavenProject project;
  MavenTargetPathRefProducer t;
  Path target;

  @BeforeEach
  void setUp() throws Exception {
    var classes = tps.getClasses();
    target = classes.getParent();

    var xfile = target.resolve(XTXT);
    IBChecksumUtils.copy(tps.getTestClasses().resolve(XTXT), xfile);
    project = new MavenProject();
    Build b = new Build();

    b.setOutputDirectory(classes.toString());
    b.setDirectory(target.toString());
    project.setBuild(b);
    project.setFile(xfile.toFile());

    MavenProjectSupplier p2 = new MavenProjectSupplier(project);
    t = new MavenTargetPathRefProducer(p2);
  }

  @Test
  void test() {
    assertEquals(MavenTargetPathRefProducer.MAVEN_TARGET, t.getName());
    assertNotNull(t.getLog());
    PathRefFileSystem rr = t.with(null);
    assertNotNull(rr);
//    assertTrue(rr.isPath());
//    assertEquals(target, rr.getPath().get());
//    PathRef rr2 = t.with(null).get();
//    assertTrue(rr2.isPath());
//    assertEquals(target, rr2.getPath().get().getParent());
//    assertTrue(Files.isDirectory(rr2.getPath().get()));
  }

}
