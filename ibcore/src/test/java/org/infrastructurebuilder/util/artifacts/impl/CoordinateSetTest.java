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
package org.infrastructurebuilder.util.artifacts.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.infrastructurebuilder.util.artifacts.ArtifactServices;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CoordinateSetTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private String fullSig;

  DefaultGAV l, l1, l3, l4, lnotM;

  @Before
  public void setUp() throws Exception {
    l = new DefaultGAV("A", "B", "C", new DefaultIBVersion("1.0.0").getOriginalValue(), "E");
    lnotM = new DefaultGAV("A", "B", "C", new DefaultIBVersion("2.0.0").getOriginalValue(), "E");
    fullSig = "A:B:E:C:1.0.0";
    l3 = new DefaultGAV("A", "B", "C", new DefaultIBVersion("1.0.0").getOriginalValue(), "E");
    l1 = new DefaultGAV("A", "B", "C", new DefaultIBVersion("1.0.0").getOriginalValue(),
        ArtifactServices.BASIC_PACKAGING);
    l4 = DefaultGAV.copyFromSpec(l3);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCompareTo() {
    assertFalse("L != l1", l.compareTo(l1) == 0);
    assertTrue("L == l3", l.compareTo(l3) == 0);
    assertTrue("L3 == l", l3.compareTo(l) == 0);
  }

  public void testCoordinateSetStringStringStringStringString() {
    assertNotNull("Local setup didn't fail", l);
  }

  @Test
  public void testCopyFromSpecFalse() {
    assertFalse("L1 != l4", l1.equals(l4));
  }

  @Test
  public void testCopyFromSpecTrue() {
    assertEquals("L3 === l4", l3, l4);
  }

  @Test
  public void testEqualsObject() {
    assertEquals("l  equals l3", l, l3);
    assertNotEquals("L and l1", l, l1);
  }

  @Test
  public void testFromFullSignaturePath() {
    assertEquals("Full sig is as expect (" + fullSig + ")", fullSig, l.getDefaultSignaturePath());
  }

  @Test
  public void testGetArtifactId() {
    assertEquals("Art = B", "B", l.getArtifactId());
  }

  @Test
  public void testGetClassifier() {
    assertEquals("Classifier = C", "C", l.getClassifier().orElse(null));
  }

  @Test
  public void testgetExtension() {
    assertEquals("Packaging = E", "E", l.getExtension());
  }

  @Test
  public void testGetGroupId() {
    assertEquals("Group = A", "A", l.getGroupId());
  }

  @Test
  public void testGetType() {
    assertEquals("Type of null is " + ArtifactServices.BASIC_PACKAGING, ArtifactServices.BASIC_PACKAGING,
        l1.getExtension());
  }

  @Test
  public void testGetVersion() {
    assertEquals("IBVersion 1.0.0", "1.0.0", l.getVersion().get());
  }

  @Test
  public void testSetArtifactId() {
    l.setArtifactId("B");
    assertEquals("ArtifactId is B", "B", l.getArtifactId());
  }

  @Test
  public void testSetClassifier() {
    l.setClassifier("B");
    assertEquals("Classifier == B", "B", l.getClassifier().orElse(null));
    l.setClassifier(null);
    assertTrue("Classifier empty", !l.getClassifier().isPresent());
  }

  @Test
  public void testSetExtension() {
    l.setExtension("X");
    assertEquals("Package is X", "X", l.getExtension());
  }

  @Test
  public void testSetGroupId() {
    l.setGroupId("G");
    assertEquals("Group is G", "G", l.getGroupId());
  }

  @Test
  public void testSetNullExtension() {
    l.setExtension(null);
    assertEquals(DefaultGAV.BASIC_PACKAGING,l.getExtension());
  }

}
