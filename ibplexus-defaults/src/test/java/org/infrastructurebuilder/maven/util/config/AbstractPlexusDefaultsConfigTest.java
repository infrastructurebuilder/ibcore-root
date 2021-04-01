/**
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
package org.infrastructurebuilder.maven.util.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

abstract public class AbstractPlexusDefaultsConfigTest {

  public static Map<String, String> m, n;
  public static MavenProject mp;
  public static Model mm;
  public static Properties properties;
  public final static TestingPathSupplier wps = new TestingPathSupplier();
  public static Path target;
  public static Path testClasses;

  public static MavenSession ms;
  public static MojoExecution me;
  private static Set<Artifact> artifacts;
  private static Artifact art1;
  public static File destination;
  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    target = wps.getRoot();
    testClasses = wps.getTestClasses();
    ArtifactHandler artifactHandler = new DefaultArtifactHandler();
    art1 = new DefaultArtifact("F", "A", "10.0", "runtime", "jar", "class", artifactHandler);
    destination = wps.getTestClasses().resolve("X.zip").toFile();
    art1.setFile(destination);
    artifacts = new HashSet<>();
    artifacts.add(art1);
    m = new HashMap<>();
    n = new HashMap<>();
    m.put("X", "Y");
    m.put("A", "B");
    n.put("X", "Z");
    n.put("A", "CC");
    n.put("Q", "M");

    properties = new Properties();
    properties.setProperty("a", "b");
    properties.setProperty("c", "d");
    properties.setProperty("user.home", "override");
    mp = new MavenProject();
    final Build b = new Build();
    b.setDirectory(target.toString());
    mm = new Model();
    mm.setProperties(properties);
    mp.setModel(mm);
    mp.setBuild(b);
    mp.setPackaging("jar");
    mp.setGroupId("G");
    mp.setArtifactId("A");
    mp.setVersion("1.0.0");
    mp.setArtifacts(artifacts);

    ms = mock(MavenSession.class);
    me = mock(MojoExecution.class);
    when(ms.getGoals()).thenReturn(Arrays.asList("A", "B"));
    when(ms.getStartTime()).thenReturn(new Date());
    when(me.getExecutionId()).thenReturn("A");
    when(me.getGoal()).thenReturn("B");
    when(me.getLifecyclePhase()).thenReturn("compile");

  }

  public MavenConfigMapSupplier getCms() {
    return new MavenConfigMapSupplier(mp, ms, me);
  }

  @Disabled
  @Test
  public void testSetMavenProject() {
    final ConfigMap map = getCms().get();
    assertTrue(map.size() >= properties.size());
    for (final String p : properties.stringPropertyNames()) {
      final String val = map.getString(p);
      final String pVal = properties.getProperty(p);
      assertEquals(val, pVal);
    }
    assertTrue(map.containsKey("user.home"));
    assertTrue(map.containsKey("PATH"));
  }

}
