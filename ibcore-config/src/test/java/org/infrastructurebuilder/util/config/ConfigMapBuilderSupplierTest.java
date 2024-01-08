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
package org.infrastructurebuilder.util.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.infrastructurebuilder.util.config.impl.usurped.Util;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConfigMapBuilderSupplierTest {
  private final static Logger log = LoggerFactory.getLogger(ConfigMapBuilderSupplierTest.class);

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private ConfigMapBuilder cmb, cmb1, cmb2, cmb3, cmb4, cmb5;
  private JSONObject obj1;
  final String string1 = "HasSameRef";
  final String string2 = "HasDifferentRef";

  @BeforeEach
  void setUp() throws Exception {
    obj1 = new JSONObject().put("key1", "abc").put("key2", 2).put("key3", string1);
    cmb = ConfigMapBuilderSupplier.defaultBuilder(obj1);
    cmb1 = ConfigMapBuilderSupplier.defaultBuilder().withJSONObject(obj1);
    cmb2 = ConfigMapBuilderSupplier.defaultBuilder();
    cmb3 = ConfigMapBuilderSupplier.defaultBuilder();
    cmb4 = ConfigMapBuilderSupplier.defaultBuilder();
    cmb5 = ConfigMapBuilderSupplier.defaultBuilder();
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testDefaultBuilder() {
    assertNotNull(cmb);
  }

  @Test
  void testSimilarReplica() {
    JSONObject obj2 = new JSONObject().put("key1", "abc").put("key2", 3).put("key3", string1);
    cmb2 = cmb2.withJSONObject(obj2);

    log.info("obj1 = \n" + cmb1.get().asJSON().toString(4));
    JSONObject obj3 = new JSONObject().put("key1", "abc").put("key2", 2).put("key3", new String(string1));
    cmb3 = cmb3.withJSONObject(obj3);

    JSONObject obj4 = new JSONObject().put("key1", "abc").put("key2", 2.0).put("key3", new String(string1));
    cmb4 = cmb4.withJSONObject(obj4);

    JSONObject obj5 = new JSONObject().put("key1", "abc").put("key2", 2.0).put("key3", new String(string2));
    cmb5 = cmb5.withJSONObject(obj5);

    assertFalse("obj1-obj2 Should eval to false", cmb1.get().similar(cmb2.get()));
    log.info("obj1 = \n" + cmb1.get().asJSON().toString(4));
    log.info("obj3 = \n" + cmb3.get().asJSON().toString(4));
    assertTrue("obj1-obj3 Should eval to true", cmb1.get().similar(cmb3.get()));
    assertTrue("obj1-obj4 Should eval to true", cmb1.get().similar(cmb4.get()));
    assertFalse("obj1-obj5 Should eval to false", cmb1.get().similar(cmb5.get()));
    // verify that a double and big decimal are "similar"
    assertTrue("should eval to true", new JSONObject().put("a", 1.1d).similar(new JSONObject("{\"a\":1.1}")));
    // Confirm #618 is fixed (compare should not exit early if similar numbers are found)
    // Note that this test may not work if the JSONObject map entry order changes
    JSONObject first = new JSONObject("{\"a\": 1, \"b\": 2, \"c\": 3}");
    JSONObject second = new JSONObject("{\"a\": 1, \"b\": 2.0, \"c\": 4}");
    assertFalse("first-second should eval to false", first.similar(second));
    List<JSONObject> jsonObjects = new ArrayList<JSONObject>(Arrays.asList(obj1, obj2, obj3, obj4, obj5));
    Util.checkJSONObjectsMaps(jsonObjects);

  }

}
