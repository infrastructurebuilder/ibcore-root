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
package org.infrastructurebuilder.util.plexus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.infrastructurebuilder.util.plexus.DefaultArchiveFinalizer;
import org.junit.Before;
import org.junit.Test;

public class DefaultArchiveFinalizerTest {

  private static final String TEST = "test";
  private DefaultArchiveFinalizer<String> finalizer;

  @Before
  public void setUp() throws Exception {
    finalizer = new DefaultArchiveFinalizer<>(TEST);
  }

  @Test
  public void testDefaultArchiveFinalizer() {
    assertNotNull(finalizer);
  }

  @Test
  public void testFinalizeArchiveCreation() {
    finalizer.finalizeArchiveCreation(new ZipArchiver());
  }

  @Test
  public void testFinalizeArchiveExtraction() {
    finalizer.finalizeArchiveExtraction(new ZipUnArchiver());
  }

  @Test
  public void testGetConfig() {
    assertEquals(TEST, TEST, finalizer.getConfig());
  }

  @Test
  public void testGetVirtualFiles() {
    assertTrue("No virtual files", finalizer.getVirtualFiles().isEmpty());
  }

}
