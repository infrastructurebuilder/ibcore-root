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
package org.infrastructurebuilder.util.files;

import static org.infrastructurebuilder.util.files.IBCoreReadDetectResponse.copyToDeletedOnExitTempChecksumAndPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;

import org.infrastructurebuilder.IBConstants;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.junit.Before;
import org.junit.Test;

public class IBCoreReadDetectResponseTest {

  private static final String EXPECTED = "c9ad762d49d57970dde4d10a279fb98b8b7602f845a2ed3206d902fc85a176376eda2d63706ebd06bb319cecd7043dbfd61f2132825413f0879e1938d331b237";
  private WorkingPathSupplier wps;

  @Before
  public void setUp() throws Exception {
    this.wps = new WorkingPathSupplier();
  }

  @Test
  public void testBasicType() {
    assertEquals(IBConstants.APPLICATION_OCTET_STREAM,
        new BasicIBChecksumPathType(Paths.get("."), new Checksum()).getType());
  }

  @Test
  public void testCopyToDeletedOnExitTempChecksumAndPathWithTarget() throws IOException {
    new IBCoreReadDetectResponse();
    try (InputStream ins = getClass().getResourceAsStream("/testfile.test")) {
      IBChecksumPathType cset = copyToDeletedOnExitTempChecksumAndPath(Optional.of(this.wps.get()), "A", "B", ins);
      assertEquals(7, cset.getPath().toFile().length());
      assertEquals(EXPECTED, cset.getChecksum().toString());
      assertEquals("text/plain", cset.getType());
      assertEquals(EXPECTED, new Checksum(cset.get()).toString());
      assertTrue(cset.getPath().toString().startsWith(this.wps.getRoot().toString()));
    }
  }

  @Test
  public void testCopyToDeletedOnExitTempChecksumAndPathWithoutTarget() throws IOException {
    new IBCoreReadDetectResponse();
    try (InputStream ins = getClass().getResourceAsStream("/testfile.test")) {
      IBChecksumPathType cset = copyToDeletedOnExitTempChecksumAndPath(Optional.empty(), "A", "B", ins);
      assertEquals(7, cset.getPath().toFile().length());
      assertEquals(EXPECTED, cset.getChecksum().toString());
      assertEquals("text/plain", cset.getType());
      assertEquals(EXPECTED, new Checksum(cset.get()).toString());
      assertFalse(cset.getPath().toString().startsWith(this.wps.getRoot().toString()));
    }
  }

}
