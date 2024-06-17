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

import static org.infrastructurebuilder.constants.IBConstants.MAVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.infrastructurebuilder.util.config.ConfigMap;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenConfigMapSupplierTest extends AbstractPlexusDefaultsConfigTest {
  private final static Logger log = LoggerFactory.getLogger(MavenConfigMapSupplierTest.class);

  public MavenConfigMapBuilderSupplier getCms() {
    return new MavenConfigMapBuilderSupplier(mp, ms, me);
  }

  @Test
  public void testSetMavenProject() {
    MavenConfigMapBuilderSupplier m1 = getCms();

    assertEquals(MAVEN, m1.getName());
    final var cmv = m1.get().withProperties(properties);
    final ConfigMap map = cmv.get();
    assertTrue(map.keySet().size() >= properties.size());
    Set<String> set = properties.stringPropertyNames();
    var json = map.asJSON();
    log.info("Map as JSON\n" + json.toString(2));
    for (final String p : set) {
      final String val = map.getString(p);
      final String pVal = properties.getProperty(p);
      assertEquals(val, pVal);
    }
    assertTrue(map.containsKey("user.home"));

    assertTrue(map.containsKey("PATH"));

  }

}
