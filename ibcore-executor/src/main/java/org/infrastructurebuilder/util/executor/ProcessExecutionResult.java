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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.infrastructurebuilder.pathref.ChecksumBuilderEnabled;
import org.infrastructurebuilder.pathref.JSONAndChecksumEnabled;
import org.infrastructurebuilder.pathref.JSONBuilder;
import org.infrastructurebuilder.pathref.JSONBuilderBaseFactory;
import org.infrastructurebuilder.util.executor.model.v1_0.ExecutionException;
import org.infrastructurebuilder.util.executor.model.v1_0.Stack;
import org.json.JSONObject;

public interface ProcessExecutionResult extends JSONAndChecksumEnabled, ChecksumBuilderEnabled {

  public final Function<ProcessExecutionResult, Boolean> defaultValidator = (r) -> {
    return r.getException().isPresent() || r.getResultCode().orElse(ProcessExecutionResult.FAIL) != 0;

  };

  public final static Function<StackTraceElement[], List<Stack>> toNullableStack = (elements) -> {
    if (elements == null)
      return null;
    List<Stack> l = new ArrayList<>();
    for (StackTraceElement e : elements) {
      var b = Stack.builder().withDeclaringClass(e.getClassName()) //
          .withFileName(e.getFileName()) //
          .withLineNumber((long) e.getLineNumber()) //
          .withMethodName(e.getMethodName()) //
          .withClassLoaderName(e.getClassLoaderName()) //
          .withModuleName(e.getModuleName()) //
          .withModuleVersion(e.getModuleVersion()) //
          .build();
      l.add(b);
    }
    return l;
  };

  public final static Function<Optional<Throwable>, ExecutionException> toNullableExecutionException = (o) -> {
    return o.map(e -> ExecutionException.builder() //
        .withMessage(e.getMessage()) //
        .withKlass(e.getClass().getCanonicalName()) //
        .withStack(toNullableStack.apply(e.getStackTrace())) //
        .build()) //
        .orElse(null);

  };


  String EXCEPTION = "exception";
  String EXECUTION = "execution";
  String RESULT_CODE = "result-code";
  String START = "start";
  String RUNTIME = "runtime";
  String STD_ERR = "std-err";
  String STD_OUT = "std-out";
  Integer FAIL = -1;

  String getId();

  Instant getStartTime();

  Optional<JSONObject> getException();

  Optional<ProcessExecution> getExecution();

  Map<String, String> getExecutionEnvironment();

  Optional<Integer> getResultCode();

  Duration getRunningtime();

  Optional<List<String>> getStdErr();

  Optional<List<String>> getStdOut();

  default Instant getEndTime() {
    return getStartTime().plus(getRunningtime());
  }

  @Override
  default JSONObject asJSON() {
    JSONBuilder jb = (JSONBuilder) JSONBuilderBaseFactory.newInstance()

        .addListString(STD_OUT, getStdOut())

        .addListString(STD_ERR, getStdErr())

        .addInteger(RESULT_CODE, getResultCode())

        .addJSONObject(EXCEPTION, getException())

        .addInstant(START, getStartTime())

        .addDuration(RUNTIME, getRunningtime());

    getExecution().ifPresent(e -> jb.addJSONObject(EXECUTION, e.asJSON()));

    return jb.asJSON();
  }

  default boolean isError() {
    return defaultValidator.apply(this);
  }

  default boolean isTimedOut() {
    return getException().map(e -> e.getString("klass").contains("java.util.concurrent.TimeoutException"))
        .orElse(false);
  }

}
