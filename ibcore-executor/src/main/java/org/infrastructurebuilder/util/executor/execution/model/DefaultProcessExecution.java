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

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.ChecksumBuilder;
import org.infrastructurebuilder.pathref.ChecksumBuilderFactory;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.util.executor.api.AbstractProcessExecutionResult;
import org.infrastructurebuilder.util.executor.api.ListCapturingLogOutputStream;
import org.infrastructurebuilder.util.executor.api.ModeledProcessExecution;
import org.infrastructurebuilder.util.executor.api.ProcessException;
import org.infrastructurebuilder.util.executor.api.ProcessExecution;
import org.infrastructurebuilder.util.executor.model.v1_0.EnvEntry;
import org.infrastructurebuilder.util.executor.model.v1_0.Environment;
import org.infrastructurebuilder.util.executor.model.v1_0.GeneratedProcessExecution;
import org.zeroturnaround.exec.ProcessExecutor;

public class DefaultProcessExecution implements ProcessExecution<ProcessExecutor> {

  private final ModeledProcessExecution model;
  private final PrintStream addl;
  private ProcessExecutor executor;
  private ListCapturingLogOutputStream stdErr;// = new ListCapturingLogOutputStream(empty(), empty());
  private ListCapturingLogOutputStream stdOut;// = new ListCapturingLogOutputStream(empty(), empty());
  private final ChecksumBuilder builder = ChecksumBuilderFactory.newInstance();

//  public DefaultProcessExecution() {
//    this.addl = null;
//    this.model = null;
//  }

  public DefaultProcessExecution(GeneratedProcessExecution gpe) {
    this.model = new ModeledProcessExecution(requireNonNull(gpe));
    this.addl = null;
  }

  private DefaultProcessExecution(DefaultProcessExecution e) {
    this.model = new ModeledProcessExecution(e.model);
    this.addl = e.addl;
    this.executor = e.executor;
    this.stdErr = e.stdErr;
    this.stdOut = e.stdOut;
  }

  public DefaultProcessExecution(final String id, //
      final String executable, final List<String> arguments, //
      final PathRefFileSystem root, //
      final Optional<java.time.Duration> timeout, //
      final Optional<String> stdIn, //
      final String workDirectory, //
      final boolean optional, final Optional<Map<String, String>> environment, //
      final Optional<List<Integer>> exitValues, //
      final Optional<java.io.PrintStream> addl, final boolean background)
  {
    this.model = new ModeledProcessExecution("1.0", id, executable, arguments, //
        root.toString(), // Returns key-based URI of PRFS
        timeout.map(Duration::toString).orElse(null), //
        optional, background, workDirectory.toString(),
        exitValues.map(x -> x.stream().map(i -> i.toString()).toList()).orElse(null),

        null, // getStdOut().getPath().map(Path::toString).orElse(null), // Placeholders until we set the values below
        null, // getStdErr().getPath().map(Path::toString).orElse(null), //
        null, // stdIn.map(Path::toString).orElse(null), //

        requireNonNull(environment).map(AbstractProcessExecutionResult.toEnvironment::apply).map(Environment::new)
            .orElseGet(() -> new Environment()));

    if (getWorkDirectory().getFileSystem() != root)
      throw new ProcessException("Work directory must be set");
//    Path p = getWorkDirectory();
//    String q = getStdOut().getPath().map(Path::toString).orElse(null);
    this.model.setStdOutPath(getStdOut().getPath().map(Path::toString).orElse(null));
    this.model.setStdErrPath(getStdErr().getPath().map(Path::toString).orElse(null));
    this.model.setStdInPath(stdIn.orElse(null));
    this.addl = requireNonNull(addl).orElse(null);
    model.setBackground(background);
  }

  DefaultProcessExecution(final String id, //
      final String executable, final List<String> arguments, //
      final PathRefFileSystem relativeRoot, //
      final Optional<Duration> timeout, //
      final Optional<String> stdIn, //
      final String workDirectory, //
      final boolean optional, final Optional<Map<String, String>> environment, //
      final Optional<List<Integer>> exitValues, final Optional<java.io.PrintStream> addl, //
      final boolean background, final org.infrastructurebuilder.util.executor.api.ListCapturingLogOutputStream stdout,
      final org.infrastructurebuilder.util.executor.api.ListCapturingLogOutputStream stderr)
  {
    this(id, executable, arguments, relativeRoot, timeout, stdIn, workDirectory, optional, environment, exitValues,
        addl, background);
    this.stdOut = stdout;
    this.stdErr = stderr;
  }

  public ProcessExecutor getProcessExecutor() {
    if (this.executor == null)
      this.executor = _getProcessExecutor();

    return this.executor;
  }

  private ProcessExecutor _getProcessExecutor() {
    final List<String> command = new ArrayList<>();
    command.add(getExecutable());
    command.addAll(getArguments());
    List<Integer> l = getExitValuesAsIntegers().orElseGet(() -> new ArrayList<>());
    Integer[] exitValues = (Integer[]) l.toArray(new Integer[l.size()]);
    File w = getWorkDirectory().toFile();
    log.debug("Working directory for {} is {}", getId(), w);
    ProcessExecutor _pe = new ProcessExecutor()

        .environment(getExecutionEnvironment())

        .directory(w)

        .redirectError(getStdErr())

        .redirectOutput(getStdOut())

//        .redirectInput(
//            getStdIn().map(si -> ProcessException.pet.returns(() -> Files.newInputStream(si))).orElse(System.in))

        .exitValues(exitValues)

        .command(command)

    ;
    
    
    if (getStdIn().isPresent()) {
      var sip = getStdIn().get();
      var fi =  ProcessException.pet.returns(() -> Files.newInputStream(sip));
      _pe = _pe.redirectInput(fi);
    }
    final ProcessExecutor pe = _pe;

    if (getTimeout().isPresent()) {
      final Duration d = getTimeout().get();
      if (d.isNegative())
        throw new ProcessException("Negative timeouts are disallowed " + d);
      return pe.timeout(d.get(ChronoUnit.SECONDS) * 1000 + d.get(ChronoUnit.NANOS), TimeUnit.NANOSECONDS);
    } else
      return pe;
  }


  @Override
  public void close() {
    ofNullable(model).ifPresent(e -> {
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
    return model.getArguments();
  }

  @Override
  public String getExecutable() {
    return model.getExecutable();
  }

  @Override
  public String getId() {
    return model.getId();
  }

  @Override
  public Optional<Path> getStdIn() {
    return model.getStdInPath().map(p -> {
      return getRoot().getPath(p);
    });
  }

  @Override
  public Optional<Duration> getTimeout() {
    return model.getTimeout().map(Duration::parse);
  }

  @Override
  public boolean isBackground() {
    return model.getBackground().orElse(false);
  }

  @Override
  public boolean isOptional() {
    return model.getOptional().orElse(false);
  }

  @Override
  public Optional<PrintStream> getAdditionalPrintStream() {
    return ofNullable(this.addl);
  }

  @Override
  public Map<String, String> getExecutionEnvironment() {
    return model.getEnvironment().map(AbstractProcessExecutionResult.fromEnvironment).orElseGet(Collections::emptyMap);
  }

  @Override
  public Path getWorkDirectory() {
    return model.getWorkDirectory()//
        .map(p -> {
          return this.getRoot().getPath(p);
        }).orElseThrow(() -> new ProcessException("No work directory"));
  }

  @Override
  public Optional<List<Integer>> getExitValuesAsIntegers() {
    return model.getExitValues().map(ev -> ev.stream().map(Integer::parseInt).toList());
  }

  public PathRefFileSystem getRoot() {
    return model.getRelativeRoot().orElseThrow(() -> new ProcessException("No relative root availabel"));
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
    if (this.stdOut == null) {
      var q = getWorkDirectory();
      var r = q.resolve(randomUUID().toString() + ".stdout");
      this.stdOut = new ListCapturingLogOutputStream(r, getAdditionalPrintStream());
    }
    return this.stdOut;
  }

  @Override
  protected DefaultProcessExecution clone() throws CloneNotSupportedException {
    return new DefaultProcessExecution(this);
  }

  public Optional<ChecksumBuilder> getChecksumBuilder() {
    return Optional.ofNullable(this.builder); // TODO
  }

  @Override
  public Checksum asChecksum() {
    return getChecksumBuilder().get().asChecksum();
  }

}
