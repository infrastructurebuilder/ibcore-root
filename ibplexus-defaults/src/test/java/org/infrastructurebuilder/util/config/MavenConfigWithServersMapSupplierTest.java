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

import static org.infrastructurebuilder.util.config.MavenConfigWithServersMapSupplier.MAVEN_SETTINGS_SERVER_NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Test;

public class MavenConfigWithServersMapSupplierTest extends MavenConfigMapSupplierTest {

  private static final String PRIVATEKEY = "privatekey";
  private static final String PASSWORD = "password";
  private static final String PASSPHRASE = "passphrase";
  private static final String USERNAME = "username";
  private MavenConfigWithServersMapSupplier cms;
  private Settings s;
  private String ns;
  private MavenConfigWithServersMapSupplier cms2;

  @Before
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
    cms = new MavenConfigWithServersMapSupplier(mp,s);
    cms2 = new MavenConfigWithServersMapSupplier(mp,new Settings());
  }


  @Test
  public void testNoSettings() {
    final Map<String, String> map = cms2.get();
    for (String key: map.keySet()) {
      assertFalse(key.startsWith(MAVEN_SETTINGS_SERVER_NAMESPACE));
    }
  }
  @Test
  public void testSetSettings() {
    final Map<String, String> map = cms.get();
    assertEquals(map.get(ns + USERNAME), USERNAME);
    assertEquals(map.get(ns + PASSPHRASE), PASSPHRASE);
    assertEquals(map.get(ns + PASSWORD), PASSWORD);
    assertEquals(map.get(ns + PRIVATEKEY), PRIVATEKEY);
  }

}
