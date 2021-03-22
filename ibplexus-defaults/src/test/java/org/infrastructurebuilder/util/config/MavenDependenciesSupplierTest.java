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


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.infrastructurebuilder.util.artifacts.GAV;
import org.junit.jupiter.api.Test;

public class MavenDependenciesSupplierTest extends AbstractPlexusDefaultsConfigTest {

  @Test
  public void test() {
    MavenDependenciesSupplier v = new MavenDependenciesSupplier(mp);
    List<GAV> a = v.get();
    assertEquals(1, a.size());
    assertEquals(destination, a.get(0).getFile().get().toFile());
  }

}