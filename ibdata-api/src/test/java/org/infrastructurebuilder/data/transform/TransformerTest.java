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
package org.infrastructurebuilder.data.transform;

import static org.junit.Assert.*;

import org.infrastructurebuilder.IBConstants;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.junit.Before;
import org.junit.Test;

public class TransformerTest {

  private Transformer t, t1;
  private Transformer t2;
  private Transformation transformation;

  @Before
  public void setUp() throws Exception {
    transformation = new Transformation();
    t = new Transformer();
    t2 = new Transformer(t, transformation);
  }

  @Test
  public void testSimpleGetSet() {
    assertNull(t.getHint());
    t.setId("a");
    assertEquals("a", t.getHint());
    t.setHint("B");
    assertEquals("B", t.getHint());
    ConfigMap c = new ConfigMap();
    c.put("A", "B");
    assertNotNull(t.getConfiguration());
    t.setConfiguration(c);
    assertEquals("B", t.getConfiguration().get("A"));
    assertEquals(IBConstants.APPLICATION_OCTET_STREAM, t.getTargetMimeType());
    t.setTargetMimeType(IBConstants.APPLICATION_PDF);
    assertEquals(IBConstants.APPLICATION_PDF, t.getTargetMimeType());
    assertTrue(t.isFailOnAnyError());
    t.setFailOnAnyError(false);
    assertFalse(t.isFailOnAnyError());
    assertNull(t.getTransformation());
    t1 = t.copy(transformation);
    assertEquals(transformation, t1.getTransformation());
  }

  @Test(expected = NullPointerException.class)
  public void testSetConfiguration() {
    t.setConfiguration(null);
  }
}
