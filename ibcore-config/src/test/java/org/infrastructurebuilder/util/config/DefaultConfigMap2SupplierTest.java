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

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.infrastructurebuilder.util.config.old.OldConfigMap;
import org.infrastructurebuilder.util.config.old.OldConfigMapSupplier;
import org.infrastructurebuilder.util.config.old.OldDefaultConfigMapSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultConfigMap2SupplierTest {

  private OldConfigMapSupplier supplier;
  private OldConfigMap val;

  @BeforeEach
  public void setUp() throws Exception {
    supplier = new OldDefaultConfigMapSupplier();
    val = new OldConfigMap();
  }

  @Test
  public void testCons1() {
    OldDefaultConfigMapSupplier b = new OldDefaultConfigMapSupplier(val);
    OldDefaultConfigMapSupplier b1 = new OldDefaultConfigMapSupplier(b);
    OldConfigMap c = b.get();
    OldConfigMap c1 = b1.get();
    assertEquals(val.keySet().stream().sorted().collect(toSet()), c.keySet().stream().sorted().collect(toSet()));
    assertEquals(val.keySet().stream().sorted().collect(toSet()), c1.keySet().stream().sorted().collect(toSet()));
  }

  @Test
  public void testEntrySet() {
    assertEquals(0, supplier.get().entrySet().size());
  }

  @Test
  public void testAdd2() {
    final Map<String, Object> m = new HashMap<>();
    m.put("X", "Y");
    m.put("A", "B");
    supplier.addConfiguration(m);
    OldConfigMap k = supplier.get();
    assertEquals(2, k.size());
    assertEquals("Y", k.getString("X"));
    assertEquals("B", k.getString("A"));

  }

  @Test
  public void testAdd() {
    final Map<String, Object> m = new HashMap<>();
    m.put("X", "Y");
    m.put("A", "B");
    supplier.addConfiguration(new OldConfigMap(m));
    OldConfigMap k = supplier.get();
    assertEquals(2, k.size());
    assertEquals("Y", k.getString("X"));
    assertEquals("B", k.getString("A"));
    final Map<String, Object> n = new HashMap<>();
    n.put("X", "Z");
    n.put("A", "CC");
    n.put("Q", "M");
    supplier.addConfiguration(n);
    k = supplier.get();
    assertEquals(3, k.size());
    assertEquals("Y", k.getString("X"));
    assertEquals("B", k.getString("A"));
    assertEquals("M", k.getString("Q"));

    assertTrue(k.containsKey("Q"));
    assertEquals("FAKE", k.getOrDefault("FAKE", "FAKE"));

    Map<String, String> over = new HashMap<>();
    over.put("X", "Z");
    supplier.overrideConfigurationString(over);
    OldConfigMap v2 = supplier.get();
    assertEquals("Z", v2.getString("X"));
  }

  @Test
  public void testAddProperties() {
    final Properties m = new Properties();
    m.setProperty("X", "Y");
    m.setProperty("A", "B");
    OldConfigMap mm = new OldConfigMap(m);
    supplier.addConfiguration(mm);
    OldConfigMap k = supplier.get();
    assertEquals(2, k.size());
    assertEquals("Y", k.getString("X"));
    assertEquals("B", k.getString("A"));
    final Properties n = new Properties();
    n.setProperty("X", "Z");
    n.setProperty("A", "CC");
    n.setProperty("Q", "M");
    supplier.addConfiguration(n);
    k = supplier.get();
    assertEquals(3, k.size());
    assertEquals("Y", k.getString("X"));
    assertEquals("B", k.getString("A"));
    assertEquals("M", k.getString("Q"));
  }

  @Test
  public void testDefaultValue() {
    supplier.overrideValueDefaultBlank("X", "Y");
    supplier.overrideValueDefault("A", null, "");
    final OldConfigMap m = supplier.get();
    assertNotNull(m);
    assertEquals("", m.getString("A"));
  }

  @Test
  public void testGet() {
    final OldConfigMap m = supplier.get();
    assertNotNull(m);
    assertNotNull(supplier.toString());
  }

  @Test
  public void testOverride() {
    final Map<String, Object> m = new HashMap<>();
    m.put("X", "Y");
    m.put("A", "B");
    supplier.addConfiguration(m);
    OldConfigMap k = supplier.get();
    assertEquals(2, k.size());
    assertEquals("Y", k.getString("X"));
    assertEquals("B", k.getString("A"));
    final Map<String, Object> n = new HashMap<>();
    n.put("X", "Z");
    n.put("A", "CC");
    n.put("Q", "M");
    supplier.overrideConfiguration(n);
    k = supplier.get();
    assertEquals(3, k.size());
    assertEquals("Z", k.getString("X"));
    assertEquals("CC", k.getString("A"));
    assertEquals("M", k.getString("Q"));
  }

  @Test
  public void testOverride2() {
    final Map<String, Object> m = new HashMap<>();
    m.put("X", "Y");
    m.put("A", "B");
    supplier.addConfiguration(m);
    OldConfigMap k = supplier.get();
    assertEquals(2, k.size());
    assertEquals("Y", k.getString("X"));
    assertEquals("B", k.getString("A"));
    final Map<String, Object> n = new HashMap<>();
    n.put("X", "Z");
    n.put("A", "CC");
    n.put("Q", "M");
    supplier.overrideConfiguration(new OldConfigMap(n));
    k = supplier.get();
    assertEquals(3, k.size());
    assertEquals("Z", k.getString("X"));
    assertEquals("CC", k.getString("A"));
    assertEquals("M", k.getString("Q"));
  }

  @Test
  public void testOverrideProperties() {
    final Properties m = new Properties();
    m.setProperty("X", "Y");
    m.setProperty("A", "B");
    supplier.addConfiguration(m);
    OldConfigMap k = supplier.get();
    assertEquals(2, k.size());
    assertEquals("Y", k.getString("X"));
    assertEquals("B", k.getString("A"));
    final Properties n = new Properties();
    n.setProperty("X", "Z");
    n.setProperty("A", "CC");
    n.setProperty("Q", "M");
    supplier.overrideConfiguration(n);
    k = supplier.get();
    assertEquals(3, k.size());
    assertEquals("Z", k.getString("X"));
    assertEquals("CC", k.getString("A"));
    assertEquals("M", k.getString("Q"));
  }

  @Test
  public void testPutonConfigMap2() {
    OldConfigMap k = supplier.get();
    k.put("A", "B");
    assertEquals("B", k.getString("A"));
  }

}
