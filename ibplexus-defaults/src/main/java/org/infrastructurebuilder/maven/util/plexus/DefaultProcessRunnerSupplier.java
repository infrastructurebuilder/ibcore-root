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
package org.infrastructurebuilder.maven.util.plexus;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.plexus.logging.Logger;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.executor.DefaultProcessRunner;
import org.infrastructurebuilder.util.executor.ProcessException;
import org.infrastructurebuilder.util.executor.ProcessRunner;
import org.infrastructurebuilder.util.executor.plexus.ProcessRunnerSupplier;
import org.infrastructurebuilder.util.logging.SLF4JFromMavenLogger;


@Named
public class DefaultProcessRunnerSupplier implements ProcessRunnerSupplier {

  private final Optional<PrintStream> addl;

  private final Path buildDir;
  private final ConfigMap cfgMap;
  private final Optional<Long> interimSleep;
  private final Optional<org.slf4j.Logger> logger;
  private final Optional<Path> relativeRoot;
  private final Path scratchDir;

  @Inject
  public DefaultProcessRunnerSupplier(final ConfigMapSupplier cms, final Logger logger) {
    cfgMap = Objects.requireNonNull(cms, "ConfigMapSupplier to DefaultProcessRunnerSupplier").get();
    this.logger = Optional.of(new SLF4JFromMavenLogger(Objects.requireNonNull(logger)));

    addl = Optional.ofNullable(cfgMap.getString(PROCESS_EXECUTOR_SYSTEM_OUT)).map(Boolean::valueOf)
        .flatMap(b -> Optional.ofNullable(b ? System.out : null));
    final Path p = Paths.get(Objects.requireNonNull(cfgMap.getString(PROCESS_TARGET))).toAbsolutePath().normalize();
    if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS))
      throw new ProcessException("Directory " + cfgMap.getString(PROCESS_TARGET) + " is not a valid location");
    buildDir = ProcessException.pet.withReturningTranslation(() -> Files.createDirectories(p));
    scratchDir = buildDir.resolve("process-runner-" + UUID.randomUUID());
    relativeRoot = Optional.ofNullable(cfgMap.getString(PROCESS_EXECUTOR_RELATIVE_ROOT)).map(Paths::get);
    interimSleep = Optional.ofNullable(cfgMap.getString(PROCESS_EXECUTOR_INTERIM_SLEEP)).map(Long::valueOf);
  }

  @Override
  public ProcessRunner get() {
    return new DefaultProcessRunner(scratchDir, addl, logger, relativeRoot, interimSleep);
  }

}
