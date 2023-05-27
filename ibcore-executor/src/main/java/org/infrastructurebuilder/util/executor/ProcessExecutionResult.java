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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.infrastructurebuilder.util.core.JSONAndChecksumEnabled;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.json.JSONObject;

public interface ProcessExecutionResult extends JSONAndChecksumEnabled {

  Function<ProcessExecutionResult, Boolean> defaultValidator = (r) -> {
    return r.getException().isPresent() || r.getResultCode().orElse(ProcessExecutionResult.FAIL) != 0;

  };

  String  EXCEPTION   = "exception";
  String  EXECUTION   = "execution";
  String  RESULT_CODE = "result-code";
  String  START       = "start";
  String  RUNTIME     = "runtime";
  String  STD_ERR     = "std-err";
  String  STD_OUT     = "std-out";
  Integer FAIL        = -1;

  String getId();

  Instant getStartTime();

  Optional<Throwable> getException();

  Optional<ProcessExecution> getExecution();

  Map<String, String> getExecutionEnvironment();

  Optional<Integer> getResultCode();

  Duration getRunningtime();

  List<String> getStdErr();

  List<String> getStdOut();

  default Instant getEndTime() {
    return getStartTime().plus(getRunningtime());
  }

  @Override
  default JSONObject asJSON() {
    JSONBuilder jb = JSONBuilder.newInstance()

        .addListString(STD_OUT, getStdOut())

        .addListString(STD_ERR, getStdErr())

        .addInteger(RESULT_CODE, getResultCode())

        .addThrowable(EXCEPTION, getException())

        .addInstant(START, getStartTime())

        .addDuration(RUNTIME, getRunningtime());

    getExecution().ifPresent(e -> jb.addJSONObject(EXECUTION, e.asJSON()));

    return jb.asJSON();
  }

  default boolean isError() {
    return defaultValidator.apply(this);
  }

  default boolean isTimedOut() {
    return getException().map(e -> e instanceof TimeoutException).orElse(false);
  }

}