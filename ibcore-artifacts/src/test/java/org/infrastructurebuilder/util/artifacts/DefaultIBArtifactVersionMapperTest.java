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
package org.infrastructurebuilder.util.artifacts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.versions.IBVersionsSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.System; import java.lang.System.Logger;


public class DefaultIBArtifactVersionMapperTest {

  public final static Logger log = System.getLogger(DefaultIBArtifactVersionMapperTest.class.getName());

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private Map<String, IBVersionsSupplier> ibvs;
  private DefaultIBArtifactVersionMapper v;
  private IBVersionsSupplier vs;

  @BeforeEach
  public void setUp() throws Exception {
    vs = new IBVersionsSupplier() {

      @Override
      public Supplier<String> getVersion() {
        return () -> "1.2.3";
      }

      @Override
      public Supplier<String> getGroupId() {
        return () -> "g";
      }

      @Override
      public Supplier<String> getExtension() {
        return () -> "jar";
      }

      @Override
      public Supplier<String> getArtifactId() {
        return () -> "a";
      }

      @Override
      public Supplier<String> getAPIVersion() {
        return () -> "1.2";
      }
    };
    ibvs = new HashMap<>();
    ibvs.put("ABC", vs);
    v = new DefaultIBArtifactVersionMapper( ibvs);
  }

  @AfterEach
  public void tearDown() throws Exception {
  }


  @Test
  public void testGetMatchingArtifacts() {
    List<IBVersionsSupplier> k = v.getMatchingArtifacts("A", "B");
    assertEquals(0, k.size());
    k = v.getMatchingArtifacts("g", "a");
    assertEquals(1, k.size());
  }

}
