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
package org.infrastructurebuilder.util.filescanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.infrastructurebuilder.util.core.IBDirScan;
import org.infrastructurebuilder.util.core.StringListSupplier;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIBDirScannerSupplierTest {
  public final static Logger log = LoggerFactory.getLogger(DefaultIBDirScannerSupplierTest.class.getName());

  private final static TestingPathSupplier wps = new TestingPathSupplier();
  private static Path testClasses;
  private static Path test1;
  private DefaultIBDirScannerSupplier ibdss;

  @BeforeAll
  public static void setupClass() {
    testClasses = wps.getTestClasses();
    test1 = testClasses.resolve("test1");
  }

  @BeforeEach
  public void setUp() throws Exception {
    StringListSupplier include1 = () -> Arrays.asList("**/*");
    StringListSupplier exclude1 = () -> Arrays.asList("**/b");
    ibdss = new DefaultIBDirScannerSupplier(() -> test1, include1, exclude1,
        // Exclude dires
        () -> true,
        // exclude files
        () -> false,
        // exclude hidden
        () -> false,
        // exclude dotfiles
        () -> false,
        // exclude symlinks
        () -> false,
        // add excluded defaults
        () -> true);
  }

  @Test
  public void testGet() {
    assertNotNull(ibdss.get());
  }

  @Test
  public void testConfigAndGet() throws IOException {
    var res = ibdss.get().scan();
    assertNotNull(res.getIncludedPaths());
    assertNotNull(res.getExcludedPaths());
    assertNotNull(res.getErroredPaths());
    assertEquals(1, res.getIncludedPaths().size());
    assertEquals(3, res.getExcludedPaths().size());
  }

  @Test
  public void testConfigAndGetWithTrue() throws IOException {
    StringListSupplier include1 = () -> Arrays.asList("**/*");
    StringListSupplier exclude1 = () -> Arrays.asList("**/b");
    ibdss = new DefaultIBDirScannerSupplier(() -> test1, include1, exclude1,
        // Exclude dires
        () -> false,
        // exclude files
        () -> true,
        // exclude hidden
        () -> false,
        // exclude dotfiles
        () -> false,
        // exclude symlinks
        () -> false,
        // add excluded defaults
        () -> true);
    IBDirScan v = ibdss.get().scan();
    assertEquals(2, v.getIncludedPaths().size());
    assertEquals(2, v.getExcludedPaths().size());
  }

}
