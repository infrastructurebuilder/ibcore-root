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
package org.infrastructurebuilder.util.core;

import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.api.IdentifiedAndWeighted;
import org.infrastructurebuilder.pathref.api.LoggerEnabled;
import org.infrastructurebuilder.pathref.api.base.Configurable;
import org.infrastructurebuilder.pathref.api.base.NameDescribed;
import org.infrastructurebuilder.pathref.api.base.ResponsiveTo;

public interface TSupplierResponsiveFactoryBuilder<R, T, C> extends ResponsiveTo<R>, //
    Configurable<C>, //
    NameDescribed, Hinted, LoggerEnabled, IdentifiedAndWeighted, LoggerConfigurable {

  Optional<TSupplier<T>> build(R resp);

  default TSupplier<T> get(R resp) {
    return build(resp).orElseThrow(() -> new IBException("Failure to build"));
  }

}
