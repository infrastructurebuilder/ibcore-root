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

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActivationFileProxyTest {

  private final static TestingPathSupplier wps = new TestingPathSupplier();

  private ActivationFileProxy fp,f2,f3;

  private Optional<Path> exists, missing;


  @Before
  public void setUp() throws Exception {
    fp = new ActivationFileProxy(empty(), empty());
    exists = of(wps.get());
    missing = of(wps.getRoot().resolve(UUID.randomUUID().toString()));
    f2 = new ActivationFileProxy(exists, missing);
    f3 = new ActivationFileProxy(exists, empty());
  }

  @After
  public void teardown() {
    wps.finalize();
  }

  @Test
  public void testGetExists() {
    assertEquals(exists, f2.getExists());
  }

  @Test
  public void testGetMissing() {
    assertEquals(missing, f2.getMissing());
  }

  @Test
  public void testIsActive() {
    assertTrue(fp.isActive());
    assertTrue(new ActivationFileProxy(exists, missing).isActive());
    assertTrue(new ActivationFileProxy(empty(), missing).isActive());
    assertTrue(new ActivationFileProxy(exists, empty()).isActive());
    assertFalse(new ActivationFileProxy(empty(), exists).isActive());
    assertFalse(new ActivationFileProxy(missing, empty()).isActive());

}

}
