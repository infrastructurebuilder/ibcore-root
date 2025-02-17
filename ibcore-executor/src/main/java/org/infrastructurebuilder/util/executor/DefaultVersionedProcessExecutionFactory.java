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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.util.executor.execution.model.ProcessExectionFactoryv1_0_0;

public class DefaultVersionedProcessExecutionFactory implements VersionedProcessExecutionFactory {

  private final Optional<PrintStream> addl;
  private final PathRefPath scratchDir;
  private final PathRefFileSystem root;

  public final static String DEFAULT_VERSION = "2.0.0";

  public DefaultVersionedProcessExecutionFactory(final PathRefFileSystem root, String scratchDir,
      Optional<PrintStream> addl)
  {
    this.root = requireNonNull(root);
    this.scratchDir = this.root.getPath(requireNonNull(scratchDir));
    this.addl = requireNonNull(addl);
  }

  @Override
  public ProcessExecutionFactory getDefaultFactory(final String workDirectory, final String id,
      final String executable) {
    return getFactoryForVersion(DEFAULT_VERSION, workDirectory, id, executable).get();
  }

  @Override
  public Optional<ProcessExecutionFactory> getFactoryForVersion(final String version, final String workDirectory,
      final String id, final String executable) {
    ProcessExecutionFactory f = null;
    switch (version) {
    case "1.1.0":
      break;
    case "1.0.0":
    default:
      f = createFactory_v1_0_0(workDirectory.toString(), id, executable);
      break;
    }
    return ofNullable(f);
  }

  @Override
  public Optional<PrintStream> getAddl() {
    return addl;
  }

  @Override
  public PathRefPath getScratchDir() {
    return scratchDir;
  }

  private ProcessExecutionFactory createFactory_v1_0_0(final String workDirectory, final String id,
      final String executable) {
    return new ProcessExectionFactoryv1_0_0(this, id, executable, workDirectory) //
        .withRelativeRoot(getRoot());
  }

  @Override
  public PathRefFileSystem getRoot() {
    return this.root;
  }

}
