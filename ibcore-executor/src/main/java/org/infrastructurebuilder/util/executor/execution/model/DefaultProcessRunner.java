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

import static java.nio.file.Files.exists;
import static java.time.Duration.ZERO;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.executor.api.ProcessException.pet;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.infrastructurebuilder.pathref.IBChecksumUtils;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.util.executor.api.ProcessException;
import org.infrastructurebuilder.util.executor.api.ProcessExecution;
import org.infrastructurebuilder.util.executor.api.ProcessExecutionResult;
import org.infrastructurebuilder.util.executor.api.ProcessExecutionResultBag;
import org.infrastructurebuilder.util.executor.api.ProcessRunner;
import org.infrastructurebuilder.util.logging.NOOPLogger;
import org.slf4j.Logger;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

public class DefaultProcessRunner implements ProcessRunner<ProcessExecutor> {

  private final Optional<PrintStream> addl;
  private boolean keepScratchDir = false;
  private final Logger logger;

  private final AtomicReference<Set<Future<ProcessExecutionResult<ProcessExecutor>>>> locked = new AtomicReference<>(null);
  private final AtomicReference<ProcessExecutionResultBag<ProcessExecutor>> result = new AtomicReference<>(null);

  private final Path scratchDir;
  private final Vector<ProcessExecution<ProcessExecutor>> serialList = new Vector<>();
  private final PathRefFileSystem root;

  public DefaultProcessRunner(final PathRefFileSystem root, final String scratchDir, final Optional<PrintStream> addl) {
    this(root, scratchDir, addl, empty(), empty());
  }

  public DefaultProcessRunner(final PathRefFileSystem root, final String scratchDir, final Optional<PrintStream> addl,
      final Optional<Logger> logger)
  {
    this(root, scratchDir, addl, logger, Optional.of(Long.valueOf(100L)));
  }

  public DefaultProcessRunner(final PathRefFileSystem root, final String scratchDir, final Optional<PrintStream> addl,
      final Optional<Logger> logger, final Optional<Long> iterimSleepValue)
  {
    this.root = requireNonNull(root);
    this.scratchDir = this.root.getPath(requireNonNull(scratchDir));
    if (exists(this.scratchDir))
      throw new ProcessException("Scratch directory must not exist -> " + this.scratchDir);
    pet.translate(() -> {
      Files.createDirectories(this.scratchDir);
    });
    this.addl = requireNonNull(addl);
    this.logger = requireNonNull(logger).orElse(new NOOPLogger());

  }

  @Override
  public DefaultProcessRunner add(final Supplier<ProcessExecution<ProcessExecutor>> e) {
    if (locked.get() != null)
      throw new ProcessException("Already locked");
    serialList.add(requireNonNull(e.get()));
    return this;
  }

  @Override
  public void close() throws Exception {
    if (!isKeepScratchDir()) {
      IBChecksumUtils.deletePath(scratchDir);
    }
  }

  @Override
  public Optional<ProcessExecutionResultBag<ProcessExecutor>> get() {
    return ofNullable(result.get());
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
  public Optional<ProcessExecution<ProcessExecutor>> getProcessExecutionForId(final String id) {
    requireNonNull(id);
    return serialList.stream().filter(pe -> pe.getId().equals(id)).findFirst();
  }

  @Override
  public final boolean hasErrorResult(final Map<String, ProcessExecutionResult<ProcessExecutor>> resultMap) {
    for (final ProcessExecutionResult<ProcessExecutor> res : resultMap.values()) {
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
    return lock(ZERO, empty());
  }

  @Override
  public DefaultProcessRunner lock(final Duration fin, final Optional<Long> sleepAfterDestroy) {
    synchronized (locked) {
      if (locked.get() != null)
        return this;
      else {
        if (requireNonNull(fin).isNegative())
          throw new ProcessException("Final duration cannot be negative " + fin);
        final Instant startedLock = now();
        final Instant endLock = startedLock.plus(fin.equals(ZERO) ? ProcessExecution.VERY_LONG : fin);
        locked.compareAndSet(null, new HashSet<Future<ProcessExecutionResult<ProcessExecutor>>>());

        final MutableProcessExecutionResultBag bag = new MutableProcessExecutionResultBag();
        for (final ProcessExecution<ProcessExecutor> pe : serialList) {
          ProcessExecutor pExecutor;
          pExecutor = pe.getProcessExecutor().addListener(bag);
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
              bag.afterFinish(s.getProcess(), res);
            }
          } catch (InvalidExitValueException | TimeoutException | ExecutionException | InterruptedException
              | IOException te) {
            bag.setException(pe, te);
          }
        }

        if (!fin.isZero()) {
          Duration dur = between(now(), endLock);
          while (bag.stillRunning() && dur.toNanos() > 0) {
            pet.translate(() -> Thread.sleep(100L));
            dur = between(now(), endLock);
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
  public ProcessRunner<ProcessExecutor> setKeepScratchDir(final boolean keepScratchDir) {
    this.keepScratchDir = keepScratchDir;
    return this;
  }

}
