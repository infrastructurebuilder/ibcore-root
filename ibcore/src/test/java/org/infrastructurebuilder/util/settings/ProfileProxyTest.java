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
package org.infrastructurebuilder.util.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.util.settings.ProfileProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProfileProxyTest {

  private static final String ID = "id";
  private ProfileProxy pp;

  @BeforeEach
  public void setUp() throws Exception {
    pp = new ProfileProxy(ID, true, Optional.empty(), Collections.emptyList(), new Properties(),
        Collections.emptyList());
  }

  @Test
  public void testGetId() {
    assertEquals(ID, pp.getId());
  }

  @Test
  public void testIsActive() {
    assertTrue(pp.isActive());
  }

  @Test
  public void testGetActivation() {
    assertFalse(pp.getActivation().isPresent());
  }

  @Test
  public void testGetPluginRepositories() {
    assertEquals(0,pp.getPluginRepositories().size());
  }

  @Test
  public void testGetProperties() {
    assertEquals(0,pp.getProperties().size());
  }

  @Test
  public void testGetRepositories() {
    assertEquals(0,pp.getRepositories().size());
  }

}
