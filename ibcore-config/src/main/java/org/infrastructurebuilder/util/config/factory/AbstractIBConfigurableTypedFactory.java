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

import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.IBRuntimeUtils;

/**
 * Abstract impl of IBConfigurableTypedFactory
 *
 * This is really to make boilerplate work go down when using genericss
 *
 * @author mykel.alvis
 *
 * @param <T>
 * @param <P>
 */
abstract public class AbstractIBConfigurableTypedFactory<T, P> extends AbstractIBConfigurableFactory<T>
    implements IBConfigurableTypedFactory<T, P> {

  public AbstractIBConfigurableTypedFactory(IBRuntimeUtils ibr) {
    super(ibr);
  }

  protected AbstractIBConfigurableTypedFactory(IBRuntimeUtils ibr, ConfigMapSupplier cms) {
    super(ibr, cms);
  }

}
