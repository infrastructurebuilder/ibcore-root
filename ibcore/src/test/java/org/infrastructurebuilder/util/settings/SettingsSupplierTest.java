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

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SettingsSupplierTest {
  private final static TestingPathSupplier wps = new TestingPathSupplier();

  private List<ServerProxy> servers;
  private SettingsSupplier s;
  private ServerProxy s1, s2;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  public void setUp() throws Exception {
    s1 = new ServerProxy();
    s2 = new ServerProxy("two", of("SA"), of("Secret"), empty(), empty(), empty(), empty(), empty());
    servers = Arrays.asList(s1, s2);
    s = new SettingsSupplier() {

      @Override
      public SettingsProxy get() {
        return new SettingsProxy(false, wps.get(), Charset.defaultCharset(), servers, emptyList(), emptyList(),
            emptyList(), emptyList());
      }
    };
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    assertFalse(s.getCredentialsFor("A").isPresent());
    assertTrue(s.getCredentialsFor("two").isPresent());
  }

}
