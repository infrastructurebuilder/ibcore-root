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
package org.infrastructurebuilder.util.artifacts;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IBVersionExceptionTest {

  private static final String MESSAGE = "Message";

  @Test
  public void testIBVersionExceptionString() {
    assertNotNull(new IBVersionException(MESSAGE));
  }

  @Test
  public void testIBVersionExceptionStringThrowable() {
    assertNotNull(new IBVersionException(MESSAGE, new RuntimeException()));
  }

  @Test
  public void testIBVersionExceptionStringThrowableBooleanBoolean() {
    assertNotNull(new IBVersionException(MESSAGE, new RuntimeException(), false, false));
  }

  @Test
  public void testIBVersionExceptionThrowable() {
    assertNotNull(new IBVersionException(new RuntimeException()));
  }

}
