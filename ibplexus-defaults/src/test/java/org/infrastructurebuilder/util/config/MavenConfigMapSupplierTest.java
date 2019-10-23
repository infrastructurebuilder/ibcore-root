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
package org.infrastructurebuilder.util.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class MavenConfigMapSupplierTest {

  public static Map<String, String> m, n;
  public static MavenProject mp;
  public static MavenSession ms;
  public static MojoExecution me;
  public static Model mm;
  public static Properties properties;
  public static Path target;
  public static Path testClasses;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    target = Paths.get(Optional.ofNullable(System.getProperty("target")).orElse("./target")).toRealPath();
    testClasses = target.resolve("test-classes");
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

    ms = null;
    me = null;
  }


  public MavenConfigMapSupplier getCms() {
    return new MavenConfigMapSupplier(mp, ms, me);
  }

  @Ignore
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
