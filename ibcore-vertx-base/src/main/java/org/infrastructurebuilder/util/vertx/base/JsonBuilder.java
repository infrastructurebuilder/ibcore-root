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

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.ChecksumEnabled;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.infrastructurebuilder.util.core.JSONOutputEnabled;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.UUIdentified;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Like JSONBuilder, this is verbose, tedious, slow, and readable. Not fun. Just
 * readable.
 *
 * @author mykel
 *
 */
public final class JsonBuilder implements JsonOutputEnabled {
  private final static Logger log = LoggerFactory.getLogger(JsonBuilder.class);
  public final static Function<Collection<? extends JsonOutputEnabled>, JsonArray> jsonOutputToJsonArray = oe -> {
    return new JsonArray(requireNonNull(oe).stream().map(JsonOutputEnabled::toJson).collect(toList()));
  };

  public final static Function<List<JSONOutputEnabled>, JsonArray> JSONOutputToJsonArray = oe -> {
    return new JsonArray(
        requireNonNull(oe).stream().map(JSONOutputEnabled::asJSON).map(JsonBuilder.orgToVertxObject).collect(toList()));
  };

  public final static Function<JSONObject, JsonObject> orgToVertxObject = oe -> {
    return new JsonObject(requireNonNull(oe).toString()); // inefficient AF
  };
  public final static Function<JSONArray, JsonArray> orgToVertxArray = ja -> {
    return new JsonArray(IBUtils.asStringStream(ja).map(s -> new JsonObject(s)).collect(toList()));
  };
  private static final String RELATIVE_ROOT = "__relative##_root";

  public static JsonBuilder addOn(final JSONObject j, final Optional<RelativeRoot> relativeRoot) {
    return new JsonBuilder(new JsonObject(j.toString()), relativeRoot);
  }

  public static JsonBuilder addOn(final JsonObject j, final Optional<RelativeRoot> relativeRoot) {
    return new JsonBuilder(j, relativeRoot);
  }

  public static JsonBuilder newInstance() {
    return JsonBuilder.newInstance(Optional.empty());
  }

  public static JsonBuilder newInstance(final Optional<RelativeRoot> relativeRoot) {
    return new JsonBuilder(relativeRoot);
  }

  public static JsonBuilder newInstance(final JsonObject j, final Optional<RelativeRoot> relativeRoot) {
    return new JsonBuilder(j, relativeRoot);
  }

  private final JsonObject json;

  private final Optional<RelativeRoot> relativeRoot;

  @Override
  public Optional<RelativeRoot> getJsonRelativeRoot() {
    return this.relativeRoot;
  }

  public JsonBuilder(final JsonObject j, final Optional<RelativeRoot> relativeRoot) {
    json = new JsonObject(requireNonNull(j).encode());
    RelativeRoot rr = null;
    if (j.containsKey(RELATIVE_ROOT)) {
      json.remove(RELATIVE_ROOT);
      rr = RelativeRoot.from(j.getString(RELATIVE_ROOT));
    }
    rr = requireNonNull(relativeRoot).orElse(rr);  // Overrides existing RR if supplied
    this.relativeRoot = Optional.ofNullable(rr);
  }

  public JsonBuilder(final Optional<RelativeRoot> root) {
    this(new JsonObject(), root);
  }

  public JsonBuilder addAbsolutePath(final String key, final Optional<Path> s) {
    return requireNonNull(s).map(t -> this.addAbsolutePath(key, t)).orElse(this);
  }

  public JsonBuilder addAbsolutePath(final String key, final Path s) {
    return this.addString(requireNonNull(key), requireNonNull(s).toAbsolutePath().toUri().getPath());
  }

  public JsonBuilder addBoolean(final String key, final Boolean s) {

    json.put(key, requireNonNull(s).booleanValue());
    return this;
  }

  public JsonBuilder addBoolean(final String key, final Optional<Boolean> s) {

    requireNonNull(s).ifPresent(s1 -> this.addBoolean(key, s1));
    return this;
  }

  public JsonBuilder addBytes(final String key, final byte[] b) {
    json.put(requireNonNull(key), IBUtils.getHex(b));
    return this;
  }

  public JsonBuilder addChecksum(final String key, final Checksum s) {
    return this.addString(key, requireNonNull(s).toString());
  }

  public JsonBuilder addChecksum(final String key, final Optional<Checksum> s) {
    requireNonNull(s).ifPresent(s1 -> this.addString(key, requireNonNull(s1).toString()));
    return this;
  }

  public JsonBuilder addChecksumEnabled(final String key, final ChecksumEnabled s) {

    addBytes(key, requireNonNull(s).asChecksum().getDigest());
    return this;
  }

  public JsonBuilder addChecksumEnabled(final String key, final Optional<ChecksumEnabled> s) {

    requireNonNull(s).ifPresent(s1 -> this.addChecksumEnabled(key, s1));
    return this;
  }

  public JsonBuilder addDouble(final String key, final Double s) {
    json.put(key, requireNonNull(s).doubleValue());
    return this;
  }

  public JsonBuilder addDouble(final String key, final Optional<Double> s) {

    requireNonNull(s).ifPresent(s1 -> this.addDouble(key, s1));
    return this;
  }

  public JsonBuilder addDuration(final String key, final Duration s) {

    json.put(key, requireNonNull(s).toString());
    return this;
  }

  public JsonBuilder addDuration(final String key, final Optional<Duration> s) {

    requireNonNull(s).ifPresent(s1 -> this.addDuration(key, s1));
    return this;
  }

  public JsonBuilder addFloat(final String key, final Float s) {

    json.put(key, requireNonNull(s).floatValue());
    return this;
  }

  public JsonBuilder addFloat(final String key, final Optional<Float> s) {

    requireNonNull(s).ifPresent(s1 -> this.addFloat(key, s1));
    return this;
  }

  public JsonBuilder addInstant(final String key, final Instant s) {

    json.put(key, requireNonNull(s).toString());
    return this;
  }

  public JsonBuilder addInstant(final String key, final Optional<Instant> s) {

    requireNonNull(s).ifPresent(s1 -> this.addInstant(key, s1));
    return this;
  }

  public JsonBuilder addInteger(final String key, final Integer s) {
    json.put(key, requireNonNull(s).intValue());
    return this;
  }

  public JsonBuilder addInteger(final String key, final Optional<Integer> s) {

    requireNonNull(s).ifPresent(s1 -> this.addInteger(key, s1));
    return this;
  }

  public JsonBuilder addJSONArray(final String key, final JSONArray j) {
    json.put(requireNonNull(key), orgToVertxArray.apply(requireNonNull(j)));
    return this;
  }

  public JsonBuilder addJsonArray(final String key, final JsonArray j) {
    json.put(requireNonNull(key), j);
    return this;
  }

  public JsonBuilder addJSONArray(final String key, final Optional<JSONArray> j) {
    return requireNonNull(j).map(s1 -> this.addJSONArray(key, s1)).orElse(this);
  }

  public JsonBuilder addJsonArray(final String key, final Optional<JsonArray> j) {
    return requireNonNull(j).map(s1 -> this.addJsonArray(key, s1)).orElse(this);
  }

  public JsonBuilder addJSONObject(final String key, final JSONObject j) {
    return this.addJsonObject(key, orgToVertxObject.apply(j));
  }

  public JsonBuilder addJsonObject(final String key, final Optional<JsonObject> j) {
    requireNonNull(j).ifPresent(json -> this.addJsonObject(key, json));
    return this;
  }

  public JsonBuilder addJsonObject(final String key, final JsonObject j) {
    json.put(requireNonNull(key), j);
    return this;
  }

  public JsonBuilder addJSONOutputEnabled(final String key, final JSONOutputEnabled j) {
    return addJsonObject(key, orgToVertxObject.apply(j.asJSON()));
  }

  public JsonBuilder addJsonOutputEnabled(final String key, final JsonOutputEnabled j) {
    return addJsonObject(key, j.toJson());
  }

  public JsonBuilder addJSONOutputEnabled(final String key, final Optional<? extends JSONOutputEnabled> j) {
    requireNonNull(j).ifPresent(json -> this.addJSONOutputEnabled(key, json));
    return this;
  }

  public JsonBuilder addJsonOutputEnabled(final String key, final Optional<? extends JsonOutputEnabled> j) {
    requireNonNull(j).ifPresent(json -> this.addJsonOutputEnabled(key, json));
    return this;
  }

  public JsonBuilder addListJSONOutputEnabled(final String key, final List<JSONOutputEnabled> value) {
    return this.addJsonArray(key, JSONOutputToJsonArray.apply(value));
  }

  public JsonBuilder addListJsonOutputEnabled(final String key, final Collection<? extends JsonOutputEnabled> value) {
    return this.addJsonArray(key, jsonOutputToJsonArray.apply(value));
  }

  public JsonBuilder addListString(final String key, final List<String> s) {
    json.put(requireNonNull(key), new JsonArray(s));
    return this;
  }

  public JsonBuilder addListString(final String key, final Optional<List<String>> s) {
    requireNonNull(s).ifPresent(s1 -> this.addListString(key, s1));
    return this;
  }

  public JsonBuilder addLong(final String key, final Long s) {
    json.put(requireNonNull(key), requireNonNull(s).longValue());
    return this;
  }

  public JsonBuilder addLong(final String key, final Optional<Long> s) {

    requireNonNull(s).ifPresent(s1 -> this.addLong(key, s1));
    return this;
  }

  public JsonBuilder addMapStringJSONOutputEnabled(final String key, final Map<String, JSONOutputEnabled> map) {
    return addJsonObject(key, new JsonObject(requireNonNull(map).entrySet().stream()
        .collect(toMap(k -> k.getKey(), v -> orgToVertxObject.apply(v.getValue().asJSON())))));
  }

  public JsonBuilder addMapStringJsonOutputEnabled(final String key, final Map<String, JsonOutputEnabled> map) {
    return addJsonObject(key, new JsonObject(
        requireNonNull(map).entrySet().stream().collect(toMap(k -> k.getKey(), v -> v.getValue().toJson()))));
  }

  public JsonBuilder addMapStringListJSONOutputEnabled(final String key,
      final Map<String, List<JSONOutputEnabled>> map) {
    return addJsonObject(key,

        new JsonObject(requireNonNull(map).entrySet().stream().collect(toMap(k -> k.getKey(),
            v -> orgToVertxArray.apply(JSONBuilder.jsonOutputToJSONArray.apply(v.getValue()))))));
  }

  public JsonBuilder addMapStringListJsonOutputEnabled(final String key,
      final Map<String, List<JsonOutputEnabled>> map) {
    return addJsonObject(key,

        new JsonObject(requireNonNull(map).entrySet().stream()
            .collect(toMap(k -> k.getKey(), v -> jsonOutputToJsonArray.apply(v.getValue())))));
  }

  public JsonBuilder addMapStringMapStringListJSONOutputEnabled(final String key,
      final Map<String, Map<String, List<JSONOutputEnabled>>> map) {
    final JsonBuilder j1 = JsonBuilder.newInstance();
    for (final Entry<String, Map<String, List<JSONOutputEnabled>>> builders : requireNonNull(map).entrySet()) {
      j1.addMapStringListJSONOutputEnabled(builders.getKey(), builders.getValue());
    }
    return addJsonObject(key, j1.toJson());
  }

  public JsonBuilder addMapStringMapStringListJsonOutputEnabled(final String key,
      final Map<String, Map<String, List<JsonOutputEnabled>>> map) {
    final JsonBuilder j1 = JsonBuilder.newInstance();
    for (final Entry<String, Map<String, List<JsonOutputEnabled>>> builders : requireNonNull(map).entrySet()) {
      j1.addMapStringListJsonOutputEnabled(builders.getKey(), builders.getValue());
    }
    return addJsonObject(key, j1.toJson());
  }

  public JsonBuilder addMapStringString(final String key, final Map<String, String> map) {
    return addJsonObject(key,
        new JsonObject(requireNonNull(map).entrySet().stream().collect(toMap(k -> k.getKey(), identity()))));
  }

  public JsonBuilder addMapStringString(final String key, final Optional<Map<String, String>> map) {
    requireNonNull(map).ifPresent(s1 -> this.addMapStringString(key, s1));
    return this;
  }

  public JsonBuilder addProperties(final String key, final Properties properties) {
    return addMapStringString(key, IBUtils.propertiesToMapSS.apply(requireNonNull(properties)));
  }

  public JsonBuilder addProperties(final String key, final Optional<Properties> properties) {
    requireNonNull(properties).ifPresent(s1 -> this.addProperties(key, s1));
    return this;
  }

  public JsonBuilder addPath(final String key, final Optional<Path> s) {
    return requireNonNull(s).map(t -> this.addPath(key, t)).orElse(this);
  }

  public JsonBuilder addPath(final String key, final Path s) {
    requireNonNull(s);
    json.put(key,
        ((!s.isAbsolute()) ? s : relativeRoot.flatMap(rr -> rr.relativize(requireNonNull(s))).orElse(s)).toString());
    return this;
  }

  public JsonBuilder addURLString(final String key, final Optional<String> s) {
    requireNonNull(s).ifPresent(s1 -> this.addURLString(key, s1));
    return this;
  }

  public JsonBuilder addURLString(final String key, final String s) {
    requireNonNull(s);
    json.put(key, relativeRoot.map(rr -> rr.relativize(requireNonNull(s))));
    return this;
  }

  public JsonBuilder addURL(final String key, final Optional<URL> s) {
    requireNonNull(s).ifPresent(s1 -> this.addURL(key, s1));
    return this;
  }

  public JsonBuilder addURL(final String key, final URL s) {
    return addURLString(key, requireNonNull(s).toExternalForm());
  }

  public JsonBuilder addSetString(final String key, final Optional<Set<String>> s) {
    requireNonNull(s).ifPresent(s1 -> this.addSetString(key, s1));
    return this;
  }

  public JsonBuilder addSetString(final String key, final Set<String> s) {
    json.put(requireNonNull(key), new JsonArray(Objects.requireNonNull(s).stream().collect(toList())));
    return this;
  }

  public JsonBuilder addString(final String key, final Optional<String> s) {
    requireNonNull(s).ifPresent(s1 -> this.addString(key, s1));
    return this;
  }

  public JsonBuilder addUUID(final String key, final UUID s) {
    json.put(requireNonNull(key), s.toString());
    return this;
  }

  public JsonBuilder addUUID(final String key, final Optional<UUID> s) {
    requireNonNull(s).ifPresent(s1 -> this.addUUID(key, s1));
    return this;
  }

  public JsonBuilder addCollectionUUID(final String key, final Collection<UUID> s) {
    return this.addListString(key, s.stream().map(UUID::toString).collect(toList()));
  }

  public JsonBuilder addCollectionUUID(final String key, final Optional<Collection<UUID>> s) {
    requireNonNull(s).ifPresent(s1 -> this.addCollectionUUID(key, s1)); // Careful. Breaks fluent
    return this;
  }

  public JsonBuilder addUUIDIdentified(final String key, final UUIdentified s) {
    json.put(requireNonNull(key), s.getId().toString());
    return this;
  }

  public JsonBuilder addUUIDIdentified(final String key, final Optional<UUIdentified> s) {
    requireNonNull(s).ifPresent(s1 -> this.addString(key, s1.getId().toString()));
    return this;
  }

  public JsonBuilder addCollectionUUIdentified(final String key, final Collection<? extends UUIdentified> s) {
    return this.addListString(key, s.stream().map(s1 -> s1.getId().toString()).collect(toList()));
  }

  public JsonBuilder addCollectionUUIdentified(final String key, final Optional<Collection<? extends UUIdentified>> s) {
    requireNonNull(s).ifPresent(s1 -> this.addCollectionUUIdentified(key, s1)); // Careful. Breaks fluent
    return this;
  }

  public JsonBuilder addString(final String key, final String s) {
    json.put(requireNonNull(key), s);
    return this;
  }

  public JsonBuilder addThrowable(final String key, final Optional<Throwable> s) {

    requireNonNull(s).ifPresent(s1 -> this.addThrowable(key, s1));
    return this;
  }

  public JsonBuilder addThrowable(final String key, final Throwable t) {
    return this.addJsonOutputEnabled(key, new ThrowableJsonObject(t));
  }

  @Override
  public JsonObject toJson() {
    if (json.containsKey(RELATIVE_ROOT))
      log.warn("Already contains a {} key",RELATIVE_ROOT);
    this.relativeRoot.ifPresent(rr -> json.put(RELATIVE_ROOT, rr.toString())); // FIXME Maybe not?
    return new JsonObject(json.toString());// , json.fieldNames()); // TODO
  }

}
