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
package org.infrastructurebuilder.util.readdetect;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.IBUtilsTest;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.readdetect.impl.DefaultIBResource;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IBResourceModelTest {

  private TestingPathSupplier wps;
  private DefaultIBResource c1, c2;
  private Path path;
  private Checksum checksum;
  private IBResource def;

  @BeforeEach
  public void setUp() throws Exception {
    wps = new TestingPathSupplier();
    Path source = wps.getTestClasses().resolve(IBUtilsTest.TESTFILE);
    path = wps.get().resolve(UUID.randomUUID().toString());
    IBUtils.copy(source, path);
    checksum = new Checksum(IBUtilsTest.TESTFILE_CHECKSUM);
    c2 = new DefaultIBResource(path, checksum);
    c1 = new DefaultIBResource(path, checksum, of("ABC"));

    def = new DefaultIBResource();
  }

  @AfterEach
  public void tearDown() throws Exception {
    wps.finalize();
  }

  @Test
  public void testDef() {
    assertFalse(def.getSourceName().isPresent());
    assertFalse(def.getSourceURL().isPresent());
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
    assertEquals("image/jpeg", c2.getType());
    assertEquals("ABC", c1.getType());
  }

  @Test
  public void testGet() {
    assertEquals(checksum, new Checksum(c1.get()));
    assertEquals(Long.valueOf(22152), c1.size());
    assertFalse(c1.getSourceURL().isPresent());
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
    IBResource b = c1.moveTo(p);
    assertEquals(p, b.getPath());
    assertEquals(c1.getType(), b.getType());
    assertEquals(c1.getChecksum(), b.getChecksum());
  }

  @Test
  public void testEqualsHash() {
    DefaultIBResource c3 = new DefaultIBResource(path, checksum, of("ABC"));
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
    DefaultIBResource q = new DefaultIBResource();
    assertFalse(q.getSourceURL().isPresent());
    assertFalse(q.getSourceName().isPresent());
  }

}
