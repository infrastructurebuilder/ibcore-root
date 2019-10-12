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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class DefaultBasicCredentialsMapSupplierTest {

  private DefaultBasicCredentials aCreds, bCreds, cCreds, dCreds;
  private DefaultBasicCredentialsMapSupplier cs;
  private Map<String, BasicCredentials> testData, replaceData1;

  @Before
  public void setUp() throws Exception {
    testData = new HashMap<>();
    replaceData1 = new HashMap<>();
    aCreds = new DefaultBasicCredentials("A", Optional.of("Z"));
    bCreds = new DefaultBasicCredentials("B", Optional.empty());
    cCreds = new DefaultBasicCredentials("C", Optional.empty());
    dCreds = new DefaultBasicCredentials("A", Optional.empty());
    testData.put("A", aCreds);
    testData.put("B", bCreds);
    replaceData1.put("A", dCreds);
    cs = new DefaultBasicCredentialsMapSupplier(testData);

  }

  @Test(expected = IBCryptoException.class)
  public void testExtends1() {
    assertEquals(2, cs.get().size());
    cs.addCredentials("C", cCreds);
  }

  @Test(expected = IBCryptoException.class)
  public void testExtends2() {
    cs.addCredentials("B", bCreds);
    Map<String, BasicCredentials> m = cs.get();
    assertNotNull(m);
    BasicCredentials c = m.get("B");
    assertNotNull(c.get());
  }

}
