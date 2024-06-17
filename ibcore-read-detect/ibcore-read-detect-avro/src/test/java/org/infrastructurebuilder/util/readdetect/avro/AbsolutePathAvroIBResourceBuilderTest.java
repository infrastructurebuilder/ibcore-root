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
package org.infrastructurebuilder.util.readdetect.avro;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.infrastructurebuilder.pathref.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.pathref.RelativeRoot;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.util.readdetect.path.impls.relative.RelativePathIBResourceBuilderFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbsolutePathAvroIBResourceBuilderTest {
  public final static TestingPathSupplier tps = new TestingPathSupplier();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    tps.finalize();
  }

  private RelativeRoot rrs;
  private RelativePathIBResourceBuilderFactory rcf;
  private Path avdl, avro, avsc, csv, empty;

  @BeforeEach
  void setUp() throws Exception {
    var tr = tps.getTestClasses();
    rrs = new AbsolutePathRelativeRoot(tps.get());
    this.rcf = new RelativePathIBResourceBuilderFactory(rrs);
    this.avdl = tr.resolve("ba.avdl");
    this.avro = tr.resolve("ba.avro");
    this.avsc = tr.resolve("ba.avsc");
    this.csv = tr.resolve("ba.csv");
    this.empty = tr.resolve("baempty.avsc");
  }

  @AfterEach
  void tearDown() throws Exception {
  }

//  @Test
//  void testFromPath() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testFromURLLikeString() {
//    assertTrue(rcf.fromURL(null).isEmpty());
//  }
//
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
