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

import org.infrastructurebuilder.exceptions.IBException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MultiTypeTest {

  private static final String X = "X";
  private final static Logger log = LoggerFactory.getLogger(MultiTypeTest.class);

  private final static class MOT extends MultiType<String, Throwable> {

    public MOT(String typed, Throwable thrown) {
      super(typed, thrown);
    }

    public MOT(String typed) {
      super(typed);
    }

    public MOT(Throwable thrown) {
      super(thrown);
    }

  }

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private MOT mot1;
  private MOT mot2;
  private MOT mot3;

  @BeforeEach
  void setUp() throws Exception {
    mot1 = new MOT(X);
    mot2 = new MOT(new IBException(X));
    mot3 = new MOT(X, new IBException(X));
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testGetException() {
    assertFalse(mot1.getException().isPresent());
    assertEquals(X, mot2.getException().get().getMessage());
  }

  @Test
  void testGetT() {
    assertFalse(mot2.getT().isPresent());
    assertEquals(X, mot3.getT().get());
  }

}
