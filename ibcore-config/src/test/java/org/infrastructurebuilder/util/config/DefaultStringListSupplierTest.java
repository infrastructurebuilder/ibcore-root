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
package org.infrastructurebuilder.util.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultStringListSupplierTest {

private final static TestingPathSupplier wps = new TestingPathSupplier();
  private static Path target;
  private static Path testClasses;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    target = wps.getRoot();
    testClasses = wps.getTestClasses();
  }

  private List<String> list;
  private final List<String> list2 = Arrays.asList("/a.properties", "/b.xml");
  private final List<String> list2a = Arrays.asList(DefaultStringListSupplier.ISFILE,
      DefaultStringListSupplier.ISOVERRIDE, "/a.properties", "/b.xml");
  private DefaultStringListSupplier s1, s2, s3;

  @Test
  public void noSet() {
    assertEquals(0, s1.get().size());
  }

  @BeforeEach
  public void setUp() throws Exception {
    list = Arrays.asList(testClasses.resolve("a.properties").toString(), testClasses.resolve("b.xml").toString());
    s2 = new DefaultStringListSupplier(list2a);
    s3 = new DefaultStringListSupplier(list);
    s1 = new DefaultStringListSupplier(Collections.emptyList());
  }

  @Test
  public void test() {
    final List<String> l = s3.get();
    assertEquals(2, l.size());
    assertTrue(s2.isFile());
    assertTrue(s2.isOverride());
    assertFalse(s3.isFile());
    assertFalse(s3.isOverride());
    assertEquals(list2, s2.get());
  }

}
