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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.listener.ProcessListener;

public class MutableProcessExecutionResultBag extends ProcessListener {
  private final ConcurrentMap<String, Instant> endTimes = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Throwable> exceptions = new ConcurrentHashMap<>();
  private final List<String> executedIds = new ArrayList<>();
  private final ConcurrentMap<ProcessExecution, ProcessExecutor> executors = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Integer> exitCodes = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Future<ProcessResult>> futures = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Process> processes = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Instant> startTimes = new ConcurrentHashMap<>();

  public void addExecution(final ProcessExecution pe, final ProcessExecutor pExecutor) {
    synchronized (executedIds) {
      executors.put(Objects.requireNonNull(pe), Objects.requireNonNull(pExecutor));
      executedIds.add(pe.getId());
    }
  }

  public MutableProcessExecutionResultBag addFuture(final ProcessExecution pe, final Future<ProcessResult> future) {
    futures.put(Objects.requireNonNull(pe).getId(), Objects.requireNonNull(future));
    return this;
  }

  public void addProcess(final ProcessExecution pe, final Process process) {
    processes.put(Objects.requireNonNull(pe).getId(), Objects.requireNonNull(process));

  }

  @Override
  public void afterFinish(final Process process, final ProcessResult result) {
    afterStop(process);
    processes.entrySet().stream().filter(e -> e.getValue() == process).map(ee -> ee.getKey()).findFirst()
        .ifPresent(pe -> {
          endTimes.put(pe, Instant.now());
          exitCodes.put(pe, result.getExitValue());
        });

  }

  @Override
  public void afterStart(final Process process, final ProcessExecutor executor) {
    super.afterStart(process, executor);
  }

  @Override
  public void afterStop(final Process process) {
    processes.entrySet().stream().filter(e -> e.getValue() == process).map(ee -> ee.getKey()).findFirst()
        .ifPresent(pe -> {
          endTimes.put(pe, Instant.now());
          exitCodes.put(pe, process.exitValue());
        });

  }

  @Override
  public void beforeStart(final ProcessExecutor executor) {
    Objects.requireNonNull(executor);
    executors.entrySet().stream().filter(e -> e.getValue() == executor).map(e -> e.getKey()).findFirst()
        .ifPresent(ee -> {
          startTimes.put(ee.getId(), Instant.now());
        });
  }

  public boolean destroyRemainingSleepers(final Optional<Long> sleepAfter) {
    Objects.requireNonNull(sleepAfter);
    for (final String p : getRunningFutures().keySet()) {
      processes.get(p).destroy();
    }
    for (final Future<ProcessResult> p : getRunningFutures().values()) {
      p.cancel(false);
    }
    final long sleep = sleepAfter.orElse(0L);
    try {
      Thread.sleep(sleep);
    } catch (final InterruptedException e) {
    }
    boolean retVal = false;
    for (final Future<ProcessResult> p : getRunningFutures().values()) {
      retVal |= p.cancel(true);
    }
    return retVal;
  }

  public List<String> getExecutedIds() {
    return executedIds;
  }

  public Map<String, ProcessExecutionResult> getExecutionResults() {
    synchronized (executedIds) {
      final Map<String, ProcessExecutionResult> m = new HashMap<>();
      for (final ProcessExecution pe : executors.keySet()) {
        final String id = pe.getId();
        final Instant startTime = Optional.ofNullable(startTimes.get(id)).orElse(null);
        final Instant endTime = Optional.ofNullable(endTimes.get(id)).orElse(Instant.MAX);
        final Optional<Integer> exitCode = Optional.ofNullable(exitCodes.get(id));
        final Optional<Throwable> exception = Optional.ofNullable(exceptions.get(id));
        m.put(id, new ProcessExecutionResult(pe, exitCode, exception, startTime, Duration.between(startTime, endTime)));
      }
      return m;
    }
  }

  public Map<String, Future<ProcessResult>> getFutures() {
    return futures;
  }

  public ProcessExecutionResultBag lock() {
    return new ProcessExecutionResultBag(this);
  }

  public void setException(final ProcessExecution pe, final Throwable t) {
    exceptions.put(Objects.requireNonNull(pe).getId(), Objects.requireNonNull(t));

  }

  public boolean stillRunning() {
    return getRunningFutures().values().stream().findAny().isPresent();
  }

  Map<String, Future<ProcessResult>> getRunningFutures() {
    return getFutures().entrySet().stream().filter(e -> !e.getValue().isDone())
        .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));
  }

}
