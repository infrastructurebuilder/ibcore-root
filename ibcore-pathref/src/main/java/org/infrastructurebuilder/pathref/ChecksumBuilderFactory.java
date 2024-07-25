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
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.pathref.IBChecksumUtils.getBytes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicReference;

import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.exceptions.IBException;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ChecksumBuilderFactory {

  public static ChecksumBuilder newAlternateInstance(final String t) {
    return cet.returns(() -> {
      return new ChecksumBuilderImpl(t, empty(), MessageDigest.getInstance(requireNonNull(t)));
    });
  }

  public static ChecksumBuilder newAlternateInstanceWithRelativeRoot(final String t,
      final Optional<PathRef> relativeRoot) {
    return cet.returns(() -> {
      return new ChecksumBuilderImpl(t, relativeRoot, MessageDigest.getInstance(requireNonNull(t)));
    });

  }

  public static ChecksumBuilder newAlternateInstanceWithRelativeRoot(final Optional<PathRef> relativeRoot) {
    return cet.returns(() -> {
      return new ChecksumBuilderImpl(IBConstants.DIGEST_TYPE, relativeRoot,
          MessageDigest.getInstance(requireNonNull(IBConstants.DIGEST_TYPE)));
    });

  }

  public static ChecksumBuilder newInstance() {
    return newAlternateInstance(IBConstants.DIGEST_TYPE);
  }

  public static ChecksumBuilder newInstance(final Optional<PathRef> relativeRoot) {
    return newAlternateInstanceWithRelativeRoot(IBConstants.DIGEST_TYPE, relativeRoot);
  }

  public final static ChecksumBuilder flatInstance(Checksum csum) {
    return new ChecksumBuilderImpl(csum);
  }

  private final static class ChecksumBuilderImpl implements ChecksumBuilder {

    private final AtomicReference<Checksum> checksum = new AtomicReference<>(null);
    private final MessageDigest md;
    private final Optional<PathRef> relativeRoot;
    private final String type;

    private ChecksumBuilderImpl(final String t, final Optional<PathRef> rr, final MessageDigest digestType) {
      this.type = requireNonNull(t);
      this.md = requireNonNull(digestType);
      this.relativeRoot = requireNonNull(rr);

    }

    private ChecksumBuilderImpl(Checksum csum) {
      this.checksum.set(csum);
      this.type = IBConstants.DIGEST_TYPE;
      this.md = cet.returns(() -> MessageDigest.getInstance(requireNonNull(this.type)));
      this.relativeRoot = Optional.empty();
    }

    @Override
    public ChecksumBuilder addBoolean(final Boolean s) {
      lockCheck();
      return this.addString(requireNonNull(s.toString()));
    }

    @Override
    public ChecksumBuilder addBoolean(final Optional<Boolean> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addBoolean);
      return this;
    }

    @Override
    public ChecksumBuilder addBytes(final byte[] b) {
      lockCheck();
      md.update(requireNonNull(b));
      return this;
    }

    @Override
    public ChecksumBuilder addChecksum(final Checksum s) {
      return addBytes(requireNonNull(s).getDigest());
    }

    @Override
    public ChecksumBuilder addChecksumEnabled(final ChecksumEnabled s) {
      if (s == null)
        return this;
      lockCheck();
      addBytes(s.asChecksum().getDigest());
      return this;
    }

    @Override
    public ChecksumBuilder addChecksumEnabled(final Optional<? extends ChecksumEnabled> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addChecksumEnabled);
      return this;
    }

    @Override
    public ChecksumBuilder addDouble(final Double s) {
      lockCheck();
      return this.addString(requireNonNull(s.toString()));
    }

    @Override
    public ChecksumBuilder addDouble(final Optional<Double> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addDouble);
      return this;
    }

    @Override
    public ChecksumBuilder addDuration(final Duration s) {
      lockCheck();
      return this.addString(requireNonNull(s).toString());
    }

    @Override
    public ChecksumBuilder addDuration(final Optional<Duration> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addDuration);
      return this;
    }

    @Override
    public ChecksumBuilder addFloat(final Float s) {
      lockCheck();
      return this.addString(requireNonNull(s.toString()));
    }

    @Override
    public ChecksumBuilder addFloat(final Optional<Float> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addFloat);
      return this;
    }

    @Override
    public ChecksumBuilder addDate(final Date d) {
      return addInstant(ofNullable(d).map(Date::toInstant));
    }

    @Override
    public ChecksumBuilder addDate(final Optional<Date> d) {
      return addInstant(d.map(Date::toInstant));
    }

    @Override
    public ChecksumBuilder addInstant(final Instant s) {
      lockCheck();
      return this.addString(requireNonNull(s).toString());
    }

    @Override
    public ChecksumBuilder addInstant(final Optional<Instant> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addInstant);
      return this;
    }

    @Override
    public ChecksumBuilder addInteger(final Integer s) {
      lockCheck();
      return this.addString(requireNonNull(s.toString()));
    }

    @Override
    public ChecksumBuilder addInteger(final Optional<Integer> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addInteger);
      return this;
    }

    @Override
    public ChecksumBuilder addJSONArray(final JSONArray j) {
      final ChecksumBuilder jBuilder = ChecksumBuilderFactory.newAlternateInstanceWithRelativeRoot(type, relativeRoot);
      jBuilder.addString("[");
      requireNonNull(j).forEach(o -> {
        addJObjectToBuilder(this, o);
      });
      return this.addChecksumEnabled(jBuilder.addString("]"));
    }

    @Override
    public ChecksumBuilder addJSONObject(final JSONObject j) {

      final ChecksumBuilder jBuilder = ChecksumBuilderFactory.newAlternateInstanceWithRelativeRoot(type, relativeRoot);
      jBuilder.addString("{");
      final List<String> keys = requireNonNull(j).keySet().stream().sorted().toList();
      for (final String key : keys) {
        final Object o = j.get(key);
        jBuilder.addString(key + "=");
        addJObjectToBuilder(this, o);
      }
      return this.addChecksumEnabled(jBuilder.addString("}"));
    }

    @Override
    public ChecksumBuilder addJSONObject(final Optional<JSONObject> j) {
      lockCheck();
      requireNonNull(j).ifPresent(this::addJSONObject);
      return this;
    }

    @Override
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
    @Override
    public ChecksumBuilder addSortedSetChecksumEnabled(final SortedSet<ChecksumEnabled> value) {
      lockCheck();
      requireNonNull(value).stream().forEach(l -> this.addChecksumEnabled(l));
      return this;
    }

    @Override
    public ChecksumBuilder addListString(final List<String> s) {
      lockCheck();
      requireNonNull(s).stream().forEach(this::addString);
      return this;
    }

    @Override
    public ChecksumBuilder addListString(final Optional<List<String>> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addListString);
      return this;
    }

    @Override
    public ChecksumBuilder addLong(final Long s) {
      lockCheck();
      return this.addString(requireNonNull(s.toString()));
    }

    @Override
    public ChecksumBuilder addLong(final Optional<Long> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addLong);
      return this;
    }

    @Override
    public ChecksumBuilder addMapStringChecksumEnabled(final Map<String, ChecksumEnabled> map) {
      requireNonNull(map).keySet().stream().sorted().forEach(k -> {
        addString(k + "=");
        addChecksumEnabled(map.get(k));
      });
      return this;
    }

    @Override
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

    @Override
    public ChecksumBuilder addMapStringString(final Map<String, String> map) {
      addBytes(Checksum.getMapStringStringChecksum(requireNonNull(map)).getDigest());
      return this;
    }

    @Override
    public ChecksumBuilder addMapStringString(final Optional<Map<String, String>> map) {
      lockCheck();
      requireNonNull(map).ifPresent(this::addMapStringString);
      return this;
    }

    @Override
    public ChecksumBuilder addPath(final Optional<Path> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addPath);
      return this;
    }

    @Override
    public ChecksumBuilder addPath(final Path s) {

      return requireNonNull(s).isAbsolute()
          ? this.addPathAsString(cet.returns(() -> s.toUri().toURL().toExternalForm()))
          : this.addPathAsString(s.toString());
//    lockCheck();
//     Optional<Path> j = relativeRoot.flatMap(rel -> rel.relativize(requireNonNull(s).toAbsolutePath()));
////    final Optional<Path> j = relativeRoot.map(rel -> rel.relativize(requireNonNull(s).toAbsolutePath()));
//    return this.addString(requireNonNull(j.orElse(s.toAbsolutePath())).toString());
    }

    @Override
    public ChecksumBuilder addPathAsString(final String s) {
      var str = relativeRoot.flatMap(rel -> rel.relativize(requireNonNull(s))).orElse(Paths.get(s)).toString();
      return this.addString(str);
    }

    @Override
    public ChecksumBuilder addPathAsString(final Optional<String> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addPathAsString);
      return this;
    }

    @Override
    public ChecksumBuilder addSetString(final Optional<Set<String>> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addSetString);
      return this;
    }

    @Override
    public ChecksumBuilder addSetString(final Set<String> s) {
      lockCheck();
      requireNonNull(s).stream().sorted().forEach(y -> this.addString(y));
      return this;
    }

    @Override
    public ChecksumBuilder addString(final Optional<String> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addString);
      return this;
    }

    @Override
    public ChecksumBuilder addString(final String s) {
      return s == null ? this : addBytes(getBytes.apply(requireNonNull(s)));
    }

    @Override
    public ChecksumBuilder addThrowable(final Optional<Throwable> s) {
      lockCheck();
      requireNonNull(s).ifPresent(this::addThrowable);
      return this;
    }

    @Override
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

    @Override
    public Optional<PathRef> getRelativeRoot() {
      return this.relativeRoot;
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
}
