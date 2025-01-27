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

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public interface JSONBuilder extends JSONOutputEnabled{

  JSONBuilder addAbsolutePath(String key, Optional<Path> s);

  JSONBuilder addAbsolutePath(String key, Path s);

  JSONBuilder addBoolean(String key, Boolean s);

  JSONBuilder addBoolean(String key, Optional<Boolean> s);

  JSONBuilder addBytes(String key, byte[] b);

  JSONBuilder addChecksum(String key, Checksum s);

  JSONBuilder addChecksum(String key, Optional<Checksum> s);

  JSONBuilder addChecksumEnabled(String key, ChecksumEnabled s);

  JSONBuilder addChecksumEnabled(String key, Optional<ChecksumEnabled> s);

  JSONBuilder addDouble(String key, Double s);

  JSONBuilder addDouble(String key, Optional<Double> s);

  JSONBuilder addDuration(String key, Duration s);

  JSONBuilder addDuration(String key, Optional<Duration> s);

  JSONBuilder addFloat(String key, Float s);

  JSONBuilder addFloat(String key, Optional<Float> s);

  JSONBuilder addInstant(String key, Instant s);

  JSONBuilder addInstant(String key, Optional<Instant> s);

  JSONBuilder addInteger(String key, Integer s);

  JSONBuilder addInteger(String key, Optional<Integer> s);

  JSONBuilder addJSONArray(String key, JSONArray j);

  JSONBuilder addJSONArray(String key, Optional<JSONArray> j);

  JSONBuilder addJSONObject(String key, JSONObject j);

  JSONBuilder addJSONObject(String key, Optional<JSONObject> j);

  JSONBuilder addJSONOutputEnabled(String key, JSONOutputEnabled j);

  JSONBuilder addJSONOutputEnabled(String key, Optional<? extends JSONOutputEnabled> j);

  JSONBuilder addListJSONOutputEnabled(String key, List<JSONOutputEnabled> value);

  JSONBuilder addListString(String key, List<String> s);

  JSONBuilder addListString(String key, Optional<List<String>> s);

  JSONBuilder addLong(String key, Long s);

  JSONBuilder addLong(String key, Optional<Long> s);

  JSONBuilder addMapStringJSONOutputEnabled(String key, Map<String, JSONOutputEnabled> map);

  JSONBuilder addMapStringListJSONOutputEnabled(String key, Map<String, List<JSONOutputEnabled>> map);

  JSONBuilder addMapStringMapStringListJSONOutputEnabled(String key,
      Map<String, Map<String, List<JSONOutputEnabled>>> map);

  JSONBuilder addMapStringString(String key, Map<String, String> map);

  JSONBuilder addMapStringString(String key, Optional<Map<String, String>> map);

  JSONBuilder addProperties(String key, Properties properties);

  JSONBuilder addProperties(String key, Optional<Properties> properties);

  JSONBuilder addPath(String key, Optional<Path> s);

  JSONBuilder addPath(String key, Path s);

  JSONBuilder addSetString(String key, Optional<Set<String>> s);

  JSONBuilder addSetString(String key, Set<String> s);

  JSONBuilder addString(String key, Optional<String> s);

  JSONBuilder addString(String key, String s);

  JSONBuilder addThrowable(String key, Optional<Throwable> s);

  // FIXME See JsonBuilder#addThrowable in ibcore-vertx-json
  JSONBuilder addThrowable(String key, Throwable t);

}
