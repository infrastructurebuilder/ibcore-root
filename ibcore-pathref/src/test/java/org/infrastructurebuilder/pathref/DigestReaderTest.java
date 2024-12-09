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
package org.infrastructurebuilder.pathref;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;

import org.infrastructurebuilder.constants.IBConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DigestReaderTest {

  public static final String ABC = "ABC";
  public static final String ABC_CHECKSUM = "397118fdac8d83ad98813c50759c85b8c47565d8268bf10da483153b747a74743a58a90e85aa9f705ce6984ffc128db567489817e4092d050d8a1cc596ddc119";
  public static final String NON_CHECKSUM = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e";
  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
  }

  @Test
  void testConstructors() throws NoSuchAlgorithmException, IOException {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(ABC.getBytes(UTF_8));
        Reader r = new InputStreamReader(bis);
        DigestReader rr = new DigestReader(r, IBConstants.DIGEST_TYPE, IBConstants.UTF8)) {
      assertNotNull(rr);
      rr.on(true);
      int i;
      while ((i = rr.read()) != -1) {

      }
      assertEquals(ABC_CHECKSUM, new Checksum(rr.getMessageDigest().digest()).toString());

    }

    try (ByteArrayInputStream bis = new ByteArrayInputStream(ABC.getBytes(UTF_8));
        Reader r = new InputStreamReader(bis);
        DigestReader rr = new DigestReader(r, IBConstants.DIGEST_TYPE, IBConstants.UTF8)) {
      assertNotNull(rr);
      rr.on(false);
      int i;
      while ((i = rr.read()) != -1) {
      }
      assertEquals(NON_CHECKSUM, new Checksum(rr.getMessageDigest().digest()).toString());

    }

  }

  @Test
  void testDigestReaderReaderString() throws IOException, NoSuchAlgorithmException {
    Checksum y;
    final Checksum expected = new Checksum(ABC_CHECKSUM);
    try (ByteArrayInputStream bis = new ByteArrayInputStream(ABC.getBytes(UTF_8));
        Reader r = new InputStreamReader(bis);
        DigestReader rr = new DigestReader(r, IBConstants.DIGEST_TYPE);
        OutputStream bos = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(bos)) {
      rr.transferTo(w);
      y = new Checksum(rr.getMessageDigest().digest());
    }
    assertEquals(ABC_CHECKSUM, y.toString());

  }

}
