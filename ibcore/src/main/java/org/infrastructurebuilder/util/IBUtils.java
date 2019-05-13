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

import static java.util.stream.Collectors.toMap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;

import org.infrastructurebuilder.IBConstants;
import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.IBVersion;
import org.infrastructurebuilder.util.artifacts.JSONOutputEnabled;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IBUtils {
  public final static Function<JSONObject, JSONObject> cheapCopy = j -> {
    return new JSONObject(j.toString());
  };
  public final static Function<JSONObject, JSONObject> deepCopy = j -> {
    return new JSONObject(Objects.requireNonNull(j),
        Optional.ofNullable(JSONObject.getNames(Objects.requireNonNull(j))).orElse(new String[0]));
  };
  public final static Function<String, byte[]> getBytes = x -> {
    return Optional.ofNullable(x).orElse("").getBytes(StandardCharsets.UTF_8);
  };
  public final static Function<JSONObject, Map<String, String>> mapJSONToStringString = j -> {
    return Objects.requireNonNull(j).toMap().entrySet().stream()
        .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().toString()));
  };
  public final static Function<String, String> nullIfBlank = l -> {
    return new String(Optional.ofNullable(l).orElse("")).trim().length() > 0 ? l : null;
  };

  public final static Pattern p = Pattern.compile("(\\S+):(\\S+):(.*):(.*):(.*)");

  public final static Charset UTF_8 = StandardCharsets.UTF_8;

  private final static Random random = new SecureRandom();

  static final int BUFFER_SIZE = 10240;

  private static final Logger iolog = LoggerFactory.getLogger(IBUtils.class);

  @SuppressWarnings("unchecked")
  public static <T> Iterator<T> asIterator(final JSONArray array) {
    final List<T> l = new ArrayList<>();
    for (int i = 0; i < array.length(); ++i) {
      l.add((T) array.get(i));
    }
    return l.iterator();
  }

  public static Stream<JSONObject> asJSONObjectStream(final JSONArray array) {
    final Iterable<JSONObject> iterable = () -> {
      final List<JSONObject> l = new ArrayList<>();
      for (int i = 0; i < array.length(); ++i) {
        l.add(array.getJSONObject(i));
      }
      return l.iterator();
    };
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  public static Optional<Map<String, Object>> asOptFilesystemMap(final Object o) {
    final Map<String, Object> m = new HashMap<>();
    m.put(IBConstants.FILESYSTEM_CRYPTO_CONFIGURATION, o);
    return Optional.of(m);
  }

  public static <T> Stream<T> asStream(final JSONArray array) {
    @SuppressWarnings("unchecked")
    final Iterable<T> iterable = () -> {
      final List<T> l = new ArrayList<>();
      for (int i = 0; i < array.length(); ++i) {
        l.add((T) array.get(i));
      }
      return l.iterator();
    };
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  public static Stream<String> asStringStream(final JSONArray array) {
    final Iterable<String> iterable = () -> {
      final List<String> l = new ArrayList<>();
      for (int i = 0; i < array.length(); ++i) {
        l.add(array.getString(i));
      }
      return l.iterator();
    };
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  public static final Optional<URL> asURL(final String url) {
    try {
      return Optional.of(new URL(url));
    } catch (final MalformedURLException e) {
      return Optional.empty();
    }
  }

  public static Checksum checksumInputStream(final InputStream ins) throws NoSuchAlgorithmException, IOException {
    return new Checksum(digestInputStream(ins));
  }

  public static void copy(final InputStream source, final OutputStream sink) throws IOException {
    final byte[] buffer = new byte[BUFFER_SIZE];
    for (int n = 0; (n = Objects.requireNonNull(source, "source").read(buffer)) > 0;) {
      Objects.requireNonNull(sink, "sink").write(buffer, 0, n);
    }
    return;
  }

  public static Path copy(final Path in, final Path out) throws IOException {
    try (InputStream ins = Files.newInputStream(in); OutputStream outs = Files.newOutputStream(out)) {
      copy(ins, outs);
    }
    return out;
  }

  public static Checksum copyAndDigest(final InputStream ins, final OutputStream target)
      throws IOException, NoSuchAlgorithmException {
    try (DigestInputStream sink = new DigestInputStream(ins,
        MessageDigest.getInstance(IBConstants.DIGEST_TYPE))) {
      copy(sink, target);
      final Checksum d = new Checksum(sink.getMessageDigest().digest());

      return d;
    }
  }

  public final static void deletePath(final Path root) {
    try {
      Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
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
    } catch (final IOException e) {
      iolog.warn("Fail to delete path", e);
    }

  }

  public static byte[] digestInputStream(final InputStream ins) throws IOException, NoSuchAlgorithmException {

    final byte[] buf = new byte[BUFFER_SIZE];
    final MessageDigest md = MessageDigest.getInstance(IBConstants.DIGEST_TYPE);
    try (DigestInputStream sink = new DigestInputStream(ins, md)) {
      while (sink.read(buf) > 0) {
      }
      final byte[] digest = sink.getMessageDigest().digest();

      return digest;
    }
  }

  public final static void extractFile(final ZipInputStream zipIn, final Path filePath) throws IOException {

    Files.createDirectories(filePath.getParent());

    try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(filePath))) {
      final byte[] bytesIn = new byte[BUFFER_SIZE];
      int read = 0;
      while ((read = zipIn.read(bytesIn)) != -1) {
        bos.write(bytesIn, 0, read);
      }
    }
  }

  public static Path forceDirectoryPath(final File file) {
    return forceDirectoryPath(Objects.requireNonNull(file).toPath());
  }

  public static Path forceDirectoryPath(final Path path) {
    final Path p = path.toAbsolutePath();
    if (!Files.exists(p)) {
      IBException.cet.withTranslation(() -> Files.createDirectories(p));
    }
    if (!Files.isDirectory(p))
      throw new IBException("Path " + p + " is not a directory");
    return p;

  }

  public final static String generateNonHexRandomPasswordOfLength(final int len) {
    return generateNonHexRandomPasswordOfLength(len, IBUtils.random);
  }

  public final static String generateNonHexRandomPasswordOfLength(final int len, final Random random) {
    return new BigInteger(len * 8 + 16, random).toString(32).substring(0, len);
  }

  public final static String generateRandomPassword() {
    return generateRandomPasswordOfLength(24);
  }

  public final static String generateRandomPasswordOfLength(final int len) {
    return generateRandomPasswordOfLength(len, IBUtils.random);
  }

  public final static String generateRandomPasswordOfLength(final int len, final Random random) {
    return IBUtils.getHex(generateNonHexRandomPasswordOfLength(len, random).getBytes(UTF_8));
  }

  public final static String getDTS() {
    return IBConstants.dateFormatter.format(Instant.now());
  }

  public static String getHex(final byte[] raw) {
    return getHex(raw, UTF_8);
  }

  public static String getHex(final byte[] raw, final Charset cs) {
    return raw == null ? null : new String(DatatypeConverter.printHexBinary(raw).toLowerCase().getBytes(cs));
  }

  public static String getHexStringFromInputStream(final InputStream ins) throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    copy(ins, bos);
    return getHex(bos.toByteArray());
  }

  public final static String getJREExecutable() {
    return "java";
  }

  public static List<String> getJSONArrayAsListString(final JSONObject g, final String key) {
    return asStringStream(g.getJSONArray(key)).collect(Collectors.toList());
  }

  public final static JSONArray getJSONArrayFromJSONOutputEnabled(final List<? extends JSONOutputEnabled> v) {
    return Optional.ofNullable(v)
        .map(v1 -> new JSONArray(v1.stream().map(JSONOutputEnabled::asJSON).collect(Collectors.toList()))).orElse(null);
  }

  public static JSONObject getJSONObjectFromMapStringString(final Map<String, String> map) {
    final JSONObject j = new JSONObject();
    Objects.requireNonNull(map).forEach((k, v) -> j.put(k, v));
    return j;
  }

  public static Map<String, String> getMapStringStringFromJSONObject(final JSONObject j) {
    return Optional.ofNullable(j).orElse(new JSONObject()).toMap().entrySet().stream()
        .collect(toMap(k -> k.getKey(), v -> v.getValue().toString()));
  }

  public final static Map<String, String> getMapStringStringfromMapObjectObject(final Map<Object, Object> m) {
    return m.entrySet().stream().collect(Collectors.toMap(k -> k.getKey().toString(), v -> v.getValue().toString()));
  }

  public final static Map<String, String> getMapStringStringfromMapStringObject(final Map<String, Object> m) {
    return m.entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().toString()));
  }

  public final static Optional<Boolean> getOptBoolean(final JSONObject j, final String key) {
    return Optional.ofNullable(j.has(key) ? j.getBoolean(key) : null);
  }

  public final static Optional<Integer> getOptInteger(final JSONObject orig, final String key) {
    return Optional.ofNullable(Objects.requireNonNull(orig).opt(Objects.requireNonNull(key))).map(m -> m.toString())
        .map(Integer::parseInt);
  }

  public static Optional<JSONArray> getOptionalJSONArray(final JSONObject g, final String key) {
    return Optional.ofNullable(g.optJSONArray(key));
  }

  public final static Optional<Long> getOptLong(final JSONObject j, final String key) {
    if (!Objects.requireNonNull(j).has(key))
      return Optional.empty();
    return Optional.of(j.getLong(key));
  }

  public static Optional<String> getOptString(final JSONObject g, final String key) {
    final String s = g.optString(key).trim();
    return Optional.ofNullable("".equals(s) ? null : s);
  }

  public static <T> List<T> getServicesFor(final Class<T> c) {
    final List<T> list = new ArrayList<>();
    final ServiceLoader<T> loader = ServiceLoader.load(c);
    final Iterator<T> t = loader.iterator();
    while (t.hasNext()) {
      list.add(t.next());
    }

    return list;
  }

  public final static Map<String, String> getZipFileCreateMap(final Boolean create) {
    final HashMap<String, String> m = new HashMap<>();
    m.put("create", Optional.ofNullable(create).orElse(false).toString());
    return m;
  }

  public final static FileSystem getZipFileSystem(final Path pathToZip, final boolean create) throws IOException {
    final String pathToZip2 = Objects.requireNonNull(pathToZip).toAbsolutePath().toUri().getPath();
    return FileSystems.newFileSystem(URI.create("jar:file:" + pathToZip2), getZipFileCreateMap(create));
  }

  public static JSONObject hardMergeJSONObject(final JSONObject l, final JSONObject r) {
    final JSONObject j = new JSONObject(Objects.requireNonNull(l).toString());
    r.keySet().stream().forEach(key -> j.put(key, r.get(key)));
    return j;
  }

  public final static boolean hasAll(final JSONObject j, final Collection<String> keys) {
    return Objects.requireNonNull(keys).stream().filter(key -> !Objects.requireNonNull(j).has(key))
        .collect(Collectors.toList()).size() == 0;
  }

  public final static boolean hex8Digit(final String v) {
    try {
      if (v != null && v.length() == 8) {
        Long.parseLong(v, 16);
        return true;
      }
    } catch (final NumberFormatException e) {
    }
    return false;
  }

  public static byte[] hexStringToByteArray(final String s) {
    return DatatypeConverter.parseHexBinary(s.toUpperCase());
  }

  public static InputStream inputStreamFromHexString(final String hexString) {
    return new ByteArrayInputStream(hexStringToByteArray(hexString));
  }

  public static JSONObject joinFromMap(final Map<String, String> tags) {
    final JSONObject j = new JSONObject();
    tags.entrySet().stream().forEach(e -> j.put(e.getKey(), e.getValue()));
    return j;
  }

  public static URL mapStringToURLOrNull(final Optional<String> urlString) {
    return urlString.map(u -> {
      try {
        return new URL(u);
      } catch (final MalformedURLException e) {
        throw new IBException(e);
      }
    }).orElse((URL) null);
  }

  public static boolean matches(final JSONObject metadata, final Map<Pattern, Pattern> t) {
    return t.entrySet().size() == 0
        || t.entrySet().parallelStream().allMatch(e -> _match(metadata, e.getKey(), e.getValue()));
  }

  public static JSONArray mergeJSONArray(final JSONArray base, final JSONArray tbm) {
    final Set<String> b = base.toList().stream().map(Object::toString).collect(Collectors.toSet());
    final Set<String> b1 = tbm.toList().stream().map(Object::toString).collect(Collectors.toSet());
    b.addAll(b1);
    return new JSONArray(b);
  }

  public static JSONArray mergeJSONArray(final JSONArray base, final String tbm) {
    final Set<String> b = base.toList().stream().map(Object::toString).collect(Collectors.toSet());
    b.add(tbm);
    return new JSONArray(b);
  }

  public static JSONObject mergeJsonObjects(final JSONObject base, final JSONObject tbm) {
    final JSONObject b = new JSONObject(base.toString());
    tbm.keySet().stream().forEach(k -> {
      tbm.get(k);
      Object kGot = b.has(k) ? b.get(k) : tbm.get(k);
      final Object tGot = tbm.get(k);

      if (!tGot.equals(kGot))
        if (kGot instanceof JSONObject && tGot instanceof JSONObject) {
          kGot = mergeJsonObjects((JSONObject) kGot, (JSONObject) tGot);
        } else if (kGot instanceof JSONArray && tGot instanceof JSONArray) {
          kGot = mergeJSONArray((JSONArray) kGot, (JSONArray) tGot);
        } else if (kGot instanceof JSONArray && tGot instanceof String) {
          kGot = mergeJSONArray((JSONArray) kGot, (String) tGot);
        } else if (tGot instanceof JSONArray && kGot instanceof String) {
          kGot = mergeJSONArray((JSONArray) tGot, (String) kGot);
        } else if (tGot instanceof String && kGot instanceof String) {
          kGot = new JSONArray(new HashSet<>(Arrays.asList((String) tGot, (String) kGot)));
        }

      b.put(k, kGot);
    });
    return b;
  }

  public final static Map<String, String> mergeMapSS(final Map<String, String> base,
      final Map<String, String> overlay) {
    final Map<String, String> retVal = new HashMap<>(Objects.requireNonNull(base));
    retVal.putAll(Objects.requireNonNull(overlay));
    return retVal;
  }

  public static Path moveFileToNewIdPath(final Path oldFile, final UUID newPath) throws IOException {
    final Path newFile = oldFile.getParent().resolve(newPath.toString());
    return Files.move(oldFile, newFile, StandardCopyOption.ATOMIC_MOVE);
  }

  public static String readFile(final Path path) throws IOException {
    return readFile(path, UTF_8);
  }

  public static String readFile(final Path path, final Charset encoding) throws IOException {
    final byte[] encoded = Files.readAllBytes(path);
    return new String(encoded, encoding);
  }

  public final static JSONObject readJsonObject(final Path jsonFile) throws IOException {
    return new JSONObject(readFile(jsonFile));
  }

  public static JSONObject readToJSONObject(final InputStream ins) throws IOException {
    return new JSONObject(readToString(Objects.requireNonNull(ins)));
  }

  public static String readToString(final InputStream ins) throws IOException {
    return readToString(ins, UTF_8);
  }

  public static String readToString(final InputStream ins, final Charset charset) throws IOException {
    final ByteArrayOutputStream result = new ByteArrayOutputStream();
    final byte[] buffer = new byte[2048];
    int length;
    while ((length = ins.read(buffer)) != -1) {
      result.write(buffer, 0, length);
    }
    return result.toString(charset.name());
  }

  public static Map<String, String> splitToMap(final JSONObject json) {
    return json.toMap().entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().toString()));
  }

  public static void unzip(final Path zipFilePath, final Path destDirectory) throws IOException {
    Files.createDirectories(destDirectory);
    try (InputStream ins = Files.newInputStream(zipFilePath)) {
      try (ZipInputStream zipIn = new ZipInputStream(ins)) {
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
          if (!entry.isDirectory()) {
            extractFile(zipIn, destDirectory.resolve(entry.getName()));
          } else {
            Files.createDirectories(destDirectory.resolve(entry.getName()));
          }
          zipIn.closeEntry();
          entry = zipIn.getNextEntry();
        }
      }
    }
  }

  public static boolean verify(final JarFile jar) throws IOException {
    final Enumeration<JarEntry> entries = jar.entries();
    while (entries.hasMoreElements()) {
      final JarEntry entry = entries.nextElement();
      try {
        jar.getInputStream(entry);
      } catch (final SecurityException se) {
        return false;
      }
    }
    return true;
  }

  public final static Path writeString(final Path path, final String string) throws IOException {
    return writeString(path, string, UTF_8);
  }

  public final static Path writeString(final Path path, final String string, final Charset charset) throws IOException {
    Files.write(path, string.getBytes(charset));
    return path;
  }

  public static Optional<URL> zipEntryToUrl(final Optional<URL> p, final ZipEntry e) {
    return Objects.requireNonNull(p).map(u -> IBException.cet
        .withReturningTranslation(() -> new URL("jar:" + u.toExternalForm() + "!/" + e.getName())));
  }

  private static boolean _match(final JSONObject metadata, final Pattern key, final Pattern value) {
    return metadata.keySet().parallelStream().anyMatch(k -> _matchitem(k, metadata.get(k).toString(), key, value));
  }

  private static boolean _matchitem(final String key, final String val, final Pattern kPattern,
      final Pattern vPattern) {
    return (kPattern == null || kPattern.matcher(key).matches())
        && (vPattern == null || vPattern.matcher(val).matches());
  }

  public static Optional<IBVersion> apiVersion(final GAV gav) {
    return Objects.requireNonNull(gav).getVersion().map(DefaultIBVersion::new).map(DefaultIBVersion::apiVersion);
  }

//  public final static Function<Artifact, GAV> artifactToGAV = (art) -> {
//    final Path p = Optional.ofNullable(art.getFile()).map(p2 -> p2.toPath()).orElse(null);
//    return new DefaultGAV(art.getGroupId(), art.getArtifactId(), art.getClassifier(), art.getVersion(),
//        art.getExtension()).withFile(p);
//  };

  public static String toInternalSignaturePath(final GAV gav) {
    return gav.getGroupId() + ":" + gav.getArtifactId() + ":" + gav.getClassifier().orElse("") + ":"
        + gav.getVersion().orElse("___") + ":" + gav.getExtension();
  }

  public static String getArtifactFilenamePath(final GAV art) {
    return String.format("%s%s%s.%s", art.getArtifactId(), art.getVersion().map(sv -> "-" + sv).orElse(""),
        art.getClassifier().map(cls -> "-" + cls).orElse(""), art.getExtension());
  }

  public static boolean _matcher(final String pattern, final String value) {
    if (value == null)
      return true;
    if (pattern == null)
      return true;
    final boolean b = java.util.regex.Pattern.compile(pattern).matcher(value).matches();
    return b;
  }

//  public static boolean _versionmatcher(final GAV art, final GAV range) {
//    if (!art.getVersion().isPresent())
//      return true;
//    if (!range.getVersion().isPresent())
//      return true;
//    try {
//      final boolean b = inRange(art, ((DefaultGAV) range).asRange());
//      return b;
//    } catch (final IBException e) {
//      return false;
//    }
//  }

//  public static Artifact asArtifact(final GAV art) {
//    return new DefaultArtifact(art.getDefaultSignaturePath());
//  }
//
//  public static Dependency asDependency(final GAV art, final String scope) {
//    return new Dependency(asArtifact(art), scope);
//  }

//  public static int compareVersion(final GAV art, final GAV otherVersion)
//      throws org.eclipse.aether.version.InvalidVersionSpecificationException {
//    return getVersionScheme().parseVersion(art.getVersion().get().toString())
//        .compareTo(getVersionScheme().parseVersion(otherVersion.getVersion().get().toString()));
//  }

//  public static GAV fromArtifact(final Artifact a) {
//    return new DefaultGAV(a.getGroupId(), a.getArtifactId(), a.getClassifier(), a.getVersion(), a.getExtension())
//        .withFile(Optional.ofNullable(a.getFile()).map(File::toPath).orElse(null));
//  }

  public static Optional<IBVersion> getVersion(final GAV art) {
    return art.getVersion().map(DefaultIBVersion::new);
  }

//  public static VersionScheme getVersionScheme() {
//    return new org.eclipse.aether.util.version.GenericVersionScheme();
//  }
//
//  public static boolean inRange(final GAV art, final String versionRange) {
//    return IBException.cet.withReturningTranslation(() -> {
//      return getVersionScheme().parseVersionRange(versionRange)
//          .containsVersion(getVersionScheme().parseVersion(art.getVersion().orElse(null)));
//    });
//  }

//  public static boolean matches(final GAV art, final GAV pattern) {
//    return _matcher(pattern.getGroupId(), art.getGroupId()) && _matcher(pattern.getArtifactId(), art.getArtifactId())
//        && _matcher(pattern.getClassifier().orElse(".*"), art.getClassifier().orElse(null))
//        && _matcher(pattern.getExtension(), art.getExtension()) && _versionmatcher(art, pattern);
//  }



}
