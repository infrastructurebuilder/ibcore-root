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

import java.util.Arrays;
import java.util.List;

import org.infrastructurebuilder.util.core.DefaultExecutionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultExecutionResponseTest {

  private List<String> errors;
  private DefaultExecutionResponse<String, Integer> k;
  private int i;

  @BeforeEach
  public void setUp() throws Exception {
    errors = Arrays.asList("A", "B");
    i = -1;
    k = new DefaultExecutionResponse<>(Integer.valueOf(i), errors);
  }

  @Test
  public void testGetErrors() {
    assertEquals(Arrays.asList("A", "B"), k.getErrors());
  }

  @Test
  public void testGetResponseValue() {
    assertEquals(-1, k.getResponseValue().intValue());
  }

}
