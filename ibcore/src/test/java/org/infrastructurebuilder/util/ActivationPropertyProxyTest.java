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

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class ActivationPropertyProxyTest {

  private ActivationPropertyProxy app, app2;

  @Before
  public void setUp() throws Exception {
    app = new ActivationPropertyProxy("ABC", Optional.empty());
    app2 = new ActivationPropertyProxy("ABC", Optional.of("def"));
  }

  @Test
  public void testGetName() {
    assertEquals("ABC", app.getName());
  }

  @Test
  public void testGetValue() {
    assertFalse(app.getValue().isPresent());
    assertEquals("def", app2.getValue().get());
  }

}
