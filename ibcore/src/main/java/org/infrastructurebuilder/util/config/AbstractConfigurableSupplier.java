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

abstract public class AbstractConfigurableSupplier<TYPE, CONFIG, PARAMETER>
    implements ConfigurableSupplier<TYPE, CONFIG, PARAMETER> {

  private final CONFIG config;
  private final LoggerSupplier loggerSupplier;
  private final PathSupplier wps;
  private final PARAMETER param;

  protected AbstractConfigurableSupplier(PathSupplier wps, CONFIG config, LoggerSupplier l, PARAMETER p) {
    this.wps = requireNonNull(wps);
    this.config = config;
    this.loggerSupplier = requireNonNull(l);
    this.param = p;
  }

//  protected AbstractConfigurableSupplier(PathSupplier wps, CONFIG config, LoggerSupplier l) {
//    this(wps, config, l, null);
//  }

  public PathSupplier getWorkingPathSupplier() {
    return wps;
  }

  public TYPE get() {
    return getInstance(getWorkingPathSupplier(), param);
  }

  protected abstract TYPE getInstance(PathSupplier wps, PARAMETER in);

  public CONFIG getConfig() {
    return this.config;
  }

  @Override
  public Logger getLog() {
    return this.loggerSupplier.get();
  }
}
