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
package org.infrastructurebuilder.util.core;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OptStreamTest {
  private final static TestingPathSupplier tps = new TestingPathSupplier();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private OptStream nullos;
  private OptStream rick;
  private Path rickroll;
  private Checksum csum;

  @BeforeEach
  void setUp() throws Exception {
    this.nullos = new OptStream();
    this.rickroll = this.tps.getTestClasses().resolve("rick.jpg");
    this.csum = new Checksum(this.rickroll);
  }

  @AfterEach
  void tearDown() throws Exception {
    tps.finalize();
  }

  @Test
  void testOptStream() {
    assertNotNull(this.nullos);
    assertFalse(this.nullos.getStream().isPresent());
  }

  @Test
  void testGetStream() throws IOException, Exception {
    try (OptStream rick2 = new OptStream(Files.newInputStream(rickroll))) {
      Optional<InputStream> kv = rick2.getStream();
      assertTrue(kv.isPresent());
      Checksum c = rick2.getChecksum().get();
      assertEquals(this.csum, c);
    }
  }

}
