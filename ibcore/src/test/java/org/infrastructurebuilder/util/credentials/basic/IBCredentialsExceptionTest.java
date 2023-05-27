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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class IBCredentialsExceptionTest {

  private static final String X = "X";
  private final static Logger log = LoggerFactory.getLogger(IBCredentialsExceptionTest.class);

  @Test
  void testIBCredentialsException() {
    assertNotNull(new IBCredentialsException());
  }

  @Test
  void testIBCredentialsExceptionString() {
    IBCredentialsException e = new IBCredentialsException(X);
    assertEquals(X, e.getMessage());
  }

  @Test
  void testIBCredentialsExceptionStringThrowable() {
    Throwable t = new RuntimeException();
    IBCredentialsException e = new IBCredentialsException(t);
    assertEquals(t, e.getCause());
  }

  @Test
  void testIBCredentialsExceptionThrowable() {
    Throwable t = new RuntimeException();
    IBCredentialsException e = new IBCredentialsException(X, t);
    assertEquals(t, e.getCause());
    assertEquals(X, e.getMessage());
  }
}
