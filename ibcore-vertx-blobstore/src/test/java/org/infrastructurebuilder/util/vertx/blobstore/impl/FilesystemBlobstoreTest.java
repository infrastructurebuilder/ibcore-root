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
package org.infrastructurebuilder.util.vertx.blobstore.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.RelativeRootSetPathSupplier;
import org.infrastructurebuilder.util.core.RelativeRootSupplier;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.RunTestOnContext;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class FilesystemBlobstoreTest {

  private static final String BFILENAME = "Bee dot xml";
  private static final String BFILE = "b.xml";
  private static TestingPathSupplier wps;
  private final Logger log = LoggerFactory.getLogger(FilesystemBlobstoreTest.class);

  @RegisterExtension
  RunTestOnContext rtoc = new RunTestOnContext();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    wps = new TestingPathSupplier();
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private FilesystemBlobstore fsbs;
  private Vertx vertx;
  private RelativeRootSupplier rrs;

  @BeforeEach
  void setUp(VertxTestContext testContext) throws Exception {
    Path root = wps.get();
    rrs = new RelativeRootSetPathSupplier(root);
    this.fsbs = new FilesystemBlobstore(rrs, IBConstants.BLOBSTORE_NO_MAXBYTES);
    vertx = rtoc.vertx();
    // Prepare something on a Vert.x event-loop thread
    // The thread changes with each test instance
    testContext.completeNow();
  }

  @AfterEach
  void tearDown() throws Exception {
//    wps.finalize();
  }

  @Test
  void testScanSize() {
    assertEquals(0, this.fsbs.scanSize());
  }

  @Test
  @Timeout(value = 800, timeUnit = TimeUnit.SECONDS)
  void testPutBlobStringStringPath(VertxTestContext testContext) {
    Path testfile = wps.getTestClasses().resolve(BFILE).toAbsolutePath();
    this.fsbs.putBlob(BFILE, BFILENAME, testfile)

        .compose(id -> this.fsbs.getMetadata(id))

        .compose(md -> {
          log.info("Logging {}", md.getChecksum().asUUID().get());
          Instant i = md.getCreateDate().get();
          assertTrue(Instant.now().isAfter(i));
          return Future.succeededFuture();
        }).onComplete(testContext.succeedingThenComplete());
  }

//  @Test
//  void testGetRelativeRoot() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testGetBlob() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testGetCreateDate() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testGetLastUpdated() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testPutBlobStringStringFutureOfBufferInstantInstantOptionalOfProperties() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testPutBlobStringStringPathOptionalOfProperties() {
//    fail("Not yet implemented");
//  }

//  @Test
//  void testGetLog() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testGetName() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testGetDescription() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testGetMetadata() {
//    fail("Not yet implemented");
//  }
//
//  @Test
//  void testRemoveBlob() {
//    fail("Not yet implemented");
//  }

}
