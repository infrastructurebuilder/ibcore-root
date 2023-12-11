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
package org.infrastructurebuilder.maven.util.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.ConfigMapBuilderSupplier;
import org.junit.jupiter.api.Test;

public class MavenConfigMapSupplierTest extends AbstractPlexusDefaultsConfigTest {

  public MavenConfigMapBuilderSupplier getCms() {
    return new MavenConfigMapBuilderSupplier(mp, ms, me);
  }

  @Test
  public void testSetMavenProject() {
    MavenConfigMapBuilderSupplier m1 = getCms();
    assertEquals(ConfigMapBuilderSupplier.MAVEN, m1.getName());
    final ConfigMap map = m1.get().get();
    assertTrue(map.keySet().size() >= properties.size());
    for (final String p : properties.stringPropertyNames()) {
      final String val = map.getString(p);
      final String pVal = properties.getProperty(p);
      assertEquals(val, pVal);
    }
    assertTrue(map.containsKey("user.home"));
    assertTrue(map.containsKey("PATH"));
    
  }

}
