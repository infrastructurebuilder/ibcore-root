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
package org.infrastructurebuilder.util.dag;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class DAGWalkerExceptionTest {

  @Test
  public void testDAGWalkerException() {
    assertNotNull(new DAGWalkerException());
    assertNotNull(new DAGWalkerException("test"));
    assertNotNull(new DAGWalkerException("TEST", new RuntimeException(), false, false));
    assertNotNull(new DAGWalkerException("TEST", new RuntimeException()));
    assertNotNull(new DAGWalkerException(new RuntimeException()));
  }
}
