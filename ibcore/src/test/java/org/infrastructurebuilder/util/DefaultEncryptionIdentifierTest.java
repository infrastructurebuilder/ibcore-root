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
package org.infrastructurebuilder.util;

import static org.infrastructurebuilder.IBConstants.ENCRYPTION_TYPE_NONE;
import static org.infrastructurebuilder.IBConstants.NO_OP;
import static org.infrastructurebuilder.IBConstants.PASSWORD_TYPE;
import static org.infrastructurebuilder.IBConstants.PGP_DS_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.infrastructurebuilder.IBConstants;
import org.joor.Reflect;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class DefaultEncryptionIdentifierTest {

  private final static Object getEncId(final Object a) {
    return Reflect.on(a).get("encId");
  }

  private DefaultEncryptionIdentifier d;
  private DefaultEncryptionIdentifier d10;
  private DefaultEncryptionIdentifier d11;
  private DefaultEncryptionIdentifier d12;
  private DefaultEncryptionIdentifier d13;
  private DefaultEncryptionIdentifier d2;
  private DefaultEncryptionIdentifier d3;
  private DefaultEncryptionIdentifier d99;
  private String x1;
  private String x5;
  private HashSet<String> xx;
  private String y1;

  private String y2;

  @Before
  public void setUp() throws Exception {
    d = new DefaultEncryptionIdentifier();
    xx = new HashSet<>(Arrays.asList("A", "B"));
    d2 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE, xx);
    d3 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE, new HashSet<>(Arrays.asList("A", "B")));
    d10 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE, new HashSet<>(Arrays.asList("A", "B", "C")));
    d11 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE, new HashSet<>(Arrays.asList("B", "C")));
    d12 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE, new HashSet<>(Arrays.asList("B")));

    d13 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE, new HashSet<>(Arrays.asList("B", "C")));

    x1 = "{\"id\" : \"" + NO_OP + "\"," + "\"Crypto-Encryption-Identifiers\":[],\"Crypto-Type\":\""
        + IBConstants.ENCRYPTION_TYPE_NONE + "\"}";
    x5 = "{\"id\" : \"x5\"," + "\"Crypto-Encryption-Identifiers\":[  ],\"Crypto-Type\":\""
        + IBConstants.PGP_DS_TYPE + "\"}";
    y1 = "{\"id\" : \"password\"," + "\"Crypto-Encryption-Identifiers\":[\"A\", \"B\"],\"Crypto-Type\":\""
        + IBConstants.PASSWORD_TYPE + "\"}";
    y2 = "{\"id\" : \"y2\"," + "\"Crypto-Encryption-Identifiers\":[\"A\", \"C\"],\"Crypto-Type\":\""
        + IBConstants.PGP_DS_TYPE + "\"}";

    d99 = new DefaultEncryptionIdentifier("d99", PGP_DS_TYPE, Arrays.asList("id1", "id2", "*id3"));
  }

  @Test
  public void testAbsolutelyMatch() {
    assertFalse("d3 -> null", d3.absolutelyMatch(null));
    assertFalse("d3 -> d10", d3.absolutelyMatch(d10));
    assertFalse("d10 -> d3", d10.absolutelyMatch(d3));
    assertTrue("d3 with identical", d3.absolutelyMatch(
        new DefaultEncryptionIdentifier("password", PASSWORD_TYPE, new HashSet<>(Arrays.asList("B", "A")))));
  }

  @Test
  public void testChecksums() {
    final String x = "15bb6c91c24ce569dce4349e824ab6bfbded20e77ecb691fbd2b885ec269f316ef9ddf4582409d6087bee9d918033f42a4e0226900357fcdf0ba33cef1983550";
    assertEquals("X", x, d.asChecksum().toString());
    final String y = "590dc445bfab7cbacf2e768ab61145eeb9416e538b62673140fe4837077209df7d348447670ffbc29248f51c089006ede1362150ac8df0a2d134662b9afcd4fe";
    assertEquals("Y", y, d2.asChecksum().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor() {
    assertNotNull("This will fail", new DefaultEncryptionIdentifier("password", null, Collections.emptySet()));
  }

  @Test
  public void testD99() {
    final DefaultEncryptionIdentifier d99a = new DefaultEncryptionIdentifier("d99", PGP_DS_TYPE,
        Arrays.asList("id1", "id2", "*id3"));
    assertEquals("id3", d99.getValidationIdentifier().get());
    assertEquals(d99a, d99);
  }

  @Test
  public void testDefaultEncryptionIdentifier() {
    assertNotNull("There is a default", d);
    assertNotNull("There is another", d2);
  }

  @Test
  public void testEncIdsEqualsBecauseWhateverTheHeckQuestionMark() {
    assertEquals(d, d);
    final Object j = getEncId(d);
    assertEquals(j, j);
    assertNotEquals(j, null);
    assertNotEquals("X", j);
    assertNotEquals(j, "X");
    assertEquals(j, getEncId(new DefaultEncryptionIdentifier()));
    assertEquals(getEncId(d), getEncId(new DefaultEncryptionIdentifier()));
    final Object s = getEncId(new DefaultEncryptionIdentifier("A", "B", Arrays.asList("C", "D")));
    assertEquals(s, getEncId(new DefaultEncryptionIdentifier("A", "B", Arrays.asList("C", "D"))));
    assertNotEquals(s, getEncId(new DefaultEncryptionIdentifier("A", "B", Arrays.asList("C", "D", "E"))));
    assertNotEquals(s, getEncId(new DefaultEncryptionIdentifier("A", "B", Arrays.asList("C"))));

  }

  @Test
  public void testEqualsObject() {
    assertFalse("D != D2", d.equals(d2));
    assertFalse("D != X", d.equals("X"));
    assertTrue("D == D", d.equals(d));
    assertFalse("D != null", d.equals(null));
    final DefaultEncryptionIdentifier d14 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE,
        new HashSet<>(Arrays.asList("B", "C")));
    final DefaultEncryptionIdentifier d15 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE,
        new HashSet<>(Arrays.asList("B", "C")));
    final DefaultEncryptionIdentifier d16 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE,
        new HashSet<>(Arrays.asList("B", "C", "D")));
    final DefaultEncryptionIdentifier d17 = new DefaultEncryptionIdentifier("password", PASSWORD_TYPE,
        new HashSet<>(Arrays.asList("A", "C", "D")));

    assertTrue("D13 == D14", d14.equals(d13));
    assertFalse("D16 == D15", d16.equals(d15));
    assertFalse("D16 == D17", d17.equals(d16));

  }

  @Test
  public void testFromString() {
    final DefaultEncryptionIdentifier d5 = new DefaultEncryptionIdentifier(x1);
    final DefaultEncryptionIdentifier d6 = new DefaultEncryptionIdentifier(y1);
    final DefaultEncryptionIdentifier d7 = new DefaultEncryptionIdentifier(y2);
    final DefaultEncryptionIdentifier d8 = new DefaultEncryptionIdentifier(x5);
    assertEquals("Same", d, d5);
    assertEquals("Same", d2, d6);
    assertNotEquals("Not same", d2, d7);
    assertNotEquals("Not same", d5, d6);
    assertNotEquals("Not same", d, d8);
  }

  @Test
  public void testGetEncryptionIdentifiers() {
    assertEquals("Empty set", Collections.emptySet(), d.getEncryptionIdentifiers());
    assertEquals("A and B", new HashSet<>(Arrays.asList("A", "B")), d2.getEncryptionIdentifiers());
  }

  @Test
  public void testGetid() {
    assertEquals("password", d2.getId());
  }

  @Test
  public void testGetType() {
    assertEquals("none", ENCRYPTION_TYPE_NONE, d.getType());
    assertEquals("Assyn", PASSWORD_TYPE, d2.getType());
  }

  @Test
  public void testHashCode() {
    assertFalse("Hashcode for default is != 0", d.hashCode() == 0);
    assertFalse("Hashcode for d2 is != 0", d2.hashCode() == 0);
    assertEquals("Hashcode for same is same", new DefaultEncryptionIdentifier("password", PASSWORD_TYPE, xx).hashCode(),
        d2.hashCode());
    assertEquals("Hashcode different for same values", d3.hashCode(), d2.hashCode());

  }

  @Test
  public void testMatches() {
    assertFalse("Doesn't match null", d.matches(null));
    assertTrue("Does match self", d.matches(d));
    assertFalse("Does not match d2", d.matches(d2));
    assertTrue("D2 matches d3", d2.matches(d3));
    assertFalse("D12 does not match d11", d12.matches(d11));
    assertTrue("D11 does match d12", d11.matches(d12));
    assertFalse("D11 does not match d10", d11.matches(d10));
    assertTrue("D10 does match d11", d10.matches(d11));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMultiValdiator() {
    new DefaultEncryptionIdentifier("A", "Type2", Arrays.asList("*A", "*B"));
  }

  @Test
  public void testMultiValdiatorOKButNotPResent() {
    final DefaultEncryptionIdentifier k = new DefaultEncryptionIdentifier("A", "TypeT", Arrays.asList("A", "*B"));
    assertEquals("B", k.getValidationIdentifier().get());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullId() {
    new DefaultEncryptionIdentifier(null, "TypeT", Arrays.asList("A"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullJSON() {
    new DefaultEncryptionIdentifier((JSONObject) null);
  }

  @Test
  public void testToString() {
    JSONAssert.assertEquals(x1, d.toString(), true);
    JSONAssert.assertEquals(y1, d2.toString(), true);
  }

}
