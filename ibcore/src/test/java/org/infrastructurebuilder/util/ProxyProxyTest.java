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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class ProxyProxyTest {

  private static final String USERNAME = "username";
  private static final String HTTPS = "https";
  private static final String PASSWORD = "password";
  private static final String ONE_COM = "one.com";
  private static final String DEF_COM = "def.com";
  private static final String ABC = "ABC";
  private ProxyProxy pp;

  @Before
  public void setUp() throws Exception {
    pp = new ProxyProxy(ABC, DEF_COM, Arrays.asList(ONE_COM), Optional.of(PASSWORD), 8081, HTTPS, Optional.of(USERNAME),
        true);
  }

  @Test
  public void testGetId() {
    assertEquals(ABC, pp.getId());
  }

  @Test
  public void testGetHost() {
    assertEquals(DEF_COM, pp.getHost());
  }

  @Test
  public void testGetNonProxyHosts() {
    assertEquals(ONE_COM, pp.getNonProxyHosts().get(0));
  }

  @Test
  public void testGetPassword() {
    assertEquals(PASSWORD, pp.getPassword().get());
  }

  @Test
  public void testGetPort() {
    assertEquals(8081,pp.getPort());
  }

  @Test
  public void testGetProtocol() {
    assertEquals(HTTPS, pp.getProtocol());
  }

  @Test
  public void testGetUsername() {
    assertEquals(USERNAME,pp.getUsername().get());
  }

  @Test
  public void testIsActive() {
    assertTrue(pp.isActive());
  }

}
