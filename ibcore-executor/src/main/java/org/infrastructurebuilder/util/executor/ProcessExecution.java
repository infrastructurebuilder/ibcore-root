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

import static java.time.Duration.ofHours;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.infrastructurebuilder.pathref.JSONAndChecksumEnabled;
import org.infrastructurebuilder.pathref.JSONBuilderBaseFactory;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;

public interface ProcessExecution extends JSONAndChecksumEnabled, AutoCloseable {
  static final Logger log = LoggerFactory.getLogger(ProcessExecution.class);

  public static final String ARGUMENTS = "arguments";
  public static final List<Integer> DEFAULT_EXIT = Arrays.asList(0);
  public static final String ENVIRONMENT = "environment";
  public static final String EXECUTABLE = "executable";
  public static final String ID = "id";
  public static final String OPTIONAL = "optional";
  public static final String TIMEOUT = "duration";
  public static final String STD_ERR = "stderr";
  public static final String STD_OUT = "stdout";
  public final static Duration VERY_LONG = ofHours(2 * 24 * 365 + 12);

  String getId();

  String getExecutable();

  List<String> getArguments();

  PathRefFileSystem getRoot();

  Map<String, String> getExecutionEnvironment();

  Optional<Path> getStdIn();

  Optional<Duration> getTimeout();

  boolean isBackground();

  boolean isOptional();

  Optional<PrintStream> getAdditionalPrintStream();

  ListCapturingLogOutputStream getStdOut();

  ListCapturingLogOutputStream getStdErr();

  @Override
  default JSONObject asJSON() {
    return JSONBuilderBaseFactory.newInstanceFromRelativeRoot(this.getRoot())

        .addString(ID, getId())

        .addString(EXECUTABLE, getExecutable())

        .addListString(ARGUMENTS, getArguments())

        .addDuration(TIMEOUT, getTimeout())

        .addBoolean(OPTIONAL, isOptional())

        .addPath(STD_OUT, getStdOut().getPath())

        .addPath(STD_ERR, getStdErr().getPath())

        .addMapStringString(ENVIRONMENT, getExecutionEnvironment())

        .asJSON();
  }

  Path getWorkDirectory();

  Optional<List<Integer>> getExitValuesAsIntegers();

  /**
   * One should override this and cache the value, if possible, so as to return the same executor
   *
   * @return
   */
  default ProcessExecutor getProcessExecutor() {
    final List<String> command = new ArrayList<>();
    command.add(getExecutable());
    command.addAll(getArguments());
    List<Integer> l = getExitValuesAsIntegers().orElseGet(() -> new ArrayList<>());
    Integer[] exitValues = (Integer[]) l.toArray(new Integer[l.size()]);
    File w = getWorkDirectory().toFile();
    log.debug("Working directory for {} is {}", getId(), w);
    final ProcessExecutor pe = new ProcessExecutor()

        .environment(getExecutionEnvironment())

        .directory(w)

        .redirectError(getStdErr())

        .redirectOutput(getStdOut())

        .redirectInput(getStdIn().map(si -> ProcessException.pet.returns(() -> Files.newInputStream(si)))
            .orElse(System.in))

        .exitValues(exitValues)

        .command(command)

    ;
    if (getTimeout().isPresent()) {
      final Duration d = getTimeout().get();
      if (d.isNegative())
        throw new ProcessException("Negative timeouts are disallowed " + d);
      return pe.timeout(d.get(ChronoUnit.SECONDS) * 1000 + d.get(ChronoUnit.NANOS), TimeUnit.NANOSECONDS);
    } else
      return pe;
  }

}
