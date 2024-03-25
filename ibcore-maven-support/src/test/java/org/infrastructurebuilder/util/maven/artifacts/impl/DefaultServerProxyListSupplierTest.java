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
package org.infrastructurebuilder.util.maven.artifacts.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.infrastructurebuilder.util.settings.ServerProxyMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultServerProxyListSupplierTest {

  private ServerProxyMap spl;

  @BeforeEach
  public void setUp() throws Exception {
    Settings s = new Settings();
    Server s1 = new Server(), s2 = new Server();
    s1.setId("1");
    s2.setId("2");
    List<Server> servers = Arrays.asList(s1, s2);
    s.setServers(servers);
    spl = new DefaultServerProxyListSupplier(s);
  }

  @Test
  public void testGet() {
    Set<String> k = spl.getServerIds();
    assertEquals(2, k.size());
  }

}
