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
package org.infrastructurebuilder.util.readdetect.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.infrastructurebuilder.util.core.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbsolutePathIBResourceBuilderFactoryTest {

  public final static TestingPathSupplier wps = new TestingPathSupplier();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    wps.finalize();
  }

  private RelativeRoot rrs;
  private AbsolutePathIBResourceBuilderFactory rcf;

  @BeforeEach
  void setUp() throws Exception {
    rrs = new AbsolutePathRelativeRoot(wps.get());
    this.rcf = new AbsolutePathIBResourceBuilderFactory(rrs);
  }

  @AfterEach
  void tearDown() throws Exception {
  }

//  @Test
//  void testFromPath() {
//    fail("Not yet implemented");
//  }
//
  @Test
  void testFromURLLikeString() {
    assertTrue(rcf.fromURLLike(null, null).isEmpty());
  }

//  @Test
//  void testFromJSON() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testFromURLLikeStringString() {
//    fail("Not yet implemented");
//  }

  @Test
  void testModel() {
    assertTrue(rcf.fromModel(null).isEmpty());
  }

}
