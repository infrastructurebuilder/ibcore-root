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
package org.infrastructurebuilder.data.ingest;

import java.nio.file.Path;

import org.infrastructurebuilder.data.IBDataIngesterSupplier;
import org.infrastructurebuilder.util.LoggerSupplier;
import org.infrastructurebuilder.util.config.ConfigMapSupplier;
import org.infrastructurebuilder.util.config.PathSupplier;
import org.slf4j.Logger;

abstract public class AbstractIBDataIngesterSupplier implements IBDataIngesterSupplier {

  private final PathSupplier wps;
  private final ConfigMapSupplier config;
  private final LoggerSupplier log;

  /**
   * @param wps
   * @param log
   * @param config
   * @param cacheDirectory
   */
  protected AbstractIBDataIngesterSupplier(PathSupplier wps, LoggerSupplier log, ConfigMapSupplier config) {
    this.wps = wps;
    this.log = log;
    this.config = config;
  }

  public Logger getLog() {
    return log.get();
  }

  protected ConfigMapSupplier getConfig() {
    return config;
  }

  protected PathSupplier getWps() {
    return wps;
  }

}