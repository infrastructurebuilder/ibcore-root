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

import static org.infrastructurebuilder.IBConstants.CRYPTO_ENCRYPTION_IDENTIFIERS;
import static org.infrastructurebuilder.IBConstants.CRYPTO_TYPE;
import static org.infrastructurebuilder.IBConstants.ID;
import static org.infrastructurebuilder.IBConstants.PGP_DS_TYPE;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PGPDSEncryptionIdentifierTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testPGPDSEncryptionIdentifierCollectionOfString() {
    assertNotNull(new PGPDSEncryptionIdentifier("pgptest", Arrays.asList("ABCDEF")));
  }

  @Test
  public void testPGPDSEncryptionIdentifierJSON() {
    final JSONObject j = new JSONObject().put(ID, "pgp1").put(CRYPTO_TYPE, PGP_DS_TYPE)
        .put(CRYPTO_ENCRYPTION_IDENTIFIERS, new JSONArray());
    assertNotNull(new PGPDSEncryptionIdentifier(j));
  }

  @Test
  public void testPGPDSEncryptionIdentifierString() {
    final JSONObject j = new JSONObject().put(ID, "pgp1").put(CRYPTO_TYPE, PGP_DS_TYPE)
        .put(CRYPTO_ENCRYPTION_IDENTIFIERS, new JSONArray());
    assertNotNull(new PGPDSEncryptionIdentifier(j.toString()));
  }

}
