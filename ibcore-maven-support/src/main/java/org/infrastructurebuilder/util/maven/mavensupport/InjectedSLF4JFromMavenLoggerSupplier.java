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
package org.infrastructurebuilder.util.maven.mavensupport;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.plugin.logging.Log;
import org.infrastructurebuilder.pathref.api.LoggerProvider;
import org.infrastructurebuilder.pathref.api.LoggerSupplier;
import org.infrastructurebuilder.util.logging.SLF4JFromMavenLogger;
import org.slf4j.Logger;

@Named(InjectedSLF4JFromMavenLoggerSupplier.MAVENLOG)
public class InjectedSLF4JFromMavenLoggerSupplier implements LoggerSupplier, LoggerProvider {
  public static final String MAVENLOG = "maven-log";
  private final Log mavenLog;

  @Inject
  public InjectedSLF4JFromMavenLoggerSupplier(@Nullable Log mavenLog) {
    this.mavenLog = mavenLog;
  }

  @Override
  public Logger get() {
    return new SLF4JFromMavenLogger(this.mavenLog);
  }
}
