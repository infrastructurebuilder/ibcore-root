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
package org.infrastructurebuilder.util.credentials.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.infrastructurebuilder.util.credentials.basic.DefaultBasicCredentials;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class BasicCredentialsImplTest {
  static final String PASSWORD  = "Y";
  static final String PRINCIPAL = "X";

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private DefaultBasicCredentials b;
  private DefaultBasicCredentials d;
  private DefaultBasicCredentials e;
  private DefaultBasicCredentials i, j, k;
  private DefaultBasicCredentials x;
  private DefaultBasicCredentials y;
  private DefaultBasicCredentials z;

  @BeforeEach
  public void setUp() throws Exception {
    x = new DefaultBasicCredentials(PRINCIPAL, Optional.ofNullable(PASSWORD));
    y = new DefaultBasicCredentials(PRINCIPAL, Optional.ofNullable(PASSWORD));
    z = new DefaultBasicCredentials("A", Optional.ofNullable("B"));

    b = new DefaultBasicCredentials(PRINCIPAL, Optional.ofNullable(null));
    d = new DefaultBasicCredentials(PRINCIPAL, Optional.ofNullable(null));

    e = new DefaultBasicCredentials(PRINCIPAL, Optional.ofNullable("ABC"));

    i = new DefaultBasicCredentials("ABC", Optional.ofNullable(PASSWORD));
    j = new DefaultBasicCredentials("ABC", Optional.ofNullable(PASSWORD));

  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testAbstractCredentialsImpl() {
    assertNotNull(x);
  }

  @Test
  public void testEqualsAndHash() {

    assertEquals(x.hashCode(), y.hashCode(), "Hash of equals are equal");
    assertEquals(x, x, "Equal are equal");
    assertEquals(x, y, "Equal are equal");
    assertNotEquals(x, null, "Not vs null");
    assertNotEquals("Not vs string", x, "ABC");
    assertEquals(b, d, "B is d");

    assertNotEquals(d, e, "D is not e");

    assertNotEquals(x, i, "X is not I");
    assertEquals(i, j, "I is J");

    assertNotEquals(k, j, "K is not J");

  }

  @Test
  public void testGetPassword() {
    assertEquals(PASSWORD, x.getSecret().get());
  }

  @Test
  public void testGetPrincipal() {
    assertEquals(PRINCIPAL, x.getKeyId());
  }

}
