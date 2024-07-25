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
package org.infrastructurebuilder.pathref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

class PathRefTest {

  private static final String XFILE = "X.txt";
  private static final String ABC = "abc";
  private static final String XML = ".xml";
  private static final String BOB2 = "bob";
  private static final String MYFILE_XML = "myfile.xml";
  private static final String CSUMVAL = "03e15d9a12ac783c6b65bd5dc248bd2b03a6b9281c34f5e165336ebec7d1f48031f6d3232b2350b275fa2e722e8116afe9a48412561d20d559fe553d9a672f0b";
  private static final String STRING_ROOT_S3_AMAZON_BUCKET = "{\"STRING_ROOT\":\"s3://some.amazon.com/bucket/\"}";
  private static final String URLROOT = "https://someserver.com/somepath";
  private static final String URLLIKE = "s3://some.amazon.com/bucket";
  private static final String OTHLIKE = "s3://some.amazon.com/otherbucket";
  private final static Logger log = LoggerFactory.getLogger(PathRefTest.class);
  private static TestingPathSupplier tps;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    tps = new TestingPathSupplier();
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    tps.finalize();
  }

  private PathRefFactory rrp;
  private PathRef prr;
  private Path tp;
  private Path ssPath;
  private String ss;
  private Path aPath;
  private String fileUrl;
  private URL u;

  @BeforeEach
  void setUp() throws Exception {
    tp = tps.get().toAbsolutePath();
    ss = UUID.randomUUID().toString();
    ssPath = Paths.get(ss);
    aPath = tp.resolve(ssPath).toAbsolutePath();
    u = tp.toUri().toURL();
    fileUrl = u.toExternalForm();

    prr = new URLPathRef(u);
    rrp = new PathRefFactory(Set.of(new PathRefProducer<String>() {
      @Override
      public String getName() {
        return "ABS";
      }

      @Override
      public Optional<PathRef> with(Object t) {
        return Optional.of(prr);
      }

      @Override
      public Class<? extends String> withClass() {
        return String.class;
      }

//      @Override
//      public Optional<PathRef> getPathRef() {
//        return Optional.of(prr);
//      }

    }));
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testWithParam() {
    assertEquals(prr, rrp.get("ABS", "xyz").get());
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
    var qq = r.get();
    assertNotEquals(aPath, qq);
    // Resolving an absolute path returns that path
    var r1 = prr.toResolvedPath(qq.toString());
    Path qm = r1.get();
    var r2 = prr.resolvePath(aPath);
    assertTrue(r2.isEmpty());
//    assertEquals(qm.toString(), r2.get());
  }

  @Test
  void testRelativize() {
    String s = ssPath.toString();
    Optional<Path> pq = prr.relativize(s);
    assertNotNull(pq);
    assertFalse(pq.isPresent()); // Cannot relativize from that string with no path or schema
    pq = prr.relativize(aPath);
    // Relativized paths are not the same as the original
    assertEquals(ssPath, pq.get());
  }

  @Test
  void testJsonChecksum() {
    String s = prr.toString();
    JSONObject j = prr.asJSON();
    Checksum c = prr.asChecksum();
    PathRef rr = new URLPathRef(s);
    ChecksumBuilder cb = ChecksumBuilderFactory.newAlternateInstanceWithRelativeRoot(Optional.of(rr));
    Checksum sb = cb.asChecksum();
    assertEquals(sb, c);
    JSONObject j2 = new JSONObject().put(PathRef.RELATIVE_ROOT_URLLIKE, s);
    assertEquals(j.toString(), j2.toString());

  }

  @Test
  void testAbsoluteURLRR() throws MalformedURLException {
    assertNotNull(prr);
    String k = fileUrl.concat(MYFILE_XML);
    Path v = prr.toResolvedPath(MYFILE_XML).get();
    assertEquals(k, v.toUri().toURL().toExternalForm());
  }

  @Test
  void testTempFile() {
    Path fileRel = prr.createTemporaryFile(ABC, XML).get();
    PathRef file = prr.extendAsPathRef(fileRel).get();
    // The path now points to blah.xml!/ because an extended path
    assertFalse(Files.isRegularFile(file.getPath().get()));
    String name = file.getPath().get().toString();
    assertFalse(name.endsWith(XML));
    assertTrue(file.getPath().get().getFileName().toString().startsWith(ABC));
  }

  @Test
  void testGetInputStreamFrom() throws IOException {
    assertFalse(prr.getInputStreamFrom("nonexistent").isPresent());
    Path v = prr.createTemporaryFile("A", "b").get();
    Path thePath = tps.getTestClasses().resolve("b.xml");
    Files.copy(thePath, prr.toResolvedPath(v).get(), StandardCopyOption.REPLACE_EXISTING);
    Checksum c = new Checksum(thePath);
    Checksum c2;
    try (InputStream ins = prr.getInputStreamFrom(v.toString()).get()) {
      c2 = new Checksum(ins);
    }
    assertEquals(c, c2);
  }

  @Test
  void testTempFileWithPath() {
    Path bob = Paths.get(BOB2);
    Path rel = prr.createTemporaryFile(bob, ABC, XML).get();
    Path file = prr.extendAsPathRef(rel).flatMap(PathRef::getPath).get();
    assertFalse(Files.isRegularFile(file));
    String name = file.toString();
    assertFalse(name.endsWith(XML)); // Ends with XML!
    assertTrue(file.getFileName().toString().startsWith(ABC));
    Optional<Path> file2 = prr.createTemporaryFile(bob.toAbsolutePath(), ABC, XML);
    assertFalse(file2.isPresent());
  }

  @Test
  void testPermFile() throws IOException, URISyntaxException {
    Path rel = prr.createPermanantFile(ABC, XML).get();
    Path res = prr.toResolvedPath(rel).get();
    Path parent = rel.getParent();
    Optional<URL> vv = prr.getUrl();
    Path q1 = Paths.get(vv.get().toURI());
    assertEquals(q1, res.getParent());
    Files.delete(res);
  }

  @Test
  void testPermFileWithPath() throws IOException {
    Path bob = Paths.get(BOB2);
    Path fileR = prr.createPermanantFile(bob, ABC, XML).get();
    assertFalse(fileR.isAbsolute());
    Path file = prr.extendAsPathRef(fileR).flatMap(PathRef::getPath).get();
    assertFalse(Files.isRegularFile(file));
    String name = file.toString();
    assertFalse(name.endsWith(XML)); // Ends with XML!
    assertTrue(file.getFileName().toString().startsWith(ABC));
  }

  @Test
  public void testThatFilesMustBeWithinTree() {
    Path bob = Paths.get(BOB2);
    Optional<Path> file2 = prr.createPermanantFile(bob.toAbsolutePath(), ABC, XML);
    assertFalse(file2.isPresent());
  }

  @Test
  public void testRRIsParent() {
    PathRef q = prr.extendAsPathRef(Paths.get(BOB2)).get();
    assertTrue(prr.isParentOf(q));
  }

  @Test
  public void testRRIsParentPath() throws IOException {
    Path wp = tps.get();
    Path xp = wp.resolve(XFILE);
    IBChecksumUtils.copy(tps.getTestClasses().resolve(XFILE), xp);
    var rr = new AbsolutePathRef(wp);
    assertTrue(rr.isParentOf(xp));
    assertFalse(rr.isParentOf(Paths.get(XFILE)));
    assertFalse(rr.isParentOf(prr.getPath().get()));

  }

  @Test
  public void testExtend() {
    assertFalse(prr.extendAsPathRef(null).isPresent());
    assertFalse(prr.extendAsPathRef(aPath).isPresent());
  }
}
