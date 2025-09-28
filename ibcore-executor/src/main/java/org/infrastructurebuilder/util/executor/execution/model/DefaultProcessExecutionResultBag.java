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

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.infrastructurebuilder.util.executor.api.AbstractProcessExecutionResultBag;
import org.infrastructurebuilder.util.executor.api.ProcessExecutionResult;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

public class DefaultProcessExecutionResultBag extends AbstractProcessExecutionResultBag<ProcessExecutor, ProcessResult> {

  final static DefaultProcessExecutionResultBag from(MutableProcessExecutionResultBag r) {
    Map<String, ProcessExecutionResult<ProcessExecutor>> a = r.getExecutionResults();
    List<String> b = r.getExecutedIds();
    Map<String, Future<ProcessResult>> c = r.getRunningFutures();
    return new DefaultProcessExecutionResultBag(a, b, c);

  }

//  DefaultProcessExecutionResultBag(final MutableProcessExecutionResultBag r) {
//    super(r.getExecutionResults()
//
//        , r.getExecutedIds()
//
//        , r.getRunningFutures());
//
//    Map<String, ProcessExecutionResult<ProcessExecutor>> r1 = r.getExecutionResults();
//  }

  public DefaultProcessExecutionResultBag(Map<String, ProcessExecutionResult<ProcessExecutor>> a

      , List<String> b

      , Map<String, Future<ProcessResult>> c) {
    super(a, b, c);
  }

}
