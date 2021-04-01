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
package org.infrastructurebuilder.util.maven.mavensupport;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.plugin.logging.Log;
import org.infrastructurebuilder.util.core.LoggerSupplier;
import org.infrastructurebuilder.util.logging.SLF4JFromMavenLogger;
import org.slf4j.Logger;

@Named(InjectedSLF4JFromMavenLoggerSupplier.LOG)
public class InjectedSLF4JFromMavenLoggerSupplier implements LoggerSupplier {
  public static final String LOG = "maven-log";
  private final Log mavenLog;

  @Inject
  public InjectedSLF4JFromMavenLoggerSupplier(Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  @Override
  public Logger get() {
    return new SLF4JFromMavenLogger(this.mavenLog);
  }
}
