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

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.executor.ProcessException;
import org.infrastructurebuilder.util.executor.ProcessExecution;
import org.infrastructurebuilder.util.executor.ProcessExecutionFactory;
import org.infrastructurebuilder.util.executor.ProcessRunner;
import org.infrastructurebuilder.util.executor.VersionedProcessExecutionFactory;

public class ProcessExectionFactoryv1_0_0 implements ProcessExecutionFactory {

  private final VersionedProcessExecutionFactory parent;
  private final String                           id;
  private final String                           executable;
  private final Path                             workDirectory;
  private boolean                                background;
  private List<Integer>                          exitCodes    = null;
  private Path                                   relativeRoot = null;
  private Map<String, String>                    env          = null;
  private boolean                                optional;
  private Checksum                               execChecksum = null;
  private Path                                   stdIn        = null;
  private Duration                               timeout      = null;
  private List<String>                           args         = null;

  public ProcessExectionFactoryv1_0_0(VersionedProcessExecutionFactory parent, String id, String executable,
      Path workDirectory) {
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
  public ProcessExecution get() {
    final Path execScratch = parent.getScratchDir().resolve(requireNonNull(id, "execution id"));
    if (ProcessRunner.ws.matcher(id).find())
      throw new ProcessException("No whitespace is allowed in execution ids for ProcessRunner");
    if (!isDirectory(requireNonNull(execScratch))) {
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
    return new DefaultProcessExecution(this.id, this.executable,
        ofNullable(args).orElse(new ArrayList<>()), ofNullable(timeout), ofNullable(stdIn), this.workDirectory,
        optional, ofNullable(env), ofNullable(relativeRoot), ofNullable(exitCodes), parent.getAddl(), background);
  }

  @Override
  public ProcessExecutionFactory withArguments(String... args) {
    this.args = Arrays.asList(requireNonNull(args));
    return this;
  }

  @Override
  public ProcessExecutionFactory withDuration(Duration timeout) {
    this.timeout = requireNonNull(timeout);
    return this;
  }

  @Override
  public ProcessExecutionFactory withStdIn(Path stdIn) {
    this.stdIn = requireNonNull(stdIn);
    return this;
  }

  @Override
  public ProcessExecutionFactory withChecksum(Checksum execChecksum) {
    this.execChecksum = requireNonNull(execChecksum);
    return this;
  }

  @Override
  public ProcessExecutionFactory withOptional(boolean optional) {
    this.optional = requireNonNull(optional);
    return this;
  }

  @Override
  public ProcessExecutionFactory withEnvironment(Map<String, String> env) {
    this.env = requireNonNull(env);
    return this;
  }

  @Override
  public ProcessExecutionFactory withRelativeRoot(Path relativeRoot) {
    this.relativeRoot = requireNonNull(relativeRoot);
    return this;
  }

  @Override
  public ProcessExecutionFactory withExitCodes(List<Integer> exitCodes) {
    this.exitCodes = requireNonNull(exitCodes);
    return this;
  }

  @Override
  public ProcessExecutionFactory withBackground(boolean background) {
    this.background = requireNonNull(background);
    return this;
  }

}
