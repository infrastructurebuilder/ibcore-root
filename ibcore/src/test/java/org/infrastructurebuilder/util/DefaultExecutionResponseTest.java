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

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DefaultExecutionResponseTest {

  private List<String> errors;
  private DefaultExecutionResponse<String, Integer> k;
  private int i;

  @Before
  public void setUp() throws Exception {
    errors = Arrays.asList("A", "B");
    i = -1;
    k = new DefaultExecutionResponse<>(new Integer(i), errors);
  }

  @Test
  public void testGetErrors() {
    assertEquals(Arrays.asList("A","B"), k.getErrors());
  }

  @Test
  public void testGetResponseValue() {
    assertEquals(-1, k.getResponseValue().intValue());
  }

}