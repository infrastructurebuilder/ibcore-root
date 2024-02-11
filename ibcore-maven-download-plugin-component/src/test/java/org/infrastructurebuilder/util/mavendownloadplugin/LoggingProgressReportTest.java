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

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoggingProgressReportTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  private final static Logger log = LoggerFactory.getLogger(LoggingProgressReportTest.class);
  private final static TestingPathSupplier tps = new TestingPathSupplier();
  private LoggingProgressReport lpr;
  private URI uri;
  

  @BeforeEach
  void setUp() throws Exception {
    this.lpr = new LoggingProgressReport(log);
    this.uri = tps.get().toUri();
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testInitiate() {
    this.lpr.initiate(this.uri, 1000);
  }

  @Test
  void testUpdate() {
    this.lpr.update(40);
  }

  @Test
  void testCompleted() {
    this.lpr.completed();
  }

  @Test
  void testError() {
    this.lpr.error(new IBException("Bob"));
  }

}
