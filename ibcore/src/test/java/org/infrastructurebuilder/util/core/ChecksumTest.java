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
package org.infrastructurebuilder.util.core;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.infrastructurebuilder.exceptions.IBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ChecksumTest {

  private static TestingPathSupplier wps;
  private Checksum                   cnull;
  private Checksum                   cRick;
  private Checksum                   cString;
  private final String               theString = "NEVER_GONNA_GIVE_YOU_UP";

  @BeforeAll
  public static void setupB4Class() {
    wps = new TestingPathSupplier();
  }

  @BeforeEach
  public void setUp() throws Exception {
    cnull = new Checksum();
    cRick = new Checksum(wps.getTestClasses().resolve("rick.jpg"));
    cString = new Checksum(theString.getBytes(UTF_8));
  }

  @Test
  public void testAsUUID() {
    final String uuid = "747a8830-5c25-3d28-ab17-81ed541f236e";
    assertEquals(uuid, cString.get().get().toString());
  }

  @Test
  public void testBadInputStreamSupplier() {
    Assertions.assertThrows(IBException.class, () -> new Checksum(() -> new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("FAIL, suckah!");
      }
    }));
  }

  @Test
  public void testChecksum() {
    assertNotNull(cnull);
  }

  @Test
  public void testChecksumByteArray() {
    final byte[]   x = { 78, 69, 86, 69, 82, 95, 71, 79, 78, 78, 65, 95, 71, 73, 86, 69, 95, 89, 79, 85, 95, 85, 80 };
    final Checksum c = new Checksum(x);
    assertEquals(cString, c);
  }

  @Disabled
  @Test
  public void testChecksumInputStream() {
    fail("Not yet implemented");
  }

  @Test
  public void testChecksumString() {
    final String testChecksum = "4e455645525f474f4e4e415f474956455f594f555f5550";
    assertEquals(testChecksum, cString.toString());
  }

  @Disabled
  @Test
  public void testChecksumSupplierOfInputStream() {
    fail("Not yet implemented");
  }

  @Test
  public void testCompareTo() {
    assertTrue(cRick.compareTo(cRick) == 0);
    assertTrue(cRick.compareTo(cString) < 0);
    assertTrue(cnull.compareTo(cRick) < 0);
    assertTrue(cRick.compareTo(new Checksum((byte[]) null)) > 0);
  }

  @Test
  public void testEqualsObject() {
    assertEquals(cString, cString);
    assertNotEquals(cString, null);
    assertNotEquals(cString, "ABC");
    assertEquals(cString, new Checksum(theString.getBytes(UTF_8)));
    assertNotEquals(cString, cRick);
  }

  @Test
  public void testGetDigest() {
    final byte[] x = { 78, 69, 86, 69, 82, 95, 71, 79, 78, 78, 65, 95, 71, 73, 86, 69, 95, 89, 79, 85, 95, 85, 80 };

    assertTrue(Arrays.equals(x, cString.getDigest()));
  }

  @Test
  public void testHashCode() {
    final int rickHash = cRick.hashCode();
    assertFalse(rickHash == 0);
    assertEquals(rickHash, cRick.hashCode());

    assertEquals(31, cnull.hashCode());
  }

  @Test
  public void testMapStringStringChecksum() {
    final Map<String, String> s = new HashMap<>();
    s.put("A", "B");
    s.put("B", "A");
    final String val = "420630d340defad8521c53d765c5fec23cd2e642ef7323b02d500af33e145e228f441fb655a72213dfd73e66240d4a9fd9f410ae9a7ea87d50d2f013305ed144";
    assertEquals(val, Checksum.getMapStringStringChecksum(s).toString());
  }

  @Test
  public void testNullInputStreamSupplier() {
    final Checksum c = new Checksum(() -> null);
    assertEquals(new Checksum((byte[]) null), c);
  }

}
