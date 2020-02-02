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

import org.infrastructurebuilder.util.FakeCredentialsFactory;
import org.infrastructurebuilder.util.artifacts.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractConfigurableSupplierTest {
  private final static Logger log = LoggerFactory.getLogger(AbstractConfigurableSupplierTest.class);
  private final static TestingPathSupplier wps = new TestingPathSupplier();

  private TestAbstractConfigurableSupplier a;
  private final static IBRuntimeUtils ibr = new IBRuntimeUtilsTesting(wps, log,
      new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(), new IBArtifactVersionMapper() {
      });

  @Before
  public void setUp() throws Exception {
    a = new TestAbstractConfigurableSupplier("nothing");
  }

  @Test
  public void test() {
    ConfigurableSupplier<String, String, Object> b = a.configure("new");
    assertEquals("new", b.get());

    assertNotNull(a.getRuntimeUtils());
    assertNotNull(a.getLog());
  }

  public class TestAbstractConfigurableSupplier extends AbstractConfigurableSupplier<String, String, Object> {

    public TestAbstractConfigurableSupplier(String config) {
      super(ibr, config, null);
    }

    @Override
    public ConfigurableSupplier<String, String, Object> configure(String config) {
      return new TestAbstractConfigurableSupplier(config);
    }

    @Override
    protected String getInstance(IBRuntimeUtils ibr, Object in) {
      return getConfig();
    }

  }

}
