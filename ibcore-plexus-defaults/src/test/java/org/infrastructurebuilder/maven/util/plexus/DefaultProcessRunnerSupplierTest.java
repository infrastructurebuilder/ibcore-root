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
package org.infrastructurebuilder.maven.util.plexus;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.infrastructurebuilder.util.config.ConfigMapBuilder;
import org.infrastructurebuilder.util.config.ConfigMapBuilderSupplier;
import org.infrastructurebuilder.util.config.impl.DefaultConfigMapBuilderSupplier;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.executor.ProcessException;
import org.infrastructurebuilder.util.logging.SLF4JFromMavenLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessRunnerSupplierTest {

  public final static Logger log = LoggerFactory.getLogger(DefaultProcessRunnerSupplierTest.class);
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

  private ConfigMapBuilder cms, cms2;
  private List<String> list;
  private Logger logger;
  private DefaultProcessRunnerSupplier prs;
  private ConfigMapBuilderSupplier v;

  @BeforeEach
  public void setUp() throws Exception {
    var q = new DefaultConfigMapBuilderSupplier();
    cms = q.get().withPropertiesResource("/c1.properties", false);
    logger = new SLF4JFromMavenLogger(new ConsoleLogger(org.codehaus.plexus.logging.Logger.LEVEL_DEBUG, "name"));
    prs = new DefaultProcessRunnerSupplier(q, logger);
    v = new DefaultConfigMapBuilderSupplier();
    cms2 = v.get().withPropertiesResource("/c4.properties", false);
  }

  @Test
  public void testGet() {
    assertNotNull(prs.get());
  }

  @Test
  public void testNonexistent() {
    assertThrows(ProcessException.class, () -> new DefaultProcessRunnerSupplier(v, logger));
  }
}
