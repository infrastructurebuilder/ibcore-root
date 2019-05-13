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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.infrastructurebuilder.util.artifacts.JSONOutputEnabled;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zeroturnaround.exec.ProcessResult;

public class ProcessExecutionResultBag implements JSONOutputEnabled {

  private static final String EXECUTION_IDS = "executed-ids";
  private static final String INCOMPLETE_FUTURE_IDS = "incomplete-futures-ids";
  private static final String RESULTS = "results";
  private final List<String> executedIds;
  private final Map<String, ProcessExecutionResult> executions;
  private final Map<String, Future<ProcessResult>> futures;
  private final JSONObject json;

  ProcessExecutionResultBag(final MutableProcessExecutionResultBag r) {
    executions = Collections.unmodifiableMap(r.getExecutionResults());
    executedIds = Collections.unmodifiableList(r.getExecutedIds());
    futures = Collections.unmodifiableMap(r.getRunningFutures());
    json = JSONBuilder.newInstance().addJSONArray(EXECUTION_IDS, new JSONArray(executedIds))
        .addJSONArray(INCOMPLETE_FUTURE_IDS, new JSONArray(futures.keySet())).addJSONArray(RESULTS,
            new JSONArray(executions.values().stream().map(v -> v.asJSON()).collect(Collectors.toList())))
        .asJSON();
  }

  @Override
  public JSONObject asJSON() {
    return json;
  }

  public Optional<Duration> getDuration() {
    return getStart().map(start -> Duration.between(start,
        getEnd().orElseThrow(() -> new ProcessException("No end time availabe for some weird reason"))));

  }

  public Optional<Instant> getEnd() {
    return executions.values().stream().map(ProcessExecutionResult::getEndTime).max(Instant::compareTo);
  }

  public List<String> getErrors() {
    return getExecutions().values().stream()

        .filter(ProcessExecutionResult::isError)

        .map(ProcessExecutionResult::getId)

        .collect(Collectors.toList());

  }

  public List<String> getExecutedIds() {
    return executedIds;
  }

  public Optional<ProcessExecutionResult> getExecution(final String id) {
    return Optional.ofNullable(executions.get(Objects.requireNonNull(id)));
  }

  public Map<String, Map<String, String>> getExecutionEnvironment() {
    return getExecutions().entrySet().stream()
        .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().getExecutionEnvironment()));
  }

  public Map<String, ProcessExecutionResult> getExecutions() {
    return executions;
  }

  public Map<String, ProcessExecutionResult> getResults() {
    return getExecutions().values().stream()
        .collect(Collectors.toMap(ProcessExecutionResult::getId, Function.identity()));
  }

  public Map<String, Future<ProcessResult>> getRunningFutures() {
    return futures;
  }

  public Optional<Instant> getStart() {
    return executions.values().stream().map(ProcessExecutionResult::getStartTime).min(Instant::compareTo);
  }

  public List<String> getStdErr() {
    final Map<String, List<String>> l = getStdErrs();
    return getExecutedIds().stream().map(l::get).flatMap(List::stream).collect(Collectors.toList());
  }

  public Map<String, List<String>> getStdErrs() {
    return getExecutions().values().stream()
        .collect(Collectors.toMap(ProcessExecutionResult::getId, ProcessExecutionResult::getStdErr));

  }

  public List<String> getStdOut() {
    return getExecutedIds().stream().map(key -> getStdOuts().get(key)).flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public Map<String, List<String>> getStdOuts() {
    return getExecutions().values().stream()
        .collect(Collectors.toMap(ProcessExecutionResult::getId, ProcessExecutionResult::getStdOut));
  }

}
