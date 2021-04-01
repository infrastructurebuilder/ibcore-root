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
package org.infrastructurebuilder.util.config.factory;

import static java.util.Optional.empty;

import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.IBRuntimeUtils;
import org.infrastructurebuilder.util.core.LoggerEnabled;
import org.infrastructurebuilder.util.core.Weighted;

public interface IBConfigurableParamFactory<T, P> extends Weighted, LoggerEnabled {

  IBConfigurableParamFactory<T, P> configure(ConfigMapSupplier config);

  Optional<Supplier<T>> getInstance(Optional<P> param);

  /**
   * Default with neither config nor parameter
   *
   * @return
   */
  default Optional<Supplier<T>> getInstance() {
    return this.getInstance(empty());
  }

  Optional<ConfigMapSupplier> getConfig();

  default boolean isConfigured() {
    return getConfig().isPresent();
  }

  IBRuntimeUtils getRuntimeUtils();

  boolean respondsTo(P param);

}
