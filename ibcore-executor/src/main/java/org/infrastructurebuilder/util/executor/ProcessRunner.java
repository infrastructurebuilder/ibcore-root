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

import java.io.PrintStream;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.slf4j.Logger;


public interface ProcessRunner extends Supplier<Optional<ProcessExecutionResultBag>>, AutoCloseable {
  public final static Pattern ws = Pattern.compile("\\s");

  public final static String STD_ERR_FILENAME = "stdErr";
  public final static String STD_OUT_FILENAME = "stdOut";

  ProcessRunner add(Supplier<ProcessExecution> e);

  Optional<PrintStream> getAddl();

  Logger getLogger();

  Optional<ProcessExecution> getProcessExecutionForId(String id);

  boolean hasErrorResult(Map<String, ProcessExecutionResult> resultMap);

  boolean isKeepScratchDir();

  ProcessRunner lock();

  ProcessRunner lock(Duration fin, Optional<Long> sleepAfterDestroy);

  ProcessRunner setKeepScratchDir(boolean keepScratchDir);

}