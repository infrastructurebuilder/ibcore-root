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
package org.infrastructurebuilder.util.executor;

import static java.util.Optional.empty;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Optional;

public interface VersionedProcessExecutionFactory {

  ProcessExecutionFactory getDefaultFactory(Path workDirectory, String id, String executable);

  Optional<ProcessExecutionFactory> getFactoryForVersion(String version, Path workDirectory, String id,
      String executable);

  default Optional<PrintStream> getAddl() {
    return empty();
  }

  Path getScratchDir();

}