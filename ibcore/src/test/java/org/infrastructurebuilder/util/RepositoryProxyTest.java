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

import static org.junit.Assert.*;

import java.net.URL;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;


public class RepositoryProxyTest {

  private static final String NAME = "name";
  private static final String HTTP_WWW_GOOGLE_COM = "http://www.google.com";
  private RepositoryPolicyProxy rpp;
  private RepositoryProxy rp;

  @Before
  public void setUp() throws Exception {
    rpp = new RepositoryPolicyProxy(true, ChecksumPolicy.IGNORE, UpdatePolicy.NEVER, 1);
    rp = new RepositoryProxy("ABC", Layout.DEFAULT, Optional.of(NAME), new URL(HTTP_WWW_GOOGLE_COM), Optional.of(rpp), Optional.of(rpp));
  }

  @Test
  public void testGetId() {
    assertEquals("ABC", rp.getId());
  }

  @Test
  public void testGetLayout() {
    assertEquals(Layout.DEFAULT, rp.getLayout());
  }

  @Test
  public void testGetName() {
    assertEquals(NAME, rp.getName().get());
  }

  @Test
  public void testGetUrl() {
    assertEquals(HTTP_WWW_GOOGLE_COM, rp.getUrl().toExternalForm());
  }

  @Test
  public void testGetReleases() {
    assertEquals(rpp, rp.getReleases().get());
  }

  @Test
  public void testGetSnapshots() {
    assertEquals(rpp, rp.getSnapshots().get());
  }

}
