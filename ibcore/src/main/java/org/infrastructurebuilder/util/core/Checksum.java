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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.UUID.nameUUIDFromBytes;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.core.IBUtils.digestInputStream;
import static org.infrastructurebuilder.util.core.IBUtils.getHex;
import static org.infrastructurebuilder.util.core.IBUtils.hexStringToByteArray;
import static org.infrastructurebuilder.util.core.IBUtils.readerToInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.constants.IBConstants;
import org.json.JSONObject;

public class Checksum implements Comparable<Checksum>, Supplier<Optional<UUID>> {

  public final static Checksum extend(Checksum c, byte[] bytes) {
    var ba = new byte[c.b.length + bytes.length];
    int i, j;
    for (i = 0; i < c.b.length; ++i) {
      ba[i] = c.b[i];
    }
    for (j = i; j < ba.length; ++j) {
      ba[j] = bytes[i - j];
    }
    return new Checksum(ba);
  }

  public final static Function<Path, Optional<Checksum>> ofPath = (p) -> {
    try (InputStream is = Files.newInputStream(p, StandardOpenOption.READ)) {
      return of(new Checksum(IBUtils.digestInputStream(is)));
    } catch (IOException e) {
      return empty();
    }
  };

  public final static Checksum fromUTF8StringBytes(String s) {
    return new Checksum(new ByteArrayInputStream(s.getBytes(UTF_8)));
  }

  public final static Checksum getMapStringStringChecksum(final Map<String, String> tags) {
    return getMapStringStringChecksum(tags, IBConstants.DIGEST_TYPE);
  }

  public final static Checksum getMapStringStringChecksum(final Map<String, String> tags, String digestType) {
    return cet.returns(() -> {
      final MessageDigest md = MessageDigest.getInstance(digestType);
      tags.keySet().stream().sorted().forEach(key -> {
        md.update(new StringBuffer().append(key).append("=").append(tags.get(key)).toString().getBytes(UTF_8));
      });
      return new Checksum(md.digest());
    });
  }

  private final byte[] b;

//  private final Optional<Path> relativeRoot;

  public Checksum() {
    this((byte[]) null);
  }

  public Checksum(final byte[] b) {
    this.b = b;
  }

  public Checksum(final InputStream ins) {
    try {
      b = ins == null ? null : cet.returns(() -> digestInputStream(ins));
    } finally {
      if (ins != null)
        cet.translate(() -> ins.close());
    }
  }

  public Checksum(final JSONObject j) {
    this(new StringReader(j.toString()));
  }

  public Checksum(final Reader ins) {
    this(readerToInputStream(ins));
  }

  public Checksum(final Path file) {
    this(cet.returns(() -> newInputStream(requireNonNull(file))));
  }

  /**
   * This constructore produces a checksum of a list of checksums. It DEFINITELY
   * loses fidelity but for SHA-512 strings it's...OK.
   *
   * @param relativeRoot
   * @param list
   */
  public Checksum(final List<Checksum> list) {
    this(requireNonNull(list).stream()
        // Collects all checksums as a string into a long string and then gets the
        // checksum of the UTF-8 bytes.
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString().getBytes(UTF_8)
    // RelRoot
    );
  }

  public Checksum(final String hexString) {
    this(hexStringToByteArray(hexString));
  }

  public Checksum(final Supplier<InputStream> insS) {
    this(insS.get());
  }

  public Optional<UUID> asUUID() {
    return ofNullable(b == null ? null : nameUUIDFromBytes(b));
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
