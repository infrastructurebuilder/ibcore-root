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

import static org.infrastructurebuilder.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.IBConstants.TEXT_PLAIN;
import static org.infrastructurebuilder.util.files.DefaultIBResource.copyToDeletedOnExitTempChecksumAndPath;
import static org.infrastructurebuilder.util.files.DefaultIBResource.copyToTempChecksumAndPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.IBConstants;
import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.junit.Before;
import org.junit.Test;

public class IBCoreReadDetectResponseTest {

  private static final String TESTFILE_TEST = "testfile.test";
  private static final String TFILE_TEST = "tfile.zip";
  private static final String EXPECTED = "c9ad762d49d57970dde4d10a279fb98b8b7602f845a2ed3206d902fc85a176376eda2d63706ebd06bb319cecd7043dbfd61f2132825413f0879e1938d331b237";
  private TestingPathSupplier wps;

  @Before
  public void setUp() throws Exception {
    this.wps = new TestingPathSupplier();
  }

  @Test
  public void testBasicType() {
    assertEquals(APPLICATION_OCTET_STREAM,
        DefaultIBResource.from(Paths.get("."), new Checksum(), APPLICATION_OCTET_STREAM).getType());
  }

  @Test(expected = IBException.class)
  public void testNonExistentFile() {
    DefaultIBResource.toType.apply(Paths.get(".").resolve(UUID.randomUUID().toString()));
  }
//https://file-examples.com/wp-content/uploads/2017/02/zip_2MB.zip


  @Test
  public void testOtherCopyToDeletedOnExitTempChecksumAndPathWithTarget() throws IOException {
    Path t = this.wps.getTestClasses().resolve(TFILE_TEST);
    URI uri = t.toUri();
    URL l = uri.toURL();
    String ef = l.toExternalForm();

    IBResource cset = copyToTempChecksumAndPath(this.wps.get(), t, Optional.of("zip:" + ef), TESTFILE_TEST);
    assertEquals(183, cset.getPath().toFile().length());
    assertEquals("11220f09966021668749276fdd1998f95937c85d07eb2d7f0deb15790bbcf325bfcdf07cfa35570b49ec197b872dfff7d68b6fd3b39bb11916757bff97d19f2d", cset.getChecksum().toString());
    assertEquals(IBConstants.APPLICATION_ZIP, cset.getType());
    assertEquals("11220f09966021668749276fdd1998f95937c85d07eb2d7f0deb15790bbcf325bfcdf07cfa35570b49ec197b872dfff7d68b6fd3b39bb11916757bff97d19f2d", new Checksum(cset.get()).toString());
    assertTrue(cset.getPath().toString().startsWith(this.wps.getRoot().toString()));
    assertTrue(cset.getSourceURL().get().toExternalForm().startsWith("jar:file:"));
  }

  @Test
  public void testCopyToDeletedOnExitTempChecksumAndPathWithTarget() throws IOException {
    Path t = this.wps.getTestClasses().resolve(TESTFILE_TEST);
    IBResource cset = copyToTempChecksumAndPath(this.wps.get(), t);
    assertEquals(7, cset.getPath().toFile().length());
    assertEquals(EXPECTED, cset.getChecksum().toString());
    assertEquals(TEXT_PLAIN, cset.getType());
    assertEquals(EXPECTED, new Checksum(cset.get()).toString());
    assertTrue(cset.getPath().toString().startsWith(this.wps.getRoot().toString()));
  }

  @Test
  public void testCopyToDeletedOnExitTempChecksumAndPathWithoutTarget() throws IOException {
    try (InputStream ins = Files.newInputStream(this.wps.getTestClasses().resolve(TESTFILE_TEST))) {
      IBResource cset = copyToDeletedOnExitTempChecksumAndPath(wps.get(), "A", "B", ins);
      assertEquals(7, cset.getPath().toFile().length());
      assertEquals(EXPECTED, cset.getChecksum().toString());
      assertEquals(TEXT_PLAIN, cset.getType());
      assertEquals(EXPECTED, new Checksum(cset.get()).toString());
    }
  }

  @Test
  public void testSecondarConstructor() {
    Path f = this.wps.getTestClasses().resolve(TESTFILE_TEST);
    DefaultIBResource g = new DefaultIBResource(f, new Checksum(f), Optional.empty());
    assertEquals(TEXT_PLAIN, g.getType());
  }

}
