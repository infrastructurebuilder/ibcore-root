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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newOutputStream;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.core.IBUtils.XML_PREFIX;
import static org.infrastructurebuilder.util.core.IBUtils.asIterator;
import static org.infrastructurebuilder.util.core.IBUtils.asJSONObjectStream;
import static org.infrastructurebuilder.util.core.IBUtils.asOptFilesystemMap;
import static org.infrastructurebuilder.util.core.IBUtils.asStream;
import static org.infrastructurebuilder.util.core.IBUtils.asStringStream;
import static org.infrastructurebuilder.util.core.IBUtils.cheapCopy;
import static org.infrastructurebuilder.util.core.IBUtils.copy;
import static org.infrastructurebuilder.util.core.IBUtils.copyAndDigest;
import static org.infrastructurebuilder.util.core.IBUtils.copyToDeletedOnExitTempPath;
import static org.infrastructurebuilder.util.core.IBUtils.deepCopy;
import static org.infrastructurebuilder.util.core.IBUtils.deletePath;
import static org.infrastructurebuilder.util.core.IBUtils.digestInputStream;
import static org.infrastructurebuilder.util.core.IBUtils.forceDirectoryPath;
import static org.infrastructurebuilder.util.core.IBUtils.generateRandomPassword;
import static org.infrastructurebuilder.util.core.IBUtils.getDTS;
import static org.infrastructurebuilder.util.core.IBUtils.getHex;
import static org.infrastructurebuilder.util.core.IBUtils.getHexStringFromInputStream;
import static org.infrastructurebuilder.util.core.IBUtils.getJSONArrayFromJSONOutputEnabled;
import static org.infrastructurebuilder.util.core.IBUtils.getJSONObjectFromMapStringString;
import static org.infrastructurebuilder.util.core.IBUtils.getMapStringStringFromJSONObject;
import static org.infrastructurebuilder.util.core.IBUtils.getOptBoolean;
import static org.infrastructurebuilder.util.core.IBUtils.getOptInteger;
import static org.infrastructurebuilder.util.core.IBUtils.getOptLong;
import static org.infrastructurebuilder.util.core.IBUtils.getOptString;
import static org.infrastructurebuilder.util.core.IBUtils.getOptionalJSONArray;
import static org.infrastructurebuilder.util.core.IBUtils.getRootFromURL;
import static org.infrastructurebuilder.util.core.IBUtils.getServicesFor;
import static org.infrastructurebuilder.util.core.IBUtils.getZipFileCreateMap;
import static org.infrastructurebuilder.util.core.IBUtils.getZipFileSystem;
import static org.infrastructurebuilder.util.core.IBUtils.hardMergeJSONObject;
import static org.infrastructurebuilder.util.core.IBUtils.hasAll;
import static org.infrastructurebuilder.util.core.IBUtils.hex8Digit;
import static org.infrastructurebuilder.util.core.IBUtils.hexStringToByteArray;
import static org.infrastructurebuilder.util.core.IBUtils.inputStreamFromHexString;
import static org.infrastructurebuilder.util.core.IBUtils.isJarArchive;
import static org.infrastructurebuilder.util.core.IBUtils.isZipArchive;
import static org.infrastructurebuilder.util.core.IBUtils.joinFromMap;
import static org.infrastructurebuilder.util.core.IBUtils.mapJSONToStringString;
import static org.infrastructurebuilder.util.core.IBUtils.matches;
import static org.infrastructurebuilder.util.core.IBUtils.mergeJSONArray;
import static org.infrastructurebuilder.util.core.IBUtils.mergeJsonObjects;
import static org.infrastructurebuilder.util.core.IBUtils.nullIfBlank;
import static org.infrastructurebuilder.util.core.IBUtils.reURL;
import static org.infrastructurebuilder.util.core.IBUtils.readFile;
import static org.infrastructurebuilder.util.core.IBUtils.readInputStreamAsStringStream;
import static org.infrastructurebuilder.util.core.IBUtils.readJsonObject;
import static org.infrastructurebuilder.util.core.IBUtils.readToJSONObject;
import static org.infrastructurebuilder.util.core.IBUtils.readToString;
import static org.infrastructurebuilder.util.core.IBUtils.removeXMLPrefix;
import static org.infrastructurebuilder.util.core.IBUtils.splitToMap;
import static org.infrastructurebuilder.util.core.IBUtils.strToDoc;
import static org.infrastructurebuilder.util.core.IBUtils.stringFromDocument;
import static org.infrastructurebuilder.util.core.IBUtils.touchFile;
import static org.infrastructurebuilder.util.core.IBUtils.translateToWorkableArchiveURL;
import static org.infrastructurebuilder.util.core.IBUtils.unzip;
import static org.infrastructurebuilder.util.core.IBUtils.writeString;
import static org.infrastructurebuilder.util.core.IBUtils.zipEntryToUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.settings.ServerProxy;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.w3c.dom.Document;

@SuppressWarnings("unused")
public class IBUtilsTest {

  private static final String JUNIT_4_8_2_JAR = "junit-4.8.2.jar";
  private static final String C1_PROPERTY = "process.executor.interim.sleep";
  private static final String FAKEFILE = "FAKEFILE.zip";
  private static final String CANNOT_READ_TARGET_DIR = "I cannot read the target dir";
  private static final String ABC = "ABC";
  private static final String ABC_CHECKSUM = "397118fdac8d83ad98813c50759c85b8c47565d8268bf10da483153b747a74743a58a90e85aa9f705ce6984ffc128db567489817e4092d050d8a1cc596ddc119";
  private static final String X_TXT = "X.txt";
  public static final String TESTFILE_CHECKSUM = "0bd4468980d90ef4d5e1e39bf30b93670492d282c518da95334df7bcad7ba8e0afe377a97d8fd64b4b6fd452b5d60ee9ee665e2fa5ecb13d8d51db8794011f3e";
  public static final String TESTFILE = "rick.jpg";
  private static JSONObject jj;
  private static JSONObject jjNull;
  private static JSONObject jjNull2;
  private static JSONObject jjNull3;
  private static JSONObject[] objects;
  private static Path target, testClasses, classes;
  private static TestingPathSupplier wps;
  private static final String TESTSTRING = "ABCDE";
  private static final String URL = "https://www.google.com";

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    wps = new TestingPathSupplier();
    objects = new JSONObject[] {
        new JSONObject("{ x : 1}"), new JSONObject("{x:2}"), new JSONObject("{x:3}")
    };

    jj = new JSONObject(
        "{ \"version\" : \"1.0.0\", \"extension\" : \"zip\", \"groupId\" : \"a.b.c\", \"artifactId\":\"abc\", \"classifier\":\"class\"}");
    jjNull = new JSONObject("{ \"extension\":\"jar\",\"groupId\" : \"a.b.c\", \"artifactId\":\"abc\"}");
    jjNull3 = new JSONObject("{ \"groupId\" : \"def\", \"artifactId\":\"abc\", \"additional\":\"abc\"}");
    jjNull2 = new JSONObject(
        "{ \"extension\":\"jar\",\"version\" : \"1.0.0\", \"groupId\" : \"a.b.c\", \"artifactId\":\"abc\"}");
    target = wps.getRoot();
    testClasses = wps.getTestClasses();
    classes = wps.getClasses();

  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    wps.finalize();
  }

  private Path testDir;

  @AfterEach
  public void after() {
    deletePath(testDir);
  }

  @BeforeEach
  public void before() throws IOException {
    testDir = wps.get();
  }

  @Test
  public void testMoveAtomicDirectory() throws IOException {
    Path source = wps.get();
    Path target = wps.get();
    Path adir = Files.createDirectories(source.resolve("A"));
    Path bdir = Files.createDirectories(adir.resolve("B"));
    Path cfile = writeString(bdir.resolve("C"), "HI");
    IBUtils.moveAtomic(source, target);
    assertTrue(Files.exists(target.resolve("A").resolve("B").resolve("C")));
  }

  @Test
  public void testEnumerationAsStream() {
    List<String> a = Arrays.asList("A", "B", "C", "D", "E");

    String[] s = new String[5];
    s = a.toArray(s);
    StringJoiner q = new StringJoiner("\t");
    a.forEach(ss -> q.add(ss));
    String v = q.toString();
    Enumeration<Object> e = new StringTokenizer(v);
    List<String> k = IBUtils.enumerationAsStream(e, true).map(Object::toString).collect(Collectors.toList());
    assertEquals(a, k);
  }

  @Test
  public void testEnumerationAsStream2() {
    List<String> a = Arrays.asList("A", "B", "C", "D", "E");

    String[] s = new String[5];
    s = a.toArray(s);
    StringJoiner q = new StringJoiner("\t");
    a.forEach(ss -> q.add(ss));
    String v = q.toString();
    Enumeration<Object> e = new StringTokenizer(v);
    ArrayList<String> b = new ArrayList<>();
    IBUtils.enumerationAsStream(e, true).forEach(o -> {
      b.add(o.toString());
    });
    assertEquals(a, b);
  }

  @Test
  public void testIteratorAsStream() {

    List<String> a = Arrays.asList("A", "B", "C", "D", "E");
    List<String> k = IBUtils.iteratorAsStream(a.iterator(), true).map(Object::toString).collect(Collectors.toList());
    assertEquals(a, k);

  }

  @Test
  public void canConstructor() {
    assertNotNull(new IBUtils());
  }

  @BeforeEach
  public void setUp() throws Exception {
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testAsIterator() {
    final Iterator<JSONObject> i = asIterator(new JSONArray(asList(objects)));
    int c = 0;
    while (i.hasNext()) {
      JSONAssert.assertEquals(objects[c], i.next(), true);
      c += 1;
    }
  }

  @Test
  public void testAsJSONObjectStream() {
    final JSONArray a = new JSONArray(asList(objects));
    final List<JSONObject> x = asJSONObjectStream(a).collect(toList());
    assertEquals(asList(objects), x);
  }

  @Test
  public void testAsJSONObjectStream2() {
    final JSONArray a = new JSONArray(asList(objects));
    final Stream<JSONObject> y = asStream(a);
    final List<JSONObject> x = y.collect(toList());
    assertEquals(asList(objects), x);
  }

  @Test
  public void testWalkPath() {
    SortedSet<Path> l = IBUtils.allFilesInTree(wps.getTestClasses());
    assertTrue(l.size() > 5);
  }

  @Test
  public void testAsOptFSMap() {
    final Map<String, Object> m = asOptFilesystemMap("X").get();
    assertEquals(1, m.size());
    assertEquals("X", m.get(IBConstants.FILESYSTEM_CRYPTO_CONFIGURATION));
  }

  @Test
  public void testAsStringStream() {
    final String[] s = new String[] {
        "A", "B", "C"
    };
    final List<String> l = asList(s);
    final JSONArray j = new JSONArray(l);
    final Stream<String> y = asStringStream(j);
    assertEquals(l, y.collect(toList()));
  }

  @Test
  public void testASUrl() {
    final String src = "https://www.google.com/a?b";
    final Optional<URL> u = IBUtils.asURL(src);
    assertTrue(u.isPresent());
    assertTrue(u.get().toExternalForm().contains(src));
    assertFalse(IBUtils.asURL("abc").isPresent());
  }

  @Test
  public void testCheapCopy() {
    final Double d = 1.2;
    final Float f = 1.2F;
    final JSONObject j = new JSONObject().put("X", f);
    final JSONObject k = new JSONObject().put("X", d);
    JSONAssert.assertEquals(k, cheapCopy.apply(j), true);
    JSONAssert.assertNotEquals(j, cheapCopy.apply(k), true);
  }

  @Test
  public void testCopy() throws IOException {
    final byte[] b = TESTSTRING.getBytes(UTF_8);
    final ByteArrayInputStream bas = new ByteArrayInputStream(b);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    copy(bas, bos);
    assertTrue(Arrays.equals(b, bos.toByteArray()), "Arrays must be equal after copy");
  }

  @Test
  public void testCopyAndDigestInputStream() throws IOException, NoSuchAlgorithmException {
    final String x = ABC;
    Checksum y;
    final Checksum expected = new Checksum(ABC_CHECKSUM);
    try (ByteArrayInputStream bis = new ByteArrayInputStream(x.getBytes(UTF_8));
        ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      y = copyAndDigest(bis, bos);
      bis.close();
      bos.close();
    }
    expected.equals(y);
    assertEquals(expected, y, "Set the hex stream to " + expected);
  }

  @Test
  public void testCopyPaths() throws IOException {
    final Path a = wps.get();
    final Path b = a.resolve(randomUUID().toString());
    final Path c = a.resolve(randomUUID().toString());
    writeString(b, ABC);
    final Path d = copy(b, c);
    assertEquals(c, d);
    final String g = readFile(d);
    assertEquals(ABC, g);
    deletePath(a);
  }

  @Test
  public void testCopyPathsFail1() {
    final Path a = wps.get();
    final Path b = a.resolve(randomUUID().toString());
    final Path c = a.resolve(randomUUID().toString());
    cet.translate(() -> writeString(b, ABC));
    b.toFile().setReadable(false);
    if (IBUtils.isWindows()) {
      cet.translate(() -> Files.delete(b));
    }
    assertThrows(IOException.class, () -> copy(b, c));
  }

  @Test
  public void testCopyPathsFail2() {
    final Path a = wps.get();
    final Path b = a.resolve(randomUUID().toString());
    final Path c = a.resolve(randomUUID().toString());
    cet.translate(() -> {
      Files.createDirectories(a);
      writeString(b, ABC);
      copy(b, c);
    });
    c.toFile().setWritable(false);
    assertThrows(IOException.class, () -> copy(b, c));
  }

  @Test
  public void testCopyPathsFail3() {
    final Path a = wps.get();
    final Path b = a.resolve(randomUUID().toString());
    final Path c = a.resolve(randomUUID().toString());
    assertThrows(IOException.class, () -> copy(b, c));
  }

  @Test
  public void testCopyPathsNull1() {
    final Path a = wps.get();
    final Path b = a.resolve(randomUUID().toString());
    final Path c = a.resolve(randomUUID().toString());
    a.resolve(randomUUID().toString());
    assertThrows(NullPointerException.class, () -> copy(null, c));
  }

  @Test
  public void testCopyPathsNull2() {
    final Path a = wps.get();
    final Path b = a.resolve(randomUUID().toString());
    final Path c = a.resolve(randomUUID().toString());
    cet.translate(() -> Files.createDirectories(a));
    assertThrows(IOException.class, () -> copy(c, null));
  }

  @Test
  public void testCoptToDeletedOnExitTemp() throws IOException {
    String prefix = "testpre";
    String suffix = ".tmp";
    Path p = wps.getTestClasses().resolve(TESTFILE);
    Path target = null;
    try (InputStream source = Files.newInputStream(p)) {
      target = copyToDeletedOnExitTempPath(prefix, suffix, source);
    }
    assertEquals(new Checksum(p), new Checksum(target));
  }

  @Test
  public void testCopyAndDigestNullStream() throws NullPointerException {
    assertThrows(NullPointerException.class, () -> {
      copyAndDigest(null, null);
    });
  }

  @Test
  public void testDeepCopy() {
    final Double d = 1.2;
    final Float f = 1.2F;
    final JSONObject j = new JSONObject().put("X", f);
    final JSONObject k = new JSONObject().put("X", d);
    final JSONObject j1 = deepCopy.apply(j);
    final JSONObject k1 = deepCopy.apply(k);
    JSONAssert.assertEquals(j, j1, true);
    JSONAssert.assertEquals(k, k1, true);
    JSONAssert.assertNotEquals(k, j1, true);
  }

  @Test
  public void testDeleteNonexistentPath() {
    final Path p = testClasses.resolve("nonexistent");
    deletePath(p);
  }

  @Test
  public void testDigestInputStream() throws IOException, NoSuchAlgorithmException {
    final String x = ABC;
    Checksum y;
    final Checksum expected = new Checksum(ABC_CHECKSUM);
    try (ByteArrayInputStream bis = new ByteArrayInputStream(x.getBytes(UTF_8))) {
      y = new Checksum(bis);
      bis.close();
    }
    assertEquals(expected, y);
  }

  @Test
  public void testDigestInputStreamFail() throws IOException {
    assertThrows(IOException.class, () -> {
      Checksum y;
      final Checksum expected = new Checksum(ABC_CHECKSUM);
      try (InputStream bis = Files.newInputStream(Paths.get("NOSUCHFILE"))) {
        y = new Checksum(bis);
        bis.close();
      }
      assertEquals(expected, y);
    });
  }

  @Test
  public void testDigestNullStream() {
    assertThrows(NullPointerException.class, () -> {
      digestInputStream(null);
    });

  }

  @Test
  public void testFailReadFile() throws IOException {
    assertThrows(IOException.class, () -> {

      final Path p = testClasses.resolve(FAKEFILE);

      readFile(p);
    });
  }

  @Test
  public void testFailUnzip() {
    final Path p = testClasses.resolve(FAKEFILE);
    Path t = cet.returns(() -> {
      return Files.createTempDirectory("X");
    });
    t.toFile().deleteOnExit();
    assertThrows(IOException.class, () -> unzip(p, t));
  }

  @Test
  public void testForcePath() throws IOException {
    final Path s = testDir.resolve(randomUUID().toString());
    final Path p2 = forceDirectoryPath(s.toFile());
    assertEquals(s, p2);
  }

  @Test
  public void testForcePathAlreadyAFile() throws IBException {
    final Path s = testDir.resolve(randomUUID().toString());
    assertThrows(IBException.class, () -> {
      IBUtils.writeString(s, ABC);
      forceDirectoryPath(s.toFile());
    });
  }

  @Test
  public void testFromHexString() throws IOException {
    final String y = "XX YY ZZ";
    final String x = getHex(y.getBytes(UTF_8));
    InputStream i = null;
    try {
      i = inputStreamFromHexString(x);
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      copy(i, bos);
      assertEquals(y, bos.toString());
      return;
    } finally {
      if (i != null) {
        i.close();
      }
    }
  }

  @Test
  public void testGeneratRandomPassword() {
    final String x = generateRandomPassword();
    assertEquals(48, x.length());
  }

  @Test
  public void testGenRandowmPassword() {
    String s = IBUtils.generateNonHexRandomPasswordOfLength(5);
    assertEquals(5, s.length());
    s = IBUtils.generateNonHexRandomPasswordOfLength(50);
    assertEquals(50, s.length());
  }

  @Test
  public void testGetDTS() {
    final String s = getDTS();
    assertNotNull(s);
    assertTrue(s.length() > 10);
    try {
      Thread.sleep(100L);
    } catch (final InterruptedException e) {

      e.printStackTrace();
    }
    assertTrue(getDTS().compareTo(s) > 0);
  }

  @Test
  public void testGetHex() {
    final byte[] b = {
        0x00, 0x01, 0x03, 0x0f
    };
    final String s = getHex(b);
    assertEquals("0001030f", s);
    assertTrue(Arrays.equals(b, hexStringToByteArray(s)));
  }

  @Test
  public void testGetHexCharset() {
    final byte[] b = {
        0x00, 0x01, 0x03, 0x0f
    };
    final String s = getHex(b, IBConstants.UTF8);
    assertEquals("0001030f", s);
    assertTrue(Arrays.equals(b, hexStringToByteArray(s)));
  }

  @Test
  public void testGetHexNull() {
    assertNull(getHex(null));
  }

  @Test
  public void testGetHexStringFromInputStream() throws IOException {
    final byte[] b = {
        0x00, 0x01, 0x03, 0x0f
    };
    final ByteArrayInputStream ins = new ByteArrayInputStream(b);
    final String s = getHexStringFromInputStream(ins);
    assertEquals("0001030f", s);
    assertTrue(Arrays.equals(b, hexStringToByteArray(s)));
  }

  @Test
  public void testGeTJREExec() {
    assertEquals("java", IBUtils.getJREExecutable());
  }

  @Test
  public void testGetJSONArray() {
    final JSONObject j = new JSONObject();
    assertFalse(getOptionalJSONArray(j, "X").isPresent());
    j.put("X", new JSONArray(asList("A")));
    assertTrue(getOptionalJSONArray(j, "X").isPresent());
  }

  @Test
  public void testGetJSONArrayAsListString() {
    final JSONObject j = new JSONObject().put("X", new JSONArray(asList("1", "2")));
    final List<String> s = IBUtils.getJSONArrayAsListString(j, "X");
    assertEquals(2, s.size());
    assertTrue(s.contains("1"));
    assertTrue(s.contains("2"));
  }

  @Test
  public void testGetJSONObjecFromMapStringString() {
    final Map<String, String> a = new HashMap<>();
    a.put("A", "A");
    a.put("B", "B");
    final JSONObject b = new JSONObject().put("B", "B").put("A", "A");
    final JSONObject c = getJSONObjectFromMapStringString(a);
    JSONAssert.assertEquals(b, c, true);
  }

  @Test
  public void testGetMapStringStringFromJSONObject() {
    final JSONObject j = new JSONObject();
    Map<String, String> m = getMapStringStringFromJSONObject(j);
    assertEquals(0, m.size());
    m = getMapStringStringFromJSONObject(null);
    assertEquals(0, m.size());
    j.put("X", "Y");
    m = getMapStringStringFromJSONObject(j);
    assertEquals(1, m.size());
    assertEquals("Y", m.get("X"));

  }

  @Test
  public void testGetOptBoolean() {
    final JSONObject g = new JSONObject().put("X", true).put("A", false).put("B", JSONObject.NULL);
    assertEquals(getOptBoolean(g, "X").get(), true);
    assertFalse(getOptBoolean(g, "AVC").isPresent());
    try {
      assertFalse(getOptBoolean(g, "B").isPresent());
      fail("B is no a boolean?");
    } catch (final JSONException e) {

    }

  }

  @Test
  public void testGetOptInteger() {
    final JSONObject g = new JSONObject().put("X", 1).put("A", false).put("B", JSONObject.NULL);
    assertEquals(getOptInteger(g, "X").get(), Integer.valueOf(1));
    assertFalse(getOptInteger(g, "AVC").isPresent());
    try {
      assertFalse(getOptInteger(g, "B").isPresent());
    } catch (final NumberFormatException e) {
      assertTrue(true);
    }

  }

  @Test
  public void testGetOptional() {
    final JSONObject g = new JSONObject().put("X", "Y").put("A", " ").put("B", JSONObject.NULL);
    assertEquals(getOptString(g, "X").get(), "Y");
    assertFalse(getOptString(g, "A").isPresent());
    assertFalse(getOptString(g, "B").isPresent());
  }

  @Test
  public void testGetOptLong() {
    final JSONObject g = new JSONObject().put("X", 1L).put("A", false).put("B", JSONObject.NULL);
    assertEquals(getOptLong(g, "X").get(), Long.valueOf(1), "optional Y");
    assertFalse(getOptLong(g, "AVC").isPresent(), "Optional A is not present");
    try {
      assertFalse(getOptLong(g, "B").isPresent(), "Optional B is not present");
      fail("B is no a Long?");
    } catch (final JSONException e) {

    }

  }

  @Test
  public void testGetOptString() {
    final JSONObject g = new JSONObject().put("X", "1").put("A", false).put("B", JSONObject.NULL);
    assertEquals(getOptString(g, "X").get(), new String("1"));
    assertFalse(getOptString(g, "AVC").isPresent(), "Optional A is not present");
    try {
      assertFalse(getOptString(g, "B").isPresent(), "Optional B is not present");
    } catch (final JSONException e) {
      fail("B is no a String?");

    }

  }

  @Disabled
  @Test
  public void testGetServerProxies() {
    final List<ServerProxy> a = getServicesFor(ServerProxy.class);
    assertEquals(1, a.size());
  }

  @Test
  public void testHardMergeJSONObject() {
    final JSONObject a = new JSONObject();
    final JSONObject b = new JSONObject();
    final JSONObject c = new JSONObject();
    a.put("X", 1);
    b.put("X", 2);
    c.put("X", 2);

    JSONAssert.assertEquals(c, hardMergeJSONObject(a, b), true);
  }

  @Test
  public void testHasAll() {
    final JSONObject j = new JSONObject().put("A", "B");
    assertFalse(hasAll(j, asList("A", "C")));
    assertTrue(hasAll(j, asList("A")));
  }

  @Test
  public void testHex8Digit() {
    assertFalse(hex8Digit(null), "False if null");
    assertFalse(hex8Digit(ABC), "False if len != 8");
    assertFalse(hex8Digit("ABCDEFGH"), "False if cannot parse");
    assertTrue(hex8Digit("ABCD1234"));

  }

  @Test
  public void testJSONArray() {
    final JSONArray expected = new JSONArray(asList(new FakeJSONOutputEnabled().asJSON()));
    final List<? extends JSONOutputEnabled> v = asList(new FakeJSONOutputEnabled());
    final JSONArray actual = getJSONArrayFromJSONOutputEnabled(v);
    JSONAssert.assertEquals(expected, actual, true);
  }

  @Test
  public void testJSONtoMapStringString() {
    final JSONObject j = new JSONObject().put("X", "Y");
    final Map<String, String> m = new HashMap<>();
    m.put("X", "Y");
    final Map<String, String> k = mapJSONToStringString.apply(j);
    assertEquals(m, k);
  }

  @Test
  public void testMapStringToURLorNullBad() {
    assertThrows(IBException.class, () -> IBUtils.mapStringToURLOrNull(Optional.of("Blethc")));
  }

  @Test
  public void testMapStringToURLorNullEmpty() {

    assertNull(IBUtils.mapStringToURLOrNull(Optional.empty()));
  }

  @Test
  public void testMapStringToURLorNullGood() throws MalformedURLException {
    final String STRINGX = "http://www.google.com";
    final URL u = new URL(STRINGX);
    final URL u1 = IBUtils.mapStringToURLOrNull(Optional.of(STRINGX));
    assertEquals(u, u1);

  }

  @Test
  public void testMapStringToURLorNullNull() throws NullPointerException {
    assertThrows(NullPointerException.class, () -> {

      final String STRINGX = "http://www.google.com";
      URL u;
      u = new URL(STRINGX);
      final URL u1 = IBUtils.mapStringToURLOrNull(null);
      assertEquals(u, u1);
    });

  }

  @Test
  public void testMatchesJSON() {
    HashMap<Pattern, Pattern> matches;
    final Pattern x = Pattern.compile("X");
    final Pattern y = Pattern.compile("Y");
    final Pattern a = Pattern.compile("A");
    final Pattern b = Pattern.compile("B");
    Pattern.compile("C");
    final Pattern One = Pattern.compile("1");
    final JSONObject obj = new JSONObject();
    obj.put("X", "Y").put("A", "B").put("C", 1);
    matches = new HashMap<>();
    matches.put(x, y);
    assertTrue(matches(obj, matches));
    matches = new HashMap<>();
    matches.put(x, null);
    assertTrue(matches(obj, matches));
    matches = new HashMap<>();
    matches.put(null, y);
    assertTrue(matches(obj, matches));
    matches = new HashMap<>();
    matches.put(a, b);
    assertTrue(matches(obj, matches));
    matches = new HashMap<>();
    matches.put(a, One);
    assertFalse(matches(obj, matches));
    matches = new HashMap<>();
    matches.put(b, a);
    assertFalse(matches(obj, matches));
    matches = new HashMap<>();
    assertTrue(matches(obj, matches));
  }

  @Test
  public void testMergeJSONArray() {
    final JSONArray a = new JSONArray(asList("A", "B"));
    final JSONArray c = new JSONArray(asList("C", "D"));
    final JSONArray z = mergeJSONArray(a, c);
    final JSONArray actual = new JSONArray(asList("D", "C", "B", "A"));
    JSONAssert.assertEquals(z, actual, false);
  }

  @Test
  public void testMergeJSONArray2() {
    final JSONArray a = new JSONArray(asList("A", "B"));
    final JSONArray z = mergeJSONArray(a, "C");
    final JSONArray actual = new JSONArray(asList("C", "B", "A"));
    JSONAssert.assertEquals(z, actual, false);
  }

  @Test
  public void testMergeJSONArray3() {
    final String a = "A";
    final JSONArray c = new JSONArray(asList("C", "D"));
    final JSONArray z = mergeJSONArray(c, a);
    final JSONArray actual = new JSONArray(asList("D", "C", "A"));
    JSONAssert.assertEquals(z, actual, false);
  }

  @Test
  public void testMergeJSONArrayAlreadyPresent() {
    final JSONArray a = new JSONArray(asList("A", "B"));
    final JSONArray z = mergeJSONArray(a, "B");
    final JSONArray actual = new JSONArray(asList("B", "A"));
    JSONAssert.assertEquals(z, actual, false);
  }

  @Test
  public void testMergeJSONObject() {
    final String j = jj.toString();
    final String k2 = jjNull3.toString();

    final JSONObject x = mergeJsonObjects(new JSONObject(j), new JSONObject(k2));

    assertNotNull(x);

    assertEquals(6, x.keySet().size());

    final JSONObject j1 = new JSONObject().put("X", new JSONArray("[1]"));
    final JSONObject j2 = new JSONObject().put("X", new JSONArray("[2]"));

    final JSONObject j3 = mergeJsonObjects(j1, j2);

    JSONArray j4 = j3.getJSONArray("X");
    assertTrue(j4.length() == 2);

    final JSONObject j5 = new JSONObject().put("Y", j1);
    final JSONObject j6 = new JSONObject().put("Y", j2).put("Z", "z");
    final JSONObject j7 = mergeJsonObjects(j5, j6);
    final JSONObject j8 = j7.getJSONObject("Y");
    j4 = j8.getJSONArray("X");
    assertTrue(j4.length() == 2);
    assertEquals("z", "z", j7.getString("Z"));
    assertTrue(j7.has("Y"));

    final JSONObject j9 = new JSONObject().put("X", "3");

    JSONObject j10 = mergeJsonObjects(j3, j9);
    assertEquals(3, j10.getJSONArray("X").length());

    j10 = mergeJsonObjects(j9, j3);
    assertEquals(3, j10.getJSONArray("X").length());
  }

  @Test
  public void testMergeMapSS() throws Exception {
    Map<String, String> m1, m2, m3;
    m1 = new HashMap<>();
    m2 = new HashMap<>();

    m1.put("X", "Y");
    m1.put("A", "B");
    m2.put("X", "Z");
    m2.put("C", "D");

    m3 = IBUtils.mergeMapSS(m1, m2);
    assertEquals(3, m3.size());
    assertEquals("Z", m3.get("X"));
    assertEquals("D", m3.get("C"));
    assertEquals("B", m3.get("A"));

  }

  @Test
  public void testMOO() {
    final Properties m = new Properties();
    m.put("X", "Y");
    m.put("Z", Boolean.FALSE);
    final Map<String, String> h = IBUtils.getMapStringStringfromMapObjectObject(m);
    assertEquals(2, h.size());
    assertEquals("Y", h.get("X"));
    assertEquals("false", h.get("Z"));
  }

  @Test
  public void testMoveFileToNewIdPath() throws IOException {
    final Path p = Paths.get(".", "target");
    final Path f = Files.createTempFile(p, ABC, "DEF");
    assertTrue(Files.exists(f, LinkOption.NOFOLLOW_LINKS), "Temp File exists");
    final UUID u = randomUUID();
    final Path q = p.resolve(u.toString());
    IBUtils.moveFileToNewIdPath(f, u);
    assertTrue(!Files.exists(f, LinkOption.NOFOLLOW_LINKS), "Old file does not exist");
    assertTrue(Files.exists(q, LinkOption.NOFOLLOW_LINKS), "Moved file does exist");
  }

  @Test
  public void testMSO() {
    final Map<String, Object> m = new HashMap<>();
    m.put("X", "Y");
    m.put("Z", Boolean.FALSE);
    final Map<String, String> h = IBUtils.getMapStringStringfromMapStringObject(m);
    assertEquals(2, h.size());
    assertEquals("Y", h.get("X"));
    assertEquals("false", h.get("Z"));
  }

  @Test
  public void testNonExistentReadJsonObjectFromPath() throws NoSuchFileException {
    assertThrows(NoSuchFileException.class, () -> {
      JSONObject j = null;
      j = readJsonObject(testClasses.resolve("doesnotexist.json"));
      assertEquals("Got E", "E", j.getJSONObject("C").getString("D"));
    });
  }

  @Test
  public void testNullIfBlank() {
    final String x = null;
    final String y = " ";
    final String z = ABC;

    assertEquals(null, nullIfBlank.apply(x));
    assertEquals(null, nullIfBlank.apply(y));
    assertEquals(ABC, nullIfBlank.apply(z));
  }

  @Test
  public void testPropertiesToMapSS() throws IOException {
    Properties p = new Properties();
    p.load(getClass().getResourceAsStream("/c1.properties"));
    Map<String, String> m = IBUtils.propertiesToMapSS.apply(p);
    assertEquals(4, m.size());
    assertEquals("1000", m.get(C1_PROPERTY));

    Properties v = IBUtils.mapSS2Properties.apply(m);
    assertEquals(4, v.size());
    assertEquals("1000", v.getProperty(C1_PROPERTY));

  }

  @Test
  public void testreUrl() {
    assertFalse(ofNullable(reURL(null)).isPresent());
    assertEquals(URL, reURL(URL).toExternalForm());
  }

  @Test
  public void testNullSafeUrlMapper() {
    assertFalse(IBUtils.nullSafeURLMapper.apply(null).isPresent());
    assertTrue(IBUtils.nullSafeURLMapper.apply(URL).isPresent());
  }

  @Test
  public void testReadFile() throws IOException {
    final Path p = testClasses.resolve(X_TXT);

    final String v = readFile(p, Charset.defaultCharset());

    assertEquals("ABC_123", v);

    try (InputStream ins = Files.newInputStream(p)) {
      assertEquals("ABC_123", readToString(ins, Charset.defaultCharset()));
    }
  }

  @Test
  public void testReadFilePath() throws IOException {
    final Path p = testClasses.resolve(X_TXT);
    final String v = readFile(p);
    assertEquals("ABC_123", "ABC_123", v);
  }

  @Test
  public void testReadJsonObjectFromPath() throws IOException {
    final Path p = testClasses.resolve("somefile.json");
    final JSONObject j = readJsonObject(p);
    JSONObject k;
    try (InputStream ins = Files.newInputStream(p)) {
      k = readToJSONObject(ins);
    }

    assertEquals("E", j.getJSONObject("C").getString("D"));
    assertEquals("E", k.getJSONObject("C").getString("D"));
  }

  @Test
  public void testReadToString() throws IOException {
    final ByteArrayInputStream stream = new ByteArrayInputStream(ABC.getBytes(UTF_8));
    assertEquals(ABC, ABC, readToString(stream));
  }

  @Test
  public void testSplitToMapAndJoinFromMap() {
    final JSONObject a = new JSONObject().put("X", "Y").put("Z", "false");
    final Map<String, String> m = splitToMap(a);
    assertEquals(2, m.size());
    assertEquals("Y", m.get("X"));
    assertEquals("false", m.get("Z"));

    final JSONObject b = joinFromMap(m);
    JSONAssert.assertEquals(a, b, true);
  }

  @Test
  public void testUnzipAndDelete() throws IOException {
    final Path p = testClasses.resolve("X.zip");

    final Path t = testDir.resolve(randomUUID().toString());
    Files.createDirectories(t);
    final Path f = t.resolve("X").resolve("Y");

    assertFalse(Files.isDirectory(f));
    assertFalse(Files.isRegularFile(f.resolve(TESTFILE)));

    unzip(p, t);
    assertTrue(Files.isDirectory(f));
    assertTrue(Files.isRegularFile(f.resolve(TESTFILE)));

    assertTrue(Files.isDirectory(t));
    deletePath(t);
    assertFalse(Files.isDirectory(t));
    assertFalse(Files.isRegularFile(f.resolve(TESTFILE)));
  }

  @Test
  public void testUnzipAndDeleteFAKEPATH() throws IOException {
    final Path p = testClasses.resolve("SOMEFAKE.zip");

    final Path t = testDir.resolve(randomUUID().toString());
    try {
      Files.createDirectories(t);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    final Path f = t.resolve("X").resolve("Y");

    assertFalse(Files.isDirectory(f));
    assertFalse(Files.isRegularFile(f.resolve(TESTFILE)));

    assertThrows(IOException.class, () -> unzip(p, t));
//    assertTrue(Files.isDirectory(f));
//    assertTrue(Files.isRegularFile(f.resolve(TESTFILE)));
//
//    assertTrue(Files.isDirectory(t));
//    deletePath(t);
//    assertFalse(Files.isDirectory(t));
//    assertFalse(Files.isRegularFile(f.resolve(TESTFILE)));
  }

  @Disabled

  @Test
  public void testUnzipFailUnreadable() throws AccessDeniedException {
    try {
      final Path p = copy(testClasses.resolve("X.zip"), testDir.resolve("Y.zip"));
      p.toFile().setReadable(false);
      final Path t = testDir.resolve(randomUUID().toString());
      Files.createDirectories(t);
      final Path f = t.resolve("X").resolve("Y");

      assertFalse(Files.isDirectory(f));
      assertFalse(Files.isRegularFile(f.resolve(TESTFILE)));

      unzip(p, t);
      assertTrue(Files.isDirectory(f));
      assertTrue(Files.isRegularFile(f.resolve(TESTFILE)));

      assertTrue(Files.isDirectory(t));
      deletePath(t);
      assertFalse(Files.isDirectory(t));
      assertFalse(Files.isRegularFile(f.resolve(TESTFILE)));
    } catch (IOException e) {

    }
  }

  @Test
  public void testUnzipFailUnreadable2() throws NoSuchFileException {
    final Path p = Paths.get("does", "not", "exist.zip");
    try {
      unzip(p, testDir);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Test
  public void testUnzipNotAZipFile() throws IOException {
    assertThrows(IOException.class, () -> {
      unzip(testDir, testDir);
    });
  }

  @Test
  public void testUnzipNull() throws NullPointerException {
    assertThrows(NullPointerException.class, () -> {
      unzip(null, null);
    });
  }

  @Test
  public void testUnzipUnavailablePath() throws NoSuchFileException {
    try {
      unzip(Paths.get("target", "DOES_NOT_EXIST"), Paths.get("target", "traget"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testVerifyJarfile() throws IOException {
    final String name = testClasses.resolve(JUNIT_4_8_2_JAR).toAbsolutePath().toString();
    final JarFile j = new JarFile(name);
    IBUtils.verify(j);
  }

  @Test
  public void testWriteString() throws IOException {
    final Path p = Paths.get("target", "somefile");
    final Path v2 = writeString(p, "ABC_123");
    assertEquals(v2, p);
    final String v = readFile(p);
    assertEquals("ABC_123", "ABC_123", v);
  }

  @Test
  public void testZipCreateMap() {
    Map<String, String> m = getZipFileCreateMap(true);
    assertEquals(1, m.size());
    assertEquals(m.get("create"), "true");

    m = getZipFileCreateMap(false);
    assertEquals(1, m.size());
    assertEquals(m.get("create"), "false");

    m = getZipFileCreateMap(null);
    assertEquals(1, m.size());
    assertEquals(m.get("create"), "false");
  }

  @Test
  public void testZipEntryToURL() throws MalformedURLException {
    final ZipEntry e = new ZipEntry("a");
    final Optional<URL> p = Optional.of(new URL("file://x.zip"));
    final URL r = zipEntryToUrl(p, e).get();
    assertEquals("jar:file://x.zip!/a", r.toExternalForm());
    assertFalse(zipEntryToUrl(Optional.empty(), e).isPresent());

  }

  @Test
  public void testZipFilesystem() throws IOException {
    final Path p = testClasses.resolve("X.zip");
    try (FileSystem zipFs = getZipFileSystem(p, false)) {
      final Path src = zipFs.getPath("X/Y/rick.jpg");
      final Path targetFile = Paths.get(".", "target", "testfile.rick.jpg");
      Files.copy(src, targetFile, StandardCopyOption.REPLACE_EXISTING);
      assertEquals(22152, Files.size(targetFile));
    }

  }

  @Test
  public void testZipFilesystem2() {
    final Path p = testClasses.resolve("NOSUCHFILE.FOO.zip");
    try (FileSystem zipFs = getZipFileSystem(p, false)) {
      fail("This shouldn't work");
    } catch (final FileSystemNotFoundException | NoSuchFileException e) {

    } catch (final IOException e) {
      fail("Nope!");
    }
  }

  @Test
  public void testIntneralSgiPath() {
    GAV g = new DefaultGAV("X:Y:1.0.0");
    String s = IBUtils.toInternalSignaturePath(g);
    assertEquals("X:Y::1.0.0:jar", s);
  }

  @Test
  public void getVersion() {
    GAV g = new DefaultGAV("X:Y:1.0.0");
    IBVersion ibv = IBUtils.getVersion(g).get();
    assertEquals("1.0.0", ibv.getValue());
  }

  @Test
  public void testGeAFlinemaePath() {
    GAV g = new DefaultGAV("X:Y:1.0.0");
    String s = IBUtils.getArtifactFilenamePath(g);
    assertEquals("Y-1.0.0.jar", s);
    GAV g2 = new DefaultGAV("X:Y:1.0.0:abc:jeff");
    String s2 = IBUtils.getArtifactFilenamePath(g2);
    assertEquals("Y-1.0.0-jeff.abc", s2);
  }

  @Test
  public void testAPIVersion() {
    GAV g = new DefaultGAV("X:Y:1.0.0");
    IBVersion c = IBUtils.apiVersion(g).get();
    assertEquals("1.0", c.getValue());
  }

  @Test
  public void test_Matcher() {
    assertTrue(IBUtils._matcher("anything", null));
    assertTrue(IBUtils._matcher(null, "anything"));
    assertFalse(IBUtils._matcher("abc", "def"));
    assertTrue(IBUtils._matcher("abc.*", "abcdef"));
  }

  @Disabled
  @Test
  public void testTranslateToWorkableArchiveURL() throws IOException {
    Path p = testClasses.resolve("X.zip");
    URL k = p.toUri().toURL();
    String e = k.toExternalForm() + "!/rick.jpg";
    URL u = translateToWorkableArchiveURL("jar:" + e);
    URL v = translateToWorkableArchiveURL("zip:" + e);

    URL first = new URL("https://file-examples.com/wp-content/uploads/2017/02/zip_2MB.zip");
    String secondA = "zip:" + first.toExternalForm() + "!/zip_10MB/" + "file-sample_1MB.doc";
    URL second = translateToWorkableArchiveURL(secondA);

    Path cc = wps.get().resolve("file-sample_1MB.doc");
    try (OutputStream outs = newOutputStream(cc); InputStream ins = second.openStream()) {
      IBUtils.copy(ins, outs);
    }
    assertEquals(
        "67d617a6ad2e286c46588284ddf63887c320bc30549471576f1937a9c49daefd669413d71b98b2cb42d29823d0c4acfae5abdc6dc01e05e03f200bfe13d6a15a",
        new Checksum(cc).toString());
  }

  @Test
  public void testIsJarorZip() {
    assertTrue(isJarArchive() || isZipArchive());
    assertTrue(isZipArchive() || isJarArchive());
  }

  @Test
  public void testReadInputStreamAsStringStream() throws IOException {
    try (InputStream ins = Files.newInputStream(testClasses.resolve("somefile.json"))) {
      JSONObject j = new JSONObject(readInputStreamAsStringStream(ins).collect(Collectors.joining("\n")));
      JSONObject t2 = readJsonObject(testClasses.resolve("somefile.json"));
      JSONAssert.assertEquals(t2, j, true);
    }
  }

  @Test
  public void testPathOfZip() throws IOException {
    String[] p = ("jar:" + wps.getTestClasses().resolve(JUNIT_4_8_2_JAR).toUri().toURL().toExternalForm() + "!/")
        .split("!");
    Path zip = getRootFromURL(p);
    assertNotNull(zip);
    Path file = zip.resolve("LICENSE.txt");
    String s = readFile(file);
    assertTrue(s.startsWith("BSD"));
  }

  @Test
  public void testStringFromDom() {
    String x = XML_PREFIX + "\n" + "<tag/>";
    String y = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><tag/>";
    Document document = strToDoc.apply(x).get();
    assertEquals(y, stringFromDocument(document));
  }

  @Test
  public void testRemoveXLPrefix() {
    String x = XML_PREFIX + "\n" + "<tag/>";
    String y = "<tag/>";
    String z = "<tag/>";
    assertEquals(removeXMLPrefix(x), y);
    assertEquals(removeXMLPrefix(y), z);

  }

  @Test
  public void testTouchDir() {
    assertThrows(IBException.class, () -> touchFile(target));
  }

  @Test
  public void testTouchNewFile() {
    touchFile(target.resolve("ABC"));
  }

  @Test
  public void testTouchNewFile2() {
    touchFile(target.resolve("DEF").resolve("GHI"));
  }

  @Test
  public void testIsWindows() {
    // FIXME Trivial equality test
    assertEquals(System.getProperty("os.name").toLowerCase().startsWith("windows"), IBUtils.isWindows());
  }

  @Test
  public void testParseISODateTime() {
    Instant i = Instant.now();

    String k = i.toString();
    Date d = IBUtils.parseISODateTime.apply(k);
    // Lose some fidelity in the ISO string with Date...
    assertEquals(k.substring(0, 23), d.toInstant().toString().subSequence(0, 23));
  }

  @Test
  public void testGetITerable() {
    ArrayList<String> list = new ArrayList<String>();
    list.add(ABC);
    Iterable<String> k = IBUtils.getIterable(list.iterator());
    k.forEach(c -> assertEquals(ABC, c));
  }

  @Test
  public void testInstantiate() {
    assertNotNull(new IBUtils());
  }

}
