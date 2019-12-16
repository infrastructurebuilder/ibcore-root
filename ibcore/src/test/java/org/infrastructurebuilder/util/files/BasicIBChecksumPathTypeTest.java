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
import static org.infrastructurebuilder.util.IBUtilsTest.TESTFILE;
import static org.infrastructurebuilder.util.IBUtilsTest.TESTFILE_CHECKSUM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.infrastructurebuilder.util.files.model.IBChecksumPathTypeModel;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class BasicIBChecksumPathTypeTest {

  private TestingPathSupplier wps;
  private BasicIBChecksumPathType c1, c2;
  private Path path;
  private Checksum checksum;

  @Before
  public void setUp() throws Exception {
    wps = new TestingPathSupplier();
    Path source = wps.getTestClasses().resolve(TESTFILE);
    path = wps.get().resolve(UUID.randomUUID().toString());
    IBUtils.copy(source, path);
    checksum = new Checksum(TESTFILE_CHECKSUM);
    c2 = new BasicIBChecksumPathType(path, checksum);
    c1 = new BasicIBChecksumPathType(path, checksum, "ABC");
  }

  @After
  public void tearDown() throws Exception {
    wps.finalize();
  }

  @Test
  public void testGetPath() {
    assertEquals(path, c1.getPath());
    assertEquals(path, c2.getPath());
  }

  @Test
  public void testGetChecksum() {
    assertEquals(checksum, c1.getChecksum());
    assertEquals(checksum, c2.getChecksum());
  }

  @Test
  public void testGetType() {
    assertEquals(APPLICATION_OCTET_STREAM, c2.getType());
    assertEquals("ABC", c1.getType());
  }

  @Test
  public void testGet() {
    assertEquals(checksum, new Checksum(c1.get()));
  }

  @Test
  public void testToString() {
    String v = c1.toString();
    assertTrue(v.contains(checksum.asUUID().get().toString()));
    assertTrue(v.contains(path.toString()));
    assertTrue(v.contains("ABC"));
  }

  @Test
  public void testMoveTo() throws IOException {
    Path p = wps.get().resolve(UUID.randomUUID().toString());
    IBChecksumPathType b = c1.moveTo(p);
    assertEquals(p, b.getPath());
    assertEquals(c1.getType(), b.getType());
    assertEquals(c1.getChecksum(), b.getChecksum());
  }

  @Test
  public void testEqualsHash() {
    BasicIBChecksumPathType c3 = new BasicIBChecksumPathType(path, checksum, "ABC");
    c1.hashCode();
    c1.hashCode();
    c1.hashCode();
    assertEquals(c1.hashCode(), c1.hashCode());
    assertEquals(c1, c1);
    assertNotEquals(c1, "");
    assertNotEquals(c1, c2);
    assertNotEquals(c1, null);
    assertEquals(c1, c3);
    assertEquals(c1.hashCode(), c3.hashCode());
  }

  @Test
  public void testRootInterfaceSourceURL() {
    IBChecksumPathType q = new IBChecksumPathTypeModel();
    assertFalse(q.getSourceURL().isPresent());
    assertFalse(q.getSourceName().isPresent());
  }
}
