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
package org.infrastructurebuilder.util.maven;


import static java.lang.System.Logger.Level.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.System.Logger;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.infrastructurebuilder.util.maven.mavensupport.InjectedSLF4JFromMavenLoggerSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InjectedSLF4JFromMavenLoggerSupplierTest {

  private static final String TESTING_LOGGER = "Testing logger";
  private Log logger;

  @BeforeEach
  public void setUp() throws Exception {
    logger = new DefaultLog(new ConsoleLogger());
  }

  @Test
  public void testInjectedSLF4JFromMavenLoggerSupplier() {
    Logger m = new InjectedSLF4JFromMavenLoggerSupplier(logger).get();
    assertNotNull(m);
    m.log(INFO, TESTING_LOGGER);
    m.log(DEBUG,TESTING_LOGGER); // NOTE: Does not log to the testing console
    m.log(WARNING,TESTING_LOGGER);
    m.log(ERROR, TESTING_LOGGER);
  }

}
