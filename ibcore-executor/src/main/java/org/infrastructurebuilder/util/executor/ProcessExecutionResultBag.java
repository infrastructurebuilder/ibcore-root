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
package org.infrastructurebuilder.util.executor;

import static java.time.Duration.between;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

import org.infrastructurebuilder.util.core.JSONBuilder;
import org.infrastructurebuilder.util.core.JSONOutputEnabled;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zeroturnaround.exec.ProcessResult;

public interface ProcessExecutionResultBag extends JSONOutputEnabled {
  public static final String EXECUTION_IDS         = "executed-ids";
  @Deprecated
  public static final String INCOMPLETE_FUTURE_IDS = "incomplete-futures-ids";
  public static final String RESULTS               = "results";

  List<String> getExecutedIds();

  Map<String, ProcessExecutionResult> getExecutions();

  /**
   * This will be removed before 1.0.0
   * @return
   */
  @Deprecated
  Map<String, Future<ProcessResult>> getRunningFutures();

  default Set<String> getIncompleteFuturesIds() {
    return getRunningFutures().keySet();
  }

  default Optional<Duration> getDuration() {
    return getStart().map(start -> between(start, getEnd().orElseThrow(() -> new ProcessException("No end time"))));
  }

  default Optional<Instant> getEnd() {
    return getExecutions().values().stream().map(ProcessExecutionResult::getEndTime).max(Instant::compareTo);
  }

  default List<String> getErrors() {
    return getExecutions().values().stream()

        .filter(ProcessExecutionResult::isError)

        .map(ProcessExecutionResult::getId)

        .collect(toList());

  }

  default Optional<ProcessExecutionResult> getExecution(final String id) {
    return ofNullable(getExecutions().get(requireNonNull(id)));
  }

  default Map<String, Map<String, String>> getExecutionEnvironment() {
    return getExecutions().entrySet().stream()
        .collect(toMap(k -> k.getKey(), v -> v.getValue().getExecutionEnvironment()));
  }

  default Map<String, ProcessExecutionResult> getResults() {
    return getExecutions().values().stream().collect(toMap(ProcessExecutionResult::getId, identity()));
  }

  default Optional<Instant> getStart() {
    return getExecutions().values().stream().map(ProcessExecutionResult::getStartTime).min(Instant::compareTo);
  }

  default List<String> getStdErr() {
    final Map<String, List<String>> l = getStdErrs();
    return getExecutedIds().stream().map(l::get).flatMap(List::stream).collect(toList());
  }

  default Map<String, List<String>> getStdErrs() {
    return getExecutions().values().stream()
        .collect(toMap(ProcessExecutionResult::getId, ProcessExecutionResult::getStdErr));

  }

  default List<String> getStdOut() {
    return getExecutedIds().stream().map(key -> getStdOuts().get(key)).flatMap(List::stream).collect(toList());
  }

  default Map<String, List<String>> getStdOuts() {
    return getExecutions().values().stream()
        .collect(toMap(ProcessExecutionResult::getId, ProcessExecutionResult::getStdOut));
  }

  @Override
  default JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addJSONArray(EXECUTION_IDS, new JSONArray(getExecutedIds()))

        .addJSONArray(INCOMPLETE_FUTURE_IDS, new JSONArray(getIncompleteFuturesIds()))

        .addJSONArray(RESULTS, new JSONArray(getExecutions().values().stream().map(v -> v.asJSON()).collect(toList())))

        .asJSON();
  }

}