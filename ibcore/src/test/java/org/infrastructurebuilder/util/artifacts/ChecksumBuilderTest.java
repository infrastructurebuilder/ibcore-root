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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.infrastructurebuilder.IBException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class ChecksumBuilderTest {

  private final Checksum abc = new Checksum(
      "397118fdac8d83ad98813c50759c85b8c47565d8268bf10da483153b747a74743a58a90e85aa9f705ce6984ffc128db567489817e4092d050d8a1cc596ddc119");
  private final Checksum doubleChek = new Checksum(
      "0b3402a678ec2788804994fb2df9faf66eecbdde26553e320a8d4a154f53d840d2a32245998c38f885f01137c9fcf123f3752fc841508dc771fa6faaee689b73");
  private final Checksum floatChek = new Checksum(
      "0b3402a678ec2788804994fb2df9faf66eecbdde26553e320a8d4a154f53d840d2a32245998c38f885f01137c9fcf123f3752fc841508dc771fa6faaee689b73");
  private final Checksum hoursChek = new Checksum(
      "587a8a91e468ebc630a6d514a520da8274b84c8cc2e22b39d9af2a20837f054d102a8b999f53d4c69e3d246d65b3881c74576e5e0fc69ede7c54289efe88f563");
  private final Checksum integerChek = new Checksum(
      "4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a");
  private final Checksum jsonArrayChek = new Checksum(
      "0739c4d3cad25f62492289a172dc32ab27dcbefe32aac1855a2f944d5cc4ce9e881b2544c68d24b7bf83ed8910fadbad7715f9cc06708534b91f4a334d7649c9");
//  private final Checksum jsonChek = new Checksum(
//      "4aea1a0cbef2b738c255cef844eda734c5fff6bd4a80e9d2bf92046ab09c7cafbe140461f678a71096fe7b8839ff69131b452c19dba11987a5db3906c3014041");
  private final Checksum longChek = new Checksum(
      "4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a");
  private Map<String, List<ChecksumEnabled>> m;
  private Map<String, Map<String, List<ChecksumEnabled>>> map;
  private Optional<Path> relativeRoot;
  private final Checksum relRootCheck = new Checksum(
      "ffdc68defa429277daa99fa2ef18b55c2f477bcb050bff14fd679e42aa97ddf8c1193276021b78d02645c7190ef02d81d4a0397e8de19f13129e8177df22bbd6");
//  private ChecksumBuilder sha1;
  private ChecksumBuilder sha512;
  private ChecksumEnabled t;
//  private final Checksum throwChek = new Checksum(
//      "ffdc68defa429277daa99fa2ef18b55c2f477bcb050bff14fd679e42aa97ddf8c1193276021b78d02645c7190ef02d81d4a0397e8de19f13129e8177df22bbd6");
  private final Checksum trueChek = new Checksum(
      "9120cd5faef07a08e971ff024a3fcbea1e3a6b44142a6d82ca28c6c42e4f852595bcf53d81d776f10541045abdb7c37950629415d0dc66c8d86c64a5606d32de");

  @Before
  public void setUp() throws Exception {
    relativeRoot = Optional.of(Paths.get(Optional.ofNullable(System.getProperty("target_dir")).orElse("./target")));
    sha512 = ChecksumBuilder.newInstance(relativeRoot);
    sha1 = ChecksumBuilder.newAlternateInstance("SHA-1", relativeRoot);
    map = new HashMap<>();
    m = new HashMap<>();
    t = () -> new Checksum("ABCD");

  }

  @Test
  public void testAddBooleanOptionalOfBoolean() {
    final Checksum v = sha512.addBoolean(Optional.of(Boolean.parseBoolean("true"))).asChecksum();
    assertEquals(trueChek, v);
  }

  @Test
  public void testAddChecksum() {
    assertEquals(
        "ba1a93b6c1e1abf33c1375368e44a346a45a80e657153d0076c15f0dcfc0ba6b37961790750d014942e410a32d41f8c0e3dcb84ee3e80b955db3db5a80abcc8b",
        sha512.addChecksum(relRootCheck).asChecksum().toString());
  }

  @Test
  public void testAddChecksumEnabledOptionalOfChecksumEnabled() {
    assertEquals(
        "9b3a86c5dddf6c13acb969db8f54ebb9fa50dc4f902eff843380cebfc9cc53ffc2cc4a5f4dda0fac9e3315578faebc999ee61609434e10a6c75e3d984ee2a426",
        sha512.addChecksumEnabled(Optional.of(t)).asChecksum().toString());
  }

  @Test
  public void testAddDoubleOptionalOfDouble() {
    final Checksum v = sha512.addDouble(Optional.of(Double.parseDouble("1.0"))).asChecksum();
    assertEquals(doubleChek, v);
  }

  @Test
  public void testAddDuration() {
    final Checksum v = sha512.addDuration(Optional.of(Duration.ofHours(1))).asChecksum();
    assertEquals(hoursChek.toString(), v.toString());
  }

  @Test
  public void testAddFloatOptionalOfFloat() {
    final Checksum v = sha512.addFloat(Optional.of(Float.parseFloat("1.0"))).asChecksum();
    assertEquals(floatChek, v);
  }

  @Test
  public void testAddInstantOptionalOfInstant() {
    assertEquals(
        "7b29fede180fcde6306ce321021be789a3d612e402cc4ec57dac9758545230fba2d5961d8136564561df2c653ef1fbcbee4b4014d23eb02774074a2184027705",
        sha512.addInstant(Optional.of(Instant.ofEpochMilli(100L))).asChecksum().toString());
  }

  @Test
  public void testAddIntegerOptionalOfInteger() {
    final Checksum v = sha512.addInteger(Optional.of(Integer.parseInt("1"))).asChecksum();
    assertEquals(integerChek, v);
  }

  @Test
  public void testAddJSONArray() {
    final JSONArray j1 = new JSONArray(Arrays.asList("four", "five", "six"));
    final Checksum v = sha512.addJSONArray(j1).asChecksum();
    assertEquals(jsonArrayChek, v);
  }

  @Test
  public void testAddJSONObject() {
    final JSONObject j1 = new JSONObject().put("one", "two")
        .put("three", new JSONArray(Arrays.asList("four", "five", "six"))).put("double", new Double(1.2))
        .put("float", new Float(1.2)).put("int", new Integer(1)).put("long", new Long(1L)).put("boolean", true)
        .put("obj", new JSONObject());
    final Checksum v = sha512.addJSONObject(j1).asChecksum();
    assertEquals(
        "1d285a69f4f9b4a650f3e72b4b56e95e557c34b321c70e601567f7aadd6d3b49199c840f284cb77252efa9b3a52e9ffaf12d3a16dce7e97ee2f1697f4a2e62d7",
        v.toString());

  }

  @Test
  public void testAddListStringOptionalOfListOfString() {
    assertEquals(
        "0da6633d668fae018c86dc1e86e8ca7d6c2b7bcbc519234da80e57b213c9a6504c2c7a48fd441070b5f82aa2e4bda8de4400b62bcb136880b17c12fbdd1c4d86",
        sha512.addListString(Optional.of(Arrays.asList("ABC", "DEF", "GHI"))).asChecksum().toString());
  }

  @Test
  public void testAddLongOptionalOfLong() {
    final Checksum v = sha512.addLong(Optional.of(Long.parseLong("1"))).asChecksum();
    assertEquals(longChek, v);
  }

  @Test
  public void testAddMapStringChecksumEnabled() {
    final HashMap<String, ChecksumEnabled> abc = new HashMap<>();
    abc.put("ABC", t);
    abc.put("easyas", t);
    assertEquals(
        "95fdc8ddbff3e2085ff7cf6cd16c3d2a74aff8d22c76df4ea24abf7d18cb495a3cfd3415bfa10c9ec3e6b194c499e3238052711bc3eca593f03bb3ebeeb7e08c",
        sha512.addMapStringChecksumEnabled(abc).asChecksum().toString());
  }

  @Test
  public void testAddMapStringMapStringListChecksumEnabled() {
    assertEquals(
        "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
        sha512.addMapStringMapStringListChecksumEnabled(map).asChecksum().toString());
  }

  @Test
  public void testAddMapStringMapStringListChecksumEnabled2() {
    map.put("m", m);
    assertEquals(
        "36cdc94cc412e83a64414895381854304ba268b1c8333d9636396a5afc4a7c07cfbaefe613878c05a8d1b47cbd50aa4ce063e59f9673e9050610048da7fd1231",
        sha512.addMapStringMapStringListChecksumEnabled(map).asChecksum().toString());
  }

  @Test
  public void testAddMapStringMapStringListChecksumEnabled3() {
    m.put("ABV", Arrays.asList(t));
    map.put("m", m);
    assertEquals(
        "467132947f3facab6f03fe850c28a58488bd25c4209e024fbff35ce5f26988a20e6e337bb82a6afc98fcda4ff0bcfb39de8d39996f5bd5e1e2accfdb05fa8eb9",
        sha512.addMapStringMapStringListChecksumEnabled(map).asChecksum().toString());
  }

  @Test
  public void testAddMapStringString() {
    final HashMap<String, String> abc = new HashMap<>();
    abc.put("ABC", "123");
    abc.put("easyas", "123");
    assertEquals(
        "ff0bbe472be944d9d2686ea1334c24565777752a53382af13ed15ea8f0b00bc0ba43d74e4257bb279b53ea929a0991bae488dbd552555622badde15ef79b57bf",
        sha512.addMapStringString(abc).asChecksum().toString());
  }

  @Test
  public void testAddPath() {
    final Path pp = sha512.getRelativeRoot().get().toAbsolutePath();
    assertEquals(pp, relativeRoot.get().toAbsolutePath());
    final Optional<Path> p = Optional.of(relativeRoot.get().resolve("generated-sources"));
    assertEquals(relRootCheck.toString(), sha512.addPath(p).asChecksum().toString());

    assertNotEquals(relRootCheck.toString(), ChecksumBuilder.newInstance().addPath(p).asChecksum().toString());

  }

  @Test
  public void testAddSetString() {
    final HashSet<String> s = new HashSet<>(Arrays.asList("ABC", "123", "U&Me"));
    final HashSet<String> y = new HashSet<>(Arrays.asList("123", "ABC", "U&Me"));
    final String val = "08ca7b85befcb60f61b19eb54b588faefe231e68e6a52d8008766d507b446aeb38013b54a5ec925a6867d42618cc1c847aa9b88d90f13ae6948e624af27be100";
    assertEquals(val, sha512.addSetString(Optional.of(s)).asChecksum().toString());
    sha512 = ChecksumBuilder.newInstance();
    assertEquals(val, sha512.addSetString(Optional.of(y)).asChecksum().toString());
  }

  @Test
  public void testAddString() {
    assertEquals(abc, sha512.addString(Optional.of("ABC")).asChecksum());
  }

  @Test
  public void testAddThrowable() {
    final RuntimeException throwable = new RuntimeException("Message");
    assertEquals(
        "922306ef7c958a2f7e3e5a22ef2a634312551e1d66d1589379053b83d17d0edf6ad703040f3fb0dd55eae0db933ca93d265a47c0d08e2f89e4defca0a6aed5e1",
        sha512.addThrowable(Optional.of(throwable)).asChecksum().toString());
  }

  @Test(expected = IBException.class)
  public void testLock() {
    final Checksum v = sha512.addString(Optional.of("ABC")).asChecksum();
    assertEquals(abc, v);
    sha512.addString("ABC");
  }

}
