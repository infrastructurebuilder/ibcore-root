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
package org.infrastructurebuilder.maven.util.plexus;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static org.infrastructurebuilder.util.executor.ProcessException.pet;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.ConfigMapBuilderSupplier;
import org.infrastructurebuilder.util.executor.DefaultProcessRunner;
import org.infrastructurebuilder.util.executor.ProcessException;
import org.infrastructurebuilder.util.executor.ProcessRunner;
import org.infrastructurebuilder.util.executor.ProcessRunnerSupplier;
import org.slf4j.Logger;

@Named
public class DefaultProcessRunnerSupplier implements ProcessRunnerSupplier {

  private final Optional<PrintStream> addl;

  private final Path buildDir;
  private final ConfigMap cfgMap;
  private final Optional<Long> interimSleep;
  private final Logger logger;
  private final Optional<Path> relativeRoot;
  private final Path scratchDir;

  @Inject
  public DefaultProcessRunnerSupplier(final ConfigMapBuilderSupplier cms, final Logger logger) {
    cfgMap = requireNonNull(cms, "ConfigMapBuilderSupplier to DefaultProcessRunnerSupplier").get().get();
    this.logger = requireNonNull(logger);

    addl = cfgMap.optString(PROCESS_EXECUTOR_SYSTEM_OUT).map(Boolean::valueOf)
        .flatMap(b -> Optional.ofNullable(b ? System.out : null));
    final Path p = Paths.get(cfgMap.getString(PROCESS_TARGET)).toAbsolutePath().normalize();
    if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS))
      throw new ProcessException(
          format("%s is not a valid `%s` location", cfgMap.getString(PROCESS_TARGET), PROCESS_TARGET));
    buildDir = pet.returns(() -> Files.createDirectories(p));
    scratchDir = buildDir.resolve("process-runner-" + UUID.randomUUID());
    relativeRoot = cfgMap.optString(PROCESS_EXECUTOR_RELATIVE_ROOT).map(Paths::get);
    interimSleep = cfgMap.optString(PROCESS_EXECUTOR_INTERIM_SLEEP).map(Long::valueOf);
  }

  @Override
  public ProcessRunner get() {
    return new DefaultProcessRunner(scratchDir, addl, of(logger), relativeRoot, interimSleep);
  }

}
