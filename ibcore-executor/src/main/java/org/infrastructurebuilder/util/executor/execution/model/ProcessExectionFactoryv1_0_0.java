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

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isExecutable;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Files.newInputStream;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.util.executor.api.ProcessException;
import org.infrastructurebuilder.util.executor.api.ProcessExecution;
import org.infrastructurebuilder.util.executor.api.ProcessExecutionFactory;
import org.infrastructurebuilder.util.executor.api.ProcessRunner;
import org.infrastructurebuilder.util.executor.api.VersionedProcessExecutionFactory;
import org.zeroturnaround.exec.ProcessExecutor;

public class ProcessExectionFactoryv1_0_0 implements ProcessExecutionFactory<ProcessExecutor> {

  private final VersionedProcessExecutionFactory<ProcessExecutor> parent;
  private final String id;
  private final String executable;
  private final String workDirectory;
  private boolean background;
  private List<Integer> exitCodes = null;
  private PathRefFileSystem root = null;
  private Map<String, String> env = null;
  private boolean optional;
  private Checksum execChecksum = null;
  private String stdIn = null;
  private Duration timeout = null;
  private List<String> args = null;

  public ProcessExectionFactoryv1_0_0(VersionedProcessExecutionFactory<ProcessExecutor> parent, String id, String executable,
      String workDirectory)
  {
    this.parent = requireNonNull(parent);
    this.id = requireNonNull(id);
    this.executable = requireNonNull(executable);
    this.workDirectory = requireNonNull(workDirectory);
  }

  @Override
  public String getSuppliedVersion() {
    return "1.0.0";
  }

  @Override
  public ProcessExecution<ProcessExecutor> get() {
    final Path execScratch = parent.getScratchDir().resolve(requireNonNull(id, "execution id"));
    if (ProcessRunner.ws.matcher(id).find())
      throw new ProcessException("No whitespace is allowed in execution ids for ProcessRunner");
    if (!Files.exists(execScratch) || !isDirectory(requireNonNull(execScratch))) {
      ProcessException.pet.translate(() -> Files.createDirectories(execScratch));
    }
    if (!isWritable(execScratch))
      throw new ProcessException("Cannot write to " + execScratch);
    final Path p = Paths.get(requireNonNull(executable));
    ofNullable(execChecksum).ifPresent(csum -> {
      Checksum c = null;
      if (isExecutable(p)) {
        try {
          c = new Checksum(newInputStream(p));
        } catch (final IOException e) {
        }
      }
      if (!csum.equals(c))
        throw new ProcessException("Checksum of executable " + c + " does not match supplied " + csum);
    });
    return new DefaultProcessExecution(this.id, this.executable, //
        ofNullable(args).orElse(new ArrayList<>()), Objects.requireNonNull(root), ofNullable(timeout), //
        ofNullable(stdIn), this.workDirectory, optional, ofNullable(env), //
        ofNullable(exitCodes), //
        parent.getAddl(), background);
  }

  @Override
  public ProcessExecutionFactory<ProcessExecutor> withArguments(List<String> args) {
    this.args = Objects.requireNonNull(args);
    return this;
  }

  @Override
  public ProcessExecutionFactory<ProcessExecutor> withDuration(Duration timeout) {
    this.timeout = requireNonNull(timeout);
    return this;
  }

  @Override
  public ProcessExecutionFactory<ProcessExecutor> withStdIn(Path stdIn) {
    if (!(requireNonNull(stdIn) instanceof PathRefPath))
      throw new ProcessException("stdin must be a PathRefPath per the root %s".formatted(stdIn));
    this.stdIn = stdIn.toString();
    return this;
  }

  @Override
  public ProcessExecutionFactory<ProcessExecutor> withExecutableChecksum(Checksum execChecksum) {
    this.execChecksum = requireNonNull(execChecksum);
    return this;
  }

  @Override
  public ProcessExecutionFactory<ProcessExecutor> withOptional(boolean optional) {
    this.optional = requireNonNull(optional);
    return this;
  }

  @Override
  public ProcessExecutionFactory<ProcessExecutor> withEnvironment(Map<String, String> env) {
    this.env = requireNonNull(env);
    return this;
  }

  @Override
  public ProcessExecutionFactory<ProcessExecutor> withRelativeRoot(PathRefFileSystem relativeRoot) {
    this.root = requireNonNull(relativeRoot);
    return this;
  }

  @Override
  public ProcessExecutionFactory<ProcessExecutor> withExitCodes(List<Integer> exitCodes) {
    this.exitCodes = requireNonNull(exitCodes);
    return this;
  }

  @Override
  public ProcessExecutionFactory<ProcessExecutor> withBackground(boolean background) {
    this.background = requireNonNull(background);
    return this;
  }

}
