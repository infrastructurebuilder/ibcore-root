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

import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.IBRuntimeUtils;

public class FakeIBConfigurableTypedFactory extends AbstractIBConfigurableTypedFactory<String, String> {

  public FakeIBConfigurableTypedFactory(IBRuntimeUtils ibr) {
    super(ibr);
  }

  public FakeIBConfigurableTypedFactory(IBRuntimeUtils runtimeUtils, ConfigMapSupplier config) {
    super(runtimeUtils, config);
  }

  @Override
  public boolean respondsTo(String type) {
    return type.toLowerCase().equals(type);
  }

  @Override
  public IBConfigurableFactory<String> configure(ConfigMapSupplier config) {
    return new FakeIBConfigurableTypedFactory(getRuntimeUtils(), config);
  }

  @Override
  public Optional<Supplier<String>> getInstance(Optional<ConfigMapSupplier> config) {
    return config.map(c -> () -> "jeff");
  }

}
