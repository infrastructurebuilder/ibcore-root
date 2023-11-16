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

import static org.infrastructurebuilder.util.core.RelativeRootBasicPathPropertiesSupplier.NAME;
import static org.infrastructurebuilder.util.core.RelativeRootBasicPathPropertiesSupplier.RR_BASIC_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RelativeRootTest {

  private static final String CSUMVAL = "03e15d9a12ac783c6b65bd5dc248bd2b03a6b9281c34f5e165336ebec7d1f48031f6d3232b2350b275fa2e722e8116afe9a48412561d20d559fe553d9a672f0b";
  private static final String STRING_ROOT_S3_AMAZON_BUCKET = "{\"STRING_ROOT\":\"s3://some.amazon.com/bucket/\"}";
  private static final String URLROOT = "https://someserver.com/somepath";
  private static final String URLLIKE = "s3://some.amazon.com/bucket";
  private static final String OTHLIKE = "s3://some.amazon.com/otherbucket";
  private final static Logger log = LoggerFactory.getLogger(RelativeRootTest.class);
  private static TestingPathSupplier tps;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    tps = new TestingPathSupplier();
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    tps.finalize();
  }

  private RelativeRootFactory rrp;
  private RelativeRoot prr;
  private Path tp;
  private Path ssPath;
  private String ss;
  private Path aPath;
  private RelativeRootBasicPathEnvSupplier i;
  private RelativeRootSetPathSupplier j;

  @BeforeEach
  void setUp() throws Exception {
    tp = tps.get().toAbsolutePath();
    this.i = new RelativeRootBasicPathEnvSupplier("target_dir");
    this.j = new RelativeRootSetPathSupplier(tps.getClasses());
    rrp = new RelativeRootFactory(Set.of(new RelativeRootBasicPathPropertiesSupplier(), this.i, this.j));

    System.setProperty(RR_BASIC_PATH, tp.toString());
    prr = rrp.get(RelativeRootBasicPathPropertiesSupplier.NAME).get();
    System.clearProperty(RR_BASIC_PATH);
    ss = UUID.randomUUID().toString();
    ssPath = Paths.get(ss);
    aPath = tp.resolve(ssPath).toAbsolutePath();
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testPathFrom() {
    assertTrue(prr.isPath());
    assertTrue(prr.isURL());
    assertFalse(prr.isURLLike());
    assertTrue(prr.getPath().isPresent());
  }

  @Test
  void testAbsolute() {

    Optional<Path> r = prr.relativize(aPath);
    assertTrue(r.isPresent());

    // Relativized paths are not the same as the original
    assertNotEquals(aPath, r.get());
    // Resolving an absolute path returns that path
    Path qm = prr.resolvePath(r.get().toString()).get();
    assertEquals(qm.toString(), prr.resolvePath(aPath));
  }

  @Test
  void testRelativize() {
    String s = ssPath.toString();
    String pq = prr.relativize(s);
    assertNotNull(pq);
    // Relativized paths are not the same as the original
    assertEquals(s, pq);
    // Resolving an absolute path returns that path
    assertEquals(aPath.toString(), prr.resolvePath(pq).get().toString());

  }

  @Test
  void testJsonChecksum() {
    String s = prr.toString();
    JSONObject j = prr.asJSON();
    Checksum c = prr.asChecksum();
    Checksum sb = ChecksumBuilder.newInstance().addString(s).asChecksum();
    assertEquals(sb, c);
    JSONObject j2 = new JSONObject().put(RelativeRoot.RELATIVE_ROOT_URLLIKE, s);
    assertEquals(j.toString(), j2.toString());

  }

  @Test
  void testGetPropertyBasicPathEnv() {
    assertEquals(this.rrp.get(RelativeRootBasicPathEnvSupplier.NAME).get().getPath().get(), tps.getTestClasses().getParent());

  }

  @Test
  void testGetBasicSet() {
    assertEquals(this.rrp.get(RelativeRootSetPathSupplier.NAME).get().getPath().get(), tps.getClasses());
  }



}
