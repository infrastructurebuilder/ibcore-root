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

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.config.TestingPathSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessRunnerTest {
  private final static Logger logger = LoggerFactory.getLogger(ProcessRunnerTest.class);

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  private DefaultProcessRunner runner;
  private Checksum             packerCsum;

  private Path packerExecutable;

  private Path scratchDir;

  private String ttClass;

  private Path                ttest1;
  private boolean             isWindows;
  private TestingPathSupplier wps = new TestingPathSupplier();
  private Path                target;

  @Before
  public void setUp() throws Exception {
    target = wps.getRoot();
    scratchDir = target.resolve(UUID.randomUUID().toString());
    runner = new DefaultProcessRunner(scratchDir, of(System.out), of(logger), of(target));
    isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    packerExecutable = target.resolve("packer" + (isWindows ? ".exe" : "")).toAbsolutePath();
    packerCsum = new Checksum(Files.newInputStream(packerExecutable));
    ttest1 = wps.getTestClasses();
    ttClass = "ThreadTest1S";
  }

  @After
  public void tearDown() {
    IBUtils.deletePath(scratchDir);
  }

  @Test
  public void testAddlConstructors1() {
    IBUtils.deletePath(scratchDir);
    Optional<PrintStream> addl = empty();
    assertNotNull(new DefaultProcessRunner(scratchDir, addl));
    logger.info("I totally ran!");
  }

  @Test
  public void testAddlConstructors2() {
    IBUtils.deletePath(scratchDir);
    Optional<PrintStream> addl = empty();
    assertNotNull(new DefaultProcessRunner(scratchDir, addl, of(logger)));
    logger.info("I totally ran!");
  }

  @Test(expected = ProcessException.class)
  public void testAddLocked() {
    final String id = UUID.randomUUID().toString();
    runner = runner.addExecution(id, packerExecutable.toString(),
        asList("version", "-machine-readable", "-color=false"), empty(), empty(), of(scratchDir), of(packerCsum), false,
        empty(), of(target), empty(), false);
    runner.lock(ofSeconds(15), empty()).lock(Duration.ZERO, empty());
    runner = runner.addExecution(id, packerExecutable.toString(),
        asList("version", "-machine-readable", "-color=false"), empty(), empty(), of(scratchDir), of(packerCsum), false,
        empty(), of(target), empty(), false);

  }

  @Test
  public void testDaemon() {
    final String id = UUID.randomUUID().toString();
    final Path in = ttest1.resolve("ThreadTest1S.class");
    try (ProcessRunner newrunner = runner.addExecution(id, "java", asList(ttClass, "1"), of(ofMinutes(5)), of(in),
        of(ttest1), empty(), false, empty(), of(target), empty(), true)) {
      newrunner.setKeepScratchDir(false);
      newrunner.lock(ofSeconds(15), of(25L));
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
    final Path in = ttest1.resolve("ThreadTest1S.class");
    try (ProcessRunner newrunner = runner.addExecution(id, "javac", asList(), of(ofMinutes(1)), of(in), of(wps.getRoot().resolve(id)),
        empty(), false, empty(), of(target), empty(), true)) {
      newrunner.setKeepScratchDir(false);
      newrunner.lock(ofSeconds(15), of(25L));
      final ProcessExecutionResultBag p = runner.get().get();

      assertTrue(newrunner.hasErrorResult(p.getResults()));
      final ProcessExecutionResult a = p.getExecutions().get(id);
      assertEquals(2, a.getResultCode().get().longValue());
      logger.debug(a.getId() + " " + a.getException().toString() + a.getResultCode());
    } catch (final Exception e) {
      logger.error("Unexpected exception occurred", e);
      fail(e.getClass().getCanonicalName() + " " + e.getMessage());
    }
    assertFalse(Files.exists(scratchDir));
  }

  @Test
  public void testGetExecution() throws Exception {
    final String id = "default";
    final String executable = "java";

    final List<String> arguments = asList("-version");
    final Optional<Duration> timeout = empty();
    final Optional<Path> stdIn = empty();
    final Optional<Path> workDirectory = of(scratchDir);
    final Optional<Checksum> checksum = empty();
    final boolean optional = false;
    final Optional<Map<String, String>> environment = empty();
    try (ProcessRunner e2 = runner.addExecution(id, executable, arguments, timeout, stdIn, workDirectory, checksum,
        optional, environment, of(scratchDir), empty(), false).setKeepScratchDir(true)) {
      final ProcessExecution e = e2.getProcessExecutionForId(id).get();
      assertNotNull(e);
      assertNotNull(e.getStdErr());
      assertNotNull(e.getStdIn());
      assertNotNull(e.getStdOut());
      assertNotNull(e.getArguments());
      assertFalse(e.isOptional());
      assertNotNull(e.getExecutable());
    } catch (final Exception e1) {
      throw e1;
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
    final Path in = ttest1.resolve("ThreadTest1S.class");
    runner = runner.addExecution(id, "java", asList(ttClass, "60"), of(ofMinutes(5)), of(in), of(ttest1), empty(),
        false, empty(), of(target), empty(), true);
    runner.lock(ofSeconds(4), of(25L));
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

    try (ProcessRunner prr = new DefaultProcessRunner(p, of(System.out), of(logger), of(target))) {
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
    try (ProcessRunner pu = new DefaultProcessRunner(p, of(System.out), of(logger), of(target))) {
      assertNotNull(pu.addExecution(id, "java", asList("-version"), empty(), empty(), empty(), empty(), false, empty(),
          of(scratchDir), of(asList(0)), false));
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

    try (ProcessRunner prr = new DefaultProcessRunner(p, of(System.out), of(logger), of(target))) {

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
        asList("version", "-machine-readable", "-color=false"), of(ofSeconds(20)), empty(), of(scratchDir),
        of(packerCsum), false, empty(), of(target), empty(), false);
    runner.lock(ofSeconds(15), empty()).lock(Duration.ZERO, empty());
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
        .addExecution(id, packerExecutable.toString(), asList("version", "-machine-readable", "-color=false"),
            of(ofSeconds(20)), empty(), of(scratchDir), of(packerCsum), false, empty(), of(target), empty(),
            false)

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
        asList("version", "-machine-readable", "-color=false"), empty(), empty(), empty(), of(new Checksum("abcd")),
        false, empty(), of(target), empty(), false);
  }

  @Test(expected = ProcessException.class)
  public void testwithNegativeDuration() {
    final String id = UUID.randomUUID().toString();
    runner = runner.addExecution(id, packerExecutable.toString(),
        asList("version", "-machine-readable", "-color=false"), empty(), empty(), empty(), of(packerCsum), false,
        empty(), of(target), empty(), false);
    runner.lock(ofSeconds(-15), empty()).lock(Duration.ZERO, empty());
  }

}
