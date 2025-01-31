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
import static org.infrastructurebuilder.constants.IBConstants.CAUSE;
import static org.infrastructurebuilder.constants.IBConstants.CLASS;
import static org.infrastructurebuilder.constants.IBConstants.MESSAGE;
import static org.infrastructurebuilder.constants.IBConstants.STACK_TRACE;
import static org.infrastructurebuilder.constants.IBConstants.UNKNOWN_THROWABLE_CLASS;
import static org.infrastructurebuilder.pathref.JSONBuilder.TIMESTAMP;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.infrastructurebuilder.pathref.JSONBuilderBaseFactory;
import org.infrastructurebuilder.pathref.ThrowableXsonObject;
import org.infrastructurebuilder.pathref.XsonOutputEnabled;
import org.infrastructurebuilder.pathref.api.Modeled;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
public class JsonBuilderFactory {

  private final static Logger log = LoggerFactory.getLogger(JSONBuilderBaseFactory.class);
  private final static AtomicReference<DateTimeFormatter> dts = new AtomicReference<>();

  public static JsonBuilder newInstance() {
    return new JsonBuilderImpl(null);
  }

  public static JsonBuilder newInstanceFromRelativeRoot(final PathRefFileSystem relativeRoot) {
    return new JsonBuilderImpl(relativeRoot);
  }

  public static DateTimeFormatter getDateFormat() {
    synchronized (dts) {
      if (dts.get() == null) {
        dts.set(DateTimeFormatter.ofPattern(TIMESTAMP).withZone(ZoneId.from(ZoneOffset.UTC)));
      }
    }
    return dts.get();
  }

  public static class JsonBuilderImpl extends JSONBuilderBaseFactory.XsonBuilderBaseImpl<JsonObject, JsonArray>
  implements JsonBuilder {

    private PathRefFileSystem relativeRoot;
    public JsonBuilderImpl(PathRefFileSystem relativeRoot, JsonObject j) {
      super(j);
      this.relativeRoot = relativeRoot;
      // TODO Auto-generated constructor stub
    }

    public JsonBuilderImpl(PathRefFileSystem root) {
      super();
      this.relativeRoot = root;
    }

    public Optional<PathRefFileSystem> getRelativeRoot() {
      return Optional.ofNullable(relativeRoot);
    }

    @Override
    public JsonArray from(List<XsonOutputEnabled<JsonObject>> l) {
      return new JsonArray(requireNonNull(l).stream().map(XsonOutputEnabled::asJSON).toList());
    }

    @Override
    public JsonObject from(Map<String, Object> m) {
      return new JsonObject(requireNonNull(m));
    }

    @Override
    public JsonObject asJSON() {
      return new JsonObject(json.toString());
    }

    @Override
    public JsonBuilderImpl addMapStringMapStringListJSONOutputEnabled(final String key,
        final Map<String, Map<String, List<XsonOutputEnabled<JsonObject>>>> map) {
      final JsonBuilderImpl j1 = new JsonBuilderImpl(getRelativeRoot().orElse(null));
      for (final Entry<String, Map<String, List<XsonOutputEnabled<JsonObject>>>> builders : requireNonNull(map)
          .entrySet()) {
        j1.addMapStringListJSONOutputEnabled(builders.getKey(), builders.getValue());
      }
      return (JsonBuilderImpl) addJSONObject(key, j1.asJSON());
    }

    @Override
    public JsonBuilderImpl addMapStringJSONOutputEnabled(final String key,
        final Map<String, XsonOutputEnabled<JsonObject>> map) {
      return (JsonBuilderImpl) addJSONObject(key, from(requireNonNull(map).entrySet().stream()
          .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().asJSON()))));
    }

    @Override
    public JsonBuilderImpl addProperties(final String key, final Properties properties) {
      Map<String, Object> mm = requireNonNull(properties).entrySet().stream()
          .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
      addJSONObject(key, new JsonObject(mm));
      return this;
    }

    @Override
    public JsonBuilderImpl addThrowable(final String key, final Throwable t) {
      return (JsonBuilderImpl) addJSONOutputEnabled(key, new ThrowableJsonObject(t));
    }
    @Override
    public JsonBuilderImpl addModeled(final String key, final Modeled t) {
      return (JsonBuilderImpl) addJSONObject(key, new JsonObject(Modeled.getModeledXsonString(t)));
    }

  }

  public static class ThrowableJsonObject extends ThrowableXsonObject<JsonObject> {
    public static JsonObject _getThrowableJson(Throwable t) {
      final JsonObject j2 = new JsonObject();
      if (t != null) {
        j2.put(CLASS, t.getClass().getCanonicalName());
        Optional.ofNullable(t.getCause()).ifPresent(cause -> {
          j2.put(CAUSE, _getThrowableJson(cause)); // Recurses
        });
        Optional.ofNullable(t.getMessage()).ifPresent(message -> {
          j2.put(MESSAGE, message);
        });
        var st = t.getStackTrace();
        if (st.length > 0) {
          var l = new JsonArray();
          for (StackTraceElement ste : st) {
            l.add(ste.toString());
          }
          j2.put(STACK_TRACE, l);
        }
      } else {
        j2.put(CLASS, UNKNOWN_THROWABLE_CLASS);
      }
      return j2;
    }

    public ThrowableJsonObject(Throwable t) {
      super(t);
    }

    @Override
    public JsonObject asJSON() {
      return this.jsonObject;
    }

    @Override
    public JsonObject getThrowableJson(Throwable t) {
      return _getThrowableJson(t);
    }
  }
}
