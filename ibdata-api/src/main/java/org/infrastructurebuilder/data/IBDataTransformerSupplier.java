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
package org.infrastructurebuilder.data;

import java.util.function.Supplier;

import org.infrastructurebuilder.util.config.ConfigMapSupplier;

/**
 * IMPORTANT!  READ THIS!
 * All Suppliers of IBDataTransformer, including suppliers of IBDataRecordTransformer,
 * are meant to provide disposable, non-singleton instances of the transformer.
 *
 * @author mykel.alvis
 *
 */
public interface IBDataTransformerSupplier extends Supplier<IBDataTransformer> {
//  public final static String UNCONFIGURABLEKEY_FINALIZER_KEY = "<!-- FINALIZER -->";

  /**
   * Return a NEW INSTANCE of IBDataTransformerSupplier.  Methods implementing this must not
   * <code>return this;</code> under any circumstances
   *
   * @param cms
   * @return
   */
  IBDataTransformerSupplier configure(ConfigMapSupplier cms);

  /**
   *
   * Return a NEW INSTANCE of IBDataTransformerSupplier.  Methods implementing this must not
   * <code>return this;</code> under any circumstances
   *
   * This method must be called before <code>configure()</code>
   *
   * Only actually necessary with record-based systems.  Everyone else should write
   * files like adults.
   *
   * @param finalizer
   * @return
   */

  Supplier<IBDataTransformer> withFinalizer(IBDataStreamRecordFinalizer finalizer);
}
