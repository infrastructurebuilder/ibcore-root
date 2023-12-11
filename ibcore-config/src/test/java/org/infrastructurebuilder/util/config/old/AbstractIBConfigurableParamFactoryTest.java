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
package org.infrastructurebuilder.util.config.old;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.Optional;
//import java.util.function.Supplier;
//
//import org.infrastructurebuilder.util.core.IBRuntimeUtils;
//import org.infrastructurebuilder.util.core.IBRuntimeUtilsTesting;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class AbstractIBConfigurableParamFactoryTest {
//  public final static Logger log = LoggerFactory.getLogger(AbstractIBConfigurableParamFactoryTest.class.getName());
//  public final static IBRuntimeUtils ibr = new IBRuntimeUtilsTesting(log);
//
//  @BeforeAll
//  public static void setUpBeforeClass() throws Exception {
//  }
//
//  @AfterAll
//  public static void tearDownAfterClass() throws Exception {
//  }
//
//  private FakeIBConfigurableParamFactory k;
//  private OldConfigMapSupplier cms;
//
//  @BeforeEach
//  public void setUp() throws Exception {
//    cms = new OldDefaultConfigMapSupplier();
//    k = new FakeIBConfigurableParamFactory(ibr);
//    IBRuntimeUtilsTesting l = new IBRuntimeUtilsTesting((IBRuntimeUtilsTesting) ibr);
//  }
//
//  @AfterEach
//  public void tearDown() throws Exception {
//  }
//
//  @Test
//  public void testRespondsTo() {
//    assertTrue(k.respondsTo("bob"));
//    assertFalse(k.respondsTo("bobB"));
//  }
//
//  @Test
//  public void testGetInstance() {
//    assertFalse(k.isConfigured());
//    FakeIBConfigurableParamFactory k1 = k.withConfig(cms);
//    assertFalse(k.isConfigured());
//    assertTrue(k1.isConfigured());
//    Optional<Supplier<String>> v = k1.getInstance();
//    assertFalse(v.isPresent());
//    Optional<Supplier<String>> v2 = k1.getInstance(Optional.of("BOB"));
//    assertFalse(v2.isPresent());
//    Optional<Supplier<String>> v3 = k1.getInstance(Optional.of("bob"));
//    assertEquals("jeff", v3.get().get());
//  }
//
//}
