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
package org.infrastructurebuilder.util.readdetect;

import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_ZIP;
import static org.infrastructurebuilder.util.constants.IBConstants.TEXT_PLAIN;
import static org.infrastructurebuilder.util.readdetect.IBResourceCacheFactory.toType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.readdetect.impl.DefaultIBResource;
import org.infrastructurebuilder.util.readdetect.impl.IBResourceCacheFactoryImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IBResourceTest {

  private static final String CHECKSUMVAL = "11220f09966021668749276fdd1998f95937c85d07eb2d7f0deb15790bbcf325bfcdf07cfa35570b49ec197b872dfff7d68b6fd3b39bb11916757bff97d19f2d";
  private static final String TESTFILE_TEST = "testfile.test";
  private static final String TFILE_TEST = "tfile.zip";
  private static final String EXPECTED = "c9ad762d49d57970dde4d10a279fb98b8b7602f845a2ed3206d902fc85a176376eda2d63706ebd06bb319cecd7043dbfd61f2132825413f0879e1938d331b237";
  private TestingPathSupplier wps;
  private Path testFile;

  private IBResourceCacheFactory rcf ;
  private Path root;
  @BeforeEach
  public void setUp() throws Exception {
    this.wps = new TestingPathSupplier();
    testFile = this.wps.getTestClasses().resolve(TFILE_TEST);
    this.root = this.wps.get();
    this.rcf = new IBResourceCacheFactoryImpl(this.root);
  }

  @Test
  public void testBasicType() {
    assertEquals(APPLICATION_OCTET_STREAM,
        IBResourceFactory.from(Paths.get("."), new Checksum(), APPLICATION_OCTET_STREAM).getType());
  }

  @Test
  public void testNonExistentFile() {
    assertThrows(IBException.class,
        () -> toType.apply(Paths.get(".").resolve(UUID.randomUUID().toString())));
  }

  @Test
  public void testFailOnNonFile() {
    assertThrows(IBException.class, () -> toType.apply(Paths.get(".")));
  }
//https://file-examples.com/wp-content/uploads/2017/02/zip_2MB.zip

//  @Test
//  public void testOtherCopyToDeletedOnExitTempChecksumAndPathWithTarget() throws IOException {
//    URI uri = testFile.toUri();
//    URL l = uri.toURL();
//    String ef = l.toExternalForm();
//
//    IBResource cset = IBResourceFactory.copyToTempChecksumAndPath(this.wps.get(), testFile, Optional.of("zip:" + ef),
//        TESTFILE_TEST);
//    assertEquals(183, cset.getPath().get().toFile().length());
//    assertEquals(CHECKSUMVAL, cset.getChecksum().toString());
//    assertEquals(APPLICATION_ZIP, cset.getType());
//    assertEquals(CHECKSUMVAL, new Checksum(cset.get().get()).toString());
//    assertTrue(cset.getPath().toString().startsWith(this.wps.getRoot().toString()));
//    assertTrue(cset.getSourceURL().get().toExternalForm().startsWith("jar:file:"));
//  }

//  @Test
//  public void testCopyToDeletedOnExitTempChecksumAndPathWithTarget() throws IOException {
//    Path t = this.wps.getTestClasses().resolve(TESTFILE_TEST);
//    IBResource cset = IBResourceFactory.copyToTempChecksumAndPath(this.wps.get(), t);
//    assertEquals(7, cset.getPath().get().toFile().length());
//    assertEquals(EXPECTED, cset.getChecksum().toString());
//    assertEquals(TEXT_PLAIN, cset.getType());
//    assertEquals(EXPECTED, new Checksum(cset.get().get()).toString());
//    assertTrue(cset.getPath().toString().startsWith(this.wps.getRoot().toString()));
//  }

//  @Test
//  public void testCopyToDeletedOnExitTempChecksumAndPathWithoutTarget() throws IOException {
//    try (InputStream ins = Files.newInputStream(this.wps.getTestClasses().resolve(TESTFILE_TEST))) {
//      IBResource cset = IBResourceFactory.copyToDeletedOnExitTempChecksumAndPath(wps.get(), "A", "B", ins);
//      assertEquals(7, cset.getPath().get().toFile().length());
//      assertEquals(EXPECTED, cset.getChecksum().toString());
//      assertEquals(TEXT_PLAIN, cset.getType());
//      assertEquals(EXPECTED, new Checksum(cset.get().get()).toString());
//    }
//  }

//  @Test
//  public void testSecondarConstructor() {
//    Path f = this.wps.getTestClasses().resolve(TESTFILE_TEST);
//    DefaultIBResource g = new DefaultIBResource(f, new Checksum(f), Optional.empty());
//    assertEquals(TEXT_PLAIN, g.getType());
//  }

  @Test
  public void testFromPath() {
    IBResource cset = this.rcf.fromPath(testFile).get();
    long d = new Date().toInstant().toEpochMilli();
    InputStream g = cset.get().get();
    cet.translate(() -> g.close());
    assertTrue(cset.getMostRecentReadTime().toEpochMilli() - d < 3);

    assertEquals(183, cset.getPath().get().toFile().length());
    assertEquals(CHECKSUMVAL, cset.getChecksum().toString());
    assertEquals(APPLICATION_ZIP, cset.getType());
    assertEquals(CHECKSUMVAL, new Checksum(cset.get().get()).toString());

  }

//  @Test
//  public void testJSONFromPath() {
//    IBResource cset = this.rcf.fromPath(testFile).get();
//
//    JSONObject j = cset.asJSON();
//
//    IBResource r = IBResourceFactory.fromJSON(j);
//    assertEquals(cset, r);
//
//  }
}
