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
package org.infrastructurebuilder.util;

import static org.infrastructurebuilder.util.IBUtils.*;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.infrastructurebuilder.IBConstants;
import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.JSONBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class DefaultEncryptionIdentifier implements EncryptionIdentifier, IBConstants {

  final static class EncIds {
    private final SortedSet<String> ids;
    private final Optional<String> validator;

    public EncIds(final Collection<String> ids) {
      final TreeSet<String> t = new TreeSet<>();
      final AtomicReference<String> v = new AtomicReference<>(null);
      Objects.requireNonNull(ids).stream().forEach(id -> {
        if (Objects.requireNonNull(id).startsWith("*")) {
          final String clipped = id.substring(1);
          if (!v.compareAndSet(null, clipped))
            throw new IllegalArgumentException(
                "Cannot have multiple validation identifiers in a set of encryption key ids");
          t.add(clipped);
        } else {
          t.add(id);
        }

      });
      this.ids = Collections.unmodifiableSortedSet(t);
      validator = Optional.ofNullable(v.get());
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final EncIds other = (EncIds) obj;
      if (!ids.containsAll(other.ids) || !other.ids.containsAll(ids))
        return false;
      return validator.equals(other.validator);
    }

    public SortedSet<String> getIds() {
      return ids;
    }

    public Optional<String> getValidator() {
      return validator;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ids.hashCode();
      result = prime * result + validator.hashCode();
      return result;
    }

  }

  private final EncIds encId;
  private final String id;

  private final String type;

  public DefaultEncryptionIdentifier() {
    this(NO_OP, ENCRYPTION_TYPE_NONE, Collections.emptySet());
  }

  public DefaultEncryptionIdentifier(final JSONObject jsonObject) {
    if (jsonObject == null)
      throw new IllegalArgumentException("Null is not allowed");
    type = Optional.ofNullable(Objects.requireNonNull(jsonObject).optString(CRYPTO_TYPE)).orElse(ENCRYPTION_TYPE_NONE);
    id = Optional.ofNullable(jsonObject.optString(ID)).orElse(NO_OP);
    encId = new EncIds(asStringStream(
        Optional.ofNullable(jsonObject.optJSONArray(CRYPTO_ENCRYPTION_IDENTIFIERS)).orElse(new JSONArray()))
            .collect(Collectors.toList()));
  }

  public DefaultEncryptionIdentifier(final String json) {
    this(Optional.ofNullable(json).map(m -> new JSONObject(m)).orElse((JSONObject) null));
  }

  public DefaultEncryptionIdentifier(final String id, final String t, final Collection<String> ids) {
    if (id == null)
      throw new IllegalArgumentException("Id cannot be null");
    this.id = id;
    if (t == null)
      throw new IllegalArgumentException("Encryption type cannot be null");
    type = t;
    encId = new EncIds(ids);
  }

  @Override
  public Checksum asChecksum() {
    return new Checksum(IBException.cet.withReturningTranslation(() -> {
      final MessageDigest md = MessageDigest.getInstance(DIGEST_TYPE);
      md.update(type.getBytes(UTF_8));
      getEncryptionIdentifiers().stream().forEach(id -> md.update(id.getBytes(UTF_8)));
      return md.digest();
    }));
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()

        .addString(ID, id)

        .addJSONArray(CRYPTO_ENCRYPTION_IDENTIFIERS, getEncryptionIdentifiersWithValidator())

        .addString(CRYPTO_TYPE, getType())

        .asJSON();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!EncryptionIdentifier.class.isAssignableFrom(obj.getClass()))
      return false;
    final EncryptionIdentifier other = (EncryptionIdentifier) obj;

    if (!type.equals(other.getType()))
      return false;
    if (!getValidationIdentifier().equals(other.getValidationIdentifier()))
      return false;
    return getEncryptionIdentifiers().containsAll(other.getEncryptionIdentifiers())
        && other.getEncryptionIdentifiers().containsAll(getEncryptionIdentifiers());
  }

  @Override
  public SortedSet<String> getEncryptionIdentifiers() {
    return encId.getIds();
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public Optional<String> getValidationIdentifier() {
    return encId.getValidator();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id.hashCode();
    result = prime * result + encId.hashCode();
    result = prime * result + type.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return asJSON().toString();
  }

  private JSONArray getEncryptionIdentifiersWithValidator() {
    return new JSONArray(getEncryptionIdentifiers().stream()
        .map(s -> getValidationIdentifier().map(q -> q.equals(s) ? "*" + s : s).orElse(s))
        .collect(Collectors.toList()));
  }

}
