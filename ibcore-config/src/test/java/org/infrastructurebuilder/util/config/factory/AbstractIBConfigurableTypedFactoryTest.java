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
package org.infrastructurebuilder.util.config.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.config.DefaultConfigMapSupplier;
import org.infrastructurebuilder.util.config.IBRuntimeUtils;
import org.infrastructurebuilder.util.config.IBRuntimeUtilsTesting;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractIBConfigurableTypedFactoryTest {
  public final static Logger         log = LoggerFactory.getLogger(AbstractIBConfigurableTypedFactoryTest.class);
  public final static IBRuntimeUtils ibr = new IBRuntimeUtilsTesting(log);

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private FakeIBConfigurableTypedFactory s;
  private DefaultConfigMapSupplier       cms;

  @BeforeEach
  public void setUp() throws Exception {
    s = new FakeIBConfigurableTypedFactory(ibr);
    cms = new DefaultConfigMapSupplier();
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testRespondsTo() {
    assertTrue(s.respondsTo("bob"));
    assertFalse(s.respondsTo("Bob"));
  }

  @Test
  public void testGetInstance() {
    s.getLog().info("Testing getInstance (and getLog())");
    assertFalse(s.getConfig().isPresent());
    IBConfigurableFactory<String> s1 = s.configure(cms);
    assertFalse(s.getConfig().isPresent());
    assertTrue(s1.getConfig().isPresent());
    Optional<Supplier<String>> p = s1.getInstance(Optional.of(cms));
    assertTrue(p.isPresent());
    String k = p.get().get();
    assertEquals("jeff", k);
  }

  @Test
  public void testGetInstance2() {
    s.getLog().info("Testing getInstance (and getLog())");
    assertFalse(s.getConfig().isPresent());
    IBConfigurableFactory<String> s1 = s.configure(cms);
    assertFalse(s.getConfig().isPresent());
    assertTrue(s1.getConfig().isPresent());
    assertFalse(s.isConfigured());
    assertTrue(s1.isConfigured());
    Optional<Supplier<String>> p = s1.getInstance();
    assertFalse(p.isPresent());
  }

}
