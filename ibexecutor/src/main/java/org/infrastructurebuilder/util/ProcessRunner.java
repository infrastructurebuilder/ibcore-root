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

import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.slf4j.Logger;

public interface ProcessRunner extends Supplier<Optional<ProcessExecutionResultBag>>, AutoCloseable {

  String STD_ERR_FILENAME = "stdErr";
  String STD_OUT_FILENAME = "stdOut";

  ProcessRunner add(ProcessExecution e);

  ProcessRunner addExecution(String id, String executable, List<String> arguments, Optional<Duration> timeout,
      Optional<Path> stdIn, Optional<Path> workDirectory, Optional<Checksum> checksum, boolean optional,
      Optional<Map<String, String>> environment, Optional<Path> relativeRoot, Optional<List<Integer>> exitCodes,
      boolean background);

  Optional<PrintStream> getAddl();

  Logger getLogger();

  Optional<ProcessExecution> getProcessExecutionForId(String id);

  boolean hasErrorResult(Map<String, ProcessExecutionResult> resultMap);

  boolean isKeepScratchDir();

  ProcessRunner lock();

  ProcessRunner lock(Duration fin, Optional<Long> sleepAfterDestroy);

  ProcessRunner setKeepScratchDir(boolean keepScratchDir);

}