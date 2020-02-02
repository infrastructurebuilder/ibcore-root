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

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.util.Optional;
import java.util.SortedSet;

import org.infrastructurebuilder.util.BasicCredentials;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

public class IBRuntimeUtilsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private IBRuntimeUtils t;

  @Before
  public void setUp() throws Exception {
    t = new IBRuntimeUtils() {

      @Override
      public Optional<BasicCredentials> getCredentialsFor(String query) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Logger getLog() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public String getExtensionForType(String type) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public SortedSet<String> reverseMapFromExtension(String extension) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Path getWorkingPath() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public GAV getWorkingGAV() {
        // TODO Auto-generated method stub
        return null;
      }

    };
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetDependencies() {
    assertEquals(0,t.getDependencies().size());
  }

}
