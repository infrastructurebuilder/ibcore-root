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
package org.infrastructurebuilder.util.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;


public class LateBindingWorkingPathSupplierTest {

  private SingletonLateBindingPathSupplier lb;
  private LateBindingWorkingPathSupplier wps;

  @Before
  public void setUp() throws Exception {
    lb = new SingletonLateBindingPathSupplier();
    wps = new LateBindingWorkingPathSupplier(lb, new DefaultIdentifierSupplier());
  }

  @Test(expected = NullPointerException.class)
  public void testNPE() {
    wps.get();
  }

  @Test
  public void testOk() {
    Path p = Paths.get(Optional.ofNullable(System.getProperty("target")).orElse("./target"));
    lb.setPath(p);
    Path x = wps.get();
    assertEquals(p, x.getParent());
    assertTrue(Files.isDirectory(x));
    wps.finalize();
    assertFalse(Files.isDirectory(x));
  }

}
