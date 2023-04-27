/*
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
package org.infrastructurebuilder.util.config.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.IBRuntimeUtils;

abstract public class AbstractIBConfigurableParamFactory<T, P> implements IBConfigurableParamFactory<T, P> {

  private final IBRuntimeUtils    ibr;
  private final ConfigMapSupplier cms;

  public AbstractIBConfigurableParamFactory(IBRuntimeUtils ibr) {
    this(ibr, null);
  }

  protected AbstractIBConfigurableParamFactory(IBRuntimeUtils ibr, ConfigMapSupplier cms) {
    this.ibr = Objects.requireNonNull(ibr);
    this.cms = cms;
  }

  @Override
  public IBRuntimeUtils getRuntimeUtils() {
    return ibr;
  }

  @Override
  public Logger getLog() {
    return getRuntimeUtils().getLog();
  }

  @Override
  public Optional<ConfigMapSupplier> getConfig() {
    return Optional.ofNullable(this.cms);
  }

}
