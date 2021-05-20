/*
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
package org.infrastructurebuilder.util.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.infrastructurebuilder.exceptions.IBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultiReturnTest {

  private static class COT extends MultiReturn<String, Integer, IBException> {

    public COT(final IBException thrown) {
      super(thrown);
    }

    public COT(final Integer retVal, final IBException thrown) {
      super(retVal, thrown);
    }

    public COT(final String typed, final Integer retVal, final IBException thrown) {
      super(typed, retVal, thrown);
    }

  }

  private COT a;
  private COT b;
  private COT c;
  private IBException e;

  @BeforeEach
  public void setUp() throws Exception {
    e = new IBException();
    a = new COT("X", 1, null);
    b = new COT(2, e);
    c = new COT(e);
  }

  @Test
  public void testGetException() {
    assertEquals(e, b.getException().get());
    assertFalse(a.getException().isPresent());
  }

  @Test
  public void testGetReturnedType() {
    assertFalse(c.getT().isPresent());
    assertEquals(e, c.getException().get());
    assertFalse(b.getT().isPresent());
    assertEquals("X", a.getT().get());
  }

  @Test
  public void testGetReturnValue() {
    assertEquals(2, (int) b.getReturnValue());
    assertEquals(1, (int) a.getReturnValue());
  }

  @Test
  public void testMultiReturn() {
    assertNotNull(a);
    assertNotNull(b);
  }

}
