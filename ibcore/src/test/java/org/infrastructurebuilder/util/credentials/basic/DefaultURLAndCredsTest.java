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
package org.infrastructurebuilder.util.credentials.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultURLAndCredsTest {

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private DefaultURLAndCreds d;
  private DefaultURLAndCreds e;
  private CredentialsFactory cf;

  @BeforeEach
  public void setUp() throws Exception {
    d = new DefaultURLAndCreds("url");
    e = new DefaultURLAndCreds("url2", Optional.of("A"));
    cf = new CredentialsFactory() {
      @Override
      public Optional<BasicCredentials> getCredentialsFor(String query) {
        return Optional.ofNullable("A".equals(query) ? new DefaultBasicCredentials("A", Optional.of("B")) : null);
      }
    };
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetUrl() {
    assertEquals("url", d.getUrl());
  }

  @Test
  public void testGetCreds() {
    assertFalse(d.getCredentialsQuery().isPresent());
    assertEquals("A", cf.getCredentialsFor(e.getCredentialsQuery().get()).get().getKeyId());
  }

  @Test
  public void testToString() {
    assertEquals("DefaultURLAndCreds [url=url, query=Optional.empty]", d.toString());
  }

}
