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

import org.slf4j.Logger;

abstract public class AbstractConfigurableSupplier<TYPE, CONFIG, PARAMETER>
    implements ConfigurableSupplier<TYPE, CONFIG, PARAMETER> {

  protected final CONFIG config;
  private final IBRuntimeUtils ibr;
  private final PARAMETER param;

  protected AbstractConfigurableSupplier(IBRuntimeUtils ibr, CONFIG config, PARAMETER p) {
    this.ibr = requireNonNull(ibr);
    this.config = config;
    this.param = p;
  }

  public PathSupplier getWorkingPathSupplier() {
    return () -> ibr.getWorkingPath();
  }

  public TYPE get() {
    return getInstance(getRuntimeUtils(), param);
  }

  protected abstract TYPE getInstance(IBRuntimeUtils r, PARAMETER in);

  public CONFIG getConfig() {
    return this.config;
  }

  public IBRuntimeUtils getRuntimeUtils() {
    return ibr;
  }

  @Override
  public Logger getLog() {
    return this.ibr.getLog();
  }
}
