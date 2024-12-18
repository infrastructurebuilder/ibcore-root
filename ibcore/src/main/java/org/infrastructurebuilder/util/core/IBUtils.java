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

import static java.lang.Long.MAX_VALUE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Files.readAttributes;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Comparator.nullsFirst;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.Spliterator.ORDERED;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static org.infrastructurebuilder.constants.IBConstants.DIGEST_TYPE;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedSet;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.IBChecksumUtils;
import org.infrastructurebuilder.pathref.JSONOutputEnabled;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class IBUtils {

  public final static String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

  private static boolean isJar;
  private static boolean isZip;
  static {
    try {
      new URL("zip:file://z.zip!/a");
      isZip = true;
    } catch (MalformedURLException e) {
      isZip = false;
    }
    try {
      new URL("jar:file://z.jar!/a");
      isJar = true;
    } catch (MalformedURLException e) {
      isJar = false;
    }
    if (!isJar && !isZip)
      throw new IBException("THIS JVM CANNOT HANDLE ARCHIVES.  IBDATA WILL NOT WORK");
  }

  public final static Function<String, Optional<BigInteger>> getIntValue = (s) -> {
    try {
      return ofNullable(s).map(BigInteger::new);
    } catch (NumberFormatException e) {
      return empty();
    }
  };

  public final static Function<String, Optional<BigDecimal>> getDecimalValue = (s) -> {
    try {
      return ofNullable(s).map(BigDecimal::new);
    } catch (NumberFormatException e) {
      return empty();
    }
  };

  public final static Function<Properties, Map<String, String>> propertiesToMapSS = (p) -> {
    final Map<String, String> m = new HashMap<>();
    Optional.ofNullable(p)
        .ifPresent(properties -> properties.stringPropertyNames().forEach(n -> m.put(n, p.getProperty(n))));
    return m;
  };

  private final static TransformerFactory tf = TransformerFactory.newInstance();

  private final static Supplier<Transformer> tfSupplier = () -> {
    return cet.returns(() -> tf.newTransformer());
  };

  private final static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  public final static String stringFromDocument(Document document) {
    StringWriter writer = new StringWriter();
    cet.translate(() -> tfSupplier.get().transform(new DOMSource((Document) document), new StreamResult(writer)));
    return writer.toString();
  }

  public final static Function<Path, Optional<BasicFileAttributes>> getAttributes = (i) -> {
    Optional<BasicFileAttributes> retVal = empty();
    try {
      retVal = of(readAttributes(requireNonNull(i), BasicFileAttributes.class));
    } catch (IOException e) {
      // log.error("Error reading basic attributes " + i, e);
    }
    return retVal;
  };

  public final static Function<String, Optional<Document>> strToDoc = (xmlString) -> {
    Document doc;
    try {
      doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlString)));
    } catch (Exception e) {
      doc = null;
    }
    return ofNullable(doc);
  };

  public final static boolean isJarArchive() {
    return isJar;
  }

  public final static boolean isZipArchive() {
    return isZip;
  }

  public final static java.util.Comparator<String> nullSafeCaseInsensitiveStringComparator = nullsFirst(
      String::compareToIgnoreCase);
  public final static java.util.Comparator<String> nullSafeStringComparator = nullsFirst(String::compareTo);
  public final static java.util.Comparator<java.util.UUID> nullSafeUUIDComparator = nullsFirst(
      java.util.UUID::compareTo);
  public final static java.util.Comparator<java.util.Date> nullSafeDateComparator = nullsFirst(
      java.util.Date::compareTo);

  public final static java.util.Comparator<java.time.Instant> nullSafeInstantComparator = nullsFirst(
      java.time.Instant::compareTo);

  public final static Function<String, Optional<URL>> nullSafeURLMapper = (s) -> {
    return ofNullable(s).map(u -> cet.returns(() -> translateToWorkableArchiveURL(u)));
  };

  public final static Function<Object, Optional<String>> nullSafeObjectToString = (o) -> {
    return ofNullable(o).map(k -> k.toString());
  };

  /**
   * Map a Map/String,String to a Properties object
   */
  public final static Function<Map<String, String>, Properties> mapSS2Properties = (m) -> {
    Properties p = new Properties();
    requireNonNull(m).entrySet().stream().forEach(e -> p.setProperty(e.getKey(), e.getValue()));
    return p;
  };

  public final static URL reURL(String url) {
    return ofNullable(url).map(u -> cet.returns(() -> translateToWorkableArchiveURL(u))).orElse(null);
  }

  public final static Function<JSONObject, JSONObject> cheapCopy = j -> {
    return new JSONObject(j.toString());
  };

  public final static Function<JSONObject, Map<String, String>> mapJSONToStringString = j -> {
    return requireNonNull(j).toMap().entrySet().stream().collect(toMap(k -> k.getKey(), v -> v.getValue().toString()));
  };

  public final static Function<String, String> nullIfBlank = l -> {
    return new String(ofNullable(l).orElse("")).trim().length() > 0 ? l.trim() : null;
  };

  public final static Function<String, String> blankIfNull = l -> {
    return ofNullable(l).orElse("");
  };

  public final static Function<String, Date> parseISODateTime = s -> Date.from(Instant
      .from(java.time.OffsetDateTime.parse(requireNonNull(s), java.time.format.DateTimeFormatter.ISO_DATE_TIME)));

  public final static Pattern p = Pattern.compile("(\\S+):(\\S+):(.*):(.*):(.*)");

  private final static Random random = new SecureRandom();

  static final int BUFFER_SIZE = 10240;

  private static final Logger iolog = LoggerFactory.getLogger(IBUtils.class.getName());

  public static Stream<String> readInputStreamAsStringStream(InputStream ins) {
    return cet.returns(() -> new BufferedReader(new InputStreamReader(ins)).lines());
  }

  public final static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().startsWith("windows");
  }

  @SuppressWarnings("unchecked")
  public static <T> Iterator<T> asIterator(final JSONArray array) {
    final List<T> l = new ArrayList<>();
    for (int i = 0; i < array.length(); ++i) {
      l.add((T) array.get(i));
    }
    return l.iterator();
  }

  /**
   * Modified from https://stackoverflow.com/questions/33242577/how-do-i-turn-a-java-enumeration-into-a-stream
   *
   * @param <T>
   * @param e1
   * @param parallel
   * @return
   */
  public final static <T> Stream<T> enumerationAsStream(Enumeration<T> e1, boolean parallel) {
    return stream(new Spliterators.AbstractSpliterator<T>(MAX_VALUE, ORDERED) {
      public boolean tryAdvance(Consumer<? super T> advance) {
        if (e1.hasMoreElements()) {
          advance.accept(e1.nextElement());
          return true;
        }
        return false;
      }

      public void forEachRemaining(Consumer<? super T> action) {
        while (e1.hasMoreElements())
          action.accept(e1.nextElement());
      }
    }, parallel);
  }

  public final static <T> Stream<T> iteratorAsStream(Iterator<T> e1, boolean parallel) {
    return stream(Spliterators.spliteratorUnknownSize(e1, 0), parallel);
  }

  public static Stream<JSONObject> asJSONObjectStream(final JSONArray array) {
    final Iterable<JSONObject> iterable = () -> {
      final List<JSONObject> l = new ArrayList<>();
      for (int i = 0; i < array.length(); ++i) {
        l.add(array.getJSONObject(i));
      }
      return l.iterator();
    };
    return stream(iterable.spliterator(), false);
  }

  public static <T> Iterable<T> getIterable(Iterator<T> iterator) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        return iterator;
      }
    };
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
    return stream(iterable.spliterator(), false);
  }

  public static Stream<String> asStringStream(final JSONArray array) {
    final Iterable<String> iterable = () -> {
      final List<String> l = new ArrayList<>();
      for (int i = 0; i < requireNonNull(array).length(); ++i) {
        l.add(array.getString(i));
      }
      return l.iterator();
    };
    return stream(iterable.spliterator(), false);
  }

  public static final Optional<URL> asURL(final String url) {
    try {
      return Optional.of(translateToWorkableArchiveURL(url));
    } catch (final IBException e) {
      return Optional.empty();
    }
  }

  public static Path copyTree(final Path in, final Path out) throws IOException {
    walkFileTree(in, new SimpleFileVisitor<Path>() {

      private final Path outTarget(Path p) {
        return out.resolve(in.relativize(p));
      }

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        super.preVisitDirectory(dir, attrs);
        Path outTarget = outTarget(dir);
        if (!Files.exists(outTarget.getParent()) || !Files.isDirectory(outTarget.getParent()))
          Files.createDirectories(outTarget.getParent());
        if (!Files.isDirectory(outTarget))
          Files.createDirectory(outTarget(dir));
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path outTarget = outTarget(file);
        Files.copy(file, outTarget);
        return FileVisitResult.CONTINUE;
      }

    });
    return out;
  }

  public static Checksum copyAndDigest(final String type, final InputStream ins, final OutputStream target)
      throws IOException, NoSuchAlgorithmException {
    try (DigestInputStream sink = new DigestInputStream(ins, MessageDigest.getInstance(requireNonNull(type)))) {
      IBChecksumUtils.copy(sink, target);
      final Checksum d = new Checksum(sink.getMessageDigest().digest());

      return d;
    }

  }

  public static Checksum copyAndDigest(final InputStream ins, final OutputStream target)
      throws IOException, NoSuchAlgorithmException {
    return copyAndDigest(DIGEST_TYPE, ins, target);
  }

  public final static Path copyToDeletedOnExitTempPath(String prefix, String suffix, final InputStream source)
      throws IOException {
    final Path target;
    target = Files.createTempFile(prefix, suffix);
    target.toFile().deleteOnExit();
    try (OutputStream outs = Files.newOutputStream(target)) {
      IBChecksumUtils.copy(source, outs);
    }
    return target;
  }

  public final static SortedSet<Path> allFilesInTree(final Path root) {
    SortedSet<Path> l = new TreeSet<>();
    cet.translate(() -> walkFileTree(root, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        l.add(file.toAbsolutePath());
        return FileVisitResult.CONTINUE;
      }

    }));
    return l;

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
    return forceDirectoryPath(requireNonNull(file).toPath());
  }

  public static Path forceDirectoryPath(final Path path) {
    final Path p = path.toAbsolutePath();
    if (!Files.exists(p)) {
      cet.translate(() -> Files.createDirectories(p));
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
    return IBChecksumUtils.getHex(generateNonHexRandomPasswordOfLength(len, random).getBytes(UTF_8));
  }

  public final static String getDTS() {
    return IBConstants.dateFormatter.format(Instant.now());
  }

  public final static String getJREExecutable() {
    return "java";
  }

  public static List<String> getJSONArrayAsListString(final JSONObject g, final String key) {
    return asStringStream(g.getJSONArray(key)).toList();
  }

  public final static JSONArray getJSONArrayFromJSONOutputEnabled(final List<? extends JSONOutputEnabled> v) {
    return ofNullable(v).map(v1 -> new JSONArray(v1.stream().map(JSONOutputEnabled::asJSON).toList())).orElse(null);
  }

  public static JSONObject getJSONObjectFromMapStringString(final Map<String, String> map) {
    final JSONObject j = new JSONObject();
    requireNonNull(map).forEach((k, v) -> j.put(k, v));
    return j;
  }

  public static Map<String, String> getMapStringStringFromJSONObject(final JSONObject j) {
    return ofNullable(j).orElse(new JSONObject()).toMap().entrySet().stream()
        .collect(toMap(k -> k.getKey(), v -> v.getValue().toString()));
  }

  public final static Map<String, String> getMapStringStringfromMapObjectObject(final Map<Object, Object> m) {
    return m.entrySet().stream().collect(toMap(k -> k.getKey().toString(), v -> v.getValue().toString()));
  }

  public final static Map<String, String> getMapStringStringfromMapStringObject(final Map<String, Object> m) {
    return m.entrySet().stream().collect(toMap(k -> k.getKey(), v -> v.getValue().toString()));
  }

  public final static Optional<Boolean> getOptBoolean(final JSONObject j, final String key) {
    return ofNullable(j.has(key) ? j.getBoolean(key) : null);
  }

  public final static Optional<Integer> getOptInteger(final JSONObject orig, final String key) {
    return ofNullable(requireNonNull(orig).opt(requireNonNull(key))).map(m -> m.toString()).map(Integer::parseInt);
  }

  public static Optional<JSONArray> getOptionalJSONArray(final JSONObject g, final String key) {
    return ofNullable(g.optJSONArray(key));
  }

  public final static Optional<Long> getOptLong(final JSONObject j, final String key) {
    if (!requireNonNull(j).has(key))
      return Optional.empty();
    return Optional.of(j.getLong(key));
  }

  public static Optional<String> getOptString(final JSONObject g, final String key) {
    final String s = g.optString(key).trim();
    return ofNullable("".equals(s) ? null : s);
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
    m.put("create", ofNullable(create).orElse(false).toString());
    return m;
  }

  public final static Function<File, URL> fileToURL = (f) -> cet.returns(() -> requireNonNull(f).toURI().toURL());

  public final static FileSystem getZipFileSystem(final Path pathToZip, final boolean create) throws IOException {
    final String pathToZip2 = requireNonNull(pathToZip).toAbsolutePath().toUri().getPath();
    return FileSystems.newFileSystem(URI.create("jar:file:" + pathToZip2), getZipFileCreateMap(create));
  }

  public static JSONObject hardMergeJSONObject(final JSONObject l, final JSONObject r) {
    final JSONObject j = new JSONObject(requireNonNull(l).toString());
    r.keySet().stream().forEach(key -> j.put(key, r.get(key)));
    return j;
  }

  public final static boolean hasAll(final JSONObject j, final Collection<String> keys) {
    return requireNonNull(keys).stream().filter(key -> !requireNonNull(j).has(key)).toList().size() == 0;
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

  public static JSONObject joinFromMap(final Map<String, String> tags) {
    final JSONObject j = new JSONObject();
    tags.entrySet().stream().forEach(e -> j.put(e.getKey(), e.getValue()));
    return j;
  }

  public static URL mapStringToURLOrNull(final Optional<String> urlString) {
    return urlString.map(IBUtils::translateToWorkableArchiveURL).orElse(null);

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
          kGot = new JSONArray(new HashSet<>(Arrays.asList((String) kGot, (String) tGot)));
        }

      b.put(k, kGot);
    });
    return b;
  }

  public final static Map<String, String> mergeMapSS(final Map<String, String> base,
      final Map<String, String> overlay) {
    final Map<String, String> retVal = new HashMap<>(requireNonNull(base));
    retVal.putAll(requireNonNull(overlay));
    return retVal;
  }

  /**
   * Try to atomically move a source path to a target path.
   *
   * IF THAT FAILS TO WORK, then copy source to target and then TRY to delete source but accept if it doesn't work
   *
   * @param source
   * @param target
   * @return target
   * @throws IOException
   */
  public static Path moveAtomic(final Path source, final Path target) throws IOException {
    return Files.isDirectory(source) ? moveDirectoryAtomic(source, target) : moveFileAtomic(source, target);
  }

  private static Path moveFileAtomic(final Path source, final Path target) throws IOException {
    if (Files.isDirectory(source))
      throw new IBException("Cannot move a directory as a file");
    Path s1 = requireNonNull(source).toAbsolutePath();
    Path t1 = requireNonNull(target).toAbsolutePath();
    if (Files.isDirectory(t1))
      t1 = t1.resolve(s1.getFileName());
    if (s1.equals(t1))
      return t1; // We do nothing here
    try {
      return Files.move(source, target, ATOMIC_MOVE, COPY_ATTRIBUTES, NOFOLLOW_LINKS);
    } catch (UnsupportedOperationException | AtomicMoveNotSupportedException amns) {
      // If we cannot move atomic, then copy the file instead and then TRY to delete
      // it but accept delete failure
      Path retVal = Files.copy(source, target, REPLACE_EXISTING);
      try {
        Files.delete(source);
      } catch (IOException e) {
        // Do nothing here. Leave trash on the filesystem. Boo!
      }
      return retVal;
    }
  }

  private static Path moveDirectoryAtomic(final Path source, final Path target) throws IOException {
    if (requireNonNull(source).toAbsolutePath().equals(requireNonNull(target).toAbsolutePath()))
      return target; // We do nothing here
    try {
      walkFileTree(source, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          Path newTarget = target.resolve(source.relativize(dir));
          Files.createDirectories(newTarget);
          return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
          IBChecksumUtils.deletePath(dir);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
          return super.visitFileFailed(file, exc);
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
          Path newTarget = target.resolve(source.relativize(file));
          try {
            moveFileAtomic(file, newTarget);
            Files.delete(file);
          } catch (IOException e) {
            // do Nothing
          }
          return FileVisitResult.CONTINUE;
        }

      });
    } catch (final IOException e) {
      iolog.warn("Fail to move entirely", e);
    }
    return target;

  }

  @Deprecated
  public static Path moveFileToNewIdPath(final Path oldFile, final UUID d) throws IOException {
    final Path newFile = oldFile.getParent().resolve(d.toString());
    return Files.move(oldFile, newFile, ATOMIC_MOVE);

  }

  public static Path moveFileToNewIdPath(final Path oldFile, final Checksum d) throws IOException {
    return moveFileToNewIdPath(oldFile, d.asUUID().get());
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
    return new JSONObject(readToString(requireNonNull(ins)));
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
    return json.toMap().entrySet().stream().collect(toMap(k -> k.getKey(), v -> v.getValue().toString()));
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
    return requireNonNull(p)
        .map(u -> cet.returns(() -> translateToWorkableArchiveURL("jar:" + u.toExternalForm() + "!/" + e.getName())));
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
    return requireNonNull(gav).getVersion().map(DefaultIBVersion::new).map(DefaultIBVersion::apiVersion);
  }

  // public final static Function<Artifact, GAV> artifactToGAV = (art) -> {
  // final Path p = Optional.ofNullable(art.getFile()).map(p2 ->
  // p2.toPath()).orElse(null);
  // return new DefaultGAV(art.getGroupId(), art.getArtifactId(),
  // art.getClassifier(), art.getVersion(),
  // art.getExtension()).withFile(p);
  // };

  public static String toInternalSignaturePath(final GAV gav) {
    return gav.getGroupId() + ":" + gav.getArtifactId() + ":" + gav.getClassifier().orElse("") + ":"
        + gav.getVersion().orElse("___") + ":" + gav.getExtension().get();
  }

  public static String getArtifactFilenamePath(final GAV art) {
    return String.format("%s%s%s.%s", art.getArtifactId(), art.getVersion().map(sv -> "-" + sv).orElse(""),
        art.getClassifier().map(cls -> "-" + cls).orElse(""), art.getExtension().get());
  }

  public static boolean _matcher(final String pattern, final String value) {
    if (value == null)
      return true;
    if (pattern == null)
      return true;
    final boolean b = java.util.regex.Pattern.compile(pattern).matcher(value).matches();
    return b;
  }

  // public static boolean _versionmatcher(final GAV art, final GAV range) {
  // if (!art.getVersion().isPresent())
  // return true;
  // if (!range.getVersion().isPresent())
  // return true;
  // try {
  // final boolean b = inRange(art, ((DefaultGAV) range).asRange());
  // return b;
  // } catch (final IBException e) {
  // return false;
  // }
  // }

  // public static Artifact asArtifact(final GAV art) {
  // return new DefaultArtifact(art.getDefaultSignaturePath());
  // }
  //
  // public static Dependency asDependency(final GAV art, final String scope) {
  // return new Dependency(asArtifact(art), scope);
  // }

  // public static int compareVersion(final GAV art, final GAV otherVersion)
  // throws org.eclipse.aether.version.InvalidVersionSpecificationException {
  // return getVersionScheme().parseVersion(art.getVersion().get().toString())
  // .compareTo(getVersionScheme().parseVersion(otherVersion.getVersion().get().toString()));
  // }

  // public static GAV fromArtifact(final Artifact a) {
  // return new DefaultGAV(a.getGroupId(), a.getArtifactId(), a.getClassifier(),
  // a.getVersion(), a.getExtension())
  // .withFile(Optional.ofNullable(a.getFile()).map(File::toPath).orElse(null));
  // }

  public static Optional<IBVersion> getVersion(final GAV art) {
    return art.getVersion().map(DefaultIBVersion::new);
  }

  // public static VersionScheme getVersionScheme() {
  // return new org.eclipse.aether.util.version.GenericVersionScheme();
  // }
  //
  // public static boolean inRange(final GAV art, final String versionRange) {
  // return IBException.cet.returns(() -> {
  // return getVersionScheme().parseVersionRange(versionRange)
  // .containsVersion(getVersionScheme().parseVersion(art.getVersion().orElse(null)));
  // });
  // }

  // public static boolean matches(final GAV art, final GAV pattern) {
  // return _matcher(pattern.getGroupId(), art.getGroupId()) &&
  // _matcher(pattern.getArtifactId(), art.getArtifactId())
  // && _matcher(pattern.getClassifier().orElse(".*"),
  // art.getClassifier().orElse(null))
  // && _matcher(pattern.getExtension(), art.getExtension()) &&
  // _versionmatcher(art, pattern);
  // }

  public static URL translateToWorkableArchiveURL(String url) {
    requireNonNull(url);
    String retVal = url;
    if (url.startsWith("jar:") && !isJar)
      retVal = "zip:" + url.substring(4);
    if (url.startsWith("zip:") && !isZip)
      retVal = "jar:" + url.substring(4);
    final String f = retVal;
    return cet.returns(() -> new URL(f));

  }

  public final static Path getRootFromURL(String[] uA) {
    return IBException.cet.returns(() -> {
      final FileSystem fs = FileSystems.newFileSystem(URI.create(uA[0]), new HashMap<String, String>());
      return fs.getPath(uA[1]);
    });
  }

//  public final static String stringFromDOM(Document d) {
//    Transformer transformer;
//    transformer = cet.returns(() -> tf.newTransformer());
//    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//    StringWriter outStream = new StringWriter();
//    cet.translate(() -> transformer.transform(new DOMSource(d), new StreamResult(outStream)));
//    return outStream.toString();
//  }

  public final static String removeXMLPrefix(String s) {
    return (s.startsWith(XML_PREFIX)) ? s.replace(XML_PREFIX, "").trim() : s;
  }

  public final static Path touchFile(final Path path) {
    return IBException.cet.returns(() -> {
      if (exists(path)) {
        if (!isRegularFile(path) || !isWritable(path))
          throw new IBException("File " + path.toAbsolutePath() + " is not available to write");
        return path;
      } else {
        createDirectories(path.getParent());
      }
      return createFile(path);
    });
  }

  // non-static but possibly memory intensive methods

  public static boolean isParent(Path targetDir, Path sourcePath) {
    return Objects.requireNonNull(targetDir).startsWith(sourcePath);
  };

  // ----
  // Pulled verbatim from org.apache.maven.shared.utils.StringUtils
  /**
   * <p>
   * Checks if a String is non <code>null</code> and is not empty (<code>length &gt; 0</code>).
   * </p>
   *
   * @param str the String to check
   * @return true if the String is non-null, and not length zero
   */
  public static boolean isNotEmpty(@Nullable String str) {
    return ((str != null) && (str.length() > 0));
  }

  /**
   * <p>
   * Checks if a (trimmed) String is <code>null</code> or empty.
   * </p>
   *
   * <p>
   * <strong>Note:</strong> In future releases, this method will no longer trim the input string such that it works
   * complementary to {@link #isNotEmpty(String)}. Code that wants to test for whitespace-only strings should be
   * migrated to use {@link #isBlank(String)} instead.
   * </p>
   *
   * @param str the String to check
   * @return <code>true</code> if the String is <code>null</code>, or length zero once trimmed
   */
  public static boolean isEmpty(@Nullable String str) {
    return ((str == null) || (str.trim().length() == 0));
  }

  /**
   * <p>
   * Checks if a String is whitespace, empty ("") or null.
   * </p>
   *
   * <pre>
   * StringUtils.isBlank(null)      = true
   * StringUtils.isBlank("")        = true
   * StringUtils.isBlank(" ")       = true
   * StringUtils.isBlank("bob")     = false
   * StringUtils.isBlank("  bob  ") = false
   * </pre>
   *
   * @param str the String to check, may be null
   * @return <code>true</code> if the String is null, empty or whitespace
   *
   */
  public static boolean isBlank(@Nullable String str) {
    int strLen;
    // CHECKSTYLE_OFF: InnerAssignment
    if (str == null || (strLen = str.length()) == 0)
    // CHECKSTYLE_ON: InnerAssignment
    {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * <p>
   * Checks if a String is not empty (""), not null and not whitespace only.
   * </p>
   *
   * <pre>
   * StringUtils.isNotBlank(null)      = false
   * StringUtils.isNotBlank("")        = false
   * StringUtils.isNotBlank(" ")       = false
   * StringUtils.isNotBlank("bob")     = true
   * StringUtils.isNotBlank("  bob  ") = true
   * </pre>
   *
   * @param str the String to check, may be null
   * @return <code>true</code> if the String is not empty and not null and not whitespace
   *
   */
  public static boolean isNotBlank(@Nullable String str) {
    return !isBlank(str);
  }

  public static Optional<Long> size(Path p) {
    return Optional.ofNullable(p).map(pp -> IBException.cet.returns(() -> Files.size(pp)));
  }
}
