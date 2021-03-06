/*
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
package org.infrastructurebuilder.util.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CryptoIdentifierTest {

  private final static List<String> ll = Arrays.asList("Z", "A", "B");
  private final static List<String> lj = Arrays.asList("A", "B");
  private CryptoIdentifier i, j;
  private Optional<String> valid;
  private SortedSet<String> l, l2;
  private String id;
  private String type;

  @BeforeEach
  public void setUp() throws Exception {
    type = "A";
    id = "b";
    l = new TreeSet<String>(ll);
    l2 = new TreeSet<String>(lj);
    valid = Optional.empty();
    i = new DefaultCryptoIdentifier(type, id, l, valid);
    j = new DefaultCryptoIdentifier(type, id, l2, valid);
  }

  @Test
  public void testAbsolutelyMatch() {
    assertTrue(i.absolutelyMatch(i));
    assertFalse(i.absolutelyMatch(j));
    assertFalse(j.absolutelyMatch(i));
  }

  @Test
  public void testMatches() {
    assertTrue(i.matches(i));
    assertTrue(i.matches(j));
    assertFalse(j.matches(i));
  }
  @Test
  public void testGetIdentifiers() {
    assertTrue(i.getIdentifiers().containsAll(ll));
    assertEquals(ll.size(), i.getIdentifiers().size());
  }

  @Test
  public void testGetId() {
    assertEquals("b", i.getId());
    assertNotEquals("b", new DefaultCryptoIdentifier("A", null, new TreeSet<>(), Optional.empty()).getId());
  }

  @Test
  public void testGetType() {
    assertEquals("A", i.getType());
  }

  @Test
  public void testAsChecksum() {
    assertEquals("7519cfbbe3db7a29dd4e47cd92eaaa90546a64be836479a4faec72c6ee2c4a0d891eb95e5fb9819fc7b35ee545881f82c220725790e21f1a8a3907e4e71027e3", j.asChecksum().toString());
  }

  @Test
  public void testAsJSON() {
    assertNotNull(j.asJSON());
  }
  @Test
  public void testGetValidationIdentifier() {
    assertFalse(i.getValidationIdentifier().isPresent());
  }


}
