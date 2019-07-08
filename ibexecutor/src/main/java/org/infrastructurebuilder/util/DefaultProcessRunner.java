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

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLoggerFactory;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

@Named
public class DefaultProcessRunner implements ProcessRunner {
  public final static Path touchFile(final Path path) {
    return ProcessException.pet.withReturningTranslation(() -> {
      if (Files.exists(path)) {
        if (!Files.isRegularFile(path) || !Files.isWritable(path))
          throw new ProcessException("File " + path.toAbsolutePath() + " is not available to write");
        return path;
      } else {
        Files.createDirectories(path.getParent());
      }
      return Files.createFile(path);
    });
  }

  public final Pattern ws = Pattern.compile("\\s");

  private final Optional<PrintStream> addl;
  private boolean keepScratchDir = false;
  private final AtomicReference<Set<Future<ProcessExecutionResult>>> locked = new AtomicReference<>(null);
  private final Logger logger;
  private final AtomicReference<ProcessExecutionResultBag> result = new AtomicReference<>(null);

  private final Path scratchDir;
  private final Vector<ProcessExecution> serialList = new Vector<>();

  public DefaultProcessRunner(final Path scratchDir, final Optional<PrintStream> addl) {
    this(scratchDir, addl, Optional.empty(), Optional.empty());
  }

  public DefaultProcessRunner(final Path scratchDir, final Optional<PrintStream> addl, final Optional<Logger> logger) {
    this(scratchDir, addl, logger, Optional.empty(), Optional.of(new Long(100L)));
  }

  public DefaultProcessRunner(final Path scratchDir, final Optional<PrintStream> addl, final Optional<Logger> logger,
      final Optional<Path> relativeRoot) {
    this(scratchDir, addl, logger, relativeRoot, Optional.of(new Long(100L)));
  }

  public DefaultProcessRunner(final Path scratchDir, final Optional<PrintStream> addl, final Optional<Logger> logger,
      final Optional<Path> relativeRoot, final Optional<Long> iterimSleepValue) {
    this.scratchDir = Objects.requireNonNull(scratchDir);
    if (Files.exists(this.scratchDir))
      throw new ProcessException("Scratch directory must not exist -> " + this.scratchDir);
    ProcessException.pet.withTranslation(() -> {
      Files.createDirectories(scratchDir);
    });
    this.addl = Objects.requireNonNull(addl);
    this.logger = Objects.requireNonNull(logger)
        .orElse(new NOPLoggerFactory().getLogger(DefaultProcessRunner.class.toString()));
  }

  @Override
  public DefaultProcessRunner add(final ProcessExecution e) {
    if (locked.get() != null)
      throw new ProcessException("Already locked");
    serialList.add(Objects.requireNonNull(e));
    return this;
  }

  @Override
  public DefaultProcessRunner addExecution(final String id, final String executable, final List<String> arguments,
      final Optional<Duration> timeout, final Optional<Path> stdIn, final Optional<Path> workDirectory,
      final Optional<Checksum> checksum, final boolean optional, final Optional<Map<String, String>> environment,
      final Optional<Path> relativeRoot, final Optional<List<Integer>> exitCodes, final boolean background) {

    final Path execScratch = scratchDir.resolve(Objects.requireNonNull(id, "execution id"));
    if (ws.matcher(id).find())
      throw new ProcessException("No whitespace is allowed in execution ids for ProcessRunner");
    if (!Files.isDirectory(Objects.requireNonNull(execScratch))) {
      ProcessException.pet.withTranslation(() -> {
        Files.createDirectories(execScratch);
      });
    }
    if (!Files.isWritable(execScratch))
      throw new ProcessException("Cannot write to " + execScratch);
    final Path stdOut = execScratch.resolve(STD_OUT_FILENAME);
    touchFile(stdOut);
    final Path stdErr = execScratch.resolve(STD_ERR_FILENAME);
    touchFile(stdErr);
    final Path p = Paths.get(Objects.requireNonNull(executable));
    Objects.requireNonNull(checksum).ifPresent(csum -> {
      Checksum c = null;
      if (Files.isExecutable(p)) {
        try {
          c = new Checksum(Files.newInputStream(p));
        } catch (final IOException e) {

        }
      }
      if (!csum.equals(c))
        throw new ProcessException("Checksum of executable " + c + " does not match supplied " + csum);
    });
    return add(new ProcessExecution(id, executable, Objects.requireNonNull(arguments), timeout, stdOut, stdErr, stdIn,
        workDirectory, optional, environment, relativeRoot, Objects.requireNonNull(exitCodes), getAddl(), background));
  }

  @Override
  public void close() throws Exception {
    if (!isKeepScratchDir()) {
      IBUtils.deletePath(scratchDir);
    }
  }

  @Override
  public Optional<ProcessExecutionResultBag> get() {
    return Optional.ofNullable(result.get());
  }

  @Override
  public Optional<PrintStream> getAddl() {
    return addl;
  }

  @Override
  public Logger getLogger() {
    return logger;
  }

  @Override
  public Optional<ProcessExecution> getProcessExecutionForId(final String id) {
    Objects.requireNonNull(id);
    return serialList.stream().filter(pe -> pe.getId().equals(id)).findFirst();
  }

  @Override
  public final boolean hasErrorResult(final Map<String, ProcessExecutionResult> resultMap) {
    for (final ProcessExecutionResult res : resultMap.values()) {
      final Optional<Integer> resultCode = res.getResultCode();
      if (!res.getStdErr().toString().isEmpty()) {
        getLogger().error(res.getStdErr().toString());
      }
      getLogger().info(res.getStdOut().toString());
      if (!resultCode.isPresent() || resultCode.get() != 0) {
        getLogger().error(String.format("Result code %s differed from expected result 0", resultCode.toString()));
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isKeepScratchDir() {
    return keepScratchDir;
  }

  @Override
  public DefaultProcessRunner lock() {
    return lock(Duration.ZERO, Optional.empty());
  }

  @Override
  public DefaultProcessRunner lock(final Duration fin, final Optional<Long> sleepAfterDestroy) {
    synchronized (locked) {
      if (locked.get() != null)
        return this;
      else {
        Objects.requireNonNull(fin);
        if (fin.isNegative())
          throw new ProcessException("Final duration cannot be negative " + fin);
        final Instant startedLock = Instant.now();
        final Instant endLock = startedLock.plus(fin.equals(Duration.ZERO) ? ProcessExecution.VERY_LONG : fin);
        locked.compareAndSet(null, new HashSet<Future<ProcessExecutionResult>>());

        final MutableProcessExecutionResultBag bag = new MutableProcessExecutionResultBag();
        for (final ProcessExecution pe : serialList) {
          ProcessExecutor pExecutor;
          pExecutor = pe.getProcessExecutor()

              .addListener(bag);
          bag.addExecution(pe, pExecutor);

          try {
            final StartedProcess s = pExecutor.start();
            final Future<ProcessResult> future = s.getFuture();
            bag.addProcess(pe, s.getProcess());
            if (pe.isBackground()) {
              bag.addFuture(pe, future);
            } else {
              final ProcessResult res = pe.getTimeout().isPresent()
                  ? future.get(pe.getTimeout().get().get(ChronoUnit.SECONDS), TimeUnit.SECONDS)
                  : future.get();
            }
          } catch (InvalidExitValueException | TimeoutException | ExecutionException | InterruptedException
              | IOException te) {
            bag.setException(pe, te);
          }
        }

        if (!fin.isZero()) {
          Duration dur = Duration.between(Instant.now(), endLock);
          while (bag.stillRunning() && dur.toNanos() > 0) {

            ProcessException.pet.withTranslation(() -> {
              Thread.sleep(1000L);
            });
            dur = Duration.between(Instant.now(), endLock);
          }
          if (bag.stillRunning())
            if (bag.destroyRemainingSleepers(sleepAfterDestroy)) {
              logger.warn("Failed to destroy some sleeping processes.  YMMV.");
            }
        }

        result.set(bag.lock());
        return this;
      }
    }
  }

  @Override
  public ProcessRunner setKeepScratchDir(final boolean keepScratchDir) {
    this.keepScratchDir = keepScratchDir;
    return this;
  }

}