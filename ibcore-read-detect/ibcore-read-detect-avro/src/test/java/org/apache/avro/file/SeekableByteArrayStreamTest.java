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
package org.apache.avro.file;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SeekableByteArrayStreamTest {
  private final static TestingPathSupplier tps = new TestingPathSupplier();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private Path rick;
  private SeekableByteArrayInput tus;
  private byte[] bytes;
  private byte[] g;
  private Checksum rickCsum;

  @BeforeEach
  void setUp() throws Exception {
    this.rick = tps.getTestClasses().resolve("rick.jpg");
    this.rickCsum = new Checksum(this.rick);
    this.bytes = Files.readAllBytes(this.rick);
    this.tus = new SeekableByteArrayInput(bytes);
    this.g = new byte[10];
    for (int i = 0; i < 10; ++i) {
      this.g[i] = this.bytes[i + 10];
    }
  }

  @AfterEach
  void tearDown() throws Exception {
    tps.finalize();
  }

  @Test
  void testSeekAndTell() throws IOException {
    assertEquals(this.bytes.length, this.tus.length());
    this.tus.seek(10L);
    byte[] f = new byte[10];
    this.tus.read(f);
    assertArrayEquals(this.g, f);
    assertEquals(20L, this.tus.tell());

  }

  @Test
  void testReadAllBytes() {
    assertEquals(this.rickCsum, new Checksum(new ByteArrayInputStream(this.tus.readAllBytes())));
  }

}
