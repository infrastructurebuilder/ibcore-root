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
package org.infrastructurebuilder.util.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.core.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.infrastructurebuilder.util.credentials.basic.CredentialsFactory;
import org.infrastructurebuilder.util.credentials.basic.DefaultBasicCredentials;
import org.infrastructurebuilder.util.versions.IBVersionsSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractIBRuntimeUtilsTest {
  public final static Logger              log = LoggerFactory.getLogger(AbstractIBRuntimeUtilsTest.class);
  public final static TestingPathSupplier wps = new TestingPathSupplier();
  public final static GAV                 gav = new DefaultGAV("G:A:1.0.0:jar");

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    wps.finalize();
  }

  private AbstractIBRuntimeUtils  ar;
  private String                  princ, creds;
  private IBArtifactVersionMapper avm;
  private FakeGAVSupplier         gavSupplier;

  @BeforeEach
  public void setUp() throws Exception {
    gavSupplier = new FakeGAVSupplier("G", "A", "1.0.0", null);
    princ = UUID.randomUUID().toString();
    creds = UUID.randomUUID().toString();
    this.avm = new IBArtifactVersionMapper() {

      @Override
      public List<IBVersionsSupplier> getMatchingArtifacts(String groupId, String artifactId) {
        // TODO Auto-generated method stub
        return Collections.emptyList();
      }
    };
    CredentialsFactory cf = new CredentialsFactory() {

      @Override
      public Optional<BasicCredentials> getCredentialsFor(String query) {
        return Optional.of(new DefaultBasicCredentials(princ, Optional.of(creds)));
      }
    };
    ar = new AbstractIBRuntimeUtils(wps, () -> log, gavSupplier, cf, avm, new FakeTypeToExtensionMapper()) {
    };
  }

  @Test
  public void testGetWorkingPath() {
    assertNotNull(ar.getWorkingPath());
  }

  @Test
  public void testGetWorkingGAV() {
    assertEquals(gav, ar.getGAV());
  }

  @Test
  public void testGetLogger() {
    assertEquals(log, ar.getLog());
  }

  @Test
  public void testGetMatchingArtifact() {
    assertEquals(0, ar.getMatchingArtifacts("X", "Y").size());
  }

  @Test
  public void testGetCredentialsFor() {
    assertEquals(creds, ar.getCredentialsFor(princ).get().getSecret().get());
  }

}
