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
package org.infrastructurebuilder.util.executor.execution.model;

import static java.time.Duration.ofMillis;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.pathref.fs.PathRefUtils;
import org.infrastructurebuilder.util.executor.model.executor.model.v1_0.GeneratedProcessExecution;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessExecutionTest {
  public final static Logger log = LoggerFactory.getLogger(DefaultProcessExecutionTest.class.getName());
  public final static TestingPathSupplier wps = new TestingPathSupplier();

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private ProcessExecutionModelXpp3Reader r;
  private ProcessExecutionModelXpp3WriterEx w;
  GeneratedProcessExecution p1;
  private DefaultProcessExecution p2;
  private Path _workDirectory;
  private String id;
  private Path workDirectory;
  private PathRefPath root;

  @BeforeEach
  public void setUp() throws Exception {
    id = UUID.randomUUID().toString();
    String executable = wps.getRoot().resolve("packer").toAbsolutePath().toString();
    List<String> arguments = Arrays.asList("--version");
    Optional<Duration> timeout = of(ofMillis(30000L));
    r = new ProcessExecutionModelXpp3Reader();
    w = new ProcessExecutionModelXpp3WriterEx();
    workDirectory = wps.get();
    workDirectory = PathRefUtils.fromPath(_workDirectory);

    root = PathRefUtils.fromPath(wps.getRoot());

    p1 = new GeneratedProcessExecution();
    p1.setId(id);
    p1.setRoot(workDirectory.getFileSystem().toString());

    p2 = new DefaultProcessExecution(id, executable, arguments, timeout, empty(), workDirectory, true,
        of(new HashMap<>()), of(root), empty(), empty(), false);

  }

  @AfterEach
  public void tearDown() throws Exception {
    wps.finalize();
  }

  @Test
  public void testDefaultProcessExecution() {
    assertNotNull(p1);
    assertNotNull(p2);
  }

//  @Test
//  public void testRW() throws IOException, XmlPullParserException {
//    StringWriter w2 = new StringWriter();
//    w.setFileComment("COMMENT");
//    w.write(w2, p1.clone());
//    String x = IBUtils.removeXMLPrefix(w2.toString());
//    assertNotNull(x);
//
//    StringReader w3 = new StringReader(x);
//    GeneratedProcessExecution p3 = r.read(w3);
//
//    assertTrue(p3.getId().equals(id));
//
//  }

}
