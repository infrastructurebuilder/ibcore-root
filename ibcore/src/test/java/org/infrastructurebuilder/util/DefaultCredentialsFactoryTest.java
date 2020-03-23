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
package org.infrastructurebuilder.util;

import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultCredentialsFactoryTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private Map<String, CredentialsSupplier> cs;
  private DefaultCredentialsFactory        cf;
  private DefaultURLAndCreds               uc;
  private String                           url;
  private Optional<String>                 creds;

  @Before
  public void setUp() throws Exception {
    url = "someurl";
    creds = of("Y");
    cs = new HashMap<>();
    uc = new DefaultURLAndCreds(url, creds);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetCredentialsFor() {
    cs.put("X", new FakeCredentialsSupplier());
    cs.put("Y", new FakeCredentialsSupplier(new DefaultBasicCredentials("Y", Optional.empty())));
    cs.put("Z", new FakeCredentialsSupplier(new DefaultBasicCredentials("Y", of("Y")), 1));
    cf = new DefaultCredentialsFactory(cs);
    Optional<BasicCredentials> b = cf.getCredentialsFor(uc);
    assertEquals("Y", b.get().getSecret().get());
  }

  @Test
  public void testGetCredentialsForNone() {
    cf = new DefaultCredentialsFactory(cs);
    assertFalse(cf.getCredentialsFor(uc).isPresent());
  }

}