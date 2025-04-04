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

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.pathref.ChecksumBuilder;
import org.infrastructurebuilder.pathref.ChecksumBuilderFactory;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.util.executor.ProcessExecution;
import org.infrastructurebuilder.util.executor.ProcessExecutionResult;
import org.infrastructurebuilder.util.executor.model.utils.IBCoreExecutorModelUtils;
import org.infrastructurebuilder.util.executor.model.v1_0.Environment;
import org.infrastructurebuilder.util.executor.model.v1_0.GeneratedProcessExecutionResult;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DefaultProcessExecutionResult implements ProcessExecutionResult {
  private final static Logger log = LoggerFactory.getLogger(DefaultProcessExecutionResult.class);
  private final GeneratedProcessExecutionResult gper;
  private final ProcessExecution processExecution;
  private final PathRefFileSystem pr;

  @SuppressWarnings("unchecked")
  public DefaultProcessExecutionResult(ProcessExecution pe, Optional<Integer> exitCode, Optional<Throwable> exception,
      Instant startTime, Duration between)
  {

    this.processExecution = requireNonNull(pe);
    this.pr = pe.getRoot();
    this.gper = GeneratedProcessExecutionResult.builder() //
        .withEnvironment(new Environment()) //
        .withStart(startTime) //
        .withRunTime(between.toString()) //
        .withExecutionException(toNullableExecutionException.apply(exception)) //
        .withResultCode((exitCode.orElse(0)).toString()) //
        .withStdOut(pe.getStdOut().get()) //
        .withStdErr(pe.getStdErr().get()) //
        .withStdInPath(pe.getStdIn().map(Path::toString).orElse(null)) // )

        .withRoot(this.pr.toString())

        .build();
    this.gper.setId(asChecksum().toString());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof DefaultProcessExecutionResult dper)
      return (dper.getId().equals(getId()));
    return false;
  }

  @Override
  public String getId() {
    return this.gper.getId();
  }

  @Override
  public Instant getStartTime() {
    return this.gper.getStart();
  }

  @Override
  public Optional<JSONObject> getException() {
    var o = IBCoreExecutorModelUtils.getObjectMapper();
    return this.gper.getExecutionException().map(e -> {
      try {
        return o.writeValueAsString(e);
      } catch (JsonProcessingException e1) {
        log.error("Error processinging getException()", e1);
        return null;
      }
    }).map(JSONObject::new);
  }

  @Override
  public Optional<ProcessExecution> getExecution() {
    return Optional.ofNullable(this.processExecution);
  }

  @Override
  public Map<String, String> getExecutionEnvironment() {
    return DefaultProcessExecution.fromEnvironment.apply(this.gper.getEnvironment());
  }

  @Override
  public Optional<Integer> getResultCode() {
    return this.gper.getResultCode().map(Integer::valueOf);
  }

  @Override
  public Duration getRunningtime() {
    return Duration.parse(this.gper.getRunTime());
  }

  @Override
  public Optional<List<String>> getStdErr() {
    return this.gper.getStdErr();
  }

  @Override
  public Optional<List<String>> getStdOut() {
    return this.gper.getStdOut();
  }

  public Optional<ChecksumBuilder> getChecksumBuilder() {
    return Optional.of(ChecksumBuilderFactory.newInstance(getRoot().orElse(null)) //
        .addJSONObject(getException()) //
        .addMapStringString(getExecutionEnvironment()) //
        .addInteger(getResultCode()) //
        .addDuration(getRunningtime()) //
        .addInstant(getStartTime()) //
        .addListString(getStdErr()) //
        .addListString(getStdOut()) //
        .addChecksumEnabled(getExecution()) //
    );
  }

  public Optional<PathRefFileSystem> getRoot() {
    return Optional.ofNullable(pr);
  }

}
