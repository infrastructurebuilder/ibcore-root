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
package org.infrastructurebuilder.pathref;

import static org.infrastructurebuilder.pathref.ChecksumEnabled.safeMapUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChecksumEnabledTest {

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private UUID t;

  @BeforeEach
  public void setUp() throws Exception {
    t = UUID.randomUUID();
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    assertFalse(safeMapUUID.apply(null).isPresent());
    assertEquals(safeMapUUID.apply(t.toString()).get().toString(), t.toString());
    assertNull(ChecksumEnabled.nullableSafeMapUUID.apply(null));
  }

}
