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
package org.infrastructurebuilder.util.mavendownloadplugin.nonpublic;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Named;

//import com.googlecode.download.maven.plugin.internal.cache.DownloadCache;
//import com.googlecode.download.maven.plugin.internal.checksum.Checksums;
import org.apache.http.Header;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
//import org.apache.maven.execution.MavenSession;
//import org.apache.maven.plugin.AbstractMojo;
//import org.apache.maven.plugin.MojoExecutionException;
//import org.apache.maven.plugin.MojoFailureException;
//import org.apache.maven.plugins.annotations.LifecyclePhase;
//import org.apache.maven.plugins.annotations.Mojo;
//import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.bzip2.BZip2UnArchiver;
import org.codehaus.plexus.archiver.gzip.GZipUnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.snappy.SnappyUnArchiver;
import org.codehaus.plexus.archiver.xz.XZUnArchiver;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.infrastructurebuilder.util.mavendownloadplugin.DownloadFailureException;
import org.infrastructurebuilder.util.mavendownloadplugin.FileNameUtils;
import org.infrastructurebuilder.util.mavendownloadplugin.HttpFileRequester;
import org.infrastructurebuilder.util.mavendownloadplugin.IBMavenDownloadPluginComponentException;
import org.infrastructurebuilder.util.mavendownloadplugin.ProgressReport;
import org.infrastructurebuilder.util.mavendownloadplugin.SSLProtocols;
import org.infrastructurebuilder.util.mavendownloadplugin.WGet;
import org.infrastructurebuilder.util.mavendownloadplugin.HttpFileRequester.Builder;
//import org.eclipse.aether.repository.Authentication;
//import org.eclipse.aether.repository.AuthenticationContext;
//import org.eclipse.aether.repository.Proxy;
//import org.eclipse.aether.repository.RemoteRepository;
//import org.sonatype.plexus.build.incremental.BuildContext;
import org.infrastructurebuilder.util.mavendownloadplugin.cache.DownloadCache;
import org.infrastructurebuilder.util.mavendownloadplugin.checksum.Checksums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Will download a file from a web site using the standard HTTP protocol. Copied from work by:
 * 
 * @author Marc-Andre Houle
 * @author Mickael Istria (Red Hat Inc)
 */
public class WGetComponent implements WGet {
  private final static Logger log = LoggerFactory.getLogger(WGetComponent.class);
  /**
   * Represent the URL to fetch information from.
   */
  private URI uri;

  /**
   * Flag to overwrite the file by redownloading it. {@code overwrite=true} means that if the target file pre-exists at
   * the expected target-location for the current plugin execution, then the pre-existing file will be overwritten and
   * replaced anyway; whereas default {@code overwrite=false} will entirely skip all the execution if the target file
   * pre-exists and matches specification (name, signatures...).
   */
  private boolean overwrite;

  /**
   * Represent the file name to use as output value. If not set, will use last segment of "url"
   */
  private String outputFileName;

  /**
   * Represent the directory where the file should be downloaded.
   */
  private File outputDirectory;

  /**
   * The md5 of the file. If set, file checksum will be compared to this checksum and plugin will fail.
   */
  private String md5;

  /**
   * The sha1 of the file. If set, file checksum will be compared to this checksum and plugin will fail.
   */
  private String sha1;

  /**
   * The sha256 of the file. If set, file checksum will be compared to this checksum and plugin will fail.
   */
  private String sha256;

  /**
   * The sha512 of the file. If set, file checksum will be compared to this checksum and plugin will fail.
   */
  private String sha512;

  /**
   * Whether to unpack the file in case it is an archive (.zip)
   */
  private boolean unpack;

  /**
   * Custom username for the download
   */
//    @Parameter(property = "download.auth.username")
  private String username;

  /**
   * Custom password for the download
   */
  private String password;

  private String ntlmDomain;
  private String ntlmHost;
  private String proxyHost;
  private Integer proxyPort;

  /**
   * How many retries for a download
   */
  private int retries = 2;

  /**
   * Read timeout for a download in milliseconds
   */
  private int readTimeOut = 3000;

  /**
   * Download file without polling cache. Means that the download operation will not look in the global cache to resolve
   * the file to download, and will directly proceed with the download and won't store this download in the cache. It's
   * recommended for urls that have "volatile" content.
   */
  private boolean skipCache = false;

  /**
   * The directory to use as a cache. Default is ${local-repo}/.cache/maven-download-plugin
   */
  private File cacheDirectory;

  /**
   * Flag to determine whether to fail on an unsuccessful download.
   */
  private boolean failOnError = true;

  /**
   * Whether to verify the checksum of an existing file
   * <p>
   * By default, checksum verification only occurs after downloading a file. This option additionally enforces checksum
   * verification for already existing, previously downloaded (or manually copied) files. If the checksum does not
   * match, re-download the file.
   * <p>
   * Use this option in order to ensure that a new download attempt is made after a previously interrupted build or
   * network connection or some other event corrupted a file.
   */
  private boolean alwaysVerifyChecksum = false;

  /**
   * <p>
   * Whether to follow redirects (301 Moved Permanently, 302 Found, 303 See Other).
   * </p>
   * <p>
   * If this option is disabled and the returned resource returns a redirect, the plugin will report an error and exit
   * unless {@link #failOnError} is {@code false}.
   * </p>
   */
  private boolean followRedirects = true;

  /**
   * A list of additional HTTP headers to send with the request
   */
  private Map<String, String> headers = new HashMap<>();

  protected ArchiverManager archiverManager;

  /**
   * For transfers
   */

//    @Inject
//    private BuildContext buildContext;

  /**
   * Maximum time (ms) to wait to acquire a file lock.
   *
   * Customize the time when using the plugin to download the same file from several submodules in parallel build.
   */
//    @Parameter(property = "maxLockWaitTime", defaultValue = "30000")
  private long maxLockWaitTime;

  /**
   * {@link FileMapper}s to be used for rewriting each target path, or {@code null} if no rewriting shall happen.
   *
   * @since 1.6.8
   */
//    @Parameter(property = "download.fileMappers")
  private FileMapper[] fileMappers;

  /**
   * If {@code true}, preemptive authentication will be used
   *
   * @since 1.6.9
   */
//    @Parameter(property = "preemptiveAuth", defaultValue = "false")
  private boolean preemptiveAuth;

  private static final PoolingHttpClientConnectionManager CONN_POOL;

  /**
   * A map of file caches by their location paths. Ensures one cache instance per path and enables safe execution in
   * parallel builds against the same cache.
   */
  private static final Map<String, DownloadCache> DOWNLOAD_CACHES = new ConcurrentHashMap<>();

  /**
   * A map of file locks by files to be downloaded. Ensures exclusive access to a target file.
   */
  private static final Map<String, Lock> FILE_LOCKS = new ConcurrentHashMap<>();

  static {
    CONN_POOL = new PoolingHttpClientConnectionManager(
        RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https",
                new SSLConnectionSocketFactory(SSLContexts.createSystemDefault(), SSLProtocols.supported(), null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
            .build(),
        null, null, null, 1, TimeUnit.MINUTES);
  }

  /**
   * Ensures that the output directory does not contain unresolved path variables, i.e. when running without a pom.xml.
   * If unresolved path variables are detected, set the output directory to the current working directory.
   *
   * @since 1.7.2
   * @throws MojoExecutionException If the current working directory could not be resolved. This should never happen.
   */
  private void adjustOutputDirectory() throws IBMavenDownloadPluginComponentException /* MojoExecutionException */ {
    if (this.outputDirectory.getPath().contains("${")) {
      getLog().info(format("Could not resolve outputDirectory '%s'. Consider using -Ddownload.outputDirectory=.",
          this.outputDirectory.getPath()));
      this.outputDirectory = new File(".");
      try {
        getLog().info("Adjusting outputDirectory to " + this.outputDirectory.getCanonicalPath());
      } catch (IOException e) {
//          throw new MojoExecutionException("Current working directory could not be resolved. This should never happen.");
        throw new IBMavenDownloadPluginComponentException(
            "Current working directory could not be resolved. This should never happen.");
      }
    }
  }

  /**
   * If {@code true}, SSL certificate verification is skipped
   *
   * @since 1.8.1
   */
//    @Parameter(property = "insecure", defaultValue = "false")
  private boolean insecure;

  private ProgressReport progressReport;

  private File localRepoBaseDir;

  /**
   * @param uri
   * @param overwrite
   * @param outputFileName
   * @param outputDirectory
   * @param md5
   * @param sha1
   * @param sha256
   * @param sha512
   * @param unpack
   * @param username
   * @param password
   * @param ntlmDomain
   * @param ntlmHost
   * @param proxyHost
   * @param proxyPort
   * @param retries
   * @param readTimeOut
   * @param cacheDirectory
   * @param failOnError
   * @param skip
   * @param alwaysVerifyChecksum
   * @param checkSignature
   * @param followRedirects
   * @param headers
   * @param archiverManager
   * @param maxLockWaitTime
   * @param fileMappers
   * @param preemptiveAuth
   * @param insecure
   * @param progressReport
   */
  private WGetComponent(URI uri, boolean overwrite, String outputFileName, File outputDirectory, String md5,
      String sha1, String sha256, String sha512, boolean unpack, String username, String password, String ntlmDomain,
      String ntlmHost, String proxyHost, Integer proxyPort, int retries, int readTimeOut, File cacheDirectory,
      boolean failOnError, boolean alwaysVerifyChecksum, boolean followRedirects, Map<String, String> headers,
      long maxLockWaitTime, FileMapper[] fileMappers, boolean preemptiveAuth, boolean insecure,
      ProgressReport progressReport, boolean skipCache, File localRepoBaseDir, ArchiverManager archiverManager)
  {
    super();
    this.uri = uri;
    this.overwrite = overwrite;
    this.outputFileName = outputFileName;
    this.outputDirectory = outputDirectory;
    this.md5 = md5;
    this.sha1 = sha1;
    this.sha256 = sha256;
    this.sha512 = sha512;
    this.unpack = unpack;
    this.username = username;
    this.password = password;
    this.ntlmDomain = ntlmDomain;
    this.ntlmHost = ntlmHost;
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
    this.retries = retries;
    this.readTimeOut = readTimeOut;
    this.cacheDirectory = cacheDirectory;
    this.failOnError = failOnError;
    this.alwaysVerifyChecksum = alwaysVerifyChecksum;
    this.followRedirects = followRedirects;
    this.headers = headers;
    this.maxLockWaitTime = maxLockWaitTime;
    this.fileMappers = fileMappers;
    this.preemptiveAuth = preemptiveAuth;
    this.insecure = insecure;
    this.progressReport = progressReport;
    this.localRepoBaseDir = localRepoBaseDir;
    this.archiverManager = archiverManager;
    this.skipCache = skipCache;
  }

  /**
   * Method call when the mojo is executed for the first time.
   * 
   * @return
   *
   * @throws MojoExecutionException if an error is occuring in this mojo.
   * @throws MojoFailureException   if an error is occuring in this mojo.
   */
  @Override
  public Optional<File> get() /* throws MojoExecutionException, MojoFailureException */ {

    if (this.retries < 1) {
      throw new IBMavenDownloadPluginComponentException("retries must be at least 1");
    }

    final Optional<DownloadCache> cache;
    if (!this.skipCache) {
      if (this.cacheDirectory == null) {
        this.cacheDirectory = new File(this.localRepoBaseDir, ".cache/download-maven-plugin");
      } else if (this.cacheDirectory.exists() && !this.cacheDirectory.isDirectory()) {
        throw new IBMavenDownloadPluginComponentException(
            String.format("cacheDirectory is not a directory: " + this.cacheDirectory.getAbsolutePath()));
      }
      getLog().debug("Cache is: " + this.cacheDirectory.getAbsolutePath());
      cache = Optional.of(DOWNLOAD_CACHES.computeIfAbsent(cacheDirectory.getAbsolutePath(),
          directory -> new DownloadCache(this.cacheDirectory, getLog())));
    } else {
      getLog().debug("Cache is skipped");
      cache = Optional.empty();
    }

    // PREPARE
    adjustOutputDirectory();
    if (outputDirectory.exists() && !outputDirectory.isDirectory()) {
      throw new IBMavenDownloadPluginComponentException(
          "outputDirectory is not a directory: " + outputDirectory.getAbsolutePath());
    } else {
      outputDirectory.mkdirs();
    }
    if (this.outputFileName == null) {
      this.outputFileName = FileNameUtils.getOutputFileName(this.uri);
    }
    final File outputFile = new File(this.outputDirectory, this.outputFileName);
    final Lock fileLock = FILE_LOCKS.computeIfAbsent(outputFile.getAbsolutePath(), ignored -> new ReentrantLock());

    final Checksums checksums = new Checksums(this.md5, this.sha1, this.sha256, this.sha512, getLog());
    // DO
    boolean lockAcquired = false;
    try {
      lockAcquired = fileLock.tryLock(this.maxLockWaitTime, TimeUnit.MILLISECONDS);
      if (!lockAcquired) {
        final String message = String.format("Could not acquire lock for File: %s in %dms", outputFile,
            this.maxLockWaitTime);
        if (this.failOnError) {
          throw new IBMavenDownloadPluginComponentException(message);
        } else {
          getLog().warn(message);
          return Optional.empty();
        }
      }
      boolean haveFile = outputFile.exists();
      if (haveFile) {
        boolean checksumMatch = true;
        if (this.alwaysVerifyChecksum) {
          try {
            checksums.validate(outputFile);
          } catch (final IBMavenDownloadPluginComponentException e) {
            getLog().warn("The local version of file " + outputFile.getName() + " doesn't match the expected checksum. "
                + "You should consider checking the specified checksum is correctly set.");
            checksumMatch = false;
          }
        }
        if (!checksumMatch || this.overwrite) {
          outputFile.delete();
          haveFile = false;
        } else {
          getLog().info("File already exist, skipping");
        }
      }

      if (!haveFile) {
        final Optional<File> cachedFile = cache.map(c -> c.getArtifact(this.uri, checksums));
        if (cachedFile.map(File::exists).orElse(false)) {
          getLog().debug("Got from cache: " + cachedFile.get().getAbsolutePath());
          Files.copy(cachedFile.get().toPath(), outputFile.toPath());
        } else {
          boolean done = false;
          for (int retriesLeft = this.retries; !done && retriesLeft > 0; --retriesLeft) {
            try {
              this.doGet(outputFile);
              checksums.validate(outputFile);
              done = true;
            } catch (DownloadFailureException ex) {
              // treating HTTP codes >= 500 as transient and thus always retriable
              if (this.failOnError && ex.getHttpCode() < 500) {
                throw new IBMavenDownloadPluginComponentException(ex.getMessage(), ex);
              } else {
                getLog().warn(ex.getMessage());
              }
            } catch (IOException ex) {
              if (this.failOnError) {
                throw new IBMavenDownloadPluginComponentException(ex.getMessage(), ex);
              } else {
                getLog().warn(ex.getMessage());
              }
            }
            if (!done) {
              getLog().warn("Retrying (" + (retriesLeft - 1) + " more)");
            }
          }
          if (!done) {
            if (this.failOnError) {
              throw new IBMavenDownloadPluginComponentException(
                  "Could not get content after " + this.retries + " failed attempts.");
            } else {
              getLog().warn("Ignoring download failure(s).");
              return Optional.empty();
            }
          }
        }
      }
      if (cache.isPresent()) {
        cache.get().install(this.uri, outputFile, checksums);
      }
      if (this.unpack) {
        unpack(outputFile);
        return ofNullable(this.outputDirectory);
      } else {
        return ofNullable(outputFile);
      }
    } catch (IBMavenDownloadPluginComponentException e) {
      throw e;
    } catch (IOException ex) {
      throw new IBMavenDownloadPluginComponentException("IO Error: ", ex);
    } catch (NoSuchArchiverException e) {
      throw new IBMavenDownloadPluginComponentException("No such archiver: " + e.getMessage());
    } catch (Exception e) {
      throw new IBMavenDownloadPluginComponentException("General error: ", e);
    } finally {
      if (lockAcquired) {
        fileLock.unlock();
      }
    }
  }

  private void unpack(File outputFile) throws NoSuchArchiverException {
    UnArchiver unarchiver = this.archiverManager.getUnArchiver(outputFile);
    unarchiver.setSourceFile(outputFile);
    if (isFileUnArchiver(unarchiver)) {
      unarchiver.setDestFile(
          new File(this.outputDirectory, this.outputFileName.substring(0, this.outputFileName.lastIndexOf('.'))));
    } else {
      unarchiver.setDestDirectory(this.outputDirectory);
    }
    unarchiver.setFileMappers(this.fileMappers);
    unarchiver.extract();
    outputFile.delete();
  }

  private boolean isFileUnArchiver(final UnArchiver unarchiver) {
    return unarchiver instanceof BZip2UnArchiver || unarchiver instanceof GZipUnArchiver
        || unarchiver instanceof SnappyUnArchiver || unarchiver instanceof XZUnArchiver;
  }

  private void doGet(final File outputFile) throws IOException /* , MojoExecutionException */ {
    final HttpFileRequester.Builder fileRequesterBuilder = new HttpFileRequester.Builder();

    getLog().debug("providing custom authentication");
    getLog().debug("username: " + username + " and password: ***");

    ofNullable(username).ifPresent(fileRequesterBuilder::withUsername);
    ofNullable(password).ifPresent(fileRequesterBuilder::withPassword);
    ofNullable(ntlmDomain).ifPresent(fileRequesterBuilder::withNtlmDomain);
    ofNullable(ntlmHost).ifPresent(fileRequesterBuilder::withNtlmHost);
    ofNullable(proxyHost).ifPresent(fileRequesterBuilder::withProxyHost);
    ofNullable(proxyPort).ifPresent(fileRequesterBuilder::withProxyPort);

    final HttpFileRequester fileRequester = fileRequesterBuilder.withProgressReport(this.progressReport)
        .withConnectTimeout(this.readTimeOut) //
        .withSocketTimeout(this.readTimeOut)//
        .withUri(this.uri)//
        .withUsername(this.username)//
        .withPassword(this.password)//
        .withPreemptiveAuth(this.preemptiveAuth)//
        .withRedirectsEnabled(this.followRedirects)//
        .withLog(getLog())//
        .withInsecure(this.insecure)//
        .build();
    fileRequester.download(outputFile, getAdditionalHeaders());
  }

  private List<Header> getAdditionalHeaders() {
    return headers.entrySet().stream() //
        .map(pair -> new BasicHeader(pair.getKey(), pair.getValue())) //
        .collect(toList());
  }

  public static Logger getLog() {
    return log;
  }

  public static class Builder {

    private URI uri;
    private boolean overwrite;
    private String outputFileName;
    private File outputDirectory;
    private String md5;
    private String sha1;
    private String sha256;
    private String sha512;
    private boolean unpack;
    private String username;
    private String password;
    private String ntlmDomain;
    private String ntlmHost;
    private String proxyHost;
    private Integer proxyPort;
    private int retries = 2;
    private int readTimeOut;
    private File cacheDirectory;
    private boolean failOnError = true;
    private boolean alwaysVerifyChecksum;
    private boolean checkSignature;
    private boolean followRedirects;
    private Map<String, String> headers;
    private long maxLockWaitTime;
    private FileMapper[] fileMappers;
    private boolean preemptiveAuth;
    private boolean insecure;
    private File localRepository;
    private ProgressReport progressReport;
    private ArchiverManager archiverManager;
    private boolean skipCache;

    public final WGet build() {

      return new WGetComponent(uri, overwrite, outputFileName, outputDirectory, md5, sha1, sha256, sha512, unpack,
          username, password, ntlmDomain, ntlmHost, proxyHost, proxyPort, retries, readTimeOut, cacheDirectory,
          failOnError, alwaysVerifyChecksum, followRedirects, headers, maxLockWaitTime, fileMappers, preemptiveAuth,
          insecure, progressReport, skipCache, localRepository, archiverManager);

    }

    public final Builder withArchiverManager(ArchiverManager archiverManager) {
      if (this.archiverManager == null) // only sets it once
        this.archiverManager = archiverManager;
      return this;
    }

    public final Builder withUri(URI uri) {
      this.uri = uri;
      return this;
    }

    public final Builder withOverwrite(boolean overwrite) {
      this.overwrite = overwrite;
      return this;
    }

    public final Builder withOutputFileName(String outputFileName) {
      this.outputFileName = outputFileName;
      return this;
    }

    public final Builder withOutputDirectory(File outputDirectory) {
      this.outputDirectory = outputDirectory;
      return this;
    }

    public final Builder withMd5(String md5) {
      this.md5 = md5;
      return this;
    }

    public final Builder withSha1(String sha1) {
      this.sha1 = sha1;
      return this;
    }

    public final Builder withSha256(String sha256) {
      this.sha256 = sha256;
      return this;
    }

    public final Builder withSha512(String sha512) {
      this.sha512 = sha512;
      return this;
    }

    public final Builder withUnpack(boolean unpack) {
      this.unpack = unpack;
      return this;
    }

    public final Builder withUsername(String username) {
      this.username = username;
      return this;
    }

    public final Builder withPassword(String password) {
      this.password = password;
      return this;
    }

    public final Builder withNtlmDomain(String ntlmDomain) {
      this.ntlmDomain = ntlmDomain;
      return this;
    }

    public final Builder withNtlmHost(String ntlmHost) {
      this.ntlmHost = ntlmHost;
      return this;
    }

    public final Builder withProxyHost(String proxyHost) {
      this.proxyHost = proxyHost;
      return this;
    }

    public final Builder withProxyPort(Integer proxyPort) {
      this.proxyPort = proxyPort;
      return this;
    }

    public final Builder withRetries(int retries) {
      this.retries = retries;
      return this;
    }

    public final Builder withReadTimeOut(int readTimeOut) {
      this.readTimeOut = readTimeOut;
      return this;
    }

    public final Builder withCacheDirectory(File cacheDirectory) {
      this.cacheDirectory = cacheDirectory;
      return this;
    }

    public final Builder withFailOnError(boolean failOnError) {
      this.failOnError = failOnError;
      return this;
    }

    public final Builder withAlwaysVerifyChecksum(boolean alwaysVerifyChecksum) {
      this.alwaysVerifyChecksum = alwaysVerifyChecksum;
      return this;
    }

    public final Builder withCheckSignature(boolean checkSignature) {
      this.checkSignature = checkSignature;
      return this;
    }

    public final Builder withFollowRedirects(boolean followRedirects) {
      this.followRedirects = followRedirects;
      return this;
    }
    
    public final Builder withSkipCache(boolean skipCache) {
      this.skipCache = skipCache;
      return this;
    }

    public final Builder withHeaders(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public final Builder withHeaders(Properties p) {
      return this.withHeaders(p.entrySet().stream().collect(toMap(k -> k.toString(), v -> v.toString())));
    }

    public final Builder withMaxLockWaitTime(long maxLockWaitTime) {
      this.maxLockWaitTime = maxLockWaitTime;
      return this;
    }

    public final Builder withFileMappers(FileMapper[] fileMappers) {
      this.fileMappers = fileMappers;
      return this;
    }

    public final Builder withPreemptiveAuth(boolean preemptiveAuth) {
      this.preemptiveAuth = preemptiveAuth;
      return this;
    }

    public final Builder withInsecure(boolean insecure) {
      this.insecure = insecure;
      return this;
    }

    public final Builder withProgressReport(ProgressReport progressReport) {
      this.progressReport = progressReport;
      return this;
    }
  }

}
