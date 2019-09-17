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

import org.infrastructurebuilder.data.IbdataApiVersioning;
import org.infrastructurebuilder.util.artifacts.IBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;

/**
 * Instances of IBDataEngine understand some set of data and can return instances of that data using supplier types
 * @author mykel.alvis
 *
 */
public interface IBDataEngine {
  final static IBVersion API_ENGINE_VERSION = new DefaultIBVersion(IbdataApiVersioning.getVersion());

  /**
   * Should be overriden in implementations because top-level erasure is a thingS
   * @return IBVersion instance of the implementations API
   */
  default IBVersion getEngineAPIVersion() {
    return API_ENGINE_VERSION;
  }
}
