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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import org.infrastructurebuilder.util.core.DefaultIBVersion.DefaultIBVersionBoundedRange;
import org.infrastructurebuilder.util.core.IBVersion.IBVersionBoundedRange;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VersionedPersistenceMapperTest {

  private static final String VERSION = "1.0";
  static VersionedPersistenceMapper<FakeModeled> m;
  static VersionedPersistenceProvider<FakeModeled> p;
  FakeModeled f;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    p = new VersionedPersistenceProvider<FakeModeled>() {

      @Override
      public IBVersion getVersion() {
        return new DefaultIBVersion(VERSION);
      }

      @Override
      public IBVersionBoundedRange getVersionRange() {
        return DefaultIBVersionBoundedRange.versionBoundedRangeFrom(getVersion(), getVersion());
      }

      @Override
      public void write(Writer w, FakeModeled s) throws IOException {
        w.write(s.asJSON().toString());
      }

      @Override
      public FakeModeled read(Reader r) throws IOException {
        try (BufferedReader br = new BufferedReader(r)) {
          StringJoiner j = new StringJoiner("\n");
          br.lines().forEach(j::add);
          return new FakeModeled(new JSONObject(j.toString()));
        }
      }

      @Override
      public Optional<FakeModeled> fromVersionedObject(Modeled o) {
        try {
          return Optional.of((FakeModeled) o);
        } catch (ClassCastException e) {
          return Optional.empty();
        }
      }
    };
    m = new VersionedPersistenceMapper<>(Set.of(p));
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testReaderForString() throws IOException {
    assertFalse(m.readerFor("2.0").isPresent());
    VersionedPersistenceProvider<FakeModeled> wr = m.writerFor(VERSION).get();
    VersionedPersistenceProvider<FakeModeled> rr = m.readerFor(VERSION).get();
    assertEquals(wr, rr); // in this particular case../

    StringWriter sw = new StringWriter();
    FakeModeled fake = new FakeModeled();
    wr.write(sw, fake);
    StringReader sr = new StringReader(sw.toString());
    FakeModeled fake2 = rr.read(sr);
    assertEquals(fake2.asJSON().get("type"), fake.asJSON().get("type"));

  }

  @Test
  void testWriterForString() {
    assertFalse(m.writerFor("2.0").isPresent());
  }

}
