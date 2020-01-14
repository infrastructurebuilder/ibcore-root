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
package org.infrastructurebuilder.util.config;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.util.LoggerSupplier;
import org.slf4j.Logger;

abstract public class AbstractConfigurableSupplier<T, C, P> implements ConfigurableSupplier<T, C, P> {

  private final C config;
  private final LoggerSupplier loggerSupplier;
  private final PathSupplier wps;

  protected AbstractConfigurableSupplier(PathSupplier wps, C config, LoggerSupplier l) {
    this.wps = requireNonNull(wps);
    this.config = config;
    this.loggerSupplier = requireNonNull(l);
  }

  public PathSupplier getWps() {
    return wps;
  }

  public Optional<Path> getWorkingPath() {
    return Optional.of(getWps().get());
  }

  public T get() {
    return getInstance(getWorkingPath(), empty());
  }

  protected abstract T getInstance(Optional<Path> workingPath, Optional<P> in);

  public C getConfig() {
    return this.config;
  }

  @Override
  public Logger getLog() {
    return this.loggerSupplier.get();
  }
}
