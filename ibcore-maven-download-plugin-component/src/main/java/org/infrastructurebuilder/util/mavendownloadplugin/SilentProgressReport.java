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
package org.infrastructurebuilder.util.mavendownloadplugin;

import java.net.URI;

import org.slf4j.Logger;

//import org.apache.maven.plugin.logging.Log;

/**
 * Silent (no-op) implementation of {@link ProgressReport}. Only errors will get logged at ERROR priority.
 */
public final class SilentProgressReport implements ProgressReport {

  private final Logger log;

  public SilentProgressReport(Logger log) {
    this.log = log;
  }

  @Override
  public void initiate(URI uri, long total) {
  }

  @Override
  public void update(long bytesRead) {
  }

  @Override
  public void completed() {
  }

  @Override
  public void error(Exception ex) {
    if (this.log != null)
      log.error("Error", ex);
  }

}
