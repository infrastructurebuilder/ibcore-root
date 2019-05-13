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
package org.infrastructurebuilder.util.artifacts;

import static org.infrastructurebuilder.util.IBUtils.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.IBException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ChecksumTest {

  private Checksum cnull;
  private Checksum cRick;
  private Checksum cString;
  private Path p;
  private final String theString = "NEVER_GONNA_GIVE_YOU_UP";

  @Before
  public void setUp() throws Exception {
    p = Paths.get(Optional.ofNullable(System.getProperty("target")).orElse("./target")).toRealPath().toAbsolutePath();
    cnull = new Checksum();
    cRick = new Checksum(p.resolve("test-classes").resolve("rick.jpg"));
    cString = new Checksum(theString.getBytes(UTF_8));
  }

  @Test
  public void testAsUUID() {
    final String uuid = "747a8830-5c25-3d28-ab17-81ed541f236e";
    assertEquals("UUID of cString is " + uuid, uuid, cString.get().get().toString());
  }

  @Test(expected = IBException.class)
  public void testBadInputStreamSupplier() {
    new Checksum(() -> new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("FAIL, suckah!");
      }
    });
  }

  @Test
  public void testChecksum() {
    assertNotNull("Default checksum is still a checksum", cnull);
  }

  @Test
  public void testChecksumByteArray() {
    final byte[] x = { 78, 69, 86, 69, 82, 95, 71, 79, 78, 78, 65, 95, 71, 73, 86, 69, 95, 89, 79, 85, 95, 85, 80 };
    final Checksum c = new Checksum(x);
    assertEquals("Same as cString", cString, c);
  }

  @Ignore
  @Test
  public void testChecksumInputStream() {
    fail("Not yet implemented");
  }

  @Test
  public void testChecksumString() {
    final String testChecksum = "4e455645525f474f4e4e415f474956455f594f555f5550";
    assertEquals("checksum of test string is " + testChecksum, testChecksum, cString.toString());
  }

  @Ignore
  @Test
  public void testChecksumSupplierOfInputStream() {
    fail("Not yet implemented");
  }

  @Test
  public void testCompareTo() {
    assertTrue("rick == rick", cRick.compareTo(cRick) == 0);
    assertTrue("rick < cString", cRick.compareTo(cString) < 0);
    assertTrue("null <  rick", cnull.compareTo(cRick) < 0);
    assertTrue("rick >  null", cRick.compareTo(new Checksum((byte[]) null)) > 0);
  }

  @Test
  public void testEqualsObject() {
    assertEquals("Same", cString, cString);
    assertNotEquals("Nulls", cString, null);
    assertNotEquals("Class wrong", cString, "ABC");
    assertEquals("Chekcsum of string", cString, new Checksum(theString.getBytes(UTF_8)));
    assertNotEquals("Not same as rick", cString, cRick);
  }

  @Test
  public void testGetDigest() {
    final byte[] x = { 78, 69, 86, 69, 82, 95, 71, 79, 78, 78, 65, 95, 71, 73, 86, 69, 95, 89, 79, 85, 95, 85, 80 };

    assertTrue("Digest is same", Arrays.equals(x, cString.getDigest()));
  }

  @Test
  public void testHashCode() {
    final int rickHash = cRick.hashCode();
    assertFalse("Hashcode for rick is not 0", rickHash == 0);
    assertEquals("Hashcode didn't change", rickHash, cRick.hashCode());

    assertEquals("Null hash is 31", 31, cnull.hashCode());
  }

  @Test
  public void testMapStringStringChecksum() {
    final Map<String, String> s = new HashMap<>();
    s.put("A", "B");
    s.put("B", "A");
    final String val = "420630d340defad8521c53d765c5fec23cd2e642ef7323b02d500af33e145e228f441fb655a72213dfd73e66240d4a9fd9f410ae9a7ea87d50d2f013305ed144";
    assertEquals("Checksum is val", val, Checksum.getMapStringStringChecksum(s).toString());
  }

  @Test
  public void testNullInputStreamSupplier() {
    final Checksum c = new Checksum(() -> null);
    assertEquals("Null iSsupplier == null checksum", new Checksum((byte[]) null), c);
  }

}
