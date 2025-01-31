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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.IBChecksumUtils;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystemProvider;
import org.infrastructurebuilder.pathref.fs.PathRefPathIF;
import org.infrastructurebuilder.util.readdetect.api.IBResource;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractPathRefPathIBResourceBuilderFactory.AbstractPathIBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.impls.PathRefPathIBResourceBuilderFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IBResourceModelTest {

  private TestingPathSupplier wps;
  private IBResource c1, c2;
  private Path c1path;
  private Checksum checksum;
  private PathRefPathIBResourceBuilderFactory f;
  private Path _root;
  private Path c2source;
  private Checksum lc;
  private PathRefFileSystem rrs;
  private Optional<IBResource> r;
  private String testFsId;

  @BeforeEach
  public void setUp() throws Exception {
    PathRefFileSystemProvider.reset();
    wps = new TestingPathSupplier();
    c2source = wps.getTestClasses().resolve("rick.jpg");
    _root = wps.get();
    testFsId = UUID.randomUUID().toString();
    c1path = _root.resolve(UUID.randomUUID().toString()+".jpg");
    IBChecksumUtils.copy(c2source, c1path);
    lc = new Checksum(c2source);
    rrs = PathRefPathIF.getOrCreatePRFS(_root).get();
    f = new PathRefPathIBResourceBuilderFactory(rrs);
    AbstractPathIBResourceBuilder bb = f.getBuilder().get();
    checksum = new Checksum(c2source);
    Supplier<? extends AbstractPathIBResourceBuilder> qqq = f.getBuilder();
    c2 = qqq.get().accept(() -> c1path).withType("ABC").build().get();
    assertNotNull(c2.getChecksum());
//    c2 = new AbsolutePathIBResource(path, checksum);
    c1 = f.getBuilder().get().accept(() -> c1path).withType(IBConstants.IMAGE_JPG).build().get();
    assertNotNull(c1.getChecksum());

    r = bb.accept(() -> c1path).build(false);
    assertNotNull(r);

  }

  @AfterEach
  public void tearDown() throws Exception {
    wps.finalize();
  }

  @Test
  public void testGetPath() {
    var c1name = c1path.getFileName();
    var c1nameGet = c1.getPath().get();
    assertTrue(c1nameGet.toString().endsWith(c1name.toString()));
    var c2name = c2source.getFileName().toString();
    var c2nameGet = c2.getPath().get().toString();
    assertTrue(c2nameGet.endsWith(c1name.getFileName().toString()));
  }

  @Test
  public void testGetChecksum() {
    assertEquals(c1.getChecksum(), c2.getChecksum());
    assertEquals(checksum, c1.getByteStreamChecksum());
    assertEquals(checksum, c2.getByteStreamChecksum());
  }

  @Test
  public void testGetType() {
    assertEquals("ABC", c2.getType());
    assertEquals(IMAGE_JPG, c1.getType());
  }

  @Test
  public void testGet() throws IOException {
    InputStream ins = Files.newInputStream(c1.get());
    assertEquals(checksum, new Checksum(ins));
    assertEquals(Long.valueOf(22152), c1.size().get());
    assertNotNull(c1.getSourceURI());
  }

  @Test
  public void testToString() {
    String v = c1.toString();
    assertTrue(v.contains(checksum.asUUID().get().toString()));
    assertTrue(v.contains(c1path.getFileName().toString()));
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
