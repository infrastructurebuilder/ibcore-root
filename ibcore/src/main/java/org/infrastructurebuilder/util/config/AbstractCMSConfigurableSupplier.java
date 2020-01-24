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

import org.infrastructurebuilder.util.LoggerSupplier;

abstract public class AbstractCMSConfigurableSupplier<T, P> extends AbstractConfigurableSupplier<T, ConfigMapSupplier, P>  {

  public AbstractCMSConfigurableSupplier(PathSupplier wps, ConfigMapSupplier config, LoggerSupplier l) {
    this(wps, config, l, null);
  }

  public AbstractCMSConfigurableSupplier(PathSupplier wps, ConfigMapSupplier config, LoggerSupplier l, P param) {
    super(wps, config, l, param);
  }


  @Override
  public ConfigurableSupplier<T, ConfigMapSupplier,P> configure(ConfigMapSupplier config) {
    return getConfiguredSupplier(config);
  }


  abstract public AbstractCMSConfigurableSupplier<T,P> getConfiguredSupplier(ConfigMapSupplier cms);
}
