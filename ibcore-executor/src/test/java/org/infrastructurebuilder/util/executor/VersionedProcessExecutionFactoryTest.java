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
package org.infrastructurebuilder.util.executor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VersionedProcessExecutionFactoryTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  private VersionedProcessExecutionFactory ef;

  @BeforeEach
  void setUp() throws Exception {
    ef = new VersionedProcessExecutionFactory() {

      @Override
      public Path getScratchDir() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Optional<ProcessExecutionFactory> getFactoryForVersion(String version, PathRefPath workDirectory, String id,
          String executable) {
        // TODO Auto-generated method stub
        return Optional.empty();
      }

      @Override
      public ProcessExecutionFactory getDefaultFactory(PathRefPath workDirectory, String id, String executable) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

  @Test
  void testGetAddl() {
    assertTrue(ef.getAddl().isEmpty());
  }

}
