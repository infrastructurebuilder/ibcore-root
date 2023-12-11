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
package org.infrastructurebuilder.util.config.old;

import java.util.Objects;

import org.infrastructurebuilder.util.core.IBRuntimeUtils;

/**
 *
 * @param <T> an IBConfigurableFactory type
 * @param <C> A configuration supply type
 */
@Deprecated
abstract public class AbstractIBConfigurableFactory<T, C> implements IBConfigurableFactory<T, C> {

  private final IBRuntimeUtils ibr;

  protected AbstractIBConfigurableFactory(IBRuntimeUtils ibr) {
    this.ibr = Objects.requireNonNull(ibr);
  }

  @Override
  public IBRuntimeUtils getRuntime() {
    return ibr;
  }

  @SuppressWarnings({
      "unchecked", "hiding"
  })
  @Override
  public <T> T configure(C config) {
    return (T) this;
  }
}
