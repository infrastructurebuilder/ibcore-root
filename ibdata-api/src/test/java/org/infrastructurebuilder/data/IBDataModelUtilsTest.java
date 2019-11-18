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
package org.infrastructurebuilder.data;

import static org.infrastructurebuilder.IBConstants.*;
import static org.infrastructurebuilder.data.IBDataConstants.IBDATA;
import static org.infrastructurebuilder.data.IBDataConstants.IBDATASET_XML;
import static org.infrastructurebuilder.data.IBDataModelUtils.forceToFinalizedPath;
import static org.infrastructurebuilder.data.IBDataModelUtils.getStructuredSupplyTypeClass;
import static org.infrastructurebuilder.data.IBDataModelUtils.relativizePath;
import static org.infrastructurebuilder.data.IBDataModelUtils.safeMapURL;
import static org.infrastructurebuilder.data.IBMetadataUtilsTest.TEST_INPUT_0_11_XML;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.infrastructurebuilder.IBConstants;
import org.infrastructurebuilder.data.model.DataSet;
import org.infrastructurebuilder.data.model.DataStream;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.files.IBChecksumPathType;
import org.infrastructurebuilder.util.files.TypeToExtensionMapper;
import org.junit.Test;

public class IBDataModelUtilsTest extends AbstractModelTest {
  private static final String ORG_APACHE_AVRO_GENERIC_INDEXED_RECORD2 = ORG_APACHE_AVRO_GENERIC_INDEXED_RECORD;

  @Test
  public void test() {
    new IBDataModelUtils();
  }

  @Test
  public void testMapInputStreamToDataSet() throws IOException {
    InputStream t = getClass().getResourceAsStream(TEST_INPUT_0_11_XML);
    DataSet ds = IBDataModelUtils.mapInputStreamToDataSet.apply(t);
    assertEquals(2, ds.getStreams().size());
  }

  @Test(expected = IBDataException.class)
  public void testWriteDataSet() throws IOException {
    Path w = wps.get();
    Path target = w.resolve(IBDATA).resolve(IBDATASET_XML);
    Files.createDirectories(target.getParent());
    Files.createFile(target);
    IBDataModelUtils.writeDataSet(new DataSet(), w);
  }

  @Test
  public void testSafeMapURL() throws Exception {
    assertFalse(safeMapURL.apply(null).isPresent());
    assertTrue(safeMapURL.apply("https://www.google.com").isPresent());
  }

  @Test(expected = IBDataException.class)
  public void testSafeMapURLFail() throws Exception {
    assertTrue(safeMapURL.apply("nope").isPresent());
  }

  @Test
  public void testFromPathDSAndStream() {

    //    fail("Not yet implemented");
  }

  @Test
  public void testRelPath() throws MalformedURLException {
    Path p = wps.getTestClasses();
    DataSet set = new DataSet();
    DataStream stream = new DataStream();
    String v = relativizePath(set, stream);
    assertNull(v);
    String uP = p.toUri().toURL().toExternalForm();
    set.setPath(uP);
    assertNull(relativizePath(set, stream));
    stream.setPath(uP + File.separator + "X");
    assertEquals(File.separator + "X", relativizePath(set, stream));
  }

  @Test
  public void testForceToFinalizedPath() throws IOException {
    Path workingPath = wps.get();
    Path tPath = workingPath.getParent().resolve("75b331e0-faaa-3464-9219-2ca72f0ad31e");
    IBUtils.deletePath(tPath); // Fails if exists
    List<Supplier<IBDataStream>> ibdssList = new ArrayList<>();
    TypeToExtensionMapper t2e = new FakeTypeToExtensionMapper();
    IBChecksumPathType v = forceToFinalizedPath(now, workingPath, finalData, ibdssList, t2e);
    assertEquals(tPath, v.getPath());
    assertNotNull(v.get());

  }

  @Test
  public void testTypeStreamMapper() {
    assertEquals(ORG_APACHE_AVRO_GENERIC_INDEXED_RECORD2, getStructuredSupplyTypeClass(AVRO_BINARY).get());
    assertEquals(ORG_W3C_DOM_NODE, getStructuredSupplyTypeClass(APPLICATION_XML).get());
    assertEquals(JAVA_LANG_STRING, getStructuredSupplyTypeClass(TEXT_CSV).get());
    assertEquals(JAVA_LANG_STRING, getStructuredSupplyTypeClass(TEXT_PLAIN).get());
    assertEquals(JAVA_LANG_STRING, getStructuredSupplyTypeClass(TEXT_PSV).get());
    assertEquals(JAVA_LANG_STRING, getStructuredSupplyTypeClass(TEXT_TSV).get());
    assertFalse(getStructuredSupplyTypeClass(IBConstants.APPLICATION_OCTET_STREAM).isPresent());
  }

}
