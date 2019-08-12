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

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class SettingsProxyTest {

  private List<ServerProxy> servers;
  private SettingsProxy sp;
  private ServerProxy s1, s2;

  @Before
  public void setUp() throws Exception {
    s1 = new ServerProxy("ABC", Optional.of("ABC"), Optional.empty(), Optional.empty(), Optional.empty(),

        Optional.empty(), Optional.empty(), Optional.empty());
    s2 = new ServerProxy("DEF", Optional.of("DEF"), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty());
    servers = Arrays.asList(s1, s1, s2);
    sp = new SettingsProxy(false, Paths.get("."), Charset.defaultCharset(), servers, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
  }

  @Test
  public void testGetServersAsJSON() {
    String jp = "{" + "  \"ABC\": {\n" +
        "    \"principal\": \"ABC\",\n" +
        "    \"id\": \"ABC\"\n" +
        "  },\n" +
        "  \"DEF\": {\n" +
        "    \"principal\": \"DEF\",\n" +
        "    \"id\": \"DEF\"\n" +
        "  }"
        + "}";
     JSONObject k = sp.getServersAsJSON();
     JSONAssert.assertEquals(new JSONObject(jp), k, JSONCompareMode.STRICT);
  }

}
