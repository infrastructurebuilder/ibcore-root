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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.util.core.ChecksumBuilder;
import org.infrastructurebuilder.util.executor.ProcessExecution;
import org.infrastructurebuilder.util.executor.ProcessExecutionResult;
import org.infrastructurebuilder.util.executor.execution.model.v1_0_0.GeneratedProcessExecutionResult;

public class DefaultProcessExecutionResult implements ProcessExecutionResult {

  private final GeneratedProcessExecutionResult gper;

  private final ChecksumBuilder builder = ChecksumBuilder.newInstance();

  public DefaultProcessExecutionResult(GeneratedProcessExecutionResult gper) {
    this.gper = Objects.requireNonNull(gper);
  }

  public DefaultProcessExecutionResult(ProcessExecution pe, Optional<Integer> exitCode, Optional<Throwable> exception,
      Instant startTime, Duration between)
  {
    this(new GeneratedProcessExecutionResult(pe, exitCode, exception, startTime, between));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof DefaultProcessExecutionResult)
      return ((DefaultProcessExecutionResult) obj).getId().equals(getId());
    return false;
  }

  @Override
  public String getId() {
    return this.gper.getId();
  }

  @Override
  public Instant getStartTime() {
    return this.gper.getStartTime();
  }

  @Override
  public Optional<Throwable> getException() {
    return this.gper.getOptionalException();
  }

  @Override
  public Optional<ProcessExecution> getExecution() {
    return this.gper.getExecution();
  }

  @Override
  public Map<String, String> getExecutionEnvironment() {
    return this.gper.getExecutionEnvironment();
  }

  @Override
  public Optional<Integer> getResultCode() {
    return this.gper.getOptionalResultCode();
  }

  @Override
  public Duration getRunningtime() {
    return this.gper.getRunningtime();
  }

  @Override
  public List<String> getStdErr() {
    return this.gper.getStdErr();
  }

  @Override
  public List<String> getStdOut() {
    return this.gper.getStdOut();
  }

  public ChecksumBuilder getChecksumBuilder() {
    return this.builder;
  }

}
