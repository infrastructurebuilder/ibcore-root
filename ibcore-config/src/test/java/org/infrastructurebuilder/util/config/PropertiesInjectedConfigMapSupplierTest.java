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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.core.WorkingPathSupplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertiesInjectedConfigMapSupplierTest {

  private final static TestingPathSupplier wps = new TestingPathSupplier();
  private static Path target;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    target = new WorkingPathSupplier().getRoot();
  }

  private PropertiesInjectedConfigMapSupplier k;
  private List<String> list;
  private DefaultStringListSupplier single;

  @BeforeEach
  public void setUp() throws Exception {
    list = new ArrayList<>();
    list.add(wps.getTestClasses().resolve("c1.properties").toAbsolutePath().toString());
    list.add(wps.getTestClasses().resolve("b.xml").toAbsolutePath().toString());
    list.add(DefaultStringListSupplier.ISFILE);
    single = new DefaultStringListSupplier(list);
    k = new PropertiesInjectedConfigMapSupplier(Arrays.asList(single));
  }

  @Test
  public void testGet() {
    assertNotNull(k.get());
  }

}
