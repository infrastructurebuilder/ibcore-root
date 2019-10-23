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
package org.infrastructurebuilder.util.plexus;

import static org.infrastructurebuilder.util.plexus.ProcessRunnerSupplier.PROCESS_NAMESPACE;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.DefaultConfigMapSupplier;
import org.junit.Before;
import org.junit.Test;

public class DefaultProcessRunnerConfigMapSupplierTest {

  private DefaultConfigMapSupplier c;
  private DefaultProcessRunnerConfigMapSupplier d;

  @Before
  public void setUp() throws Exception {
    c = new DefaultConfigMapSupplier();
    c.addValue(PROCESS_NAMESPACE + "X", "B");
    d = new DefaultProcessRunnerConfigMapSupplier(c);
  }

  @Test
  public void testGet() {
    final ConfigMap g = d.get();
    assertEquals(g.size(), 1);
    assertEquals(g.getString(PROCESS_NAMESPACE + "X"), "B");
  }

}
