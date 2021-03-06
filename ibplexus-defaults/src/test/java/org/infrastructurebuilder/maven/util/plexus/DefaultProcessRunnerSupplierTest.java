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
package org.infrastructurebuilder.maven.util.plexus;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.infrastructurebuilder.util.config.DefaultStringListSupplier;
import org.infrastructurebuilder.util.config.ExtendedListSupplier;
import org.infrastructurebuilder.util.config.PropertiesInjectedConfigMapSupplier;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.executor.ProcessException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class DefaultProcessRunnerSupplierTest {

  public final static System.Logger log = System.getLogger(DefaultProcessRunnerSupplierTest.class.getName());
  private static Path random_target;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    random_target = Paths.get(Optional.ofNullable(System.getProperty("target")).orElse("./target")).toRealPath()
        .toAbsolutePath().resolve(UUID.randomUUID().toString());
    Files.createDirectories(random_target);

    final Path tempfile = Files.createTempFile("tempconfig", ".xml");
    tempfile.toFile().deleteOnExit();

  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    IBUtils.deletePath(random_target);
  }

  private PropertiesInjectedConfigMapSupplier cms;
  private List<String> list;
  private Logger logger;
  private DefaultProcessRunnerSupplier prs;
  private List<ExtendedListSupplier> suppliers;

  @BeforeEach
  public void setUp() throws Exception {
    list = Arrays.asList(DefaultStringListSupplier.ISOVERRIDE, "/c1.properties");
    suppliers = Arrays.asList(new DefaultStringListSupplier(list));
    cms = new PropertiesInjectedConfigMapSupplier(suppliers);
    final Properties p = new Properties();
    p.load(getClass().getResourceAsStream("/c1.properties"));
    cms.addConfiguration(p);
    logger = new ConsoleLogger(org.codehaus.plexus.logging.Logger.LEVEL_DEBUG,"name");
    prs = new DefaultProcessRunnerSupplier(cms, logger);
  }

  @Test
  public void testGet() {
    assertNotNull(prs.get());
  }

  @Test
  public void testNonexistent() {
    final PropertiesInjectedConfigMapSupplier cms4 = new PropertiesInjectedConfigMapSupplier(Arrays
        .asList(new DefaultStringListSupplier(Arrays.asList(DefaultStringListSupplier.ISOVERRIDE, "/c4.properties"))));
    assertThrows(ProcessException.class, () -> new DefaultProcessRunnerSupplier(cms4, logger));
  }
}
