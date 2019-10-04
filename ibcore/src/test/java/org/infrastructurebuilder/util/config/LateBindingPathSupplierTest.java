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
package org.infrastructurebuilder.util.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class LateBindingPathSupplierTest {

  private LateBindingPathSupplier lb;
  private Path p;
  private Path p2;

  @Before
  public void setUp() throws Exception {
    this.lb = new LateBindingPathSupplier();
    this.p = Paths.get(".");
    this.p2 = this.p.resolve(UUID.randomUUID().toString());
  }

  @Test
  public void test() {
    assertNull(this.lb.get());
    PathSupplier x2 = this.lb.withLateBoundPath(this.p);
    assertEquals(this.p, x2.get());
    assertNull(this.lb.get());
    PathSupplier x3 = this.lb.withLateBoundPath(p2);
    assertEquals(this.p2, x3.get());
    assertEquals(this.p2, x3.get());
    assertEquals(this.p, x2.get());
    assertNull(this.lb.get());

  }

}