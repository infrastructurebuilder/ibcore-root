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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.joor.Reflect;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.zeroturnaround.exec.ProcessOutput;
import org.zeroturnaround.exec.ProcessResult;

public class ProcessExecutionResultTest {
  private static final List<String> ARGS = Arrays.asList("-version");

  private static final String EXEC = "java";
  private static final String ID = "default";

  private Future<ProcessResult> future;
  private OverrideListCapturingOutputStream lpaoE;
  private OverrideListCapturingOutputStream lpaoO;
  private MutableProcessExecutionResultBag merb;

  private ProcessExecution pe;

  private PrintStream pr;

  private ProcessExecutionResult res;

  private ProcessExecutionResult res3;

  private Path scratchDir;

  private List<String> stdErr;

  private Path stdErrPth;

  private List<String> stdOut;

  private Path stdOutPth;

  @Before
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
    scratchDir = Paths.get(Optional.ofNullable(System.getProperty("target_dir")).orElse("./target"));

    stdOutPth = DefaultProcessRunner.touchFile(scratchDir.resolve("extraStdOut"));

    stdErrPth = DefaultProcessRunner.touchFile(scratchDir.resolve("extraStdErr"));
    pe = new ProcessExecution(ID, EXEC, ARGS, Optional.empty(), stdOutPth, stdErrPth, Optional.empty(),
        Optional.empty(), false, Optional.empty(), Optional.of(scratchDir), Optional.empty(), Optional.empty(), false);

    lpaoO = new OverrideListCapturingOutputStream(Optional.of(stdOutPth), stdOut);
    lpaoE = new OverrideListCapturingOutputStream(Optional.of(stdErrPth), stdErr);
    pe = Reflect.on(pe).set("stdOut", lpaoO).set("stdErr", lpaoE).get();

    res = new ProcessExecutionResult(pe, Optional.of(0), Optional.empty(), Instant.ofEpochMilli(100L),
        Duration.ofMillis(100L));
    res3 = new ProcessExecutionResult(pe, Optional.of(0), Optional.empty(), Instant.ofEpochMilli(100L),
        Duration.ofMillis(100L));
    pr = new PrintStream(new ByteArrayOutputStream());
  }

  @Test
  public void testAsJSON() {
    final JSONObject a = res.asJSON();
    final String t = "{\n" + "  \"execution\": {\n" + "    \"arguments\": [\"-version\"],\n"
        + "    \"optional\": false,\n" + "    \"id\": \"default\",\n" + "    \"executable\": \"java\"\n" + "  },\n"
        + "  \"result-code\": 0,\n" + "  \"runtime\": \"PT0.1S\",\n" + "  \"std-out\": [\n" + "    \"hello\",\n"
        + "    \"gentlepersons\"\n" + "  ],\n" + "  \"std-err\": [\n" + "    \"Hi\",\n" + "    \"there\"\n" + "  ]\n"
        + "}";
    final JSONObject target = new JSONObject(t);
    JSONAssert.assertEquals(target, a, true);

  }

  @Test
  public void testChecksum() {

    final Checksum peC = new Checksum(
        "9280158a026b8ac8eafd9f241080135d9701b6f47a7e4bb388004e0908aeb71845b47c4c322bbfe5d593767e85aef8bd33b88bb4cf57856b5fff3d277b56394b");
    assertEquals(peC.toString(), pe.asChecksum().toString());
    final Checksum s = new Checksum(
        "b4a9c4c0458552946113b93388104e5decb6a6c969ab3cbd1fe5e8317ac71dc3772aca7c9f8d451610cfd8d4a14943ab3cb34495ef0f6964b993e32a89bb2dca");
    assertEquals(s.toString(), res.asChecksum().toString());
  }

  @Test
  public void testClosingCapturedStream() {
    try (ListCapturingLogOutputStream abc = new ListCapturingLogOutputStream(Optional.empty(), Optional.of(pr))) {

    } catch (final IOException e) {

      e.printStackTrace();
    }
  }

  @Test
  public void testEqualsObject() {
    final ProcessExecution pe2 = new ProcessExecution("abc", EXEC, ARGS, Optional.empty(),
        scratchDir.resolve(UUID.randomUUID().toString()), scratchDir.resolve(UUID.randomUUID().toString()),
        Optional.empty(), Optional.empty(), false, Optional.empty(), Optional.of(scratchDir), Optional.empty(),
        Optional.empty(), false);
    assertNotEquals(res, new ProcessExecutionResult(pe2, Optional.of(0), Optional.empty(), Instant.ofEpochMilli(100L),
        Duration.ofMillis(100L)));
    assertEquals(res, res);
    assertNotEquals(res, "abc");
    assertNotEquals(res, null);
    assertEquals(res, res3);
  }

  @Test
  public void testGetExecution() {
    assertEquals(pe, res.getExecution());
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
    final ProcessExecutionResultBag b = merb.lock();
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
  public void testNegativeDuration() {
    try (ProcessExecution vv = new ProcessExecution(ID, EXEC, ARGS, Optional.of(Duration.ofHours(-1)), stdOutPth,
        stdErrPth, Optional.empty(), Optional.empty(), false, Optional.empty(), Optional.of(scratchDir),
        Optional.empty(), Optional.empty(), false)) {

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
    ProcessExecutionResult p = new ProcessExecutionResult(pe, Optional.empty(), Optional.of(new RuntimeException()),
        Instant.now(), Duration.ofSeconds(1));
    assertFalse(p.isTimedOut());
    p = new ProcessExecutionResult(pe, Optional.empty(), Optional.of(new TimeoutException()), Instant.now(),
        Duration.ofSeconds(1));
    assertTrue(p.isTimedOut());
  }

  @Test
  public void testTimes() {
    final Instant x = Instant.ofEpochMilli(100L);
    final Duration d = Duration.ofMillis(100L);
    final Instant end = x.plus(d);
    assertEquals(x, res.getStartTime());
    assertEquals(d, res.getRunningtime());
    assertEquals(end, res.getEndTime());

  }

}
