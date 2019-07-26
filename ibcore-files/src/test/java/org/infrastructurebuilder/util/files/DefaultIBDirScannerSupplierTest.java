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
package org.infrastructurebuilder.util.files;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.infrastructurebuilder.util.config.StringListSupplier;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIBDirScannerSupplierTest {
  public final static Logger log = LoggerFactory.getLogger(DefaultIBDirScannerSupplierTest.class);

  private static WorkingPathSupplier wps;
  private static Path target;
  private static Path testClasses;
  private static Path test1;
  private static Path test2;
  private DefaultIBDirScannerSupplier ibdss;

  @BeforeClass
  public static void setupClass() {
    wps = new WorkingPathSupplier();
    target = wps.getRoot();
    testClasses = target.resolve("test-classes");
    test1 = testClasses.resolve("test1");
    test2 = testClasses.resolve("test2");
  }

  @Before
  public void setUp() throws Exception {
    StringListSupplier include1 = () -> Arrays.asList("**/*");
    StringListSupplier exclude1 = () -> Arrays.asList("b");
    ibdss = new DefaultIBDirScannerSupplier(() -> test1, include1, exclude1,
        // Exclude dires
        () -> true,
        //
        () -> false,
        //
        () -> false,
        //
        () -> false);
  }

  @Test
  public void testGet() {
    assertNotNull(ibdss.get());
  }

  @Test
  public void testConfigAndGet() {
    Map<Boolean, List<Path>> v = ibdss.get().scan();
    assertEquals(v.size(), 2);
    assertEquals(2, v.size());
    List<Path> incl = v.get(true);
    assertEquals(incl.size(), 2);
    List<Path> excl = v.get(false);
    assertEquals(0, excl.size());
  }

  @Test
  public void testConfigAndGetWithTrue() {
    StringListSupplier include1 = () -> Arrays.asList("**/*");
    StringListSupplier exclude1 = () -> Arrays.asList("b");
    ibdss = new DefaultIBDirScannerSupplier(() -> test1, include1, exclude1,
        // Exclude dires
        () -> false,
        //
        () -> true,
        //
        () -> true,
        //
        () -> true);
    Map<Boolean, List<Path>> v = ibdss.get().scan();
    assertEquals(v.size(), 2);
    assertEquals(2, v.size());
    List<Path> incl = v.get(true);
    assertEquals(3, incl.size());
    List<Path> excl = v.get(false);
    assertEquals(0, excl.size());
  }

}
