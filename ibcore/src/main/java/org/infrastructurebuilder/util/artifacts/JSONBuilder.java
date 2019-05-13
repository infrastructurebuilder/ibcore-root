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

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.IBUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public final class JSONBuilder implements JSONOutputEnabled {

  public final static Function<List<JSONOutputEnabled>, JSONArray> jsonOutputToJSONArray = oe -> {
    return new JSONArray(
        Objects.requireNonNull(oe).stream().map(JSONOutputEnabled::asJSON).collect(Collectors.toList()));
  };

  public static JSONBuilder addOn(final JSONObject j, final Optional<Path> relativeRoot) {
    return new JSONBuilder(j, relativeRoot);
  }

  public static JSONBuilder newInstance() {
    return JSONBuilder.newInstance(Optional.empty());
  }

  public static JSONBuilder newInstance(final Optional<Path> relativeRoot) {
    return new JSONBuilder(relativeRoot);
  }

  private final JSONObject json;

  private final Optional<Path> relativeRoot;

  public JSONBuilder(final JSONObject j, final Optional<Path> relativeRoot) {
    json = Objects.requireNonNull(j);
    this.relativeRoot = Objects.requireNonNull(relativeRoot);
  }

  public JSONBuilder(final Optional<Path> root) {
    json = new JSONObject();
    relativeRoot = Objects.requireNonNull(root);
  }

  public JSONBuilder addAbsolutePath(final String key, final Optional<Path> s) {
    return Objects.requireNonNull(s).map(t -> this.addAbsolutePath(key, t)).orElse(this);
  }

  public JSONBuilder addAbsolutePath(final String key, final Path s) {
    return this.addString(Objects.requireNonNull(key), Objects.requireNonNull(s).toAbsolutePath().toUri().getPath());
  }

  public JSONBuilder addBoolean(final String key, final Boolean s) {

    json.put(key, Objects.requireNonNull(s).booleanValue());
    return this;
  }

  public JSONBuilder addBoolean(final String key, final Optional<Boolean> s) {

    Objects.requireNonNull(s).ifPresent(s1 -> this.addBoolean(key, s1));
    return this;
  }

  public JSONBuilder addBytes(final String key, final byte[] b) {
    json.put(Objects.requireNonNull(key), IBUtils.getHex(b));
    return this;
  }

  public JSONBuilder addChecksum(final String key, final Checksum s) {
    return this.addString(key, Objects.requireNonNull(s).toString());
  }

  public JSONBuilder addChecksum(final String key, final Optional<Checksum> s) {
    Objects.requireNonNull(s).ifPresent(s1 -> this.addString(key, Objects.requireNonNull(s1).toString()));
    return this;
  }

  public JSONBuilder addChecksumEnabled(final String key, final ChecksumEnabled s) {

    addBytes(key, Objects.requireNonNull(s).asChecksum().getDigest());
    return this;
  }

  public JSONBuilder addChecksumEnabled(final String key, final Optional<ChecksumEnabled> s) {

    Objects.requireNonNull(s).ifPresent(s1 -> this.addChecksumEnabled(key, s1));
    return this;
  }

  public JSONBuilder addDouble(final String key, final Double s) {
    json.put(key, Objects.requireNonNull(s).doubleValue());
    return this;
  }

  public JSONBuilder addDouble(final String key, final Optional<Double> s) {

    Objects.requireNonNull(s).ifPresent(s1 -> this.addDouble(key, s1));
    return this;
  }

  public JSONBuilder addDuration(final String key, final Duration s) {

    json.put(key, Objects.requireNonNull(s).toString());
    return this;
  }

  public JSONBuilder addDuration(final String key, final Optional<Duration> s) {

    Objects.requireNonNull(s).ifPresent(s1 -> this.addDuration(key, s1));
    return this;
  }

  public JSONBuilder addFloat(final String key, final Float s) {

    json.put(key, Objects.requireNonNull(s).floatValue());
    return this;
  }

  public JSONBuilder addFloat(final String key, final Optional<Float> s) {

    Objects.requireNonNull(s).ifPresent(s1 -> this.addFloat(key, s1));
    return this;
  }

  public JSONBuilder addInstant(final String key, final Instant s) {

    json.put(key, Objects.requireNonNull(s).toString());
    return this;
  }

  public JSONBuilder addInstant(final String key, final Optional<Instant> s) {

    Objects.requireNonNull(s).ifPresent(s1 -> this.addInstant(key, s1));
    return this;
  }

  public JSONBuilder addInteger(final String key, final Integer s) {
    json.put(key, Objects.requireNonNull(s).intValue());
    return this;
  }

  public JSONBuilder addInteger(final String key, final Optional<Integer> s) {

    Objects.requireNonNull(s).ifPresent(s1 -> this.addInteger(key, s1));
    return this;
  }

  public JSONBuilder addJSONArray(final String key, final JSONArray j) {
    json.put(Objects.requireNonNull(key), j);
    return this;
  }

  public JSONBuilder addJSONArray(final String key, final Optional<JSONArray> j) {
    return Objects.requireNonNull(j).map(s1 -> this.addJSONArray(key, s1)).orElse(this);
  }

  public JSONBuilder addJSONObject(final String key, final JSONObject j) {
    json.put(Objects.requireNonNull(key), j);
    return this;
  }

  public JSONBuilder addJSONOutputEnabled(final String key, final JSONOutputEnabled j) {
    return addJSONObject(key, j.asJSON());
  }

  public JSONBuilder addJSONOutputEnabled(final String key, final Optional<? extends JSONOutputEnabled> j) {
    Objects.requireNonNull(j).ifPresent(json -> this.addJSONOutputEnabled(key, json));
    return this;
  }

  public JSONBuilder addListJSONOutputEnabled(final String key, final List<JSONOutputEnabled> value) {
    return this.addJSONArray(key, jsonOutputToJSONArray.apply(value));
  }

  public JSONBuilder addListString(final String key, final List<String> s) {
    json.put(Objects.requireNonNull(key), new JSONArray(s));
    return this;
  }

  public JSONBuilder addListString(final String key, final Optional<List<String>> s) {
    Objects.requireNonNull(s).ifPresent(s1 -> this.addListString(key, s1));
    return this;
  }

  public JSONBuilder addLong(final String key, final Long s) {
    json.put(Objects.requireNonNull(key), Objects.requireNonNull(s).longValue());
    return this;
  }

  public JSONBuilder addLong(final String key, final Optional<Long> s) {

    Objects.requireNonNull(s).ifPresent(s1 -> this.addLong(key, s1));
    return this;
  }

  public JSONBuilder addMapStringJSONOutputEnabled(final String key, final Map<String, JSONOutputEnabled> map) {
    return addJSONObject(key, new JSONObject(Objects.requireNonNull(map).entrySet().stream()
        .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().asJSON()))));
  }

  public JSONBuilder addMapStringListJSONOutputEnabled(final String key,
      final Map<String, List<JSONOutputEnabled>> map) {
    return addJSONObject(key,

        new JSONObject(Objects.requireNonNull(map).entrySet().stream()
            .collect(Collectors.toMap(k -> k.getKey(), v -> jsonOutputToJSONArray.apply(v.getValue())))));
  }

  public JSONBuilder addMapStringMapStringListJSONOutputEnabled(final String key,
      final Map<String, Map<String, List<JSONOutputEnabled>>> map) {
    final JSONBuilder j1 = JSONBuilder.newInstance();
    for (final Entry<String, Map<String, List<JSONOutputEnabled>>> builders : Objects.requireNonNull(map).entrySet()) {
      j1.addMapStringListJSONOutputEnabled(builders.getKey(), builders.getValue());
    }
    return addJSONObject(key, j1.asJSON());
  }

  public JSONBuilder addMapStringString(final String key, final Map<String, String> map) {
    addJSONObject(key, new JSONObject(Objects.requireNonNull(map)));
    return this;
  }

  public JSONBuilder addMapStringString(final String key, final Optional<Map<String, String>> map) {
    Objects.requireNonNull(map).ifPresent(s1 -> this.addMapStringString(key, s1));
    return this;
  }

  public JSONBuilder addPath(final String key, final Optional<Path> s) {
    return Objects.requireNonNull(s).map(t -> this.addPath(key, t)).orElse(this);
  }

  public JSONBuilder addPath(final String key, final Path s) {
    Objects.requireNonNull(s);
    json.put(key, relativeRoot.map(rr -> rr.relativize(s)).orElse(s).toString());
    return this;
  }

  public JSONBuilder addSetString(final String key, final Optional<Set<String>> s) {
    Objects.requireNonNull(s).ifPresent(s1 -> this.addSetString(key, s1));
    return this;
  }

  public JSONBuilder addSetString(final String key, final Set<String> s) {
    json.put(Objects.requireNonNull(key), new JSONArray(s));
    return this;
  }

  public JSONBuilder addString(final String key, final Optional<String> s) {
    Objects.requireNonNull(s).ifPresent(s1 -> this.addString(key, s1));
    return this;
  }

  public JSONBuilder addString(final String key, final String s) {
    json.put(Objects.requireNonNull(key), s);
    return this;
  }

  public JSONBuilder addThrowable(final String key, final Optional<Throwable> s) {

    Objects.requireNonNull(s).ifPresent(s1 -> this.addThrowable(key, s1));
    return this;
  }

  public JSONBuilder addThrowable(final String key, final Throwable t) {
    JSONObject j2;
    j2 = new JSONObject().put("class",
        Optional.ofNullable(Objects.requireNonNull(t).getClass().getCanonicalName()).orElse("unknown throwable class"));
    Optional.ofNullable(t.getMessage()).ifPresent(m -> j2.put("message", m));
    json.put(Objects.requireNonNull(key), j2);
    return this;
  }

  @Override
  public JSONObject asJSON() {
    return new JSONObject(json, JSONObject.getNames(json));
  }

}
