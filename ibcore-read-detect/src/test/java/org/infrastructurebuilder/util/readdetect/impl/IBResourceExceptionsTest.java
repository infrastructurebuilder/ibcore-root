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
package org.infrastructurebuilder.util.readdetect.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.readdetect.IBResourceException;
import org.junit.jupiter.api.Test;

class IBResourceExceptionsTest {

  @Test
  void testIBWGetExceptions() {
    assertNotNull(new IBWGetException());
    assertNotNull(new IBWGetException("string"));
    assertNotNull(new IBWGetException(new IBException()));
  }

  @Test
  void testIBWResourceExceptions() {
    assertNotNull(new IBResourceException("string"));
    assertNotNull(new IBResourceException("String", new IBException()));
  }

}
