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

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Supplies the config for, specifically, an ingestion
 * @author mykel.alvis
 *
 */
public interface IBDataSourceSupplier extends Supplier<IBDataSource>{
  /**
   * Return the id of the targeted dataset.  In most cases, it'll be the same as the id for this dataset.
   * @return
   */
  UUID getId();

}
