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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static org.infrastructurebuilder.constants.IBConstants.DIGEST_TYPE;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.pathref.IBChecksumUtils.hexStringToByteArray;
import static org.infrastructurebuilder.pathref.IBChecksumUtils.readerToInputStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.json.JSONObject;

/**
 * Checksums are used extensively within InfrastructureBuilder code
 *
 * They are, by convention, SHA-512 sums of portable values. This is often metadata, but is frequently done by
 * specifying a {@link PathRef} and using {@link ChecksumBuilder} and {@link JSONBuilder} to manage metadata specifics.
 */
public class Checksum extends AbstractChecksum {

  public static final Checksum extend(Checksum c, byte[] bytes) {
    var ba = new byte[c.b.length + bytes.length];
    int i, j;
    for (i = 0; i < c.b.length; ++i) {
      ba[i] = c.b[i];
    }
    for (j = 0; j < ba.length; ++j) {
      ba[j + i] = bytes[j];
    }
    return new Checksum(ba, UNKNOWN);
  }

  public static InputStream inputStreamFromHexString(final String hexString) {
    return new ByteArrayInputStream(hexStringToByteArray(hexString));
  }

  public static final BiFunction<Path, String, Optional<Checksum>> ofPathWithType = (p, t) -> {
    try {
      return of(new Checksum(t, p));
    } catch (Throwable e) {
      return Optional.empty();
    }
  };

  public static final Function<Path, Optional<Checksum>> ofPath = (p) -> {
    return ofPathWithType.apply(p, DIGEST_TYPE);
  };

  public static final Checksum fromUTF8StringBytes(String s) {
    return new Checksum(new ByteArrayInputStream(s.getBytes(UTF_8)));
  }

  public static final Checksum getMapStringStringChecksum(final Map<String, String> tags) {
    return getMapStringStringChecksum(tags, DIGEST_TYPE);
  }

  public static final Checksum getMapStringStringChecksum(final Map<String, String> tags, String digestType) {
    return cet.returns(() -> {
      final MessageDigest md = MessageDigest.getInstance(digestType);
      tags.keySet().stream().sorted().forEach(key -> {
        md.update(new StringBuffer().append(key).append("=").append(tags.get(key)).toString().getBytes(UTF_8));
      });
      return new Checksum(md.digest());
    });
  }

  public Checksum() {
    this((byte[]) null);
  }

  public Checksum(final byte[] b) {
    super(b);
  }

  private Checksum(final byte[] b, String digestType) {
    super(digestType, b);
  }

  public Checksum(final InputStream ins, String digestType) {
    super(digestType, ins);
  }

  public Checksum(final InputStream ins) {
    this(ins, DIGEST_TYPE);
  }

  public Checksum(final JSONObject j, String digestType) {
    this(new StringReader(IBChecksumUtils.deepMapJSONtoOrderedString.apply(j)), digestType);
  }

  public Checksum(final JSONObject j) {
    this(j, DIGEST_TYPE);
  }

  public Checksum(final Reader ins) {
    this(readerToInputStream(ins), DIGEST_TYPE);
  }

  public Checksum(final Reader ins, String digestType) {
    this(readerToInputStream(ins), digestType);
  }

  public Checksum(final Path file) {
    this(cet.returns(() -> newInputStream(requireNonNull(file))));
  }

  public Checksum(final String digestType, final Path file) {
    this(cet.returns(() -> newInputStream(requireNonNull(file))), digestType);
  }

  /**
   * This constructor produces a checksum of a list of checksums. It DEFINITELY loses fidelity but for SHA-512 strings
   * it's...OK.
   *
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

  public Checksum(final Supplier<InputStream> insS, String digestType) {
    this(insS.get(), digestType);
  }

  public Checksum(final Supplier<InputStream> insS) {
    this(insS, DIGEST_TYPE);
  }

}
