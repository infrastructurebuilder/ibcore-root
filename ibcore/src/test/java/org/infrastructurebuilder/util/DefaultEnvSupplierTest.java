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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class DefaultEnvSupplierTest {

  private PropertiesSupplier ps;

  @Before
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
