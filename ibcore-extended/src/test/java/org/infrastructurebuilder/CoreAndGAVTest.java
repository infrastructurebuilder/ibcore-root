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
package org.infrastructurebuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.eclipse.aether.graph.Dependency;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.GAVServices;
import org.infrastructurebuilder.util.artifacts.IBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

@SuppressWarnings("unused")
public class CoreAndGAVTest {

  private static class GAVTst {

  }

  private GAV g;
  private GAV g2NullVersion;

  private GAV g3Classified;

  @Before
  public void before() {
    g = new DefaultGAV("org.infrastructurebuilder", "b", "c");

    g2NullVersion = new DefaultGAV(g.getGroupId(), g.getArtifactId(), null, (String) null, "jar");
    g3Classified = new DefaultGAV(g.getGroupId(), g.getArtifactId(), "yes", "1.0.0", "jar");
  }

  @Test
  public void testAPIVersion() {
    assertEquals("1.0", IBVersion.apiVersion(g3Classified).get().toString());
  }

  @Test
  public void testAsChecksum() {
    assertEquals("Checksum length should be " + 128, 128, g.asChecksum().toString().length());
  }

  @Test
  public void testAsDependency() {
    final Dependency d = GAVServices.asDependency(g, GAV.RUNTIME_SCOPE);
    assertNotNull("Got dependency", d);
    assertEquals("Scope", d.getScope(), GAV.RUNTIME_SCOPE);
  }

  @Test
  public void testAsJSON() {
    final JSONObject j = g.asJSON();
    final String x = "{\"groupId\":" + g.getGroupId() + ",\"artifactId\":\"" + g.getArtifactId()
        + "\",\"extension\":\"jar\",\"version\":\"" + g.getVersion().get() + "\"}";
    JSONAssert.assertEquals(x, g.asJSON(), true);
  }

  @Test
  public void testCopy() {
    final DefaultGAV copy = (DefaultGAV) g3Classified.copy();
    assertEquals(g3Classified.asMavenDependencyGet().get(), copy.asMavenDependencyGet().get());
  }

  @Test
  public void testGetClassifiedFullSignaturePath() {
    final String s = g3Classified.getDefaultSignaturePath();
    final String expected = g.getGroupId() + ":" + g.getArtifactId() + ":jar:yes" + ":"
        + new DefaultIBVersion("1.0.0").toString();
    assertEquals("Expected " + expected, expected, s);
  }

  @Test
  public void testGetFullSignaturePath() {
    final String s = g.getDefaultSignaturePath();
    final String expected = g.getGroupId() + ":" + g.getArtifactId() + ":jar" + ":" + g.getVersion().get();
    assertEquals("Expected " + expected, expected, s);
  }

  @Test(expected = IBException.class)
  public void testGetFullSignaturePathFail() {
    final String s = g2NullVersion.getDefaultSignaturePath();
    final String expected = g.getGroupId() + ":" + g.getArtifactId() + ":jar" + ":" + g.getVersion();
    assertEquals("Expected " + expected, expected, s);
  }

  @Test
  public void testGetStringNullVersion() {
    final Optional<String> v = g2NullVersion.getVersion();
    assertFalse("Got a string", v.isPresent());
  }

  @Test
  public void testGetStringVersion() {
    final Optional<String> v = g.getVersion();
    assertTrue("Got a string", v.isPresent());
    assertEquals("String is same", "c", v.get());
  }

  @Test
  public void testGetTheseCoordinates() {
    assertNotNull("Get a GAV", g);
    assertEquals("g.getGroupId() is still org.infrastructurebuilder", "org.infrastructurebuilder", g.getGroupId());
    assertNotNull("Has a g.getVersion()", g.getVersion());
    assertNotNull("Has an g.getArtifactId()", g.getArtifactId());
    assertNotNull("Has an optional classifier", g.getClassifier());
    assertEquals("Default type is jar", "jar", g.getExtension());
    assertEquals("Default pacagomg is also jar", "jar", g.getExtension());
  }

}
