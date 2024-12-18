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
package org.infrastructurebuilder.util.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.constants.IBConstants;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class SettingsProxyTest {

  private static final String ABC = "ABC";
  private List<ServerProxy> servers;
  private SettingsProxy sp;
  private ServerProxy s1, s2;

  @BeforeEach
  public void setUp() throws Exception {
    s1 = new ServerProxy(ABC, Optional.of(ABC), Optional.empty(), Optional.empty(), Optional.empty(),

        Optional.empty(), Optional.empty(), Optional.empty());
    s2 = new ServerProxy("DEF", Optional.of("DEF"), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty());
    servers = Arrays.asList(s1, s1, s2);
    sp = new SettingsProxy(false, Paths.get("."), IBConstants.UTF8, servers, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
  }

  @Test
  public void testGetServersAsJSON() {
    String jp = "{" + "  \"ABC\": {\n" + "    \"principal\": \"ABC\",\n" + "    \"id\": \"ABC\"\n" + "  },\n"
        + "  \"DEF\": {\n" + "    \"principal\": \"DEF\",\n" + "    \"id\": \"DEF\"\n" + "  }" + "}";
    JSONObject k = sp.getServersAsJSON();
    JSONAssert.assertEquals(new JSONObject(jp), k, JSONCompareMode.STRICT);
  }

  @Test
  public void testGetActiveProfiles() {
    assertEquals(0, sp.getActiveProfiles().size());
  }

  @Test
  public void testGetProfileAsMap() {
    assertEquals(0, sp.getProfilesAsMap().size());
  }

  @Test
  public void testGetServerString() {
    assertNotNull(sp.getServer(ABC));
  }

  @Test
  public void testLocalRepo() {
    Path k = sp.getLocalRepository();
    assertEquals(".", k.toString());
  }

  @Test
  public void testGetMirror() {
    assertFalse(sp.getMirror("ABC").isPresent());
  }

  @Test
  public void testGetMirrorOf() {
    assertFalse(sp.getMirrorOf("ABC").isPresent());
  }

  @Test
  public void tstOffile() {
    assertFalse(sp.isOffline());
  }

  @Test
  public void testActiveProxy() {
    assertFalse(sp.getActiveProxy().isPresent());
  }

  @Test
  public void testPluginReg() {
    assertFalse(sp.isUsePluginRegistry());
  }

  @Test
  public void testIsInteractive() {
    assertFalse(sp.isInteractiveMode());
  }

  @Test
  public void testBasics() {
    assertEquals(IBConstants.UTF_8, sp.getModelEncoding().name());
    assertEquals(0, sp.getPluginGroups().size());
    assertTrue(sp.getCredentialsFor(ABC).isPresent());
  }

}
