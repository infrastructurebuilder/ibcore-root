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
package org.infrastructurebuilder.util.core;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiFunction;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractBaseIBDirScanTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  private AbstractBaseIBDirScan scan;

  @BeforeEach
  void setUp() throws Exception {
    this.scan = new AbstractBaseIBDirScan() {
      @Override
      public BiFunction<Path, BasicFileAttributes, Boolean> getExclusionFunction() {
        return (a,b) -> true;
      }
    };
  }

  @Test
  void testGetIncludedPaths() {
    assertEquals(0,this.scan.getIncludedPaths().size());
  }

  @Test
  void testGetExcludedPaths() {
    assertEquals(0,this.scan.getExcludedPaths().size());
  }

  @Test
  void testGetErroredPaths() {
    assertEquals(0,this.scan.getErroredPaths().size());
  }

  @Test
  void testGetAttributesForPath() {
//    fail("Not yet implemented");
  }

  @Test
  void testGetExceptionForPath() {
//    fail("Not yet implemented");
  }

  @Test
  void testAddPath() {
//    fail("Not yet implemented");
  }

  @Test
  void testPreVisitDirectoryPathBasicFileAttributes() {
//    fail("Not yet implemented");
  }

  @Test
  void testVisitFilePathBasicFileAttributes() {
//    fail("Not yet implemented");
  }

  @Test
  void testVisitFileFailedPathIOException() {
//    fail("Not yet implemented");
  }

  @Test
  void testGetExclusionFunction() {
//    fail("Not yet implemented");
  }

}
