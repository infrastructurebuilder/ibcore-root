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
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.infrastructurebuilder.util.IBUtils.isWindows;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
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
  private final static Logger              logger                = LoggerFactory.getLogger(ProcessRunnerTest.class);
  private final static TestingPathSupplier wps                   = new TestingPathSupplier();
  private final static String[]            PACKER_VERSION_PARAMS = { "version", "-machine-readable", "-color=false" };

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  private DefaultProcessRunner             runner;
  private Checksum                         packerCsum;
  private Path                             packerExecutable;
  private Path                             scratchDir;
  private String                           ttClass;
  private Path                             ttest1;
  private Path                             target;
  private VersionedProcessExecutionFactory vpef;

  @Before
  public void setUp() throws Exception {
    target = wps.getRoot();
    scratchDir = target.resolve(UUID.randomUUID().toString());
    runner = new DefaultProcessRunner(scratchDir, of(System.out), of(logger), of(target));
    packerExecutable = target.resolve("packer" + (isWindows() ? ".exe" : "")).toAbsolutePath();
    packerCsum = new Checksum(packerExecutable);
    ttest1 = wps.getTestClasses();
    ttClass = "ThreadTest1S";
    vpef = new DefaultVersionedProcessExecutionFactory(scratchDir, Optional.empty());
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
    ProcessExecutionFactory e = vpef.getFactoryForVersion("1.0.0", scratchDir, id, packerExecutable.toString()).get()

        .withArguments(PACKER_VERSION_PARAMS)

        .withChecksum(packerCsum);
    runner = runner.add(e);
    runner.lock(ofSeconds(15), empty()).lock(Duration.ZERO, empty());
    runner = runner.add(e);

  }

  @Test
  public void testDaemon() {
    final String id = UUID.randomUUID().toString();
    final Path in = ttest1.resolve("ThreadTest1S.class");
    ProcessExecutionFactory e2 = vpef.getDefaultFactory(ttest1, id, "java")

        .withArguments(ttClass, "1")

        .withDuration(ofMinutes(5))

        .withStdIn(in)

        .withRelativeRoot(target)

        .withBackground(true);

    try (ProcessRunner newrunner = runner.add(e2)) {
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

    ProcessExecutionFactory e2 = vpef.getFactoryForVersion("1.0.0", ttest1, id, "javac").get()

        .withDuration(ofMinutes(1))

        .withStdIn(in)

        .withRelativeRoot(target)

        .withBackground(true);

    try (ProcessRunner newrunner = runner.add(e2)) {
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

    ProcessExecutionFactory e3 = vpef.getFactoryForVersion("1.0.0", scratchDir, id, "java").get();

    try (ProcessRunner e2 = runner.add(e3)) {

      final ProcessExecution e = e2.getProcessExecutionForId(id).get();
      assertNotNull(e);
      assertNotNull(e.getStdErr());
      assertNotNull(e.getStdIn());
      assertNotNull(e.getStdOut());
      assertNotNull(e.getArguments());
      assertFalse(e.isOptional());
      assertNotNull(e.getExecutable());
    } catch (

    final Exception e1) {
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
    ProcessExecutionFactory e2 = vpef.getFactoryForVersion("1.0.0", ttest1, id, "java").get()

        .withArguments(ttClass, "60")

        .withDuration(ofMinutes(5))

        .withStdIn(in)

        .withRelativeRoot(target)

        .withBackground(true);

    runner = runner.add(e2);
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

//  @Test(expected = ProcessException.class)
//  public void testPEFNoScratchUnwriteableExec() throws Exception {
//    final Path p = scratchDir.resolve(UUID.randomUUID().toString());
//    if (Files.exists(p))
//      throw new RuntimeException("Test failed because of a random thing");
//    final String id = UUID.randomUUID().toString();
//    final Path under = p.resolve(id);
//    Files.createDirectories(under);
//    under.toFile().setReadOnly();
//    try (ProcessRunner pu = new DefaultProcessRunner(p, of(System.out), of(logger), of(target))) {
//      assertNotNull(pu.addExecution(id, "java", asList("-version"), empty(), empty(), empty(), empty(), false, empty(),
//          of(scratchDir), of(asList(0)), false));
//    }
//  }

  @Test(expected = ProcessException.class)
  public void testPEFUnwriteable() throws Exception {
    final Path unknown = scratchDir.resolve(UUID.randomUUID().toString());
    if (Files.exists(unknown))
      throw new RuntimeException("Test failed because of a random thing");

    final Path p = Files.createDirectories(unknown);
    if (!isWindows() && !p.toFile().setReadOnly())
      throw new RuntimeException("Cannot set readonly to file");

    try (ProcessRunner prr = new DefaultProcessRunner(p, of(System.out), of(logger), of(target))) {

    }
  }

  @Test
  public void testwithChecksum() {
    final String id = UUID.randomUUID().toString();
    ProcessExecutionFactory e2 = vpef.getFactoryForVersion("1.0.0", scratchDir, id, packerExecutable.toString()).get()
        // packer checksum
        .withChecksum(packerCsum)
        // Execution arguents
        .withArguments(PACKER_VERSION_PARAMS)
        // Default max runtime
        .withDuration(ofSeconds(20))
        // From the target root
        .withRelativeRoot(target);

    runner = runner.add(e2);
    runner.lock(ofSeconds(15), empty()).lock(Duration.ZERO, empty()); // Test double-locking
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

    ProcessExecutionFactory e2 = vpef.getFactoryForVersion("1.0.0", scratchDir, id, packerExecutable.toString()).get()

        .withArguments(PACKER_VERSION_PARAMS)

        .withDuration(ofSeconds(20))

        .withRelativeRoot(target)

        .withChecksum(packerCsum)

    ;

    runner = runner.add(e2).lock();
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
    ProcessExecutionFactory e2 = vpef.getFactoryForVersion("1.0.0", scratchDir, id, packerExecutable.toString()).get()

        .withArguments(PACKER_VERSION_PARAMS)

        .withDuration(ofSeconds(20))

        .withRelativeRoot(target)

        .withChecksum(new Checksum("abcd"))

    ;
    runner.add(e2);

  }

  @Test(expected = ProcessException.class)
  public void testwithNegativeDuration() {
    final String id = UUID.randomUUID().toString();
    runner = runner.add(vpef.getFactoryForVersion("1.0.0", scratchDir, id, packerExecutable.toString()).get())
        .lock(ofSeconds(-15), empty()).lock(Duration.ZERO, empty());
  }

}
