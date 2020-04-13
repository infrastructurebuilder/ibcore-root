/**
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
 */
package org.infrastructurebuilder.util.execution.model.v1_0_0;

import static java.time.Duration.ofMillis;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.infrastructurebuilder.util.execution.model.v1_0_0.DefaultProcessExecution;
import org.infrastructurebuilder.util.execution.model.v1_0_0.io.xpp3.ProcessExecutionModelXpp3Reader;
import org.infrastructurebuilder.util.execution.model.v1_0_0.io.xpp3.ProcessExecutionModelXpp3Writer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessExecutionTest {
  public final static Logger              log = LoggerFactory.getLogger(DefaultProcessExecutionTest.class);
  public final static TestingPathSupplier wps = new TestingPathSupplier();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private ProcessExecutionModelXpp3Reader r;
  private ProcessExecutionModelXpp3Writer w;
  private DefaultProcessExecution         p1, p2;
  private Path                            workDirectory;
  private String                          id;

  @Before
  public void setUp() throws Exception {
    id = UUID.randomUUID().toString();
    String executable = wps.getRoot().resolve("packer").toAbsolutePath().toString();
    List<String> arguments = Arrays.asList("--version");
    Optional<Duration> timeout = of(ofMillis(30000L));
    r = new ProcessExecutionModelXpp3Reader();
    w = new ProcessExecutionModelXpp3Writer();
    workDirectory = wps.get();

    p1 = new DefaultProcessExecution();
    p1.setId(id);
    p1.setRoot(workDirectory.toAbsolutePath().toString());

    p2 = new DefaultProcessExecution(id, executable, arguments, timeout, empty(), workDirectory, true,
        of(new HashMap<>()), of(wps.getRoot()), empty(), empty(), false);

  }

  @After
  public void tearDown() throws Exception {
    wps.finalize();
  }

  @Test
  public void testDefaultProcessExecution() {
    assertNotNull(p1);
    assertNotNull(p2);
  }

  @Test
  public void testRW() throws IOException, XmlPullParserException {
    StringWriter w2 = new StringWriter();
    w.setFileComment("COMMENT");
    w.write(w2, p2.clone());
    String x = IBUtils.removeXMLPrefix(w2.toString());
    assertNotNull(x);

    StringReader w3 = new StringReader(x);
    DefaultProcessExecution p3 = r.read(w3);

    assertTrue(p3.getId().equals(id));

  }

}
