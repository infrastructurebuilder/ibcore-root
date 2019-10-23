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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class DefaultConfigMapSupplierTest {

  private ConfigMapSupplier supplier;
  private ConfigMap val;

  @Before
  public void setUp() throws Exception {
    supplier = new DefaultConfigMapSupplier();
    val = new ConfigMap();
  }

  @Test
  public void testEntrySet() {
    assertEquals(0,supplier.get().entrySet().size());
  }


  @Test
  public void testAdd() {
    final Map<String, Object> m = new HashMap<>();
    m.put("X", "Y");
    m.put("A", "B");
    supplier.addConfiguration(new ConfigMap(m));
    ConfigMap k = supplier.get();
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
  }

  @Test
  public void testAddProperties() {
    final Properties m = new Properties();
    m.setProperty("X", "Y");
    m.setProperty("A", "B");
    ConfigMap mm = new ConfigMap(m);
    supplier.addConfiguration(mm);
    ConfigMap k = supplier.get();
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
    final ConfigMap m = supplier.get();
    assertNotNull(m);
    assertEquals("", m.getString("A"));
  }

  @Test
  public void testGet() {
    final ConfigMap m = supplier.get();
    assertNotNull(m);
    assertNotNull(supplier.toString());
  }

  @Test
  public void testOverride() {
    final Map<String, Object> m = new HashMap<>();
    m.put("X", "Y");
    m.put("A", "B");
    supplier.addConfiguration(m);
    ConfigMap k = supplier.get();
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
  public void testOverrideProperties() {
    final Properties m = new Properties();
    m.setProperty("X", "Y");
    m.setProperty("A", "B");
    supplier.addConfiguration(m);
    ConfigMap k = supplier.get();
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
  public void testPutonConfigMap() {
    ConfigMap k = supplier.get();
    k.put("A", "B");
    assertEquals("B",k.getString("A"));
  }

}
