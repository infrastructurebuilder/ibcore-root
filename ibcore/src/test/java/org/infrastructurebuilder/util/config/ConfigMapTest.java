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

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class ConfigMapTest {

  private static final Object ABC = "ABC";
  private HashMap<String, Object> m;
  private Object o = UUID.randomUUID();
  private ConfigMap m3;

  @Before
  public void setUp() throws Exception {
    m = new HashMap<>();
    m.put("A", o);
    ConfigMap m2 = new ConfigMap(m);
    m3 = new ConfigMap(m2);
  }

  @Test
  public void testContainsKeyObject() {
    assertFalse(m3.containsKey(ABC));
    assertTrue(m3.containsKey("A"));
  }

  @Test
  public void testContainsValue() {
    assertFalse(m3.containsValue("A"));
    assertTrue(m3.containsValue(o));
  }

  @Test
  public void testGetObject() {
    assertEquals(o, m3.get("A"));
    m3.put((String)ABC, o);
    assertTrue(m3.containsKey(ABC));
    assertEquals(o, m3.get((Object)ABC));
  }

  @Test
  public void testRemove() {
    assertFalse(m3.containsValue("A"));
    assertTrue(m3.containsValue(o));
    m3.remove("A");
    assertFalse(m3.containsValue(o));
  }

  @Test
  public void testPutAll() {
    m3.clear();
    m3.putAll(m);;
    assertTrue(m3.containsValue(o));
  }

  @Test
  public void testClear() {
    assertEquals(1, m3.size());
    m3.clear();
    assertTrue(m3.isEmpty());
    assertEquals(0, m3.size());
  }

  @Test
  public void testValues() {
    assertEquals(Arrays.asList(o).stream().collect(toList()), m3.values().stream().collect(toList()));
  }

}
