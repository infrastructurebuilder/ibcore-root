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
package org.infrastructurebuilder.util.readdetect.base;

import static org.infrastructurebuilder.constants.IBConstants.IMAGE_JPG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.pathref.AbsolutePathRef;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.IBChecksumUtils;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.OptStream;
import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractPathIBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractPathIBResourceBuilderFactory.AbstractPathIBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.path.impls.absolute.AbsolutePathIBResourceBuilderFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IBResourceModelTest {

  private TestingPathSupplier wps;
  private IBResource c1, c2;
  private Path path;
  private Checksum checksum;
  private AbsolutePathIBResourceBuilderFactory f;
  private Path root;
  private Path source;
  private Checksum lc;
  private PathRef rrs;
  private Optional<IBResource> r;

  @BeforeEach
  public void setUp() throws Exception {
    wps = new TestingPathSupplier();
    source = wps.getTestClasses().resolve("rick.jpg");
    root = wps.get();
    path = wps.get().resolve(UUID.randomUUID().toString());
    IBChecksumUtils.copy(source, path);
    lc = new Checksum(source);
    rrs = new AbsolutePathRef(root);
    f = new AbsolutePathIBResourceBuilderFactory();
    AbstractPathIBResourceBuilder bb = f.getBuilder().get();
    checksum = new Checksum(source);
    c2 = f.getBuilder().get().accept(() -> source).withType("ABC").build().get();
    assertNotNull(c2.getChecksum());
//    c2 = new AbsolutePathIBResource(path, checksum);
    c1 = f.getBuilder().get().accept(() -> path).withType(IBConstants.IMAGE_JPG).build().get();
    assertNotNull(c1.getChecksum());

    r = bb.accept(() -> path).build(false);
    assertNotNull(r);

  }

  @AfterEach
  public void tearDown() throws Exception {
    wps.finalize();
  }

  @Test
  public void testGetPath() {
    assertEquals(path, c1.getPath().get());
    assertEquals(source, c2.getPath().get());
  }

  @Test
  public void testGetChecksum() {
    assertEquals(c1.getChecksum(), c2.getChecksum());
    assertEquals(checksum, c1.getTChecksum());
    assertEquals(checksum, c2.getTChecksum());
  }

  @Test
  public void testGetType() {
    assertEquals("ABC", c2.getType());
    assertEquals(IMAGE_JPG, c1.getType());
  }

  @Test
  public void testGet() {
    Optional<InputStream> ins = c1.get().getStream();
    if (ins.isEmpty()) {
      fail("No inputstream");
    }
    assertEquals(checksum, new Checksum(ins.get()));
    assertEquals(Long.valueOf(22152), c1.size().get());
    assertTrue(c1.getSourceURL().isPresent());
  }

  @Test
  public void testToString() {
    String v = c1.toString();
    assertTrue(v.contains(checksum.asUUID().get().toString()));
    assertTrue(v.contains(path.toString()));
    assertTrue(v.contains(IMAGE_JPG));
  }

  @Test
  public void testEqualsHash() {
//    AbsolutePathIBResource c3 = new AbsolutePathIBResource(path, checksum, of(IMAGE_JPG));
    c1.hashCode();
    c1.hashCode();
    c1.hashCode();
    assertEquals(c1.hashCode(), c1.hashCode());
    assertEquals(c1, c1);
    assertNotEquals(c1, "");
    assertNotEquals(c1, c2);
    assertNotEquals(c1, null);
//    assertEquals(c1, c3);
//    assertEquals(c1.hashCode(), c3.hashCode());
  }

  @Test
  public void testRootInterfaceSourceURL() {
  }

}
