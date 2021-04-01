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
package org.infrastructurebuilder.util.credentials.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.infrastructurebuilder.util.credentials.basic.DefaultBasicCredentials;
import org.infrastructurebuilder.util.credentials.basic.DefaultBasicCredentialsMapSupplier;
import org.infrastructurebuilder.util.crypto.IBCryptoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultBasicCredentialsMapSupplierTest {

  private DefaultBasicCredentials            aCreds, bCreds, cCreds, dCreds;
  private DefaultBasicCredentialsMapSupplier cs;
  private Map<String, BasicCredentials>      testData, replaceData1;

  @BeforeEach
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

  @Test
  public void testExtends1() {
    assertEquals(2, cs.get().size());
    Assertions.assertThrows(IBCredentialsException.class, () -> cs.addCredentials("C", cCreds));
  }

  @Test
  public void testExtends2() throws IBCryptoException {
    Assertions.assertThrows(IBCredentialsException.class, () -> cs.addCredentials("B", bCreds));
    Map<String, BasicCredentials> m = cs.get();
    assertNotNull(m);
    BasicCredentials c = m.get("B");
    assertNotNull(c.get());
  }

}
