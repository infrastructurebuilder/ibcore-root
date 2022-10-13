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

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.System.Logger;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.infrastructurebuilder.util.versions.IBVersionsSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IBRuntimeUtilsTest {
  public final static Logger log = System.getLogger(IBRuntimeUtilsTest.class.toString());

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private IBRuntimeUtils t;

  @BeforeEach
  public void setUp() throws Exception {
    t = new IBRuntimeUtils() {

      @Override
      public Optional<BasicCredentials> getCredentialsFor(String query) {
        return empty();
      }

      @Override
      public Logger getLog() {
        return log;
      }

      @Override
      public String getExtensionForType(String type) {
        return null;
      }

      @Override
      public SortedSet<String> reverseMapFromExtension(String extension) {
        return Collections.emptySortedSet();
      }

      @Override
      public Path getWorkingPath() {
        return null;
      }

      @Override
      public GAV getGAV() {
        return null;
      }

      @Override
      public Optional<String> getDescription() {
        return empty();
      }

      @Override
      public Optional<String> getStructuredSupplyTypeClassName(String type) {
        return empty();
      }

      @Override
      public List<IBVersionsSupplier> getMatchingArtifacts(String groupId, String artifactId) {
        return Collections.emptyList();
      }

    };
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetDependencies() {
    assertEquals(0,t.getDependencies().size());
  }

}
