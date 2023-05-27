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
package org.infrastructurebuilder.util.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
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
import java.util.Set;

import org.infrastructurebuilder.util.constants.IBConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class JSONBuilderTest {

  private JSONBuilder jb;
  private Path target;

  @BeforeEach
  public void setUp() throws Exception {
    jb = JSONBuilder.newInstance();
    target = Paths.get(Optional.ofNullable(System.getProperty("target")).orElse("./target")).toRealPath()
        .toAbsolutePath();

  }

  @Test
  public void testInstantParser() {
    Instant i = Instant.now();

    final JSONObject k = JSONBuilder.newInstance().addInstant("X", i).asJSON();
    assertTrue(JSONBuilder.instantFromJSON.apply(null).isEmpty());
    assertEquals(JSONBuilder.instantFromJSON.apply(k.getString("X")).get(),i);
  }

  @Test
  public void testAddBooleanStringBoolean() {
    final JSONObject j = new JSONObject().put("X", true);
    final JSONObject k = jb.addBoolean("X", Optional.of(true)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddBytes() {
    final JSONObject j = new JSONObject().put("X", "414243");
    final JSONObject k = jb.addBytes("X", "ABC".getBytes(StandardCharsets.UTF_8)).asJSON();
    JSONAssert.assertEquals(j, k, true);

  }

  @Test
  public void testAddChecksum() {
    final JSONObject j = new JSONObject().put("X", "abcd");
    final JSONObject k = jb.addChecksum("X", Optional.of(new Checksum("ABCD"))).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddChecksumEnabledStringChecksumEnabled() {
    final ChecksumEnabled r = new RoseTreeTest.X(new JSONObject());
    final JSONObject j = new JSONObject().put("X", r.asChecksum().toString());
    final JSONObject k = jb.addChecksumEnabled("X", Optional.of(r)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddDoubleStringDouble() {
    final JSONObject j = new JSONObject().put("X", 1.2);
    final JSONObject k = jb.addDouble("X", Optional.of(1.2)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddDurationStringDuration() {
    final Duration dur = Duration.ofHours(1);
    final JSONObject j = new JSONObject().put("X", dur.toString());
    final JSONObject k = jb.addDuration("X", Optional.of(dur)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddFloatStringFloat() {
    final Float f = 1.2F;
    final JSONObject j = new JSONObject().put("X", f);

    final JSONObject k = jb.addFloat("X", Optional.of(f)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddInstantStringInstant() {
    final Instant now = Instant.now();
    final JSONObject j = new JSONObject().put("X", now.toString());
    final JSONObject k = jb.addInstant("X", Optional.of(now)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddIntegerStringInteger() {
    final JSONObject j = new JSONObject().put("X", 1);
    final JSONObject k = jb.addInteger("X", Optional.of(1)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddJSONArray() {
    final JSONArray r = new JSONArray(Arrays.asList(new JSONObject().put("A", "B")));
    final JSONObject k = jb.addJSONArray("X", Optional.of(r)).asJSON();
    final JSONObject j = new JSONObject("{\"X\": [{\"A\": \"B\"}]}");
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddJSONOutputEnabled() {
    final JSONOutputEnabled r = new RoseTreeTest.X(new JSONObject().put("A", "B"));
    final JSONObject k = jb.addJSONOutputEnabled("X", Optional.of(r)).asJSON();
    final JSONObject j = new JSONObject("{\"X\": {\"A\": \"B\"}}");
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddListJSONOutputEnabled() {
    final JSONOutputEnabled r = new RoseTreeTest.X(new JSONObject().put("A", "B"));
    final List<JSONOutputEnabled> l = Arrays.asList(r, r, r);
    final String xx = "{\"X\": [\n" + "  {\"A\": \"B\"},\n" + "  {\"A\": \"B\"},\n" + "  {\"A\": \"B\"}\n" + "]}";
    final JSONObject j = new JSONObject(xx);
    final JSONObject k = jb.addListJSONOutputEnabled("X", l).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddListStringStringListOfString() {
    final List<String> l = Arrays.asList("A", "B", "C");
    final JSONObject j = new JSONObject().put("X", l);
    final JSONObject k = jb.addListString("X", Optional.of(l)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddLongStringLong() {
    final JSONObject j = new JSONObject().put("X", "ABCD");
    final JSONObject k = jb.addString("X", Optional.of("ABCD")).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddLongStringOptionalOfLong() {
    final JSONObject j = new JSONObject().put("X", 1L);
    final JSONObject k = jb.addLong("X", Optional.of(1L)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddMapStringJSONOutputEnabled() {
    final JSONOutputEnabled r = new RoseTreeTest.X(new JSONObject().put("A", "B"));
    final List<JSONOutputEnabled> l = Arrays.asList(r, r, r);
    final Map<String, JSONOutputEnabled> map = new HashMap<>();
    final Map<String, List<JSONOutputEnabled>> y = new HashMap<>();
    y.put("X", l);
    map.put("X", r);
    final String xx = "{\"X\": {\"X\": {\"A\": \"B\"}}}";
    final JSONObject j = new JSONObject(xx);
    final JSONObject k = jb.addMapStringJSONOutputEnabled("X", map).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddMapStringMapStringListJSONOutputEnabled() {
    final JSONOutputEnabled r = new RoseTreeTest.X(new JSONObject().put("A", "B"));
    final List<JSONOutputEnabled> l = Arrays.asList(r, r, r);
    final Map<String, Map<String, List<JSONOutputEnabled>>> map = new HashMap<>();
    final Map<String, List<JSONOutputEnabled>> y = new HashMap<>();
    y.put("X", l);
    map.put("X", y);
    final String xx = "{\"X\": {\"X\": {\"X\": [\n" + "  {\"A\": \"B\"},\n" + "  {\"A\": \"B\"},\n"
        + "  {\"A\": \"B\"}\n" + "]}}}";
    final JSONObject j = new JSONObject(xx);
    final JSONObject k = jb.addMapStringMapStringListJSONOutputEnabled("X", map).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddMapStringStringStringOptionalOfMapOfStringString() {
    final Map<String, String> l = new HashMap<>();
    l.put("A", "B");
    l.put("C", "D");
    final JSONObject j = new JSONObject().put("X", l);
    final JSONObject k = jb.addMapStringString("X", Optional.of(l)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddOn() {
    final JSONBuilder jb2 = JSONBuilder.addOn(jb.addString("A", "A").asJSON(), Optional.empty());
    JSONAssert.assertEquals(new JSONObject().put("A", "A").put("B", "B"), jb2.addString("B", "B").asJSON(), true);
  }

  @Test
  public void testAddPath() {
    final String root = FileSystems.getDefault().getRootDirectories().iterator().next().toAbsolutePath().toUri()
        .getPath().toString();
    final JSONObject j = new JSONObject("{\"X\": \"" + root + "\"}");
    Paths.get("/");
    final JSONObject k = jb.addAbsolutePath("X", Optional.of(Paths.get("/"))).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddPAth() {
    final Path l = Paths.get("ABC");
    final JSONObject j = new JSONObject().put("X", l.toString());
    final JSONObject k = jb.addPath("X", Optional.of(l)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddPAth2() {
    jb = JSONBuilder.newInstance(Optional.of(target));
    final Path l = target.resolve("ABC");
    final JSONObject j = new JSONObject().put("X", l);
    final JSONObject j2 = new JSONObject().put("X", target.relativize(l).toString());
    final JSONObject k = jb.addPath("X", Optional.of(l)).asJSON();
    JSONAssert.assertNotEquals(j, k, true);
    JSONAssert.assertEquals(j2, k, true);
  }

  @Test
  public void testAddStringStringString() {
    final JSONObject j = new JSONObject().put("X", "ABCD");
    final JSONObject k = jb.addString("X", Optional.of("ABCD")).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testAddThrowableStringThrowable() {
    final JSONObject j = new JSONObject().put("X",
        new JSONObject()

        .put(IBConstants.MESSAGE, "@")

        .put(IBConstants.CLASS, RuntimeException.class.getCanonicalName()));

    final JSONObject k = jb.addThrowable("X", Optional.of(new RuntimeException("@"))).asJSON();
    var st = k.getJSONObject("X").remove(IBConstants.STACK_TRACE);
    assertNotNull(st);
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testSetString() {
    final Set<String> l = new HashSet<>(Arrays.asList("A", "B", "C"));
    final JSONObject j = new JSONObject().put("X", l);
    final JSONObject k = jb.addSetString("X", Optional.of(l)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

  @Test
  public void testSetString2() {
    final Set<String> l = new HashSet<>(Arrays.asList("C", "B", "A"));
    final JSONObject j = new JSONObject().put("X", l);
    final JSONObject k = jb.addSetString("X", Optional.of(l)).asJSON();
    JSONAssert.assertEquals(j, k, true);
  }

}
