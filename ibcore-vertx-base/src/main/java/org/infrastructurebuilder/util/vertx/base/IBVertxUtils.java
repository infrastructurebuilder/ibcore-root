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
package org.infrastructurebuilder.util.vertx.base;

import static java.time.Instant.ofEpochSecond;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.INSTANT;
import static org.infrastructurebuilder.util.core.ChecksumEnabled.CHECKSUM;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.Timestamped;
import org.infrastructurebuilder.util.core.UUIdentified;
import org.infrastructurebuilder.util.core.UUIdentifiedAndTimestamped;
import org.infrastructurebuilder.util.core.UUIdentifiedAndWeighted;
import org.json.JSONObject;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class IBVertxUtils {

  @SuppressWarnings("unchecked")
  public static <T> Iterator<T> asIterator(final JsonArray array) {
    final List<T> l = new ArrayList<>();
    for (int i = 0; i < array.size(); ++i) {
      l.add((T) array.getList().get(i));
    }
    return l.iterator();
  }

  public final Function<JSONObject, JsonObject> toJson = (j) -> {
    return new JsonObject(requireNonNull(j).toString());
  };

  /**
   * This is here because UUIdentified has no JsonObject from Vert.x, only
   * JSONObject from org.json
   *
   * @param u
   * @return
   */
  public static JsonBuilder uuidentifiedJsonBuilder(UUIdentified u) {
    return JsonBuilder.newInstance().addString(UUIdentified.ID, u.getId().toString());
  }

  /**
   * This is here because UUIdentified has no JsonObject from Vert.x, only
   * JSONObject from org.json
   *
   * @param u
   * @return
   */
  public static JsonBuilder uuidentifiedJsonBuilder(UUIdentified u, RelativeRoot p) {
    return JsonBuilder.newInstance(ofNullable(p)).addString(UUIdentified.ID, u.getId().toString());
  }

  /**
   * This is here because UUIdentifiedAndTimestamped has no JsonObject from
   * Vert.x, only JSONObject from org.json
   *
   * @param u
   * @return
   */
  public static JsonBuilder uuidentifiedAndTimestampedJsonBuilder(UUIdentifiedAndTimestamped u) {
    return uuidentifiedJsonBuilder(u).addInstant(Timestamped.TIMESTAMP, u.getTimestamp());
  }

  /**
   * This is here because UUIdentifiedAndTimestamped has no JsonObject from
   * Vert.x, only JSONObject from org.json
   *
   * @param u
   * @return
   */
  public static JsonBuilder uuidentifiedAndWeightedJsonBuilder(UUIdentifiedAndWeighted u) {
    return uuidentifiedJsonBuilder(u).addInteger(UUIdentifiedAndWeighted.WEIGHT, u.getWeight());
  }

  /**
   * This is a VERY SLOOW computing a checksum off of a VERY SIMPLE json object.
   * Specifically, it handles the various types of json (recursively and slowly)
   * as sorted maps or ordered lists of values.
   *
   * @param json
   * @return
   */
  public final static Checksum getChecksumFromJsonObject(JsonObject json) {
    if (json == null)
      return new Checksum();

    return Checksum.fromUTF8StringBytes(getStringFromJsonObject(json));
  }

  /**
   * Returns a string representation that is consistent over time using sorted
   * keys THIS IS EXPENSIVE and RECURSIVE and DUMB but required to obtain
   * checksums for json
   *
   * @param json
   * @return A determinsitically acquired string based on recursive json mapping
   */
  public final static String getStringFromJsonObject(JsonObject json) {
    StringBuilder sb = new StringBuilder();
    json.getMap().keySet().stream().sorted().forEach(k -> sb.append(k + "=" + mapVal(json.getValue(k)) + "\n"));
    return sb.toString();

  }

  private static String getStringFromJsonArray(JsonArray v) {
    // Generally unpredictable but not for our purposes here.
    @SuppressWarnings("unchecked")
    Stream<String> m = v.getList().stream().map(v2 -> mapVal(v2).toString());
    return m.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
  }

  private static String mapVal(Object v) {
    if (v instanceof JsonObject)
      return getStringFromJsonObject((JsonObject) v);
    else if (v instanceof JsonArray)
      return getStringFromJsonArray((JsonArray) v);
    else
      return v.toString();
  }

  // Dangerous
  public static UUID getUUID(JsonObject j, String originator) {
    return UUID.fromString(j.getString(originator));
  }

  public final static List<String> getListStringFromJsonArray(JsonArray j) {
    List<String> l = new ArrayList<>();
    asIterator(j).forEachRemaining(o -> l.add(mapVal(o)));
    return l;
  }

  public final static Checksum deserializeChecksum(JsonObject j) {
    return new Checksum(j.getString(CHECKSUM));
  }

  public final static JsonObject serializeChecksum(Checksum c) {
    return new JsonObject().put(CHECKSUM, c.toString());
  }

  public final static Instant deserializeInstant(JsonObject j) {
    return ofEpochSecond(j.getLong(INSTANT));
  }

  public final static JsonObject serializeInstant(Instant i) {
    return new JsonObject().put(INSTANT, requireNonNull(i).getEpochSecond());
  }

  public static VertxGAV readGAVFromServiceFile(Class<?> class1) {
    return new DefaultVertxGAV((String)null);
  }

}
