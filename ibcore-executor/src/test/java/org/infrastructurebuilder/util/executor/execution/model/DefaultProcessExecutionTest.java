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

import java.nio.file.Files;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPathIF;
import org.infrastructurebuilder.util.executor.ModeledProcessExecution;
import org.infrastructurebuilder.util.executor.model.v1_0.GeneratedProcessExecution;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessExecutionTest {
  public final static Logger log = LoggerFactory.getLogger(DefaultProcessExecutionTest.class.getName());
  public final static TestingPathSupplier wps = new TestingPathSupplier();

  GeneratedProcessExecution p1;
  private DefaultProcessExecution p2;
  private String id;
  private String workDirectory;
  private static PathRefFileSystem root;

  @BeforeAll
  public static void setUpAll() throws Exception {
    root = PathRefPathIF.getOrCreatePRFS(wps.get(), Optional.of(DefaultProcessExecutionTest.class.getName())).get();
  }
  @BeforeEach
  public void setUp() throws Exception {
    id = UUID.randomUUID().toString();
    String executable = wps.getRoot().resolve("packer").toAbsolutePath().toString();
    List<String> arguments = Arrays.asList("--version");
    Optional<Duration> timeout = of(ofMillis(30000L));
    workDirectory = UUID.randomUUID().toString();
    Files.createDirectories(root.getPath(workDirectory));

    p1 = new GeneratedProcessExecution();

    p1.setId(id);
    p1.setRoot(root.toString());

    p2 = new DefaultProcessExecution(id, executable, arguments, root,timeout, empty(), workDirectory, true,
        of(new HashMap<>()),  empty(), empty(), false);

  }

  @AfterEach
  public void tearDown() throws Exception {
    wps.finalize();
  }


//  @Test
  public void testModeled() {
    ModeledProcessExecution aa = new ModeledProcessExecution(p1);
    assertNotNull(aa);
  }
//  @Test
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
