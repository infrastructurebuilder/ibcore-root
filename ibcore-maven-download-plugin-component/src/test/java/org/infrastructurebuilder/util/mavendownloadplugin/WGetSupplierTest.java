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
package org.infrastructurebuilder.util.mavendownloadplugin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.mavendownloadplugin.nonpublic.DefaultWGetSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WGetSupplierTest {
  private final static TestingPathSupplier tps = new TestingPathSupplier();
  private static final String URL = "https://releases.hashicorp.com/packer/1.10.1/packer_1.10.1_windows_386.zip";
  private final static Logger log = LoggerFactory.getLogger(WGetSupplierTest.class);

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private LoggingProgressReport lpr;
  private WGetBuilderFactory wgs;
  private Path od;
  private WGetBuilder b;

  @BeforeEach
  void setUp() throws Exception {
    this.od = tps.get();
    this.lpr = new LoggingProgressReport(log);
    this.wgs = new DefaultWGetSupplier(new FakeArchiverManager());
    this.b = wgs //
        .withLogger(log) //
        .withCacheDirectory(od) //
        .builder();
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testGet() throws MalformedURLException, URISyntaxException {
    b.withUri(new URL(URL).toURI())
    .withOutputDirectory(this.od);
    Optional<WGetResult> f = b.wget();
    assertTrue(f.isPresent());
    var res = f.get();
    assertTrue(Files.exists(res.getOriginal().get()));
  }

}
