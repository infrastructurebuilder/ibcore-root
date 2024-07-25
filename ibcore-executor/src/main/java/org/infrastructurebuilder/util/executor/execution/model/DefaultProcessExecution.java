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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;

import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.infrastructurebuilder.pathref.AbsolutePathRef;
import org.infrastructurebuilder.pathref.ChecksumBuilder;
import org.infrastructurebuilder.pathref.ChecksumBuilderFactory;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.util.executor.ListCapturingLogOutputStream;
import org.infrastructurebuilder.util.executor.ModeledProcessExecution;
import org.infrastructurebuilder.util.executor.ProcessException;
import org.infrastructurebuilder.util.executor.ProcessExecution;
import org.infrastructurebuilder.util.executor.model.executor.model.v1_0.EnvEntry;
import org.infrastructurebuilder.util.executor.model.executor.model.v1_0.Environment;
import org.infrastructurebuilder.util.executor.model.executor.model.v1_0.GeneratedProcessExecution;
import org.zeroturnaround.exec.ProcessExecutor;

public class DefaultProcessExecution implements ProcessExecution {

  public final static Function<Map<String, String>, Environment> toEnvironment = (m) -> {
    return new Environment(
        requireNonNull(m).entrySet().stream().map(e -> new EnvEntry(e.getKey(), e.getValue())).toList());
  };
  public final static Function<Environment, Map<String, String>> fromEnvironment = (e) -> {
    return requireNonNull(e).getEnvEntry()
        .map(l -> l.stream().collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue())))
        .orElseGet(Collections::emptyMap);
  };
  private final ModeledProcessExecution gpe;
  private final PrintStream addl;
  private ProcessExecutor executor;
  private ListCapturingLogOutputStream stdErr;// = new ListCapturingLogOutputStream(empty(), empty());
  private ListCapturingLogOutputStream stdOut;// = new ListCapturingLogOutputStream(empty(), empty());
  private final ChecksumBuilder builder = ChecksumBuilderFactory.newInstance();

  public DefaultProcessExecution() {
    this.addl = null;
    this.gpe = null;
  }

  public DefaultProcessExecution(GeneratedProcessExecution e) {
    this.gpe = new ModeledProcessExecution(requireNonNull(e));
    this.addl = null;
  }

  private DefaultProcessExecution(DefaultProcessExecution e) {
    this.gpe = new ModeledProcessExecution(e.gpe);
    this.addl = e.addl;
    this.executor = e.executor;
    this.stdErr = e.stdErr;
    this.stdOut = e.stdOut;
  }

  public DefaultProcessExecution(final String id, final String executable, final List<String> arguments,
      final Optional<java.time.Duration> timeout, final Optional<Path> stdIn, final Path workDirectory,
      final boolean optional, final Optional<Map<String, String>> environment, final Optional<Path> relativeRoot,
      final Optional<List<Integer>> exitValues, final Optional<java.io.PrintStream> addl, final boolean background)
  {
    this.gpe = new ModeledProcessExecution("1.0", id, executable, arguments,
        timeout.map(Duration::toString).orElse(null), //
        optional, background, workDirectory.toString(),
        exitValues.map(x -> x.stream().map(i -> i.toString()).toList()).orElse(null),

        null, // Placeholders until we set the values below
        null, //
        null, //
        relativeRoot.map(AbsolutePathRef::new).flatMap(PathRef::getUrl).map(URL::toExternalForm).orElse(null),
        requireNonNull(environment).map(DefaultProcessExecution.toEnvironment::apply).map(Environment::new)
            .orElseGet(() -> new Environment()));
    this.gpe.setStdOutPath(getStdOut().getPath().map(Path::toString).orElse(null));
    this.gpe.setStdErrPath(getStdErr().getPath().map(Path::toString).orElse(null));
    this.gpe.setStdInPath(stdIn.map(Path::toString).orElse(null));
    this.addl = requireNonNull(addl).orElse(null);
    gpe.setBackground(background);
  }

  DefaultProcessExecution(final String id, final String executable, final List<String> arguments,
      final Optional<Duration> timeout, final Optional<Path> stdIn, final Path workDirectory, final boolean optional,
      final Optional<Map<String, String>> environment, final Optional<Path> relativeRoot,
      final Optional<List<Integer>> exitValues, final Optional<java.io.PrintStream> addl, final boolean background,
      final org.infrastructurebuilder.util.executor.ListCapturingLogOutputStream stdout,
      final org.infrastructurebuilder.util.executor.ListCapturingLogOutputStream stderr)
  {
    this(id, executable, arguments, timeout, stdIn, workDirectory, optional, environment, relativeRoot, exitValues,
        addl, background);
    this.stdOut = stdout;
    this.stdErr = stderr;
  }

  @Override
  public ProcessExecutor getProcessExecutor() {
    if (this.executor == null)
      this.executor = ProcessExecution.super.getProcessExecutor();
    return this.executor;
  }

  @Override
  public void close() {
    ofNullable(gpe).ifPresent(e -> {
      try {
        getStdOut().close();
      } catch (Exception e1) {
      }
      try {
        getStdErr().close();
      } catch (Exception e1) {
      }
    });
  }

  @Override
  public Optional<List<String>> getArguments() {
    return gpe.getArguments();
  }

  @Override
  public String getExecutable() {
    return gpe.getExecutable();
  }

  @Override
  public String getId() {
    return gpe.getId();
  }

  @Override
  public Optional<Path> getStdIn() {
    return gpe.getStdInPath().map(Paths::get);
  }

  @Override
  public Optional<Duration> getTimeout() {
    return gpe.getTimeout().map(Duration::parse);
  }

  @Override
  public boolean isBackground() {
    return gpe.getBackground().orElse(false);
  }

  @Override
  public boolean isOptional() {
    return gpe.getOptional().orElse(false);
  }

  @Override
  public Optional<PrintStream> getAdditionalPrintStream() {
    return ofNullable(this.addl);
  }

  @Override
  public Map<String, String> getExecutionEnvironment() {
    return gpe.getEnvironment().map(DefaultProcessExecution.fromEnvironment).orElseGet(Collections::emptyMap);
  }

  @Override
  public Path getWorkDirectory() {
    return gpe.getWorkDirectory().map(Paths::get).orElseThrow(() -> new ProcessException("No work directory"));
  }

  @Override
  public Optional<List<Integer>> getExitValuesAsIntegers() {
    return gpe.getExitValues().map(ev -> ev.stream().map(Integer::parseInt).toList());
  }

  public Optional<PathRef> getRelativeRoot() {
    return gpe.getRelativeRoot();
  }

  @Override
  public ListCapturingLogOutputStream getStdErr() {
    if (this.stdErr == null)
      this.stdErr = new ListCapturingLogOutputStream(getWorkDirectory().resolve(randomUUID().toString() + ".stderr"),
          getAdditionalPrintStream());
    return this.stdErr;
  }

  @Override
  public ListCapturingLogOutputStream getStdOut() {
    if (this.stdOut == null)
      this.stdOut = new ListCapturingLogOutputStream(getWorkDirectory().resolve(randomUUID().toString() + ".stdout"),
          getAdditionalPrintStream());
    return this.stdOut;
  }

  @Override
  protected DefaultProcessExecution clone() throws CloneNotSupportedException {
    return new DefaultProcessExecution(this);
  }

  public ChecksumBuilder getChecksumBuilder() {
    return this.builder;
  }

}
