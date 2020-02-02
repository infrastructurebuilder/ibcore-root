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
package org.infrastructurebuilder.util.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.BasicCredentials;
import org.infrastructurebuilder.util.CredentialsFactory;
import org.infrastructurebuilder.util.DefaultBasicCredentials;
import org.infrastructurebuilder.util.FakeTypeToExtensionMapper;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractIBRuntimeUtilsTest {
  public final static Logger log = LoggerFactory.getLogger(AbstractIBRuntimeUtilsTest.class);
  public final static TestingPathSupplier wps = new TestingPathSupplier();
  public final static GAV gav = new DefaultGAV("G:A:1.0.0:jar");

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    wps.finalize();
  }

  private AbstractIBRuntimeUtils ar;
  private String princ, creds;
  private IBArtifactVersionMapper avm;

  @Before
  public void setUp() throws Exception {
    princ = UUID.randomUUID().toString();
    creds = UUID.randomUUID().toString();
    this.avm = new IBArtifactVersionMapper() {
    };
    CredentialsFactory cf = new CredentialsFactory() {

      @Override
      public Optional<BasicCredentials> getCredentialsFor(String query) {
        return Optional.of(new DefaultBasicCredentials(princ, Optional.of(creds)));
      }
    };
    ar = new AbstractIBRuntimeUtils(wps, () -> log, () -> gav, cf, avm, new FakeTypeToExtensionMapper()) {
    };
  }

  @Test
  public void testGetWorkingPath() {
    assertNotNull(ar.getWorkingPath());
  }

  @Test
  public void testGetWorkingGAV() {
    assertEquals(gav, ar.getWorkingGAV());
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