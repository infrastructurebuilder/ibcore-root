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
package org.infrastructurebuilder.util.config.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultProcessableTypedTest {

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private DefaultProcessableTyped<String> k;
  private DefaultProcessableTyped<String> m;

  @BeforeEach
  public void setUp() throws Exception {
    k = new DefaultProcessableTyped<>("t", Optional.of("p"), false, "X");
    m = new DefaultProcessableTyped<>("p", "Y");
  }


  @Test
  public void testIsInbound() {
    assertFalse(k.isInbound());
    assertTrue(m.isInbound());
  }

  @Test
  public void testGetProcessableType() {
    assertEquals("t", k.getProcessableType());
    assertEquals("p", m.getProcessableType());
  }

  @Test
  public void testGetSpecificProcessor() {
    assertEquals("p", k.getSpecificProcessor().get());
    assertFalse(m.getSpecificProcessor().isPresent());
  }

  @Test
  public void testGetValue() {
    assertEquals("X", k.getValue());
    assertEquals("Y", m.getValue());
  }

}
