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
package org.infrastructurebuilder.util.extensionmapper.basic;

import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_XML;
import static org.infrastructurebuilder.util.constants.IBConstants.DEFAULT_EXTENSION;
import static org.infrastructurebuilder.util.constants.IBConstants.ORG_W3C_DOM_NODE;
import static org.infrastructurebuilder.util.constants.IBConstants.VIDEO_AVI_1;
import static org.infrastructurebuilder.util.constants.IBConstants.XML;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.SortedSet;

import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultTypeToExtensionMapperTest {

  private TypeToExtensionMapper t2e;

  @BeforeEach
  public void setUp() throws Exception {
    t2e = new DefaultTypeToExtensionMapper();
  }

  @Test
  public void testGetExtensionForType() {
    assertEquals(XML, t2e.getExtensionForType(APPLICATION_XML));
    assertEquals(DEFAULT_EXTENSION, t2e.getExtensionForType("text/whackadoodle"));
  }

  @Test
  public void testReverseMap1() {
    SortedSet<String> t = t2e.reverseMapFromExtension(XML);
    assertEquals(4, t.size());
    assertTrue(t.contains(APPLICATION_XML));
  }

  @Test
  public void testStructuredType() {
    assertEquals(ORG_W3C_DOM_NODE, t2e.getStructuredSupplyTypeClassName(APPLICATION_XML).get());
    assertFalse(t2e.getStructuredSupplyTypeClassName(VIDEO_AVI_1).isPresent());
  }

}
