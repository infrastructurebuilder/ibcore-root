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
package org.infrastructurebuilder.util.credentials.basic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.infrastructurebuilder.util.core.DefaultPropertiesSupplier;
import org.infrastructurebuilder.util.core.PropertiesSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultEnvSupplierTest {

  private PropertiesSupplier ps;

  @BeforeEach
  public void setUp() throws Exception {
    ps = new DefaultPropertiesSupplier();
  }

  @Test
  public void testGet() {
    assertNotNull(ps);
    Properties p = ps.get();
    assertNotNull(p);
    assertTrue(p.containsKey("java.runtime.name"));
  }

}
