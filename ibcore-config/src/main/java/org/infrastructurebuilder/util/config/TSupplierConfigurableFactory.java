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
package org.infrastructurebuilder.util.config;

import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Configurable;
import org.infrastructurebuilder.util.core.Hinted;
import org.infrastructurebuilder.util.core.IdentifiedAndWeighted;
import org.infrastructurebuilder.util.core.LoggerConfigurable;
import org.infrastructurebuilder.util.core.LoggerEnabled;
import org.infrastructurebuilder.util.core.NameDescribed;
import org.infrastructurebuilder.util.core.TSupplier;

public interface TSupplierConfigurableFactory<T> extends Supplier<TSupplier<T>>, //
    Configurable<ConfigMapBuilder>, //
    NameDescribed, Hinted, LoggerEnabled, LoggerConfigurable, IdentifiedAndWeighted {
  Optional<TSupplier<T>> build();

  default TSupplier<T> get() {
    return build().orElseThrow(() -> new IBException("Failure to build"));
  }

}
