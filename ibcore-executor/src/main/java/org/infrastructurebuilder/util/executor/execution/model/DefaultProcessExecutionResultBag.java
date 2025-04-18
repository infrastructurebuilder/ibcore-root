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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.infrastructurebuilder.util.executor.api.ProcessExecutionResult;
import org.infrastructurebuilder.util.executor.api.ProcessExecutionResultBag;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

public class DefaultProcessExecutionResultBag implements ProcessExecutionResultBag<ProcessExecutor> {

  private final List<String> executedIds;
  private final Map<String, ProcessExecutionResult<ProcessExecutor>> executions;
  private final Map<String, Future<ProcessResult>> futures;

  DefaultProcessExecutionResultBag(final MutableProcessExecutionResultBag r) {
    executions = unmodifiableMap(r.getExecutionResults());
    executedIds = unmodifiableList(r.getExecutedIds());
    futures = unmodifiableMap(r.getRunningFutures());
  }

  @Override
  public List<String> getExecutedIds() {
    return executedIds;
  }

  @Override
  public Map<String, ProcessExecutionResult<ProcessExecutor>> getExecutions() {
    return executions;
  }

  @Override
  public Set<String> getIncompleteFuturesIds() {
    return unmodifiableSet(this.futures.keySet());
  }

}
