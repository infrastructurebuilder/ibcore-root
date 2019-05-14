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

import org.junit.Test;

public class CryptoExceptionTest {

  private static final String STRING = "SOMESTRING";

  @Test
  public void testIBCryptoException() {
    assertNotNull("Empty", new IBCryptoException());
  }

  @Test
  public void testIBCryptoExceptionString() {
    final IBCryptoException x = new IBCryptoException(STRING);
    assertNotNull("String", x);
    assertEquals("String same", STRING, x.getMessage());
  }

  @Test
  public void testIBCryptoExceptionStringThrowable() {
    final Throwable t = new NullPointerException();
    final IBCryptoException x = new IBCryptoException(STRING, t);
    assertNotNull("Throwable", x);
    assertEquals("String same", STRING, x.getMessage());
    assertEquals("Same throwable", t, x.getCause());
  }

  @Test
  public void testIBCryptoExceptionStringThrowableBooleanBoolean() {
    final Throwable t = new NullPointerException();
    final IBCryptoException x = new IBCryptoException(STRING, t, false, false);
    assertNotNull("Throwable", x);
    assertEquals("String same", STRING, x.getMessage());
    assertEquals("Same throwable", t, x.getCause());
  }

  @Test
  public void testIBCryptoExceptionThrowable() {
    final Throwable t = new NullPointerException();
    final IBCryptoException x = new IBCryptoException(t);
    assertNotNull("Throwable", x);
    assertEquals("Same throwable", t, x.getCause());
  }

}
