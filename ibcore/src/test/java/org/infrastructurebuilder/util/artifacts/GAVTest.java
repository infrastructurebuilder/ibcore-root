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
package org.infrastructurebuilder.util.artifacts;

import static org.infrastructurebuilder.util.artifacts.GAV.BASIC_PACKAGING;
import static org.infrastructurebuilder.util.artifacts.GAV.SNAPSHOT_DESIGNATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class GAVTest {

  private static final String ACLASSIFIER = "a";
  private static final String COLON       = ":";
  private static final String SLASH       = "/";
  private static final String V_1_0_0     = "1.0.0";
  private static final String X           = "X";
  private static final String Y           = "y";
  private GAV                 gav;
  private DefaultGAV          gav2;
  private DefaultGAV          gav2NoC;
  private DefaultGAV          gav3;
  private DefaultGAV          gavNoC;
//  private DefaultGAV gavNoC2;
  private GAV                 gavNoV;
  private GAV                 gavs;
  private JSONObject          j;

  @BeforeEach
  public void before() {
    gav = new DefaultGAV(X, Y, ACLASSIFIER, V_1_0_0, BASIC_PACKAGING);
    gavs = new DefaultGAV(X, Y, ACLASSIFIER, V_1_0_0 + SNAPSHOT_DESIGNATOR, BASIC_PACKAGING);
    gavNoV = new DefaultGAV(X, Y, ACLASSIFIER, null, BASIC_PACKAGING);
    gavNoC = new DefaultGAV(X, Y, null, null, BASIC_PACKAGING);
    gav2 = new DefaultGAV(gav.asJSON());
//    gavNoC2 = new DefaultGAV(X, Y, "", null, BASIC_PACKAGING);
    gav2NoC = new DefaultGAV(gav.asJSON(), "");
    gav3 = DefaultGAV.copyFromSpec(gav2);
    j = new JSONObject("{\n" + "  \"extension\": \"" + BASIC_PACKAGING + "\",\n" + "  \"groupId\": \"" + X + "\",\n"
        + "  \"classifier\": \"" + ACLASSIFIER + "\",\n" + "  \"artifactId\": \"" + Y + "\",\n" + "  \"version\": \""
        + V_1_0_0 + "\"\n" + "}");

  }

  @Test
  public void testAPIVersion() {
    assertEquals("1.0", gav.getAPIVersion().get());
  }

  @Test
  public void compareToNull() throws NullPointerException {
    final DefaultGAV v = new DefaultGAV(X, Y, ACLASSIFIER, V_1_0_0, BASIC_PACKAGING);
    Assertions.assertThrows(NullPointerException.class, () -> v.compareTo(null));
  }

  @Test
  public void testAsChecksum() {
    assertEquals(
        "845f502e6322cb379272b14f5edafead134a4316be5b25880242a120c99463fd1a8da6a85073b437a0b4ee14c8078bed2bbd2dd21ef89fe97e79714e03a8aa6f",
        gav.asChecksum().toString());
  }

  @Test
  public void testAsJSON() {
    assertEquals(DefaultGAV.class.getCanonicalName(), gav2.getResourceType());

    final JSONObject g = new JSONObject(gav.toString());
    JSONAssert.assertEquals(j, g, true);

    final JSONObject x        = gavNoC.asJSON();
    final JSONObject expected = new JSONObject(
        "{\n" + "    \"extension\": \"jar\",\n" + "    \"groupId\": \"X\",\n" + "    \"artifactId\": \"y\"\n" + "}");
    JSONAssert.assertEquals(expected, x, true);
  }

  @Test
  public void testAsMavenDependencyGet() {
    assertEquals("X:y:1.0.0:jar:a", gav.asMavenDependencyGet().get());
    assertFalse(gavNoV.asMavenDependencyGet().isPresent());
    assertEquals("X:y:1.0.0-SNAPSHOT:jar:a", gavs.asMavenDependencyGet().get());
    assertEquals("X:y:1.0.0:jar", gav2NoC.asMavenDependencyGet().get());

  }

  @Test
  public void testAsModelId() {
    final Optional<String> a = gav.asModelId();
    assertTrue(a.isPresent());
    assertEquals("X:y:1.0.0", a.get());
    assertFalse(gavNoV.asModelId().isPresent());
  }

  @Test
  public void testAsRange() {
    assertEquals("[" + V_1_0_0 + "]", gav.asRange());
    assertEquals("[0.0.0,99999.99999.99999]", gavNoV.asRange());
  }

  @Test
  public void testComparables() {
    final DefaultGAV v = new DefaultGAV(X, Y, ACLASSIFIER, V_1_0_0, BASIC_PACKAGING);
    assertEquals(0, v.compareTo(v));
    assertEquals(0, v.compareTo(new DefaultGAV(X, Y, ACLASSIFIER, V_1_0_0, BASIC_PACKAGING)));
    assertTrue(v.compareTo(new DefaultGAV(X, Y, ACLASSIFIER, V_1_0_0, "k")) < 0);
  }

  @Test
  public void testcompareVersion() {
    final DefaultGAV v = new DefaultGAV(X, Y, ACLASSIFIER, null, BASIC_PACKAGING);
    assertEquals(0, v.compareVersion(v));
    final DefaultGAV q = new DefaultGAV(X, Y, ACLASSIFIER, "1.1.0", BASIC_PACKAGING);
    assertEquals(-1, v.compareVersion(q));

  }

  @Test
  public void testConstruction() {
    assertNotNull(new DefaultGAV("ACLASSIFIER", "B", "1.0"));
    assertNotNull(new DefaultGAV("ACLASSIFIER", "B", "1.0"));

  }

  @Test
  public void testEquals() {
    final DefaultGAV v = new DefaultGAV(X, Y, ACLASSIFIER, V_1_0_0, BASIC_PACKAGING);
    assertEquals(gav, gav);
    assertEquals(gav, v);
    assertNotEquals(gav, null);
    assertNotEquals(gav, "X");
    assertNotEquals(gav, new DefaultGAV(X, Y, ACLASSIFIER, V_1_0_0, "something"));
    assertNotEquals(gav, new DefaultGAV("xxx", Y, ACLASSIFIER, V_1_0_0, BASIC_PACKAGING));
    assertNotEquals(gav, new DefaultGAV(X, "whyme", ACLASSIFIER, V_1_0_0, BASIC_PACKAGING));
    assertNotEquals(gav, new DefaultGAV(X, Y, "awful", V_1_0_0, BASIC_PACKAGING));
    assertNotEquals(gav, new DefaultGAV(X, Y, ACLASSIFIER, "999", BASIC_PACKAGING));
    final DefaultGAV gcc = new DefaultGAV(X, Y, V_1_0_0, BASIC_PACKAGING);
    assertNotEquals(gav, gcc);
    assertNotEquals(gcc, gav);

    final DefaultGAV gc2 = new DefaultGAV(X, Y, ACLASSIFIER, null, BASIC_PACKAGING);
    assertEquals(gc2, new DefaultGAV(X, Y, ACLASSIFIER, null, BASIC_PACKAGING));
    assertNotEquals(gc2, gav);
    assertNotEquals(gav, gc2);
    final DefaultGAV gc3 = new DefaultGAV(X, Y, null, V_1_0_0, BASIC_PACKAGING);
    assertNotEquals(gav, gc3);
    assertNotEquals(gc3, gav);
  }

  @Test
  public void testFrom() {
    final DefaultGAV g = new DefaultGAV("a:b:1.0.0:jar:e");
    assertEquals("1.0.0", g.getVersion().get());
  }

  @Test
  public void testGetDefaultSignaturePath() {
    assertEquals(X + COLON + Y + COLON + BASIC_PACKAGING + COLON + ACLASSIFIER + COLON + V_1_0_0,
        gav.getDefaultSignaturePath());
    assertEquals(X + COLON + Y + COLON + V_1_0_0 + COLON + BASIC_PACKAGING + COLON + ACLASSIFIER,
        gav.asMavenDependencyGet().get());
    assertEquals("mvn:" + X + SLASH + Y + SLASH + V_1_0_0 + SLASH + BASIC_PACKAGING + SLASH + ACLASSIFIER,
        gav.asPaxUrl());
    assertEquals("mvn:" + X + SLASH + Y + SLASH + V_1_0_0 + SLASH + BASIC_PACKAGING + SLASH + ACLASSIFIER,
        gav2.asPaxUrl());
    assertEquals("mvn:" + X + SLASH + Y + SLASH + V_1_0_0 + SLASH + BASIC_PACKAGING + SLASH + ACLASSIFIER,
        gav3.asPaxUrl());

  }

  @Test
  public void testGetDefaultSignaturePathFail() throws IBException {
    Assertions.assertThrows(IBException.class,
        () -> assertEquals("mvn:" + X + SLASH + Y + SLASH + V_1_0_0 + SLASH + BASIC_PACKAGING + SLASH + ACLASSIFIER,
            gavNoV.getDefaultSignaturePath()));
  }

  @Test
  public void testGetPackaging() {
    assertEquals(gav.getExtension(), BASIC_PACKAGING);
  }

  @Test
  public void testGetStringVersion() {
    final String target = V_1_0_0;
    assertEquals(target, gav.getVersion().get());
  }

  @Test
  public void testHash() {
    assertEquals(193274601, gav.hashCode());
  }

  @Test
  public void testIsSnapshot() {
    assertFalse(gav.isSnapshot());
    assertFalse(gavNoV.isSnapshot());
    assertTrue(gavs.isSnapshot());
  }
}
