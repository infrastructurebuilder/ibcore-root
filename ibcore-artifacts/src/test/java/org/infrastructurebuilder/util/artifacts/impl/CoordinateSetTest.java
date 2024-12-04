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
package org.infrastructurebuilder.util.artifacts.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.infrastructurebuilder.util.artifacts.ArtifactServices;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.DefaultIBVersion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CoordinateSetTest {

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private String fullSig;

  DefaultGAV l, l1, l3, l4, lnotM;

  @BeforeEach
  public void setUp() throws Exception {
    l = new DefaultGAV("A", "B", "C", new DefaultIBVersion("1.0.0").getOriginalValue(), "E");
    lnotM = new DefaultGAV("A", "B", "C", new DefaultIBVersion("2.0.0").getOriginalValue(), "E");
    fullSig = "A:B:E:C:1.0.0";
    l3 = new DefaultGAV("A", "B", "C", new DefaultIBVersion("1.0.0").getOriginalValue(), "E");
    l1 = new DefaultGAV("A", "B", "C", new DefaultIBVersion("1.0.0").getOriginalValue(),
        ArtifactServices.BASIC_PACKAGING);
    l4 = DefaultGAV.copyFromSpec(l3);
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testCompareTo() {
    assertFalse(l.compareTo(l1) == 0);
    assertTrue(l.compareTo(l3) == 0);
    assertTrue(l3.compareTo(l) == 0);
  }

  public void testCoordinateSetStringStringStringStringString() {
    assertNotNull(l);
  }

  @Test
  public void testCopyFromSpecFalse() {
    assertFalse(l1.equals(l4));
  }

  @Test
  public void testCopyFromSpecTrue() {
    assertEquals(l3, l4);
  }

  @Test
  public void testEqualsObject() {
    assertEquals(l, l3);
    assertNotEquals(l, l1);
  }

  @Test
  public void testFromFullSignaturePath() {
    assertEquals(fullSig, l.getDefaultSignaturePath(), "Full sig is as expect (" + fullSig + ")");
  }

  @Test
  public void testGetArtifactId() {
    assertEquals("B", l.getArtifactId());
  }

  @Test
  public void testGetClassifier() {
    assertEquals("C", l.getClassifier().orElse(null));
  }

  @Test
  public void testgetExtension() {
    assertEquals("E", l.getExtension().get());
  }

  @Test
  public void testGetGroupId() {
    assertEquals("A", l.getGroupId());
  }

  @Test
  public void testGetType() {
    assertEquals(ArtifactServices.BASIC_PACKAGING, l1.getExtension().get());
  }

  @Test
  public void testGetVersion() {
    assertEquals("1.0.0", l.getVersion().get());
  }

  @Test
  public void testSetArtifactId() {
    l.setArtifactId("B");
    assertEquals("B", l.getArtifactId());
  }

  @Test
  public void testSetClassifier() {
    l.setClassifier("B");
    assertEquals("B", l.getClassifier().orElse(null));
    l.setClassifier(null);
    assertTrue(!l.getClassifier().isPresent(), "Classifier empty");
  }

  @Test
  public void testSetExtension() {
    l.setExtension("X");
    assertEquals("X", l.getExtension().get());
  }

  @Test
  public void testSetGroupId() {
    l.setGroupId("G");
    assertEquals("G", l.getGroupId());
  }

  @Test
  public void testSetNullExtension() {
    l.setExtension(null);
    assertEquals(DefaultGAV.BASIC_PACKAGING, l.getExtension().get());
  }

}
