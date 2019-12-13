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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.infrastructurebuilder.IBConstants.*;
public class ProcessRunnerTest {
  private final static Logger logger = LoggerFactory.getLogger(ProcessRunnerTest.class);

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  private DefaultProcessRunner runner;
  private Checksum packerCsum;

  private Path packerExecutable;

  private Path scratchDir;


  private String ttClass;

  private Path ttest1;
  private boolean isWindows;
  private TestingPathSupplier wps = new TestingPathSupplier();
  private Path target;

  @Before
  public void setUp() throws Exception {
    target = wps.getRoot();
    scratchDir = target.resolve(UUID.randomUUID().toString());
    runner = new DefaultProcessRunner(scratchDir, Optional.of(System.out), Optional.of(logger), Optional.of(target));
    isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    packerExecutable = target.resolve("packer" + (isWindows ? ".exe" : "")).toAbsolutePath();
    packerCsum = new Checksum(Files.newInputStream(packerExecutable));
    ttest1 = target.resolve("test-classes");
    ttClass = "ThreadTest1S";
  }

  @Test(expected = ProcessException.class)
  public void testAddLocked() {
    final String id = UUID.randomUUID().toString();
    runner = runner.addExecution(id, packerExecutable.toString(),
        Arrays.asList("version", "-machine-readable", "-color=false"), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.of(packerCsum), false, Optional.empty(), Optional.of(target), Optional.empty(),
        false);
    runner.lock(Duration.ofSeconds(15), Optional.empty()).lock(Duration.ZERO, Optional.empty());
    runner = runner.addExecution(id, packerExecutable.toString(),
        Arrays.asList("version", "-machine-readable", "-color=false"), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.of(packerCsum), false, Optional.empty(), Optional.of(target), Optional.empty(),
        false);

  }

  @Test
  public void testDaemon() {
    final String id = UUID.randomUUID().toString();
    final Path in = target.resolve("test-classes").resolve("ThreadTest1S.class");
    try (ProcessRunner newrunner = runner.addExecution(id, "java", Arrays.asList(ttClass, "1"),
        Optional.of(Duration.ofMinutes(5)), Optional.of(in), Optional.of(ttest1), Optional.empty(), false,
        Optional.empty(), Optional.of(target), Optional.empty(), true)) {
      newrunner.setKeepScratchDir(false);
      newrunner.lock(Duration.ofSeconds(15), Optional.of(25L));
      final ProcessExecutionResultBag p = runner.get().get();
      assertTrue(p.getDuration().isPresent());
      assertNotNull(p.getStdErrs());
      assertNotNull(p.getStdOuts());
      assertNotNull(p.getStdOut());
      assertNotNull(p.getStdErr());
      assertNotNull(p.getExecutionEnvironment());
      assertNotNull(p.getResults());
      assertFalse(newrunner.hasErrorResult(p.getResults()));

      final ProcessExecutionResult a = p.getExecutions().get(id);
      final String x = String.join("\n", a.getStdOut());
      assertTrue(x.contains("SUCCESS"));
    } catch (final Exception e) {
      fail(e.getClass().getCanonicalName() + " " + e.getMessage());
    }
    assertFalse(Files.exists(scratchDir));
  }

  @Test
  public void testErrorResult() {
    final String id = UUID.randomUUID().toString();
    final Path in = target.resolve("test-classes").resolve("ThreadTest1S.class");
    try (ProcessRunner newrunner = runner.addExecution(id, "javac", Arrays.asList(), Optional.of(Duration.ofMinutes(1)),
        Optional.of(in), Optional.of(ttest1), Optional.empty(), false, Optional.empty(), Optional.of(target),
        Optional.empty(), true)) {
      newrunner.setKeepScratchDir(false);
      newrunner.lock(Duration.ofSeconds(15), Optional.of(25L));
      final ProcessExecutionResultBag p = runner.get().get();

      assertTrue(newrunner.hasErrorResult(p.getResults()));
      final ProcessExecutionResult a = p.getExecutions().get(id);
      assertEquals(2, a.getResultCode().get().longValue());
      logger.debug(a.getId() + " " + a.getException().toString() + a.getResultCode());
    } catch (final Exception e) {
      logger.error("Unexpected exception occurred" ,e);
      fail(e.getClass().getCanonicalName() + " " + e.getMessage());
    }
    assertFalse(Files.exists(scratchDir));
  }

  @Test
  public void testGetExecution() {
    final String id = "default";
    final String executable = "java";

    final List<String> arguments = Arrays.asList("-version");
    final Optional<Duration> timeout = Optional.empty();
    final Optional<Path> stdIn = Optional.empty();
    final Optional<Path> workDirectory = Optional.empty();
    final Optional<Checksum> checksum = Optional.empty();
    final boolean optional = false;
    final Optional<Map<String, String>> environment = Optional.empty();
    try (ProcessRunner e2 = runner.addExecution(id, executable, arguments, timeout, stdIn, workDirectory, checksum,
        optional, environment, Optional.of(scratchDir), Optional.empty(), false).setKeepScratchDir(true)) {
      final ProcessExecution e = e2.getProcessExecutionForId(id).get();
      assertNotNull(e);
      assertNotNull(e.getStdErr());
      assertNotNull(e.getStdIn());
      assertNotNull(e.getStdOut());
      assertNotNull(e.getArguments());
      assertFalse(e.isOptional());
      assertNotNull(e.getExecutable());
      assertNotNull(e.getExecutionString());
    } catch (final Exception e1) {
      fail();
    }
  }

  @Test
  public void testGetLogger() {
    assertNotNull(runner.getLogger());
    runner.getLogger().info("Test get the logger");
  }

  @Test
  public void testGetUnRunResults() {
    assertFalse(runner.get().isPresent());
  }

  @Test
  public void testLongRunningDaemon() {
    final String id = UUID.randomUUID().toString();
    final Path in = target.resolve("test-classes").resolve("ThreadTest1S.class");
    runner = runner.addExecution(id, "java", Arrays.asList(ttClass, "60"), Optional.of(Duration.ofMinutes(5)),
        Optional.of(in), Optional.of(ttest1), Optional.empty(), false, Optional.empty(), Optional.of(target),
        Optional.empty(), true);
    runner.lock(Duration.ofSeconds(4), Optional.of(25L));
    final ProcessExecutionResultBag p = runner.get().get();
    assertTrue(p.getDuration().isPresent());
    assertNotNull(p.getStdErrs());
    assertNotNull(p.getStdOuts());
    assertNotNull(p.getStdOut());
    assertNotNull(p.getStdErr());
    assertNotNull(p.getExecutionEnvironment());
    assertNotNull(p.getResults());

    final ProcessExecutionResult a = p.getExecutions().get(id);
    final String x = String.join("\n", a.getStdOut());
    assertFalse(x.contains("FAILURE"));
  }

  @Test
  public void testPEFNoScratch() throws Exception {
    final Path p = scratchDir.resolve(UUID.randomUUID().toString());
    if (Files.exists(p))
      throw new RuntimeException("Test failed because of a random thing");

    try (ProcessRunner prr = new DefaultProcessRunner(p, Optional.of(System.out), Optional.of(logger),
        Optional.of(target))) {
    }
  }

  @Test(expected = ProcessException.class)
  public void testPEFNoScratchUnwriteableExec() throws Exception {
    final Path p = scratchDir.resolve(UUID.randomUUID().toString());
    if (Files.exists(p))
      throw new RuntimeException("Test failed because of a random thing");
    final String id = UUID.randomUUID().toString();
    final Path under = p.resolve(id);
    Files.createDirectories(under);
    under.toFile().setReadOnly();
    try (ProcessRunner pu = new DefaultProcessRunner(p, Optional.of(System.out), Optional.of(logger),
        Optional.of(target))) {
      assertNotNull(pu.addExecution(id, "java", Arrays.asList("-version"), Optional.empty(), Optional.empty(),
          Optional.empty(), Optional.empty(), false, Optional.empty(), Optional.of(scratchDir),
          Optional.of(Arrays.asList(0)), false));
    }
  }

  @Test(expected = ProcessException.class)
  public void testPEFUnwriteable() throws Exception {
    final Path unknown = scratchDir.resolve(UUID.randomUUID().toString());
    if (Files.exists(unknown))
      throw new RuntimeException("Test failed because of a random thing");

    final Path p = Files.createDirectories(unknown);
    if (!isWindows && !p.toFile().setReadOnly())
      throw new RuntimeException("Cannot set readonly to file");

    try (ProcessRunner prr = new DefaultProcessRunner(p, Optional.of(System.out), Optional.of(logger),
        Optional.of(target))) {

    }
  }

  @Test(expected = ProcessException.class)
  public void testTouchDir() {
    DefaultProcessRunner.touchFile(scratchDir);
  }

  @Test
  public void testTouchNewFile() {
    DefaultProcessRunner.touchFile(scratchDir.resolve("ABC"));
  }

  @Test
  public void testTouchNewFile2() {
    DefaultProcessRunner.touchFile(scratchDir.resolve("DEF").resolve("GHI"));
  }

  @Test
  public void testwithChecksum() {
    final String id = UUID.randomUUID().toString();
    runner = runner.addExecution(id, packerExecutable.toString(),
        Arrays.asList("version", "-machine-readable", "-color=false"), Optional.of(Duration.ofSeconds(20)),
        Optional.empty(), Optional.empty(), Optional.of(packerCsum), false, Optional.empty(), Optional.of(target),
        Optional.empty(), false);
    runner.lock(Duration.ofSeconds(15), Optional.empty()).lock(Duration.ZERO, Optional.empty());
    final ProcessExecutionResultBag p = runner.get().get();
    assertTrue(p.getDuration().isPresent());
    assertNotNull(p.getStdErrs());
    assertNotNull(p.getStdOuts());
    assertNotNull(p.getStdOut());
    assertNotNull(p.getStdErr());
    assertNotNull(p.getExecutionEnvironment());
    assertNotNull(p.getResults());

    final ProcessExecutionResult a = p.getExecutions().get(id);
    final String x = String.join("\n", a.getStdOut());
    assertTrue(x.contains("version-prelease"));
  }

  @Test
  public void testwithChecksum2() {
    final String id = UUID.randomUUID().toString();
    runner = runner
        .addExecution(id, packerExecutable.toString(), Arrays.asList("version", "-machine-readable", "-color=false"),
            Optional.of(Duration.ofSeconds(20)), Optional.empty(), Optional.empty(), Optional.of(packerCsum), false,
            Optional.empty(), Optional.of(target), Optional.empty(), false)

        .lock();
    final ProcessExecutionResultBag p = runner.get().get();
    assertTrue(p.getDuration().isPresent());
    assertNotNull(p.getStdErrs());
    assertNotNull(p.getStdOuts());
    assertNotNull(p.getStdOut());
    assertNotNull(p.getStdErr());
    assertNotNull(p.getExecutionEnvironment());
    assertNotNull(p.getResults());

    final ProcessExecutionResult a = p.getExecution(id).get();
    final String x = String.join("\n", a.getStdOut());
    assertTrue(x.contains("version-prelease"));
  }

  @Test(expected = ProcessException.class)
  public void testwithFakeChecksum() {
    final String id = UUID.randomUUID().toString();
    runner = runner.addExecution(id, packerExecutable.toString(),
        Arrays.asList("version", "-machine-readable", "-color=false"), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.of(new Checksum("abcd")), false, Optional.empty(), Optional.of(target),
        Optional.empty(), false);
  }

  @Test(expected = ProcessException.class)
  public void testwithNegativeDuration() {
    final String id = UUID.randomUUID().toString();
    runner = runner.addExecution(id, packerExecutable.toString(),
        Arrays.asList("version", "-machine-readable", "-color=false"), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.of(packerCsum), false, Optional.empty(), Optional.of(target), Optional.empty(),
        false);
    runner.lock(Duration.ofSeconds(-15), Optional.empty()).lock(Duration.ZERO, Optional.empty());
  }

}
