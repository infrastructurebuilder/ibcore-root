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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("unused")
public class BasicCredentialsImplTest {
  static final String PASSWORD = "Y";
  static final String PRINCIPAL = "X";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private DefaultBasicCredentials b;
  private DefaultBasicCredentials d;
  private DefaultBasicCredentials e;
  private DefaultBasicCredentials i, j, k;
  private DefaultBasicCredentials x;
  private DefaultBasicCredentials y;
  private DefaultBasicCredentials z;

  @Before
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

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testAbstractCredentialsImpl() {
    assertNotNull("Created one", x);
  }

  @Test
  public void testEqualsAndHash() {

    assertEquals("Hash of equals are equal", x.hashCode(), y.hashCode());
    assertEquals("Equal are equal", x, x);
    assertEquals("Equal are equal", x, y);
    assertNotEquals("Not vs null", x, null);
    assertNotEquals("Not vs string", x, "ABC");
    assertEquals("B is d", b, d);

    assertNotEquals("D is not e", d, e);

    assertNotEquals("X is not I", x, i);
    assertEquals("I is J", i, j);

    assertNotEquals("K is not J", k, j);

  }

  @Test
  public void testGetPassword() {
    assertEquals("Password is " + PASSWORD, PASSWORD, x.getSecret().get());
  }

  @Test
  public void testGetPrincipal() {
    assertEquals("Principal is " + PRINCIPAL, PRINCIPAL, x.getKeyId());
  }

}
