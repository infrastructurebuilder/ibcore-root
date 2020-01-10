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

import org.infrastructurebuilder.util.LoggerSupplier;
import org.slf4j.Logger;

abstract public class AbstractConfigurableSupplier<T, C> implements ConfigurableSupplier<T, C> {

  private final C config;
  private final LoggerSupplier loggerSupplier;

  public AbstractConfigurableSupplier(C config, LoggerSupplier l) {
    this.config = config;
    this.loggerSupplier = requireNonNull(l);
  }

  public T get() {
    return getInstance();
  }

  protected abstract T getInstance();

  public C getConfig() {
    return this.config;
  }

  @Override
  public Logger getLog() {
    return this.loggerSupplier.get();
  }
}
