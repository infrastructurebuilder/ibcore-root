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
package org.infrastructurebuilder.util.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//class TypeMapTupleTest {
//
//  @BeforeAll
//  static void setUpBeforeClass() throws Exception {
//
//  }
//
//  @AfterAll
//  static void tearDownAfterClass() throws Exception {
//  }
//
//  private TypeMapTuple t;
//  private TypeMapTuple u;
//  private List<TypeMapTuple> list;
//  private TreeMap<Integer, TypeMapTuple> treeMap;
//
//  @BeforeEach
//  void setUp() throws Exception {
//    this.t = new TypeMapTuple("a", "b", "c");
//    this.u = new TypeMapTuple("d", "e", "f", 2);
//    this.list = List.of(this.t, this.u);
//  }
//
//  @AfterEach
//  void tearDown() throws Exception {
//  }
//
//  @Test
//  void testGetId() {
//    assertEquals("a", this.t.getId());
//  }
//
//  @Test
//  void testGetExtension() {
//    assertEquals("b", this.t.getExtension());
//  }
//
//  @Test
//  void testGetStructuredType() {
//    assertEquals("c", this.t.getStructuredType().get());
//  }
//
//  @Test
//  void testGetWeight() {
//    assertEquals(2, this.u.getWeight());
//    assertEquals(0, this.t.getWeight());
//  }
//
//}
