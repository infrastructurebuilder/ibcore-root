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
import static java.util.stream.Collectors.toList;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.util.core.ChecksumBuilder;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.executor.ListCapturingLogOutputStream;
import org.infrastructurebuilder.util.executor.ProcessException;
import org.infrastructurebuilder.util.executor.ProcessExecution;
import org.infrastructurebuilder.util.executor.execution.model.v2_0_0.GeneratedProcessExecution;
import org.zeroturnaround.exec.ProcessExecutor;

public class DefaultProcessExecution implements ProcessExecution {

  private final GeneratedProcessExecution gpe;
  private final PrintStream addl;
  private  ProcessExecutor executor;
  private ListCapturingLogOutputStream stdErr;
  private ListCapturingLogOutputStream stdOut;
  private final ChecksumBuilder builder = ChecksumBuilder.newInstance();

  public DefaultProcessExecution() {
    this.addl = null;
    this.gpe = null;
  }

  public DefaultProcessExecution(GeneratedProcessExecution e) {
    this.gpe = requireNonNull(e);
    this.addl = null;
  }

  private DefaultProcessExecution(DefaultProcessExecution e) {
    this.gpe = e.gpe.clone();
    this.addl = e.addl;
    this.executor = e.executor;
    this.stdErr = e.stdErr;
    this.stdOut = e.stdOut;
  }

  public DefaultProcessExecution(final String id, final String executable, final List<String> arguments,
      final Optional<java.time.Duration> timeout, final Optional<Path> stdIn, final Path workDirectory,
      final boolean optional, final Optional<Map<String, String>> environment, final Optional<Path> relativeRoot,
      final Optional<List<Integer>> exitValues, final Optional<java.io.PrintStream> addl, final boolean background) {
    gpe = new GeneratedProcessExecution();
    gpe.setId(id);
    gpe.setExecutable(executable);
    gpe.setArguments(arguments);
    gpe.setTimeout(requireNonNull(timeout).map(Duration::toString).orElse(null));
    gpe.setStdInPath(requireNonNull(stdIn).map(Path::toAbsolutePath).map(Path::toString).orElse(null));
    gpe.setWorkDirectory(requireNonNull(workDirectory).toAbsolutePath().toString());
    gpe.setOptional(optional);
    gpe.setEnvironment(requireNonNull(environment).map(IBUtils.mapSS2Properties::apply).orElse(null));

    gpe.setRelativeRoot(requireNonNull(relativeRoot).map(Path::toAbsolutePath).map(Path::toString).orElse(null));
    gpe.setExitValues(requireNonNull(exitValues).orElse(ProcessExecution.DEFAULT_EXIT).stream().map(String::valueOf)
        .collect(toList()));
    this.addl = requireNonNull(addl).orElse(null);
    gpe.setBackground(background);
  }

  DefaultProcessExecution(final String id, final String executable, final List<String> arguments,
      final Optional<Duration> timeout, final Optional<Path> stdIn, final Path workDirectory, final boolean optional,
      final Optional<Map<String, String>> environment, final Optional<Path> relativeRoot,
      final Optional<List<Integer>> exitValues, final Optional<java.io.PrintStream> addl, final boolean background,
      final org.infrastructurebuilder.util.executor.ListCapturingLogOutputStream stdout,
      final org.infrastructurebuilder.util.executor.ListCapturingLogOutputStream stderr) {
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
  public List<String> getArguments() {
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
    return ofNullable(gpe.getStdInPath()).map(Paths::get);
  }

  @Override
  public Optional<Duration> getTimeout() {
    return ofNullable(gpe.getTimeout()).map(Duration::parse);
  }

  @Override
  public boolean isBackground() {
    return gpe.isBackground();
  }

  @Override
  public boolean isOptional() {
    return gpe.isOptional();
  }

  @Override
  public Optional<PrintStream> getAdditionalPrintStream() {
    return ofNullable(this.addl);
  }

  @Override
  public Map<String, String> getExecutionEnvironment() {
    return IBUtils.propertiesToMapSS.apply(gpe.getEnvironment());
  }

  @Override
  public Path getWorkDirectory() {
    return ofNullable(gpe.getWorkDirectory()).map(Paths::get)
        .orElseThrow(() -> new ProcessException("No work directory"));
  }

  @Override
  public List<Integer> getExitValuesAsIntegers() {
    return gpe.getExitValues().stream().map(Integer::parseInt).collect(toList());
  }

  public java.util.Optional<java.nio.file.Path> getRelativeRoot() {
    return ofNullable(gpe.getRelativeRoot()).map(java.nio.file.Paths::get);
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
