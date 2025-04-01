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
package org.infrastructurebuilder.util.readdetect.avro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.pathref.fs.PathRefPathIF;
import org.infrastructurebuilder.util.readdetect.api.IBResource;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractPathRefPathIBResourceBuilderFactory.AbstractPathIBResourceBuilder;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RelativePathAvroIBResourceBuilderFactoryTest {
  private static final String RICK_JPG = "rick.jpg";
  private final static TestingPathSupplier tps = new TestingPathSupplier();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private Path _root;
  private Supplier<? extends AbstractPathIBResourceBuilder> b;
  private Checksum rick;
  private PathRefFileSystem prpfs;
  private PathRefPath root;
  private Optional<String> config ;

  @BeforeEach
  void setUp() throws Exception {
    config = Optional.of(UUID.randomUUID().toString());
    this._root = tps.getTestClasses();
    prpfs = PathRefPathIF.getOrCreatePRFS(this._root, config).get();
    this.root = prpfs.getRoot();

    this.rick = new Checksum(this._root.resolve(RICK_JPG));
    this.b = new PathRefPathAvroIBResourceBuilderFactory(this.prpfs)//
        .fromPath(this.root.resolve(RICK_JPG));

  }

  @AfterEach
  void tearDown() throws Exception {
    tps.finalize();
  }

  @Test
  void testGetBuilder() {
    assertNotNull(this.b);
    AbstractPathIBResourceBuilder qq = b.get();
    Optional<IBResource> q = qq
        .withAcquired(Instant.now())
        .withDescription("desc")
        .withName("name")
        .build();
    assertTrue(q.isPresent());
    IBResource v = q.get();
    assertEquals(this.rick, v.getByteStreamChecksum());
  }

  @Test
  void testAsJson() {
    Optional<IBResource> q = b.get().withAcquired(Instant.now()).withDescription("desc").withName("name").build();
    assertTrue(q.isPresent());
    IBResource v = q.get();
    JSONObject j = v.asJSON();
    assertNotNull(j);
  }

}
