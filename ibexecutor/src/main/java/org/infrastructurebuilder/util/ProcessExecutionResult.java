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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.JSONAndChecksumEnabled;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;

public final class ProcessExecutionResult implements JSONAndChecksumEnabled {
  public final static Function<ProcessExecutionResult, Boolean> defaultValidator = (r) -> {
    return r.getException().isPresent() || r.getResultCode().orElse(ProcessExecutionResult.FAIL) != 0;

  };

  private static final String EXCEPTION = "exception";
  private static final String EXECUTION = "execution";
  private static final String RESULT_CODE = "result-code";

  private static final String RUNTIME = "runtime";

  private static final String STD_ERR = "std-err";

  private static final String STD_OUT = "std-out";

  static final Integer FAIL = -1;

  private final Checksum checksum;

  private final Optional<Throwable> exception;
  private final JSONObject json;
  private final ProcessExecution processExecution;

  private final Optional<Integer> resultCode;

  private final Duration runtime;

  private final Instant start;

  private final List<String> stdOuts, stdError;

  public ProcessExecutionResult(final ProcessExecution pe, final Optional<Integer> exitCode,
      final Optional<Throwable> exception, final Instant startTime, final Duration between) {
    processExecution = Objects.requireNonNull(pe);
    stdOuts = processExecution.getStdOut().getList();
    stdError = processExecution.getStdErr().getList();
    processExecution.close();
    resultCode = Objects.requireNonNull(exitCode);
    this.exception = Objects.requireNonNull(exception);
    start = Objects.requireNonNull(startTime);
    runtime = Objects.requireNonNull(between);
    checksum = ChecksumBuilder.newInstance(Objects.requireNonNull(pe.getRelativeRoot()))
        .addListString(processExecution.getStdOut().getList()).addListString(processExecution.getStdErr().getList())
        .addInteger(resultCode).addThrowable(this.exception).addInstant(start).addDuration(runtime)
        .addChecksumEnabled(processExecution).asChecksum();
    json = JSONBuilder.newInstance().addListString(STD_OUT, processExecution.getStdOut().getList())
        .addListString(STD_ERR, processExecution.getStdErr().getList()).addInteger(RESULT_CODE, resultCode)
        .addThrowable(EXCEPTION, this.exception).addDuration(RUNTIME, runtime)
        .addJSONObject(EXECUTION, processExecution.asJSON()).asJSON();
  }

  @Override
  public Checksum asChecksum() {
    return checksum;
  }

  @Override
  public JSONObject asJSON() {
    return json;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final ProcessExecutionResult other = (ProcessExecutionResult) obj;
    return processExecution.getId().equals(other.getId());
  }

  public Instant getEndTime() {
    return getStartTime().plus(getRunningtime());
  }

  public Optional<Throwable> getException() {
    return exception;
  }

  public ProcessExecution getExecution() {
    return processExecution;
  }

  public Map<String, String> getExecutionEnvironment() {
    return getExecution().getExecutionEnvironment();
  }

  public String getId() {
    return getExecution().getId();
  }

  public Optional<Integer> getResultCode() {
    return resultCode;
  }

  public Duration getRunningtime() {
    return runtime;
  }

  public Instant getStartTime() {
    return start;
  }

  public List<String> getStdErr() {
    return stdError;
  }

  public List<String> getStdOut() {
    return stdOuts;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + processExecution.getId().hashCode();
    return result;
  }

  public boolean isError() {
    return defaultValidator.apply(this);
  }

  public boolean isTimedOut() {
    return exception.map(e -> e instanceof TimeoutException).orElse(false);
  }
}
