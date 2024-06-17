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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ChecksumBuilder extends ChecksumEnabled {

  ChecksumBuilder addBoolean(Boolean s);

  ChecksumBuilder addBoolean(Optional<Boolean> s);

  ChecksumBuilder addBytes(byte[] b);

  ChecksumBuilder addChecksum(Checksum s);

  ChecksumBuilder addChecksumEnabled(ChecksumEnabled s);

  ChecksumBuilder addChecksumEnabled(Optional<? extends ChecksumEnabled> s);

  ChecksumBuilder addDouble(Double s);

  ChecksumBuilder addDouble(Optional<Double> s);

  ChecksumBuilder addDuration(Duration s);

  ChecksumBuilder addDuration(Optional<Duration> s);

  ChecksumBuilder addFloat(Float s);

  ChecksumBuilder addFloat(Optional<Float> s);

  ChecksumBuilder addDate(Date d);

  ChecksumBuilder addDate(Optional<Date> d);

  ChecksumBuilder addInstant(Instant s);

  ChecksumBuilder addInstant(Optional<Instant> s);

  ChecksumBuilder addInteger(Integer s);

  ChecksumBuilder addInteger(Optional<Integer> s);

  ChecksumBuilder addJSONArray(JSONArray j);

  ChecksumBuilder addJSONObject(JSONObject j);

  ChecksumBuilder addJSONObject(Optional<JSONObject> j);

  ChecksumBuilder addListChecksumEnabled(List<ChecksumEnabled> value);

  /**
   * Warning If this is not a SortedSet, things could go awry
   *
   * @param value
   * @return
   */
  ChecksumBuilder addSortedSetChecksumEnabled(SortedSet<ChecksumEnabled> value);

  ChecksumBuilder addListString(List<String> s);

  ChecksumBuilder addListString(Optional<List<String>> s);

  ChecksumBuilder addLong(Long s);

  ChecksumBuilder addLong(Optional<Long> s);

  ChecksumBuilder addMapStringChecksumEnabled(Map<String, ChecksumEnabled> map);

  ChecksumBuilder addMapStringMapStringListChecksumEnabled(Map<String, Map<String, List<ChecksumEnabled>>> map);

  ChecksumBuilder addMapStringString(Map<String, String> map);

  ChecksumBuilder addMapStringString(Optional<Map<String, String>> map);

  ChecksumBuilder addPath(Optional<Path> s);

  ChecksumBuilder addPath(Path s);

  ChecksumBuilder addPathAsString(String s);

  ChecksumBuilder addPathAsString(Optional<String> s);

  ChecksumBuilder addSetString(Optional<Set<String>> s);

  ChecksumBuilder addSetString(Set<String> s);

  ChecksumBuilder addString(Optional<String> s);

  ChecksumBuilder addString(String s);

  ChecksumBuilder addThrowable(Optional<Throwable> s);

  ChecksumBuilder addThrowable(Throwable t);

  Checksum asChecksum();

  Optional<RelativeRoot> getRelativeRoot();

  ChecksumBuilder getChecksumBuilder();

}
