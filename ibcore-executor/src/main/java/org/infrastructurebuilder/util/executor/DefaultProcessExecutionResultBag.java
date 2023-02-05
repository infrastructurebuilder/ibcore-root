/*
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
package org.infrastructurebuilder.util.executor;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.zeroturnaround.exec.ProcessResult;

public class DefaultProcessExecutionResultBag implements ProcessExecutionResultBag {

  private final List<String>                        executedIds;
  private final Map<String, ProcessExecutionResult> executions;
  private final Map<String, Future<ProcessResult>>  futures;

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
  public Map<String, ProcessExecutionResult> getExecutions() {
    return executions;
  }


  @Override
  public Map<String, Future<ProcessResult>> getRunningFutures() {
    return futures;
  }


}
