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
import java.util.Optional;

import org.infrastructurebuilder.util.executor.api.AbstractProcessExecutionResult;
import org.infrastructurebuilder.util.executor.api.ProcessExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;

public class DefaultProcessExecutionResult extends AbstractProcessExecutionResult<ProcessExecutor> {
  private final static Logger log = LoggerFactory.getLogger(DefaultProcessExecutionResult.class);

  @SuppressWarnings("unchecked")
  public DefaultProcessExecutionResult(ProcessExecution<ProcessExecutor> pe, Optional<Integer> exitCode,
      Optional<Throwable> exception, Instant startTime, Duration between)
  {
    super(pe, exitCode, exception, startTime, between);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof DefaultProcessExecutionResult dper)
      return (dper.getId().equals(getId()));
    return false;
  }


}
