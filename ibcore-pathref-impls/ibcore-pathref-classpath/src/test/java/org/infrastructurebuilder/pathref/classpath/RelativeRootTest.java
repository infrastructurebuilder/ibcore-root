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
package org.infrastructurebuilder.pathref.classpath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefFactory;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.classpath.ClasspathPathRefSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RelativeRootTest {

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

  private PathRefFactory rrp;
  private ClasspathPathRefSupplier h;

  @BeforeEach
  void setUp() throws Exception {
    h = new ClasspathPathRefSupplier();
    rrp = new PathRefFactory(Set.of(this.h));
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testClasspath() {
    PathRef cprr = rrp.get(ClasspathPathRefSupplier.NAME).get();
    assertEquals(cprr, cprr.extendAsPathRef(Paths.get(MYFILE_XML)).get());
    String k = cprr.relativize(ClasspathPathRefSupplier.NAME + MYFILE_XML).map(Path::toString).get();
    String q = cprr.getUrl().get().toExternalForm();
    assertEquals(q, ClasspathPathRefSupplier.NAME);
    assertEquals(MYFILE_XML, k);

    Optional<InputStream> q1 = cprr.getInputStreamFrom("/" + MYFILE_XML);
    assertTrue(q1.isPresent());
    assertFalse(cprr.getInputStreamFrom("/" + "doesnotexist.txt").isPresent());
  }

}
