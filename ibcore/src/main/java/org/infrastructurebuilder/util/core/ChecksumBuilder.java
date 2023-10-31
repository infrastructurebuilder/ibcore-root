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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.core.IBUtils.getBytes;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ChecksumBuilder implements ChecksumEnabled {

  public static ChecksumBuilder newAlternateInstance(final String t) {
    return IBException.cet.returns(() -> {
      return new ChecksumBuilder(t, MessageDigest.getInstance(requireNonNull(t)), empty());
    });
  }

  public static ChecksumBuilder newAlternateInstance(final String t, final Optional<Path> relativeRoot) {
    return IBException.cet.returns(() -> {
      return new ChecksumBuilder(t, MessageDigest.getInstance(requireNonNull(t)), relativeRoot);
    });
  }

  public static ChecksumBuilder newInstance() {
    return newAlternateInstance(IBConstants.DIGEST_TYPE);
  }

  public static ChecksumBuilder newInstance(final Optional<Path> relativeRoot) {
    return newAlternateInstance(IBConstants.DIGEST_TYPE, relativeRoot);
  }

  public final static ChecksumBuilder flatInstance(Checksum csum) {
    return new ChecksumBuilder(csum);
  }

  private final AtomicReference<Checksum> checksum = new AtomicReference<>(null);
  private final MessageDigest md;
  private final Optional<Path> relativeRoot;
  private final String type;

  private ChecksumBuilder(final String t, final MessageDigest digestType, final Optional<Path> relativeRoot) {
    type = requireNonNull(t);
    md = requireNonNull(digestType);
    final Optional<Path> rr = requireNonNull(relativeRoot);
    this.relativeRoot = rr.map(r -> r.toAbsolutePath());
  }

  public ChecksumBuilder(Checksum csum) {
    this.checksum.set(csum);
    this.type = IBConstants.DIGEST_TYPE;
    this.md = IBException.cet.returns(() -> MessageDigest.getInstance(requireNonNull(this.type)));
    this.relativeRoot = Optional.empty();
  }

  public ChecksumBuilder addBoolean(final Boolean s) {
    lockCheck();
    return this.addString(requireNonNull(s.toString()));
  }

  public ChecksumBuilder addBoolean(final Optional<Boolean> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addBoolean);
    return this;
  }

  public ChecksumBuilder addBytes(final byte[] b) {
    lockCheck();
    md.update(requireNonNull(b));
    return this;
  }

  public ChecksumBuilder addChecksum(final Checksum s) {
    return addBytes(requireNonNull(s).getDigest());
  }

  public ChecksumBuilder addChecksumEnabled(final ChecksumEnabled s) {
    if (s == null)
      return this;
    lockCheck();
    addBytes(s.asChecksum().getDigest());
    return this;
  }

  public ChecksumBuilder addChecksumEnabled(final Optional<ChecksumEnabled> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addChecksumEnabled);
    return this;
  }

  public ChecksumBuilder addDouble(final Double s) {
    lockCheck();
    return this.addString(requireNonNull(s.toString()));
  }

  public ChecksumBuilder addDouble(final Optional<Double> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addDouble);
    return this;
  }

  public ChecksumBuilder addDuration(final Duration s) {
    lockCheck();
    return this.addString(requireNonNull(s).toString());
  }

  public ChecksumBuilder addDuration(final Optional<Duration> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addDuration);
    return this;
  }

  public ChecksumBuilder addFloat(final Float s) {
    lockCheck();
    return this.addString(requireNonNull(s.toString()));
  }

  public ChecksumBuilder addFloat(final Optional<Float> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addFloat);
    return this;
  }

  public ChecksumBuilder addDate(final Date d) {
    return addInstant(ofNullable(d).map(Date::toInstant));
  }

  public ChecksumBuilder addDate(final Optional<Date> d) {
    return addInstant(d.map(Date::toInstant));
  }

  public ChecksumBuilder addInstant(final Instant s) {
    lockCheck();
    return this.addString(requireNonNull(s).toString());
  }

  public ChecksumBuilder addInstant(final Optional<Instant> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addInstant);
    return this;
  }

  public ChecksumBuilder addInteger(final Integer s) {
    lockCheck();
    return this.addString(requireNonNull(s.toString()));
  }

  public ChecksumBuilder addInteger(final Optional<Integer> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addInteger);
    return this;
  }

  public ChecksumBuilder addJSONArray(final JSONArray j) {
    final ChecksumBuilder jBuilder = ChecksumBuilder.newAlternateInstance(type, relativeRoot);
    jBuilder.addString("[");
    requireNonNull(j).forEach(o -> {
      addJObjectToBuilder(this, o);
    });
    return this.addChecksumEnabled(jBuilder.addString("]"));
  }

  public ChecksumBuilder addJSONObject(final JSONObject j) {

    final ChecksumBuilder jBuilder = ChecksumBuilder.newAlternateInstance(type, relativeRoot);
    jBuilder.addString("{");
    final List<String> keys = requireNonNull(j).keySet().stream().sorted().collect(Collectors.toList());
    for (final String key : keys) {
      final Object o = j.get(key);
      jBuilder.addString(key + "=");
      addJObjectToBuilder(this, o);
    }
    return this.addChecksumEnabled(jBuilder.addString("}"));
  }

  public ChecksumBuilder addListChecksumEnabled(final List<ChecksumEnabled> value) {
    lockCheck();
    requireNonNull(value).stream().forEach(l -> this.addChecksumEnabled(l));
    return this;
  }

  /**
   * Warning If this is not a SortedSet, things could go awry
   *
   * @param value
   * @return
   */
  public ChecksumBuilder addSortedSetChecksumEnabled(final SortedSet<ChecksumEnabled> value) {
    lockCheck();
    requireNonNull(value).stream().forEach(l -> this.addChecksumEnabled(l));
    return this;
  }

  public ChecksumBuilder addListString(final List<String> s) {
    lockCheck();
    requireNonNull(s).stream().forEach(this::addString);
    return this;
  }

  public ChecksumBuilder addListString(final Optional<List<String>> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addListString);
    return this;
  }

  public ChecksumBuilder addLong(final Long s) {
    lockCheck();
    return this.addString(requireNonNull(s.toString()));
  }

  public ChecksumBuilder addLong(final Optional<Long> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addLong);
    return this;
  }

  public ChecksumBuilder addMapStringChecksumEnabled(final Map<String, ChecksumEnabled> map) {
    requireNonNull(map).keySet().stream().sorted().forEach(k -> {
      addString(k + "=");
      addChecksumEnabled(map.get(k));
    });
    return this;
  }

  public ChecksumBuilder addMapStringMapStringListChecksumEnabled(
      final Map<String, Map<String, List<ChecksumEnabled>>> map) {
    lockCheck();
    for (final Entry<String, Map<String, List<ChecksumEnabled>>> builders : requireNonNull(map).entrySet()) {
      md.update(getBytes.apply(builders.getKey() + "="));
      for (final Entry<String, List<ChecksumEnabled>> targets : builders.getValue().entrySet()) {
        md.update(getBytes.apply(targets.getKey() + "="));
        addListChecksumEnabled(targets.getValue());
      }
    }
    return this;
  }

  public ChecksumBuilder addMapStringString(final Map<String, String> map) {
    addBytes(Checksum.getMapStringStringChecksum(requireNonNull(map)).getDigest());
    return this;
  }

  public ChecksumBuilder addMapStringString(final Optional<Map<String, String>> map) {
    lockCheck();
    requireNonNull(map).ifPresent(this::addMapStringString);
    return this;
  }

  public ChecksumBuilder addPath(final Optional<Path> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addPath);
    return this;
  }

  public ChecksumBuilder addPath(final Path s) {
    lockCheck();
    final Optional<Path> j = relativeRoot.map(rel -> rel.relativize(requireNonNull(s).toAbsolutePath()));
    return this.addString(requireNonNull(j.orElse(s.toAbsolutePath())).toString());
  }

  public ChecksumBuilder addSetString(final Optional<Set<String>> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addSetString);
    return this;
  }

  public ChecksumBuilder addSetString(final Set<String> s) {
    lockCheck();
    requireNonNull(s).stream().sorted().forEach(y -> this.addString(y));
    return this;
  }

  public ChecksumBuilder addString(final Optional<String> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addString);
    return this;
  }

  public ChecksumBuilder addString(final String s) {
    return s == null ? this : addBytes(getBytes.apply(requireNonNull(s)));
  }

  public ChecksumBuilder addThrowable(final Optional<Throwable> s) {
    lockCheck();
    requireNonNull(s).ifPresent(this::addThrowable);
    return this;
  }

  public ChecksumBuilder addThrowable(final Throwable t) {
    lockCheck();
    return this.addString(ofNullable(requireNonNull(t).getClass().getCanonicalName()).orElse("null"))
        .addString(ofNullable(t.getMessage()));
  }

  @Override
  public synchronized Checksum asChecksum() {
    synchronized (checksum) {
      checksum.compareAndSet(null, new Checksum(md.digest()));
      return checksum.get();
    }
  }

  public Optional<Path> getRelativeRoot() {
    return relativeRoot;
  }

  private ChecksumBuilder addJObjectToBuilder(final ChecksumBuilder b, final Object o) {
    if (o instanceof JSONObject) {
      b.addJSONObject((JSONObject) o);
    } else if (o instanceof JSONArray) {
      b.addJSONArray((JSONArray) o);
    } else if (o instanceof Integer) {
      b.addInteger((Integer) o);
    } else if (o instanceof Long) {
      b.addLong((Long) o);
    } else if (o instanceof Double) {
      b.addDouble((Double) o);
    } else if (o instanceof Float) {
      b.addFloat((Float) o);
    } else if (o instanceof Boolean) {
      b.addBoolean((Boolean) o);
    } else {
      b.addString(o.toString());
    }
    return b;

  }

  private void lockCheck() {
    synchronized (checksum) {
      if (checksum.get() != null)
        throw new IBException("Attempted to set a locked checksum.  This is a BAD THING");
    }
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return this;
  }
}
