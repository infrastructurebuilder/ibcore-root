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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WorkingPathSupplierTest {

  private final Map<String, String> params = new HashMap<>();
  private WorkingPathSupplier w, w2;

  private ConsecutiveIDSupplier cid;

  @After
  public void after() throws Exception {
    w.finalize();
    w2.finalize();
  }

  @Before
  public void setUp() throws Exception {
    cid = new ConsecutiveIDSupplier();
    w = new WorkingPathSupplier();
    w2 = new WorkingPathSupplier(params, cid);
  }

  @Test
  public void testGet() throws IOException {
    assertTrue(Files.isDirectory(w.get()));
  }

  @Test
  public void testGetCrash() throws IOException {
    final String k = cid.getId();
    final Path pp = w2.getRoot().resolve(k);
    assertFalse(Files.isDirectory(pp));
    Files.createDirectories(pp);
    assertTrue(Files.isDirectory(w2.get()));
  }

  @Test
  public void testGetRoot() {
    final Path p = w.getRoot();
    assertTrue(Files.exists(p));
    assertTrue(Files.isDirectory(p));
  }

}
