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
package org.infrastructurebuilder.util;

import static java.time.Duration.ofHours;
import static java.time.Duration.ofMillis;
import static java.time.Instant.ofEpochMilli;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.infrastructurebuilder.util.ProcessExecutionResult.EXECUTION;
import static org.infrastructurebuilder.util.ProcessExecutionResult.START;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.infrastructurebuilder.util.execution.model.v1_0_0.DefaultProcessExecution;
import org.infrastructurebuilder.util.execution.model.v1_0_0.DefaultProcessExecutionResult;
import org.joor.Reflect;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.zeroturnaround.exec.ProcessOutput;
import org.zeroturnaround.exec.ProcessResult;

public class ProcessExecutionResultTest {
  public final static TestingPathSupplier wps  = new TestingPathSupplier();
  private static final List<String>       ARGS = Arrays.asList("-version");

  private static final String EXEC = "java";
  private static final String ID   = "default";

  private Future<ProcessResult>             future;
  private OverrideListCapturingOutputStream lpaoE;
  private OverrideListCapturingOutputStream lpaoO;
  private MutableProcessExecutionResultBag  merb;

  private DefaultProcessExecution pe;

  private PrintStream pr;

  private DefaultProcessExecutionResult res;

  private ProcessExecutionResult res3;

  private Path scratchDir;

  private List<String> stdErr;

  private List<String> stdOut;

  private Path stdOutPth;

  private Path stdErrPth;

  @BeforeEach
  public void setUp() throws Exception {
    merb = new MutableProcessExecutionResultBag();
    future = new Future<ProcessResult>() {
      @Override
      public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
      }

      @Override
      public ProcessResult get() throws InterruptedException, ExecutionException {
        return new ProcessResult(0, new ProcessOutput(new byte[0]));
      }

      @Override
      public ProcessResult get(final long timeout, final TimeUnit unit)
          throws InterruptedException, ExecutionException, TimeoutException {
        return get();
      }

      @Override
      public boolean isCancelled() {
        return false;
      }

      @Override
      public boolean isDone() {
        return true;
      }

    };

    stdErr = Arrays.asList("Hi", "there");
    stdOut = Arrays.asList("hello", "gentlepersons");
    scratchDir = wps.get();

    stdOutPth = IBUtils.touchFile(scratchDir.resolve("extraStdOut"));

    stdErrPth = IBUtils.touchFile(scratchDir.resolve("extraStdErr"));
    pe = new DefaultProcessExecution(ID, EXEC, ARGS, empty(), empty(), scratchDir, false, empty(), of(scratchDir),
        empty(), empty(), false);

    lpaoO = new OverrideListCapturingOutputStream(of(stdOutPth), stdOut);
    lpaoE = new OverrideListCapturingOutputStream(of(stdErrPth), stdErr);
    pe = Reflect.on(pe).set("stdOut", lpaoO).set("stdErr", lpaoE).get();

    res = new DefaultProcessExecutionResult(pe, of(0), empty(), ofEpochMilli(100L), ofMillis(100L));
    res3 = new DefaultProcessExecutionResult(pe, of(0), empty(), ofEpochMilli(100L), ofMillis(100L));
    pr = new PrintStream(new ByteArrayOutputStream());
  }

  @Test
  public void testAsJSON() {
    final JSONObject a = res.asJSON();
    String start = a.getString(START);
    JSONObject e = a.getJSONObject(EXECUTION);
    String se = e.getString("stderr");
    String so = e.getString("stdout");
    final String t = "{\n" + " \"start\": \"1970-01-01T00:00:00.100Z\",\n" + "  \"execution\": {\n"
        + "    \"arguments\": [\"-version\"],\n" + "    \"optional\": false,\n" + "    \"id\": \"default\",\n"
        + "\"environment\": {}," + "    \"stdout\": \"" + so + "\",\n" + "    \"stderr\": \"" + se + "\",\n"
        + "    \"executable\": \"java\"\n" + "  },\n" + "  \"result-code\": 0,\n" + "  \"runtime\": \"PT0.1S\",\n"
        + "  \"std-out\": [\n" + "    \"hello\",\n" + "    \"gentlepersons\"\n" + "  ],\n" + "  \"std-err\": [\n"
        + "    \"Hi\",\n" + "    \"there\"\n" + "  ]" + "\n}";
    final JSONObject target = new JSONObject(t);
    JSONAssert.assertEquals(target, a, true);

  }

  @Test
  public void testChecksum() {
    // FIXME Maybe we remove values for checksums?

    final Checksum peC = new Checksum(
        "cd6d8aa65baa44edbaa1ea4f2b0afcf029f01ce69162213d6b0f33ac518ebe6d1f7c30eb67a85d7e47deccf901ed2a024878283bc017964d17a6033ae075e72e");
//    assertEquals(peC.toString(), pe.asChecksum().toString());
    assertNotNull(pe.asChecksum());
    final Checksum s = new Checksum(
        "98deda0db70b68131ffba08224ef13c854e7d6ffdadfc9489bf0b7eef6d0cedb8bcbdc464942166aad39c50c29cde19815d0060ccd173a087b3d3450434b75e2");
//    assertEquals(s.toString(), res.asChecksum().toString());
    assertNotNull(res.asChecksum());
  }

  @Test
  public void testClosingCapturedStream() {
    try (ListCapturingLogOutputStream abc = new ListCapturingLogOutputStream(empty(), of(pr))) {

    } catch (final IOException e) {

      e.printStackTrace();
    }
  }

  @Test
  public void testEqualsObject() {
    final DefaultProcessExecution pe2 = new DefaultProcessExecution("abc", EXEC, ARGS, empty(), empty(), scratchDir,
        false, empty(), of(scratchDir), empty(), empty(), false);
    assertNotEquals(res, new DefaultProcessExecutionResult(pe2, of(0), empty(), ofEpochMilli(100L), ofMillis(100L)));
    assertEquals(res, res);
    assertNotEquals(res, "abc");
    assertNotEquals(res, null);
    assertEquals(res, res3);
  }

  @Test
  public void testGetExecution() {
    assertEquals(pe, res.getExecution().get());
  }

  @Test
  public void testGetExecutionEnvironment() {
    assertNotNull(res.getExecutionEnvironment());
  }

  @Test
  public void testGetId() {
    assertEquals(ID, res.getId());
  }

  @Test
  public void testGetResult() {
    assertNotNull(res.getEndTime());
    assertNotNull(res.toString());
    assertFalse(res.isError());
  }

  @Test
  public void testGetRunningfutures() {
    final ProcessExecutionResultBag b = merb.lock();
    assertNotNull(b.getRunningFutures());
  }

  @Test
  public void testGetStdErr() {
    assertNotNull(res.getStdErr());
  }

  @Test
  public void testGetStdOut() {
    assertNotNull(res.getStdOut());
  }

  @Test
  public void testHashCode() {
    assertNotEquals(0, res.hashCode());
  }

  @Test
  public void testIsError() {
    assertFalse(res.isError());
  }

  @Test
  public void testMerbLock() {
    final DefaultProcessExecutionResultBag b = merb.lock();
    final JSONObject jj = new JSONObject(
        "{\n" + "  \"executed-ids\": [],\n" + "  \"results\": [],\n" + "  \"incomplete-futures-ids\": []\n" + "}");
    final JSONObject b2 = b.asJSON();
    JSONAssert.assertEquals(jj, b2, true);
    assertEquals(new ArrayList<String>(), b.getErrors());
    merb.addFuture(pe, future);
    final ProcessExecutionResultBag p = merb.lock();
    assertNotNull(p);
    assertNotEquals(merb, p);

  }

  @Test(expected = ProcessException.class)
  public void testNegativeDuration() throws Exception {
    try (ProcessExecution vv = new DefaultProcessExecution(ID, EXEC, ARGS, of(ofHours(-1)), empty(), scratchDir, false,
        empty(), of(scratchDir), empty(), empty(), false)) {
      vv.getProcessExecutor();
    }
    ;
  }

  @Test
  public void testOutcomeStuff() {
  }

  @Test
  public void testSetException() {
    merb.setException(pe, new RuntimeException());
    final ConcurrentMap<String, Throwable> exceptions = Reflect.on(merb).field("exceptions").get();
    assertTrue(exceptions.get(pe.getId()) instanceof RuntimeException);
  }

  @Test
  public void testTimedOut1() {
    ProcessExecutionResult p = new DefaultProcessExecutionResult(pe, empty(), of(new RuntimeException()), Instant.now(),
        Duration.ofSeconds(1));
    assertFalse(p.isTimedOut());
    p = new DefaultProcessExecutionResult(pe, empty(), of(new TimeoutException()), Instant.now(),
        Duration.ofSeconds(1));
    assertTrue(p.isTimedOut());
  }

  @Test
  public void testTimes() {
    final Instant x = ofEpochMilli(100L);
    final Duration d = ofMillis(100L);
    final Instant end = x.plus(d);
    assertEquals(x, res.getStartTime());
    assertEquals(d, res.getRunningtime());
    assertEquals(end, res.getEndTime());

  }

}
