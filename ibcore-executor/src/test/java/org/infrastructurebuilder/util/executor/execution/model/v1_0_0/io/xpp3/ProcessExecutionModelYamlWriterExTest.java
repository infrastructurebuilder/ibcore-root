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
package org.infrastructurebuilder.util.executor.execution.model.v1_0_0.io.xpp3;

//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.List;
//import java.util.Properties;
//import java.util.UUID;
//
//import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
//import org.infrastructurebuilder.util.constants.IBConstants;
//import org.infrastructurebuilder.util.executor.execution.model.v1_0_0.GeneratedProcessExecutionResult;
//import org.infrastructurebuilder.util.executor.execution.model.v1_0_0.ResultInputSource;
//import org.infrastructurebuilder.util.executor.execution.model.v1_0_0.io.snakeyaml.ProcessExecutionResultModelSnakeYamlReader;
//import org.infrastructurebuilder.util.executor.execution.model.v1_0_0.io.snakeyaml.ProcessExecutionResultModelSnakeYamlWriter;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//class ProcessExecutionModelYamlWriterExTest {
//
//  @BeforeAll
//  static void setUpBeforeClass() throws Exception {
//  }
//
//  @AfterAll
//  static void tearDownAfterClass() throws Exception {
//  }
//
//  private ProcessExecutionResultModelSnakeYamlWriter w;
//  private ProcessExecutionResultModelSnakeYamlReader r;
//  private GeneratedProcessExecutionResult m;
//  private String id;
//  private List<String> stdErr;
//  private List<String> stdOut;
//  private Properties properties;
//
//  @BeforeEach
//  void setUp() throws Exception {
//    this.id = UUID.randomUUID().toString();
//    this.stdErr = List.of("");
//    this.stdOut = List.of("Hello, world");
//    this.properties = new Properties();
//    this.m = new GeneratedProcessExecutionResult();
//    m.setId(this.id);
//    this.m.setEnvironment(this.properties);
//    this.m.setException(null);
//    this.m.setModelEncoding(IBConstants.UTF_8);
//    this.m.setResultCode("0");
//    this.m.setRunningtimeAsDuration(Duration.ofMinutes(1L));
//    this.m.setStart(Instant.now().toString());
//    this.m.setStdErr(this.stdErr);
//    this.m.setStdOut(this.stdOut);
//    this.w = new ProcessExecutionResultModelSnakeYamlWriter();
//    this.r = new ProcessExecutionResultModelSnakeYamlReader();
//  }
//
//  @AfterEach
//  void tearDown() throws Exception {
//  }
//
//  @Test
//  void testWriterGeneratedProcessExecution() throws IOException, XmlPullParserException {
//    StringWriter os = new StringWriter();
//    this.w.write(os, m);
//    StringReader is = new StringReader(os.toString());
//    ResultInputSource s = new ResultInputSource();
//    GeneratedProcessExecutionResult q = this.r.read(is, true);
//    assertEquals(m.getId(),q.getId());
//    assertEquals(m.getStdOut(),q.getStdOut());
//    assertEquals(m.getRunningtime() ,q.getRunningtime());
//
//  }
//
//  @Test
//  void testOutputStreamGeneratedProcessExecution() throws IOException, XmlPullParserException {
//    ByteArrayOutputStream os = new ByteArrayOutputStream(500);
//    this.w.write(os, m);
//    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
//    ResultInputSource s = new ResultInputSource();
//    GeneratedProcessExecutionResult q = this.r.read(is, true);
//    assertEquals(m.getId(),q.getId());
//    assertEquals(m.getStdOut(),q.getStdOut());
//    assertEquals(m.getRunningtime() ,q.getRunningtime());
//  }
//
//}
