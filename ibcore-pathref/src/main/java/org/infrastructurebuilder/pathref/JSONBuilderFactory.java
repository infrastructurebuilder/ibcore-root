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

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.infrastructurebuilder.exceptions.IBException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JSONBuilderFactory {
  public static final String TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'";
  private final static Logger log = LoggerFactory.getLogger(JSONBuilderFactory.class);
  private final static AtomicReference<DateTimeFormatter> dts = new AtomicReference<>();

  public final static Function<List<JSONOutputEnabled>, JSONArray> jsonOutputToJSONArray = oe -> {
    return new JSONArray(requireNonNull(oe).stream().map(JSONOutputEnabled::asJSON).toList());
  };

  public static JSONBuilder addOn(final JSONObject j, final Optional<Path> relativeRoot) {
    return new JSONBuilderImpl(relativeRoot.map(AbsolutePathRef::new), j);
  }

  public static JSONBuilder newInstance() {
    return new JSONBuilderImpl(Optional.empty());
  }

  public static JSONBuilder newInstance(final Optional<Path> relativeRoot) {
    return newInstanceFromPathRef(relativeRoot.map(AbsolutePathRef::new));
  }

  public static JSONBuilder newInstanceFromPathRef(final Optional<PathRef> relativeRoot) {
    return new JSONBuilderImpl(relativeRoot);
  }

  public static Function<String, Optional<Instant>> instantFromJSON = s -> {
    return Optional.ofNullable(s).map(str -> getDateFormat().parse(str)).map(t -> Instant.from(t)); // mebbe?
  };

  public static DateTimeFormatter getDateFormat() {
    synchronized (dts) {
      if (dts.get() == null) {
        dts.set(DateTimeFormatter.ofPattern(TIMESTAMP).withZone(ZoneId.from(ZoneOffset.UTC)));
      }
    }
    return dts.get();
  }

  private final static class JSONBuilderImpl implements JSONBuilder {
    private final static AtomicReference<DateTimeFormatter> localdts = new AtomicReference<>();

    private final JSONObject json;

    private final PathRef relativeRoot;

    public final static JSONBuilder fromPathRef(final JSONObject j, final Optional<PathRef> relativeRoot) {
      return new JSONBuilderImpl(relativeRoot, j);
    }

    public JSONBuilderImpl(final JSONObject j, final Optional<Path> relativeRoot) {
      this(relativeRoot.map(AbsolutePathRef::new), j);
    }

    public JSONBuilderImpl(final Optional<PathRef> relativeRoot, final JSONObject j) {
      json = requireNonNull(j);
      this.relativeRoot = requireNonNull(relativeRoot).orElse(null);
    }

    public JSONBuilderImpl(final Optional<PathRef> root) {
      json = new JSONObject();
      relativeRoot = requireNonNull(root).orElse(null);
    }

    public JSONBuilder withDateTimeFormat(String format) {
      synchronized (dts) {
        if (!dts.compareAndSet(null, DateTimeFormatter.ofPattern(format).withZone(ZoneId.from(ZoneOffset.UTC)))) {
          // TODO Warn?
          log.warn("Attempted to set datetime format to %s but already set to %s", format, dts.toString());
        }
        return this;
      }
    }

    public static DateTimeFormatter getDateFormat() {
      synchronized (localdts) {
        if (localdts.get() == null) {
          localdts.set(DateTimeFormatter.ofPattern(TIMESTAMP).withZone(ZoneId.from(ZoneOffset.UTC)));
        }
      }
      return localdts.get();
    }

    @Override
    public JSONBuilderImpl addAbsolutePath(final String key, final Optional<Path> s) {
      return requireNonNull(s).map(t -> this.addAbsolutePath(key, t)).orElse(this);
    }

    @Override
    public JSONBuilderImpl addAbsolutePath(final String key, final Path s) {
      return this.addString(requireNonNull(key), requireNonNull(s).toAbsolutePath().toUri().getPath());
    }

    @Override
    public JSONBuilder addBoolean(final String key, final Boolean s) {
      json.put(key, requireNonNull(s).booleanValue());
      return this;
    }

    @Override
    public JSONBuilderImpl addBoolean(final String key, final Optional<Boolean> s) {
      requireNonNull(s).ifPresent(s1 -> this.addBoolean(key, s1));
      return this;
    }

    @Override
    public JSONBuilderImpl addBytes(final String key, final byte[] b) {
      json.put(requireNonNull(key), IBChecksumUtils.getHex(b));
      return this;
    }

    @Override
    public JSONBuilder addChecksum(final String key, final Checksum s) {
      return this.addString(key, requireNonNull(s).toString());
    }

    @Override
    public JSONBuilderImpl addChecksum(final String key, final Optional<Checksum> s) {
      requireNonNull(s).ifPresent(s1 -> this.addString(key, requireNonNull(s1).toString()));
      return this;
    }

    @Override
    public JSONBuilder addChecksumEnabled(final String key, final ChecksumEnabled s) {

      addBytes(key, requireNonNull(s).asChecksum().getDigest());
      return this;
    }

    @Override
    public JSONBuilderImpl addChecksumEnabled(final String key, final Optional<ChecksumEnabled> s) {

      requireNonNull(s).ifPresent(s1 -> this.addChecksumEnabled(key, s1));
      return this;
    }

    @Override
    public JSONBuilder addDouble(final String key, final Double s) {
      json.put(key, requireNonNull(s).doubleValue());
      return this;
    }

    @Override
    public JSONBuilderImpl addDouble(final String key, final Optional<Double> s) {

      requireNonNull(s).ifPresent(s1 -> this.addDouble(key, s1));
      return this;
    }

    @Override
    public JSONBuilderImpl addDuration(final String key, final Duration s) {

      json.put(key, requireNonNull(s).toString());
      return this;
    }

    @Override
    public JSONBuilderImpl addDuration(final String key, final Optional<Duration> s) {

      requireNonNull(s).ifPresent(s1 -> this.addDuration(key, s1));
      return this;
    }

    @Override
    public JSONBuilder addFloat(final String key, final Float s) {

      json.put(key, requireNonNull(s).floatValue());
      return this;
    }

    @Override
    public JSONBuilderImpl addFloat(final String key, final Optional<Float> s) {

      requireNonNull(s).ifPresent(s1 -> this.addFloat(key, s1));
      return this;
    }

    @Override
    public JSONBuilderImpl addInstant(final String key, final Instant s) {
      var string = getDateFormat().format(requireNonNull(s));
      log.info("Adding instant {} as '{}'", s, string);
      json.put(key, string);
      return this;
    }

    @Override
    public JSONBuilderImpl addInstant(final String key, final Optional<Instant> s) {

      requireNonNull(s).ifPresent(s1 -> this.addInstant(key, s1));
      return this;
    }

    @Override
    public JSONBuilderImpl addInteger(final String key, final Integer s) {
      json.put(key, requireNonNull(s).intValue());
      return this;
    }

    @Override
    public JSONBuilderImpl addInteger(final String key, final Optional<Integer> s) {

      requireNonNull(s).ifPresent(s1 -> this.addInteger(key, s1));
      return this;
    }

    @Override
    public JSONBuilderImpl addJSONArray(final String key, final JSONArray j) {
      json.put(requireNonNull(key), j);
      return this;
    }

    @Override
    public JSONBuilderImpl addJSONArray(final String key, final Optional<JSONArray> j) {
      return requireNonNull(j).map(s1 -> this.addJSONArray(key, s1)).orElse(this);
    }

    @Override
    public JSONBuilderImpl addJSONObject(final String key, final JSONObject j) {
      json.put(requireNonNull(key), j);
      return this;
    }

    @Override
    public JSONBuilder addJSONObject(final String key, final Optional<JSONObject> j) {
      requireNonNull(j).ifPresent(json -> this.addJSONObject(key, json));
      return this;
    }

    @Override
    public JSONBuilder addJSONOutputEnabled(final String key, final JSONOutputEnabled j) {
      return addJSONObject(key, j.asJSON());
    }

    @Override
    public JSONBuilderImpl addJSONOutputEnabled(final String key, final Optional<? extends JSONOutputEnabled> j) {
      requireNonNull(j).ifPresent(json -> this.addJSONOutputEnabled(key, json));
      return this;
    }

    @Override
    public JSONBuilderImpl addListJSONOutputEnabled(final String key, final List<JSONOutputEnabled> value) {
      return this.addJSONArray(key, jsonOutputToJSONArray.apply(value));
    }

    @Override
    public JSONBuilder addListString(final String key, final List<String> s) {
      json.put(requireNonNull(key), new JSONArray(s));
      return this;
    }

    @Override
    public JSONBuilderImpl addListString(final String key, final Optional<List<String>> s) {
      requireNonNull(s).ifPresent(s1 -> this.addListString(key, s1));
      return this;
    }

    @Override
    public JSONBuilder addLong(final String key, final Long s) {
      json.put(requireNonNull(key), requireNonNull(s).longValue());
      return this;
    }

    @Override
    public JSONBuilderImpl addLong(final String key, final Optional<Long> s) {

      requireNonNull(s).ifPresent(s1 -> this.addLong(key, s1));
      return this;
    }

    @Override
    public JSONBuilderImpl addMapStringJSONOutputEnabled(final String key, final Map<String, JSONOutputEnabled> map) {
      return addJSONObject(key, new JSONObject(requireNonNull(map).entrySet().stream()
          .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().asJSON()))));
    }

    @Override
    public JSONBuilder addMapStringListJSONOutputEnabled(final String key,
        final Map<String, List<JSONOutputEnabled>> map) {
      return addJSONObject(key,

          new JSONObject(requireNonNull(map).entrySet().stream()
              .collect(Collectors.toMap(k -> k.getKey(), v -> jsonOutputToJSONArray.apply(v.getValue())))));
    }

    @Override
    public JSONBuilderImpl addMapStringMapStringListJSONOutputEnabled(final String key,
        final Map<String, Map<String, List<JSONOutputEnabled>>> map) {
      final JSONBuilderImpl j1 = new JSONBuilderImpl(getRelativePathRef());
      for (final Entry<String, Map<String, List<JSONOutputEnabled>>> builders : requireNonNull(map).entrySet()) {
        j1.addMapStringListJSONOutputEnabled(builders.getKey(), builders.getValue());
      }
      return addJSONObject(key, j1.asJSON());
    }

    @Override
    public JSONBuilder addMapStringString(final String key, final Map<String, String> map) {
      addJSONObject(key, new JSONObject(requireNonNull(map)));
      return this;
    }

    @Override
    public JSONBuilderImpl addMapStringString(final String key, final Optional<Map<String, String>> map) {
      requireNonNull(map).ifPresent(s1 -> this.addMapStringString(key, s1));
      return this;
    }

    @Override
    public JSONBuilder addProperties(final String key, final Properties properties) {
      addJSONObject(key, new JSONObject(requireNonNull(properties)));
      return this;
    }

    @Override
    public JSONBuilder addProperties(final String key, final Optional<Properties> properties) {
      requireNonNull(properties).ifPresent(s1 -> this.addProperties(key, s1));
      return this;
    }

    @Override
    public JSONBuilderImpl addPath(final String key, final Optional<Path> s) {
      return requireNonNull(s).map(t -> this.addPath(key, t)).orElse(this);
    }

    @Override
    public JSONBuilderImpl addPath(final String key, final Path s) {
      if (s == null)
        return addPath(key, Optional.empty()); // Trickery

      if (!s.isAbsolute()) {
        json.put(key, s.toString());
        return this;
      }

      String sVal = s.toString(); // Now a string of an absolute path

      json.put(key, getRelativeRoot().flatMap(rr -> rr.relativize(sVal)).map(Path::toString)
          .orElseThrow(() -> new IBException(String.format("Cannot relativize %s to %s", sVal, this.relativeRoot))));
      return this;
    }

//  public JSONBuilder addPathAsString(final String key, final Optional<String> s) {
//    return requireNonNull(s).map(t -> this.addPathAsString(key, t)).orElse(this);
//  }
//
//  public JSONBuilder addPathAsString(final String key, final String s) {
//    if (s == null)
//      return addPathAsString(key, Optional.empty()); // More trickery
//
//    var b = relativeRoot.map(rr -> {
//      rr.sourceRelativize(s);
//    }).orElse(s);
//
//
//    json.put(key,
//
//
//        );
//    return this;
//  }

    @Override
    public JSONBuilderImpl addSetString(final String key, final Optional<Set<String>> s) {
      requireNonNull(s).ifPresent(s1 -> this.addSetString(key, s1));
      return this;
    }

    @Override
    public JSONBuilderImpl addSetString(final String key, final Set<String> s) {
      json.put(requireNonNull(key), new JSONArray(s));
      return this;
    }

    @Override
    public JSONBuilderImpl addString(final String key, final Optional<String> s) {
      requireNonNull(s).ifPresent(s1 -> this.addString(key, s1));
      return this;
    }

    @Override
    public JSONBuilderImpl addString(final String key, final String s) {
      json.put(requireNonNull(key), s);
      return this;
    }

    @Override
    public JSONBuilderImpl addThrowable(final String key, final Optional<Throwable> s) {

      requireNonNull(s).ifPresent(s1 -> this.addThrowable(key, s1));
      return this;
    }

    // FIXME See JsonBuilder#addThrowable in ibcore-vertx-json
    @Override
    public JSONBuilder addThrowable(final String key, final Throwable t) {
      return addJSONOutputEnabled(key, new ThrowableJSONObject(t));
    }

    @Override
    public JSONObject asJSON() {
      return new JSONObject(json, JSONObject.getNames(json));
    }

    @Override
    public final Optional<PathRef> getRelativeRoot() {
      return Optional.ofNullable(this.relativeRoot);
    }
  }
}
