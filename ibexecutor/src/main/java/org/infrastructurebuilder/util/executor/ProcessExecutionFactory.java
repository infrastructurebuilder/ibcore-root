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

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.artifacts.Checksum;

/**
 * A {@code ProcessExecutionFactory} must, by contract, supply the work directory as a
 * {@link Path}, a runner-unique id as a {@code String}, and a path to an
 * executable as a {@code String}, generally as constructor arguments
 *
 * @author mykel.alvis
 *
 */
public interface ProcessExecutionFactory extends Supplier<ProcessExecution> {

  String getSuppliedVersion();

  ProcessExecutionFactory withArguments(String... args);

  ProcessExecutionFactory withDuration(Duration timeout);

  ProcessExecutionFactory withStdIn(Path stdIn);

  ProcessExecutionFactory withChecksum(Checksum execChecksum);

  ProcessExecutionFactory withOptional(boolean optional);

  ProcessExecutionFactory withEnvironment(Map<String, String> env);

  ProcessExecutionFactory withRelativeRoot(Path relativeRoot);

  ProcessExecutionFactory withExitCodes(List<Integer> exitCodes);

  ProcessExecutionFactory withBackground(boolean background);

}