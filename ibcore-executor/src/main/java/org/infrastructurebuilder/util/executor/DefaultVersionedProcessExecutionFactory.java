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

import static java.util.Optional.ofNullable;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.util.executor.execution.model.ProcessExectionFactoryv1_0_0;

public class DefaultVersionedProcessExecutionFactory implements VersionedProcessExecutionFactory {

  private final Optional<PrintStream> addl;
  private final Path scratchDir;

  public final static String DEFAULT_VERSION = "1.0.0";

  public DefaultVersionedProcessExecutionFactory(Path scratchDir, Optional<PrintStream> addl) {
    this.scratchDir = Objects.requireNonNull(scratchDir);
    this.addl = Objects.requireNonNull(addl);
  }

  @Override
  public ProcessExecutionFactory getDefaultFactory(final Path workDirectory, final String id, final String executable) {
    return getFactoryForVersion(DEFAULT_VERSION, workDirectory, id, executable).get();
  }

  @Override
  public Optional<ProcessExecutionFactory> getFactoryForVersion(final String version, final Path workDirectory,
      final String id, final String executable) {
    ProcessExecutionFactory f = null;
    switch (version) {
    case "1.1.0":
      break;
    case "1.0.0":
    default:
      f = createFactory_v1_0_0(workDirectory, id, executable);
      break;
    }
    return ofNullable(f);
  }

  @Override
  public Optional<PrintStream> getAddl() {
    return addl;
  }

  @Override
  public Path getScratchDir() {
    return scratchDir;
  }

  private ProcessExecutionFactory createFactory_v1_0_0(final Path workDirectory, final String id,
      final String executable) {
    return new ProcessExectionFactoryv1_0_0(this, id, executable, workDirectory);
  }

}
