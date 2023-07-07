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
package org.infrastructurebuilder.util.readdetect.impl;

import java.net.URI;
import java.util.Objects;

import org.slf4j.Logger;

import com.googlecode.download.maven.plugin.internal.ProgressReport;

public class LoggerProgressReport implements ProgressReport {
  private final Logger log;

  public LoggerProgressReport(Logger log) {
    this.log = Objects.requireNonNull(log);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void initiate(URI uri, long total) {
    // TODO Auto-generated method stub

  }

  @Override
  public void update(long bytesRead) {
    // TODO Auto-generated method stub

  }

  @Override
  public void completed() {
    // TODO Auto-generated method stub

  }

  @Override
  public void error(Exception ex) {
    log.error("Error", ex);
  }

}
