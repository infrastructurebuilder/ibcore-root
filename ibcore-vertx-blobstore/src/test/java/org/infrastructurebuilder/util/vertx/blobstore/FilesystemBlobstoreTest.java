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
package org.infrastructurebuilder.util.vertx.blobstore;

import static org.infrastructurebuilder.util.constants.IBConstants.BLOBSTORE_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.vertx.blobstore.impl.FilesystemBlobstore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class FilesystemBlobstoreTest {

  private final static Logger log = LoggerFactory.getLogger(FilesystemBlobstoreTest.class);
  private static TestingPathSupplier tps;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    tps = new TestingPathSupplier();
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  FilesystemBlobstore bs;
  private Path root;

  @BeforeEach
  void setUp() throws Exception {
    root = tps.get();
    JsonObject j = new JsonObject().put(BLOBSTORE_ROOT, root.toAbsolutePath().toString());
    bs = new FilesystemBlobstore(j);
  }

  @AfterEach
  void tearDown() throws Exception {
    tps.finalize();
  }

  @Test
  void testGetRelativeRoot() {
    var p1 = bs.getRelativeRoot().getPath().get().toString();
    var p2 = root.toString();
    assertEquals(p1, p2);
  }

  @Test
  void testGetBlob() {
  }

  @Test
  void testGetCreateDate() {
  }

//  @Test
  @Timeout(value = 60, timeUnit = TimeUnit.SECONDS)
  void testPutBlobStringStringPath(Vertx vertx, VertxTestContext testContext) {
    FileSystem fs = vertx.fileSystem();
    Path target = tps.get();
    String finalTarget = target.resolve("rick.jpg").toString();
    Path rick = tps.getTestClasses().resolve("rick.jpg");
    Future<String> fv = bs.putBlob(rick.toString(), "DESC", rick);
    fv.onComplete(testContext.succeeding(buffer -> {
      assertNotNull(buffer);
      bs.getBlob(buffer).compose(r -> {
        return fs.writeFile(finalTarget, r);
      }).onComplete(res -> {
        log.info("Inside oncomplete");
      });
    }));
    log.info("Done");
  }

  @Test
  void testGetMetadata() {
  }
}
