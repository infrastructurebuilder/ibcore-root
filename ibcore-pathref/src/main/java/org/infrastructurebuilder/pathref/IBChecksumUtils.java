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
import static java.nio.file.Files.walkFileTree;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.constants.IBConstants.DIGEST_TYPE;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.function.Function;

import org.infrastructurebuilder.exceptions.IBException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IBChecksumUtils {
  private static final Logger iolog = LoggerFactory.getLogger(IBChecksumUtils.class.getName());
  static final int BUFFER_SIZE = 10240;
  public final static Function<String, byte[]> getBytes = x -> {
    return ofNullable(x).orElse("").getBytes(UTF_8);
  };
  public static Function<String, String> stripTrailingSlash = (s) -> {
    if (s == null)
      return s;
    String k = s;
    while (k.endsWith("/") && k.length() > 0)
      k = k.substring(0, k.length() - 1);
    return k;
  };

  public final static Function<JSONObject, JSONObject> deepCopy = j -> {
    return new JSONObject(requireNonNull(j), ofNullable(JSONObject.getNames(requireNonNull(j))).orElse(new String[0]));
  };

  public final static void deletePath(final Path root) {
    try {
      walkFileTree(root, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

      });
      if (Files.exists(root))
        Files.delete(root);
    } catch (final IOException e) {
      iolog.debug("Fail to delete path", e);
    }

  }




  public static byte[] digestInputStream(final String type, final InputStream ins) throws IOException {
    final byte[] buf = new byte[BUFFER_SIZE];
    try (DigestInputStream sink = new DigestInputStream(ins, cet.returns(() -> MessageDigest.getInstance(type)))) {
      while (sink.read(buf) > 0) {}
      return sink.getMessageDigest().digest();
    }
  }

  public static byte[] digestInputStream(final InputStream ins) throws IOException {
    return digestInputStream(DIGEST_TYPE, ins);
  }

  public static String getHex(final byte[] raw) {
    return getHex(raw, UTF_8);
  }

  public static void copy(final InputStream source, final OutputStream sink) throws IOException {
    final byte[] buffer = new byte[BUFFER_SIZE];
    for (int n = 0; (n = requireNonNull(source, "source").read(buffer)) > 0;) {
      requireNonNull(sink, "sink").write(buffer, 0, n);
    }
    return;
  }

  public static Path copy(final Path in, final Path out) throws IOException {
    try (

        InputStream ins = Files.newInputStream(in);

        OutputStream outs = Files.newOutputStream(out)) {
      copy(ins, outs);
    }
    return out;
  }

  public static String byteToHex(byte num) {
    char[] hexDigits = new char[2];
    hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
    hexDigits[1] = Character.forDigit((num & 0xF), 16);
    return new String(hexDigits);
  }

  public static String getHex(final byte[] raw, final Charset cs) {
    if (raw == null)
      return null;
    StringBuffer hexStringBuffer = new StringBuffer();
    for (int i = 0; i < raw.length; i++) {
      hexStringBuffer.append(byteToHex(raw[i]));
    }
    return new String(hexStringBuffer.toString().toLowerCase().getBytes(cs));
  }

  public static String getHexStringFromInputStream(final InputStream ins) throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    copy(ins, bos);
    return getHex(bos.toByteArray());
  }

  /**
   * Do not use this to process large files!
   *
   * @param ins
   * @return
   * @throws IOException
   * @throws NoSuchAlgorithmException
   */
  public static byte[] digestReader(final Reader ins) throws IOException {
    return digestInputStream(readerToInputStream(ins));
  }

  /**
   * Do not use this to process large files!
   *
   * @param ins
   * @return
   */
  public static InputStream readerToInputStream(Reader ins) {
    return IBException.cet.returns(() -> {
      char[] b = new char[1024];
      StringBuilder string = new StringBuilder();
      int i;
      while ((i = ins.read(b, 0, b.length)) != -1) {
        string.append(b, 0, i);
      }
      ins.close();
      return new ByteArrayInputStream(string.toString().getBytes(UTF_8));
    });
  }

  public static byte[] hexStringToByteArray(final String s) {
    if (s == null)
      return null;

    if (s.length() % 2 == 1) {
      throw new IllegalArgumentException("Not a hex string");
    }

    byte[] bytes = new byte[s.length() / 2];
    for (int i = 0; i < s.length(); i += 2) {
      bytes[i / 2] = hexToByte(s.substring(i, i + 2));
    }
    return bytes;
  }

  public static InputStream inputStreamFromHexString(final String hexString) {
    return new ByteArrayInputStream(hexStringToByteArray(hexString));
  }

  public static byte hexToByte(String hexString) {
    int firstDigit = toDigit(hexString.charAt(0));
    int secondDigit = toDigit(hexString.charAt(1));
    return (byte) ((firstDigit << 4) + secondDigit);
  }

  private static int toDigit(char hexChar) {
    int digit = Character.digit(hexChar, 16);
    if (digit == -1) {
      throw new IllegalArgumentException("Invalid Hexadecimal Character: " + hexChar);
    }
    return digit;
  }
  public final static Function<JSONObject, String> deepMapJSONtoOrderedString = j -> {
    return new IBChecksumUtils()._deepMapJSONtoOrderedString(j);
  };

  private final String _deepMapJSONtoOrderedString(JSONObject j) {
    StringJoiner sb = new StringJoiner(",");
    var so = new TreeMap<String, Object>(requireNonNull(j).toMap());
    so.forEach((k, v) -> {
      String ov;
      if (v == null || JSONObject.NULL.equals(v)) {
        ov = "null";
      } else if (v instanceof JSONObject) {
        ov = String.format("{%s}", _deepMapJSONtoOrderedString((JSONObject) v));
      } else if (v instanceof String) {
        ov = String.format("\"%s\"", (String) v);
      } else {
        ov = v.toString();
      }
      sb.add(String.format("%s:%s", k, ov));
    });
    return sb.toString();
  }

  public IBChecksumUtils() {
  }

}
