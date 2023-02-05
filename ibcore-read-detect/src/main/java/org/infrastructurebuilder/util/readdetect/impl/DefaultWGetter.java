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
package org.infrastructurebuilder.util.readdetect.impl;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.constants.IBConstants.IBDATA_PREFIX;
import static org.infrastructurebuilder.util.readdetect.IBResourceFactory.copyToTempChecksumAndPath;

import java.io.File;
import java.lang.System.Logger;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.ssl.SSLContexts;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.bzip2.BZip2UnArchiver;
import org.codehaus.plexus.archiver.gzip.GZipUnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.snappy.SnappyUnArchiver;
import org.codehaus.plexus.archiver.xz.XZUnArchiver;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.StringUtils;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBConfigurable;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceFactory;
import org.infrastructurebuilder.util.readdetect.WGetter;
import org.json.JSONObject;

import com.googlecode.download.maven.plugin.internal.HttpFileRequester;
import com.googlecode.download.maven.plugin.internal.SilentProgressReport;
import com.googlecode.download.maven.plugin.internal.cache.DownloadCache;

public

class DefaultWGetter implements WGetter {

  private final WGet wget;
  private final Log log;
  private final ArchiverManager am;
  private final Path workingDir;
  private final TypeToExtensionMapper t2e;

  public DefaultWGetter(Logger log, TypeToExtensionMapper t2e, Map<String, String> headers, Path cacheDir,
      Path workingDir, ArchiverManager archiverManager, Optional<WagonManager> pi, Optional<FileMapper[]> mappers) {
    this.t2e = requireNonNull(t2e);
    // this.wps = requireNonNull(wps);
    this.wget = new WGet();
    // FIXME Add dep on version > 0.10.0 of iblogging-maven-component and then
    // create a new LoggingMavenComponent from log
    // Log l2 = new LoggingMavenComponent(log);
//      Logger localLogger = requireNonNull(log); // FIXME (See above)
    Log l2 = new DefaultLog(new ConsoleLogger(0, WGetter.class.getCanonicalName()));
    this.wget.setLog(l2);
    this.wget.setT2EMapper(Objects.requireNonNull(t2e));
    this.wget.setCacheDirectory(requireNonNull(cacheDir).toFile());
    this.wget.setHeaders(requireNonNull(headers));
    requireNonNull(pi).ifPresent(p -> this.wget.setWagonManager(p));
    requireNonNull(mappers).ifPresent(m -> this.wget.setFileMappers(m));
    this.log = l2;
    this.am = requireNonNull(archiverManager);
    this.workingDir = requireNonNull(workingDir);
    this.wget.setArchiverManager(archiverManager);
  }

  @Override
  synchronized public final Optional<List<IBResource>> collectCacheAndCopyToChecksumNamedFile(
      boolean deleteExistingCacheIfPresent, Optional<BasicCredentials> creds, Path outputPath, String sourceString,
      Optional<Checksum> checksum, Optional<String> type, int retries, int readTimeOut, boolean skipCache,
      boolean expandArchives) {

    wget.setDeleteIfPresent(deleteExistingCacheIfPresent);
    requireNonNull(creds).ifPresent(bc -> {
      wget.setUsername(bc.getKeyId());
      wget.setPassword(bc.getSecret().orElse(null));
    });

    wget.setOutputPath(outputPath);
    requireNonNull(checksum).ifPresent(c -> wget.setSha512(c.toString().toLowerCase()));
    wget.setUri(cet.returns(() -> IBUtils.translateToWorkableArchiveURL(requireNonNull(sourceString)).toURI()));
    wget.setFailOnError(false);
    wget.setOverwrite(false);
    wget.setRetries(retries);
    wget.setReadTimeOut(readTimeOut);
    wget.setSkipCache(skipCache);
    wget.setCheckSignature(checksum.isPresent());
    wget.setMimeType(type.orElse(null));
    Optional<List<IBResource>> o = cet.returns(() -> this.wget.downloadIt());
    if (expandArchives) {
      o.ifPresent(c -> {
        IBResource src = c.get(0);
        List<IBResource> l = expand(workingDir, src, src.getSourceURL().map(URL::toExternalForm).map(n -> "zip:" + n));
        c.addAll(l);
      });
    }
    return o;
  }

  @Override
  public List<IBResource> expand(Path tempPath, IBResource src, Optional<String> oSource) {

    Path source = requireNonNull(src).getPath();
    List<IBResource> l = new ArrayList<>();
    Path targetDir = cet.returns(() -> Files.createTempDirectory(IBDATA_PREFIX)).toAbsolutePath();
    File outputFile = source.toFile();
    File outputDirectory = targetDir.toFile();
    String outputFileName = source.toAbsolutePath().toString();
    try {
      String type = t2e.getExtensionForType(src.getType()).substring(1);
      UnArchiver unarchiver = this.am.getUnArchiver(type);
      log.debug("Unarchiver type is " + type + " " + unarchiver.toString());
      unarchiver.setSourceFile(outputFile);
      if (isFileUnArchiver(unarchiver)) {
        unarchiver.setDestFile(new File(outputDirectory, outputFileName.substring(0, outputFileName.lastIndexOf('.'))));
      } else {
        unarchiver.setDestDirectory(outputDirectory);
      }
      unarchiver.extract();
      String rPath = cet.returns(() -> targetDir.toUri().toURL().toExternalForm());
      for (Path p : IBUtils.allFilesInTree(targetDir)) {
        String tPath = cet.returns(() -> p.toUri().toURL().toExternalForm()).substring(rPath.length());
        IBResource q = cet.returns(() -> copyToTempChecksumAndPath(tempPath, p, oSource, tPath));
        l.add(q);
      }

      IBUtils.deletePath(targetDir);
    } catch (NoSuchArchiverException e) {
      // File has no archiver because reasons, but that's OK
      log.debug("File " + outputFile + " has no available archiver");
    }
    return l;
  }

  private boolean isFileUnArchiver(final UnArchiver unarchiver) {
    return unarchiver instanceof BZip2UnArchiver || unarchiver instanceof GZipUnArchiver
        || unarchiver instanceof SnappyUnArchiver || unarchiver instanceof XZUnArchiver;
  }

}