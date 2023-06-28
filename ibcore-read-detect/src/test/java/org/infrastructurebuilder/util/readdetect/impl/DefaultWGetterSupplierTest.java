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
package org.infrastructurebuilder.util.readdetect.impl;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.infrastructurebuilder.util.constants.IBConstants.CACHEDIR;
import static org.infrastructurebuilder.util.constants.IBConstants.FILEMAPPERS;
import static org.infrastructurebuilder.util.constants.IBConstants.WORKINGDIR;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.HeadersSupplier;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.LoggerSupplier;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.infrastructurebuilder.util.credentials.basic.DefaultBasicCredentials;
import org.infrastructurebuilder.util.extensionmapper.basic.DefaultTypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.WGetter;
import org.infrastructurebuilder.util.readdetect.WGetterSupplier;
import org.infrastructurebuilder.util.readdetect.WGetter;
import org.infrastructurebuilder.util.readdetect.WGetterSupplier;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWGetterSupplierTest {

  private static final String HTTP_WWW_EXAMPLE_COM_INDEX_HTML = "http://www.example.com/index.html";
  private static final String WWW_IANA_ORG = "/www.iana.org/";
  private static final Optional<Checksum> ZIP_CHECKSUM = Optional.of(new Checksum(
      "e7513f1f1f2f07f5dcf8259145134833aa70dfe50b4efaf10b477634966b9eed45cc3c91ba908bcb029aeff6530fb630895c141d950e6c19bb97400fbd803c96"));
  private static final Optional<Checksum> CHECKSUM = Optional.of(new Checksum(
      "d06b93c883f8126a04589937a884032df031b05518eed9d433efb6447834df2596aebd500d69b8283e5702d988ed49655ae654c1683c7a4ae58bfa6b92f2b73a"));
  private final static Logger log = LoggerFactory.getLogger(DefaultWGetterSupplierTest.class.toString());
  private final static LoggerSupplier ls = new LoggerSupplier() {
    @Override
    public Logger get() {
      return log;
    }

  };
  private static TestingPathSupplier wps;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    wps = new TestingPathSupplier();
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    wps.finalize();
  }

  private WGetterSupplier ws;

  @BeforeEach
  public void setUp() throws Exception {
    Map<String, FileMapper> fileMAppers = new HashMap<>();
    Map<String, PathSupplier> pathSuppliers = Map.of(WORKINGDIR, wps, CACHEDIR, wps);
    HeadersSupplier headerSupplier = () -> new HashMap<>();
    JSONObject config = new JSONObject().put(WORKINGDIR, WORKINGDIR).put(CACHEDIR, CACHEDIR).put(FILEMAPPERS,
        new JSONArray());
    ProxyInfoProvider pip = new ProxyInfoProvider() {
      @Override
      public ProxyInfo getProxyInfo(String arg0) {
        return null;
      }
    };
    this.ws = new DefaultWGetterSupplier(ls, new DefaultTypeToExtensionMapper(), pathSuppliers, fileMAppers,
        headerSupplier, new FakeArchiverManager(), pip).configure(config);


  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testRetries() {
    assertThrows(IBException.class, () ->
    this.ws.get().collectCacheAndCopyToChecksumNamedFile(true, empty(), wps.get(), HTTP_WWW_EXAMPLE_COM_INDEX_HTML,
        CHECKSUM, empty(), 0, 1000, true, false));
  }

  @Test
  public void testGet() throws IOException {
    WGetter w = this.ws.get();
    Path outputPath = wps.get();

    String src = HTTP_WWW_EXAMPLE_COM_INDEX_HTML; // wps.getTestClasses().resolve("rick.jpg").toUri().toURL().toExternalForm();
    Optional<IBResource> q = w
        .collectCacheAndCopyToChecksumNamedFile(true, empty(), outputPath, src, CHECKSUM, empty(), 5, 1000, true, false)
        .map(l -> l.get(0));
    assertTrue(q.isPresent());
    assertEquals(CHECKSUM.get().toString(), q.get().getChecksum().toString());
    assertEquals(IBConstants.TEXT_HTML, q.get().getType());
    String v = IBUtils.readToString(q.get().get());
    assertTrue(v.contains(WWW_IANA_ORG));

    // Do it again
    q = w.collectCacheAndCopyToChecksumNamedFile(false, empty(), outputPath, src, CHECKSUM, empty(), 5, 1000, false,
        false).map(l -> l.get(0));
    assertTrue(q.isPresent());
    assertEquals(CHECKSUM.get().toString(), q.get().getChecksum().toString());
    assertEquals(IBConstants.TEXT_HTML, q.get().getType());
    v = IBUtils.readToString(q.get().get());
    assertTrue(v.contains(WWW_IANA_ORG));

  }

  @Test
  public void testGetWithCreds() throws IOException {
    WGetter w = this.ws.get();
    Path outputPath = wps.get();

    String src = HTTP_WWW_EXAMPLE_COM_INDEX_HTML; // wps.getTestClasses().resolve("rick.jpg").toUri().toURL().toExternalForm();
    BasicCredentials creds = new DefaultBasicCredentials("A", of("B"));
    Optional<IBResource> q;
    q = w
        .collectCacheAndCopyToChecksumNamedFile(false, of(creds), outputPath, src, CHECKSUM, empty(), 5, 0, true, false)
        .map(l -> l.get(0));
    assertTrue(q.isPresent());
    assertEquals(CHECKSUM.get().toString(), q.get().getChecksum().toString());
    assertEquals(IBConstants.TEXT_HTML, q.get().getType());
    String v = IBUtils.readToString(q.get().get());
    assertTrue(v.contains(WWW_IANA_ORG));

    // Do it again
    q = w.collectCacheAndCopyToChecksumNamedFile(true, empty(), outputPath, src, CHECKSUM, empty(), 5, 1000, false,
        false).map(l -> l.get(0));
    assertTrue(q.isPresent());
    assertEquals(CHECKSUM.get().toString(), q.get().getChecksum().toString());
    assertEquals(IBConstants.TEXT_HTML, q.get().getType());
    v = IBUtils.readToString(q.get().get());
    assertTrue(v.contains(WWW_IANA_ORG));

  }

  @Test
  public void testZip() throws IOException {
    WGetter w = this.ws.get();
    Path outputPath = wps.get();
    String src = "https://file-examples.com/storage/feb01e0890649c510949c8e/2017/02/zip_2MB.zip";
//    String src = wps.getTestClasses().resolve("test.zip").toUri().toURL().toExternalForm();
    Optional<List<IBResource>> v = w.collectCacheAndCopyToChecksumNamedFile(false, empty(), outputPath, src,
        ZIP_CHECKSUM, empty(), 5, 0, true, true);
    assertTrue(v.isPresent());
    List<IBResource> l = v.get();
    assertEquals(IBConstants.APPLICATION_ZIP, l.get(0).getType());
    assertEquals(IBConstants.APPLICATION_MSWORD, l.get(1).getType());
    assertEquals(1027072, Files.size(l.get(1).getPath()));
  }

}
