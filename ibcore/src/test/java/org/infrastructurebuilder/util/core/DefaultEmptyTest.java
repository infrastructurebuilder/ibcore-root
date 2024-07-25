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
package org.infrastructurebuilder.util.core;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;
import java.util.SortedSet;

import org.infrastructurebuilder.api.base.NameDescribed;
import org.infrastructurebuilder.pathref.JSONOutputEnabled;
import org.infrastructurebuilder.pathref.TypeToExtensionMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class DefaultEmptyTest {

  @Test
  void testGetRelativeRoot() {
    assertFalse(new JSONOutputEnabled() {
      @Override
      public JSONObject asJSON() {
        return new JSONObject();
      }
    }.getRelativeRoot().isPresent());
  }

  @Test
  void testNameDescribed() {
    assertFalse(new NameDescribed() {
      @Override
      public String getName() {
        return null;
      }
    }.getDisplayName().isPresent());

  }
}
