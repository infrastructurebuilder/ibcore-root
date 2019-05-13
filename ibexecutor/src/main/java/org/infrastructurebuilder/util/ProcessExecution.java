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

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.JSONAndChecksumEnabled;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONObject;
import org.zeroturnaround.exec.ProcessExecutor;

public final class ProcessExecution implements JSONAndChecksumEnabled, AutoCloseable {
  public final static Duration VERY_LONG = Duration.ofHours(2 * 24 * 365 + 12);

  private static final String ARGUMENTS = "arguments";
  private final static int[] DEFAULT_EXIT = { 0 };
  private static final String ENVIRONMENT = "environment";
  private static final String EXECUTABLE = "executable";
  private static final String ID = "id";
  private static final String OPTIONAL = "optional";

  private static final String TIMEOUT = "duration";

  private final List<String> arguments;

  private final boolean background;

  private final Optional<Map<String, String>> environment;

  private final String executable;
  private final Map<String, String> executionEnvironment = new HashMap<>();
  private final String executionString;
  private final AtomicReference<ProcessExecutor> executor = new AtomicReference<>();

  private final int[] exitValues;

  private final String id;

  private final boolean optional;

  private final Optional<Path> relativeRoot;

  private final ListCapturingLogOutputStream stdErr;

  private final Optional<Path> stdIn;

  private final ListCapturingLogOutputStream stdOut;

  private final Optional<Duration> timeout;

  private final Optional<Path> workDirectory;

  public ProcessExecution(final String id, final String executable, final List<String> arguments,
      final Optional<Duration> timeout, final Path stdOut, final Path stdErr, final Optional<Path> stdIn,
      final Optional<Path> workDirectory, final boolean optional, final Optional<Map<String, String>> environment,
      final Optional<Path> relativeRoot, final Optional<List<Integer>> exitValues, final Optional<PrintStream> addl,
      final boolean background) {
    this.id = Objects.requireNonNull(id, "process execution id");
    this.executable = Objects.requireNonNull(executable);
    this.arguments = Objects.requireNonNull(arguments);
    this.timeout = Objects.requireNonNull(timeout);
    this.timeout.filter(t -> t.isNegative()).ifPresent(n -> {
      throw new ProcessException("Negative durations a invalid " + n);
    });
    this.optional = optional;
    this.stdOut = new ListCapturingLogOutputStream(Optional.of(stdOut), addl);
    this.stdErr = new ListCapturingLogOutputStream(Optional.of(stdErr), addl);
    this.stdIn = Objects.requireNonNull(stdIn);
    executionString = String.join(" ", Objects.requireNonNull(arguments));
    this.environment = Objects.requireNonNull(environment);
    this.workDirectory = Objects.requireNonNull(workDirectory);
    this.relativeRoot = Objects.requireNonNull(relativeRoot);
    this.background = background;
    this.exitValues = exitValues.map(l -> l.stream().mapToInt(ii -> ii).toArray()).orElse(DEFAULT_EXIT);

  }

  @Override
  public Checksum asChecksum() {

    return ChecksumBuilder.newInstance(relativeRoot)

        .addString(id)

        .addString(executable)

        .addListString(arguments)

        .addDuration(timeout)

        .addBoolean(optional)

        .addPath(stdOut.getPath().get())

        .addPath(stdErr.getPath().get()).addMapStringString(environment).asChecksum();
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance().addString(ID, id).addString(EXECUTABLE, executable)
        .addListString(ARGUMENTS, arguments).addDuration(TIMEOUT, timeout).addBoolean(OPTIONAL, optional)
        .addMapStringString(ENVIRONMENT, environment).asJSON();
  }

  @Override
  public void close() {
    ProcessException.pet.withTranslation(() -> stdOut.close());
    ProcessException.pet.withTranslation(() -> stdErr.close());
  }

  public List<String> getArguments() {
    return arguments;
  }

  public String getExecutable() {
    return executable;
  }

  public Map<String, String> getExecutionEnvironment() {
    return executionEnvironment;
  }

  public String getExecutionString() {
    return executionString;
  }

  public String getId() {
    return id;
  }

  public ProcessExecutor getProcessExecutor() {
    synchronized (executor) {
      if (executor.get() == null) {
        ProcessExecutor pe = new ProcessExecutor()

            .environment(environment.orElse(new HashMap<>()))

            .directory(workDirectory.map(Path::toFile).orElse((File) null))

            .redirectError(stdErr)

            .redirectOutput(stdOut)

            .exitValues(exitValues)

            .command(getCommand())

        ;

        if (timeout.isPresent()) {
          final Duration d = timeout.get();
          pe = pe.timeout(d.get(ChronoUnit.SECONDS) * 1000 + d.get(ChronoUnit.NANOS), TimeUnit.NANOSECONDS);
        }
        final ProcessExecutor pe2 = pe;
        if (stdIn.isPresent()) {
          pe = ProcessException.pet.withReturningTranslation(() -> {
            return pe2.redirectInput(Files.newInputStream(stdIn.get()));
          });
        }
        executor.compareAndSet(null, pe2);
      }
    }
    return executor.get();
  }

  public Optional<Path> getRelativeRoot() {
    return relativeRoot;
  }

  public ListCapturingLogOutputStream getStdErr() {
    return stdErr;
  }

  public Optional<Path> getStdIn() {
    return stdIn;
  }

  public ListCapturingLogOutputStream getStdOut() {
    return stdOut;
  }

  public Optional<Duration> getTimeout() {
    return timeout;
  }

  public boolean isBackground() {
    return background;
  }

  public boolean isOptional() {
    return optional;
  }

  private List<String> getCommand() {
    final ArrayList<String> l = new ArrayList<>();
    l.add(getExecutable());
    l.addAll(getArguments());
    return l;
  }

}
