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

import static org.infrastructurebuilder.IBException.cet;
import static org.infrastructurebuilder.util.IBUtils.UTF_8;
import static org.infrastructurebuilder.util.IBUtils.digestInputStream;
import static org.infrastructurebuilder.util.IBUtils.getHex;
import static org.infrastructurebuilder.util.IBUtils.hexStringToByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.infrastructurebuilder.IBConstants;
import org.infrastructurebuilder.IBException;

public class Checksum implements Comparable<Checksum>, Supplier<Optional<UUID>> {

  public final static Checksum getMapStringStringChecksum(final Map<String, String> tags) {
    return cet.withReturningTranslation(() -> {
      final MessageDigest md = MessageDigest.getInstance(IBConstants.DIGEST_TYPE);
      tags.keySet().stream().sorted().forEach(key -> {
        md.update(new StringBuffer().append(key).append("=").append(tags.get(key)).toString().getBytes(UTF_8));
      });
      return new Checksum(md.digest());
    });
  }

  private final byte[] b;

  private final Optional<Path> relativeRoot;

  public Checksum() {
    this((byte[]) null);
  }

  public Checksum(final byte[] b) {
    this(b, Optional.empty());
  }

  public Checksum(final byte[] b, final Optional<Path> relativeRoot) {
    this.b = b;
    this.relativeRoot = Objects.requireNonNull(relativeRoot);
  }

  public Checksum(final InputStream ins) {
    this(ins, Optional.empty());
  }

  public Checksum(final InputStream ins, final Optional<Path> relativeRoot) {
    try {
      b = ins == null ? null : digestInputStream(ins);
    } catch (NoSuchAlgorithmException | IOException e) {
      throw new IBException(e);
    } finally {
      if (ins != null)
        IBException.cet.withTranslation(() -> ins.close());
    }
    this.relativeRoot = Objects.requireNonNull(relativeRoot);
  }

  public Checksum(final Path file) {
    this(cet.withReturningTranslation(() -> Files.newInputStream(Objects.requireNonNull(file))));
  }

  /**
   * This constructore produces a checksum of a list of checksums.  It DEFINITELY loses fidelity
   * but for SHA-512 strings it's...OK.
   * @param relativeRoot
   * @param list
   */
  public Checksum(final Optional<Path> relativeRoot, final List<Checksum> list) {
    this(Objects.requireNonNull(list).stream()
        // Collects all checksums as a string into a long string and then gets the checksum of the UTF-8 bytes.
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString().getBytes(UTF_8)
    // RelRoot
        , relativeRoot);
  }

  public Checksum(final String hexString) {
    this(hexString, Optional.empty());
  }

  public Checksum(final String hexString, final Optional<Path> relativeRoot) {
    this(hexStringToByteArray(hexString), relativeRoot);
  }

  public Checksum(final Supplier<InputStream> insS) {
    this(insS, Optional.empty());
  }

  public Checksum(final Supplier<InputStream> insS, final Optional<Path> relativeRoot) {
    this(insS.get(), relativeRoot);
  }

  public Checksum(final List<Checksum> checksums) {
    this(Optional.empty(), checksums);
  }

  public Optional<UUID> asUUID() {
    return Optional.ofNullable(b == null ? null : UUID.nameUUIDFromBytes(b));
  }

  @Override
  public int compareTo(final Checksum o) {
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
    return Optional.ofNullable(b).map(c -> Arrays.copyOf(c, c.length)).orElse(new byte[0]);
  }

  public Optional<Path> getRelativeRoot() {
    return relativeRoot;
  }

  @Override
  public int hashCode() {
    return Objects.hash(toString());
  }

  @Override
  public String toString() {
    return getHex(b);
  }

}
