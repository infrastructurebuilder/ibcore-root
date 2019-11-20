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

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractCMSConfigurableSupplierTest {
  private final static Logger log = LoggerFactory.getLogger(AbstractCMSConfigurableSupplierTest.class);

  private DefaultConfigMapSupplier cms;
  private AbstractCMSConfigurableSupplier<String> l;

  @Before
  public void setUp() throws Exception {
    cms = new DefaultConfigMapSupplier();
    cms.addValue("B", "C");
    cms.addValue("A", "G");
    l = new AbstractCMSConfigurableSupplier<String>(cms, () -> log) {

      @Override
      public AbstractCMSConfigurableSupplier<String> getConfiguredSupplier(ConfigMapSupplier cms) {
        return this;
      }

      @Override
      protected String configuredType(ConfigMapSupplier config) {
        return config.get().getString("A");
      }
    };
  }

  @Test
  public void test() {
    assertEquals("G", l.configure(cms).get());
  }

}
