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

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.core.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractPathIBResourceBuilderFactory.AbstractPathIBResourceBuilder;
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

  private Path root;
  private AbsolutePathRelativeRoot rr;
  private Supplier<? extends AbstractPathIBResourceBuilder> b;
  private Checksum rick;

  @BeforeEach
  void setUp() throws Exception {
    this.root = tps.getTestClasses();
    this.rick = new Checksum(this.root.resolve(RICK_JPG));
    this.rr = new AbsolutePathRelativeRoot(this.root);
    this.b = new RelativePathAvroIBResourceBuilderFactory(this.rr)//
        .fromPath(Paths.get(RICK_JPG));

  }

  @AfterEach
  void tearDown() throws Exception {
    tps.finalize();
  }

  @Test
  void testGetBuilder() {
    assertNotNull(this.b);
    Optional<IBResource> q = b.get().withAcquired(Instant.now()).withDescription("desc").withName("name").build();
    assertTrue(q.isPresent());
    IBResource v = q.get();
    assertEquals(this.rick, v.getTChecksum());
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
