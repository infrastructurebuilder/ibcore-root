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

import static org.infrastructurebuilder.util.config.ConfigMapBuilderSupplier.MAVEN_SETTINGS_SERVER_NAMESPACE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenConfigWithServersMapSupplierTest extends MavenConfigMapSupplierTest {
  private static final Logger log = LoggerFactory.getLogger(MavenConfigWithServersMapSupplierTest.class);
  private static final String PRIVATEKEY = "privatekey";
  private static final String PASSWORD = "password";
  private static final String PASSPHRASE = "passphrase";
  private static final String USERNAME = "username";
  private MavenConfigWithServersMapSupplier cms;
  private Settings s;
  private String ns, ns2;
  private MavenConfigWithServersMapSupplier cms2;

  @BeforeEach
  public void setUp() throws Exception {
    s = new Settings();
    final Server server = new Server();
    ns = MAVEN_SETTINGS_SERVER_NAMESPACE + "id.";
    server.setId("id");
    server.setUsername(USERNAME);
    server.setPassphrase(PASSPHRASE);
    server.setPassword(PASSWORD);
    server.setPrivateKey(PRIVATEKEY);
    s.addServer(server);

    final Server server2 = new Server();
    ns2 = MAVEN_SETTINGS_SERVER_NAMESPACE + "id2.";
    server2.setId("id2");
    server2.setUsername(USERNAME + "2");
    server2.setPassphrase(PASSPHRASE + "2");
    server2.setPassword(PASSWORD + "2");
    server2.setPrivateKey(PRIVATEKEY + "2");
    s.addServer(server2);

    cms = new MavenConfigWithServersMapSupplier(mp, s, ms, me);
    cms2 = new MavenConfigWithServersMapSupplier(mp, new Settings(), ms, me);
  }

  @Test
  public void testNoSettings() {
    final ConfigMap map = cms2.get().get();
    for (String key : map.keySet()) {
      assertFalse(key.startsWith(MAVEN_SETTINGS_SERVER_NAMESPACE));
    }
  }

  @Test
  public void testSetSettings() {
    final ConfigMap map = cms.get().get();
    log.info("JSON is " + map.asJSON().toString(2));
    assertEquals(map.getString(ns + USERNAME), USERNAME);
    assertEquals(map.getString(ns + PASSPHRASE), PASSPHRASE);
    assertEquals(map.getString(ns + PASSWORD), PASSWORD);
    assertEquals(map.getString(ns + PRIVATEKEY), PRIVATEKEY);

    assertEquals(map.getString(ns2 + PRIVATEKEY), PRIVATEKEY + "2");
  }

}
