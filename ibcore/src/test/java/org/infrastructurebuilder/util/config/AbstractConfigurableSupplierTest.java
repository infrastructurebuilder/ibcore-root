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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractConfigurableSupplierTest {

  private TestAbstractConfigurableSupplier a;

  @Before
  public void setUp() throws Exception {
    a = new TestAbstractConfigurableSupplier("nothing");
  }

  @Test
  public void test() {
    ConfigurableSupplier<String, String> b = a.configure("new");
    assertEquals("new", b.get());
  }

  public class TestAbstractConfigurableSupplier extends AbstractConfigurableSupplier<String, String> {

    public TestAbstractConfigurableSupplier(String config) {
      super(config);
    }

    @Override
    public ConfigurableSupplier<String, String> configure(String config) {
      return new TestAbstractConfigurableSupplier(config);
    }

    @Override
    protected String configuredType(String config) {
      return config;
    }

  }

}
