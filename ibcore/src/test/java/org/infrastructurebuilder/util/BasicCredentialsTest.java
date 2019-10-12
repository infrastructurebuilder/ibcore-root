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
import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class BasicCredentialsTest {

  private BasicCredentials creds;

  @Before
  public void setUp() throws Exception {
    creds = new BasicCredentials() {

      @Override
      public String getKeyId() {
        return "1";
      }

      @Override
      public Optional<String> getSecret() {
        return Optional.of("secret");
      }

    };
  }

  @Test
  public void testGetPassword() {
    assertEquals("secret", creds.getSecret().get());
  }

  @Test
  public void testGetPrincipal() {
    assertEquals("1", creds.getKeyId());
  }

  @Test
  public void testGet() {
    assertNotNull(creds.get());
  }

}
