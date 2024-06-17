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
import static org.infrastructurebuilder.pathref.IBChecksumUtils.getHex;
import static org.infrastructurebuilder.pathref.IBChecksumUtils.getHexStringFromInputStream;
import static org.infrastructurebuilder.pathref.IBChecksumUtils.hexStringToByteArray;
import static org.infrastructurebuilder.pathref.IBChecksumUtils.stripTrailingSlash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.infrastructurebuilder.constants.IBConstants;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IBChecksumUtilsTest {
  public static final String TESTFILE_CHECKSUM = "0bd4468980d90ef4d5e1e39bf30b93670492d282c518da95334df7bcad7ba8e0afe377a97d8fd64b4b6fd452b5d60ee9ee665e2fa5ecb13d8d51db8794011f3e";

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  void setUp() throws Exception {
  }

  @AfterEach
  void tearDown() throws Exception {
  }
  @Test
  public void testDeepMaptoOrderedString() {
    String x = """
            { "a" : "B",
            "z" : 1,
            "y" : 3
            }
        """.trim();
    JSONObject j = new JSONObject(x);
    var expected = """
        a:"B",y:3,z:1
        """.trim();
    assertEquals(expected, IBChecksumUtils.deepMapJSONtoOrderedString.apply(j));
  }

  @Test
  public void testStripTrailingSlash() {
    var f1 = "file://mylocation/bobo";
    assertNull(stripTrailingSlash.apply(null));
    assertEquals("", stripTrailingSlash.apply("/"));
    assertEquals(".", stripTrailingSlash.apply("./"));
    assertEquals(f1, stripTrailingSlash.apply(f1 + "/"));
    assertEquals(f1, stripTrailingSlash.apply(f1));
  }


  @Test
  public void testFromHexString() throws IOException {
    final String y = "XX YY ZZ";
    final String x = getHex(y.getBytes(UTF_8));
    InputStream i = null;
    try {
      i = IBChecksumUtils.inputStreamFromHexString(x);
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      IBChecksumUtils.copy(i, bos);
      assertEquals(y, bos.toString());
      return;
    } finally {
      if (i != null) {
        i.close();
      }
    }
  }

  @Test
  public void testGetHex() {
    final byte[] b = {
        0x00, 0x01, 0x03, 0x0f
    };
    final String s = getHex(b);
    assertEquals("0001030f", s);
    assertTrue(Arrays.equals(b, hexStringToByteArray(s)));
  }

  @Test
  public void testGetHexCharset() {
    final byte[] b = {
        0x00, 0x01, 0x03, 0x0f
    };
    final String s = getHex(b, IBConstants.UTF8);
    assertEquals("0001030f", s);
    assertTrue(Arrays.equals(b, hexStringToByteArray(s)));
  }

  @Test
  public void testGetHexNull() {
    assertNull(getHex(null));
  }

  @Test
  public void testGetHexStringFromInputStream() throws IOException {
    final byte[] b = {
        0x00, 0x01, 0x03, 0x0f
    };
    final ByteArrayInputStream ins = new ByteArrayInputStream(b);
    final String s = getHexStringFromInputStream(ins);
    assertEquals("0001030f", s);
    assertTrue(Arrays.equals(b, hexStringToByteArray(s)));
  }

}
