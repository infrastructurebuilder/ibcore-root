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

import java.lang.System.Logger;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.config.IBRuntimeUtils;
import org.infrastructurebuilder.util.core.IBConfigurable;
import org.infrastructurebuilder.util.core.Weighted;

/**
 * All IBConfigurableFactory instances have a weighted order, an available
 * logger, and an IBRuntimeUtils available.
 *
 * @author mykel.alvis
 *
 * @param <T>
 */
public interface IBConfigurableFactory<T, C> extends Weighted, IBConfigurable<C> {
  Optional<Supplier<T>> getInstance(C config);

  IBRuntimeUtils getRuntime();

  @Override
  default Logger getLog() {
    return getRuntime().getLog();
  }
}
