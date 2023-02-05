/*
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
package org.infrastructurebuilder.util.core;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.UUID.nameUUIDFromBytes;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.constants.IBConstants.DIGEST_TYPE;
import static org.infrastructurebuilder.util.core.IBUtils.digestInputStream;
import static org.infrastructurebuilder.util.core.IBUtils.getHex;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class AbstractChecksum implements Comparable<AbstractChecksum>, Supplier<Optional<UUID>> {
  static final String UNKNOWN = "UNKNOWN";
  private final static Function<byte[], String> digestTypeFromLen = (l) -> {
    if (l == null)
      return "NULL";
    switch (l.length) {
    case 16:
      return "MD5";
    case 20:
      return "SHA-1";
    case 32:
      return "SHA-256";
    case 64:
      return "SHA-512";
    default:
      return UNKNOWN;
    }
  };
  private final String digestType;
  protected final byte[] b;

  protected AbstractChecksum(byte[] b) {
    this(digestTypeFromLen.apply(b), b);
  }

  protected AbstractChecksum(String type, byte[] b) {
    super();
    this.digestType = requireNonNull(type);
    this.b = b;
  }

  protected AbstractChecksum(String digestType, final InputStream ins) {
    try {
      b = ins == null ? null : cet.returns(() -> digestInputStream(digestType, ins));
      this.digestType = requireNonNull(digestType);
    } finally {
      if (ins != null)
        cet.translate(() -> ins.close());
    }
  }

  protected AbstractChecksum(final InputStream ins) {
    this(DIGEST_TYPE, ins);
  }

  public String getDigestType() {
    return digestType;
  }

  public Optional<UUID> asUUID() {
    return ofNullable(b == null ? null : nameUUIDFromBytes(b));
  }

  @Override
  public int compareTo(final AbstractChecksum o) {
    int v = 0;
    if (b == null && o.b != null) {
      v = -1;
    } else if (b != null && o.b == null) {
      v = 1;
    } else {
      v = toString().compareTo(o.toString());
    }
    return v;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final Checksum c = (Checksum) obj;
    return b == null && c.b == null || toString().equals(c.toString());

  }

  @Override
  public Optional<UUID> get() {
    return asUUID();
  }

  public byte[] getDigest() {
    return ofNullable(b).map(c -> Arrays.copyOf(c, c.length)).orElse(new byte[0]);
  }

  @Override
  public int hashCode() {
    return hash(toString());
  }

  @Override
  public String toString() {
    return getHex(b);
  }

}