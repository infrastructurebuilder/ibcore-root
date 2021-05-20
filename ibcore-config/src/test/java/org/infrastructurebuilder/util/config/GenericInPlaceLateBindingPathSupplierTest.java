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
package org.infrastructurebuilder.util.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.util.UUID;

import org.infrastructurebuilder.util.core.WorkingPathSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericInPlaceLateBindingPathSupplierTest {

  private WorkingPathSupplier wps;
  private GenericInPlaceLateBindingPathSupplier gps;

  @BeforeEach
  public void setUp() throws Exception {
    this.wps = new WorkingPathSupplier();
    this.gps = new GenericInPlaceLateBindingPathSupplier();
  }

  @Test
  public void test() {
    Path p = wps.get();
    assertNull(gps.get());
    gps.setT(p);
    assertEquals(p, gps.get());
    gps.setT(p.resolve(UUID.randomUUID().toString()));
    assertEquals(p, gps.get());

  }

}
