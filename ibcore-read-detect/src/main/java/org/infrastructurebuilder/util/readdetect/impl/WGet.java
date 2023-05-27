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

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.util.readdetect.IBResourceFactory.toType;

import java.io.File;
import java.net.ProxySelector;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.http.Header;
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
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyUtils;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.util.StringUtils;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceFactory;

import com.googlecode.download.maven.plugin.internal.HttpFileRequester;
import com.googlecode.download.maven.plugin.internal.SilentProgressReport;
import com.googlecode.download.maven.plugin.internal.cache.DownloadCache;
import com.googlecode.download.maven.plugin.internal.checksum.Checksums;

/**
 * Will download a file from a web site using the standard HTTP protocol.
 *
 * @author Marc-Andre Houle
 * @author Mickael Istria (Red Hat Inc)
 */
public final class WGet {

  private static final PoolingHttpClientConnectionManager CONN_POOL;
  /**
   * A map of file caches by their location paths. Ensures one cache instance per
   * path and enables safe execution in parallel builds against the same cache.
   */
  private static final Map<String, DownloadCache> DOWNLOAD_CACHES = new ConcurrentHashMap<>();

  private static final Map<String, Lock> FILE_LOCKS = new ConcurrentHashMap<>();

  static {
    CONN_POOL = new PoolingHttpClientConnectionManager(
        RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https",
                new SSLConnectionSocketFactory(SSLContexts.createSystemDefault(),
                    new String[] { "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
            .build(),
        null, null, null, 1, TimeUnit.MINUTES);
  }

  /**
   * Represent the URL to fetch information from.
   */
  // @Parameter(alias = "url", property = "download.url", required = true)
  private URI uri;

  /**
   * Flag to overwrite the file by redownloading it
   */
  // @Parameter(property = "download.overwrite")
  private boolean overwrite;

  /**
   * Represent the file name to use as output value. If not set, will use last
   * segment of "url"
   */
  // @Parameter(property = "download.outputFileName")
  // private String outputFileName;

  /**
   * Represent the directory where the file should be downloaded.
   */
  // @Parameter(property = "download.outputDirectory", defaultValue =
  // "${project.build.directory}", required = true)
  // private Path outputDirectory;
  private Path outputPath;

  /**
   * The md5 of the file. If set, file signature will be compared to this
   * signature and plugin will fail.
   */
  // @Parameter
  // private String md5;

  /**
   * The sha1 of the file. If set, file signature will be compared to this
   * signature and plugin will fail.
   */
  // @Parameter
  // private String sha1;

  /**
   * The sha512 of the file. If set, file signature will be compared to this
   * signature and plugin will fail.
   */
  // @Parameter
  private String sha512;

  // /**
  // * Whether to unpack the file in case it is an archive (.zip)
  // */
  //// @Parameter(property = "download.unpack", defaultValue = "false")
  // private boolean unpack;

  /**
   * Server Id from settings file to use for authentication Only one of serverId
   * or (username/password) may be supplied
   */
  // @Parameter
  // private String serverId;

  /**
   * Custom username for the download
   */
  // @Parameter
  private String username;

  /**
   * Custom password for the download
   */
  // @Parameter
  private String password;

  /**
   * How many retries for a download
   */
  // @Parameter(defaultValue = "2")
  private int retries;

  /**
   * Read timeout for a download in milliseconds
   */
  // @Parameter(defaultValue = "0")
  private int readTimeOut;

  /**
   * Download file without polling cache
   */
  // @Parameter(property = "download.cache.skip", defaultValue = "false")
  private boolean skipCache;

  /**
   * The directory to use as a cache. Default is
   * ${local-repo}/.cache/maven-download-plugin
   */
  // @Parameter(property = "download.cache.directory")
  private File cacheDirectory;

  /**
   * Flag to determine whether to fail on an unsuccessful download.
   */
  // @Parameter(defaultValue = "true")
  private boolean failOnError = false; // Return empty

  /**
   * Whether to skip execution of Mojo
   */
  // @Parameter(property = "download.plugin.skip", defaultValue = "false")
  // private boolean skip;

  /**
   * Whether to verify the checksum of an existing file
   * <p>
   * By default, checksum verification only occurs after downloading a file. This
   * option additionally enforces checksum verification for already existing,
   * previously downloaded (or manually copied) files. If the checksum does not
   * match, re-download the file.
   * <p>
   * Use this option in order to ensure that a new download attempt is made after
   * a previously interrupted build or network connection or some other event
   * corrupted a file.
   */
//  @Parameter(property = "alwaysVerifyChecksum", defaultValue = "false")
  private boolean alwaysVerifyChecksum = true; // ALWAYS

  /**
   * @deprecated The option name is counter-intuitive and not related to
   *             signatures but to checksums, in fact. Please use
   *             {@link #alwaysVerifyChecksum} instead. This option might be
   *             removed in a future release.
   */
//  @Parameter(property = "checkSignature", defaultValue = "false")
  @Deprecated
  private boolean checkSignature;

  // @Parameter(property = "session")
  // private MavenSession session;

  // @Component
  // private ArchiverManager archiverManager;

  /**
   * For transfers
   */
  // @Component
  // private WagonManager wagonManager;

  // @Component
  // private BuildContext buildContext;

  // @Parameter(defaultValue = "${settings}", readonly = true, required = true)
  // private Settings settings;

  // /**
  // * Maven Security Dispatcher
  // */
  // @Component( hint = "mng-4384" )
  // private SecDispatcher securityDispatcher;

  /** Instance logger */
  // @Component
  private Log log;

  /**
   * Maximum time (ms) to wait to acquire a file lock.
   *
   * Customize the time when using the plugin to download the same file from
   * several submodules in parallel build.
   */
//  @Parameter(property = "maxLockWaitTime", defaultValue = "30000")
  private long maxLockWaitTime = 30000L;

  /**
   * {@link FileMapper}s to be used for rewriting each target path, or
   * {@code null} if no rewriting shall happen.
   *
   * @since 1.6.8
   */
//  @Parameter(property = "download.fileMappers")
  private FileMapper[] fileMappers;

  private boolean deleteIfPresent = false;

  public Log getLog() {
    return log;
  }

  public void setDeleteIfPresent(boolean deleteExistingCacheIfPresent) {
    this.deleteIfPresent = deleteExistingCacheIfPresent;
  }

  /**
   * Method call when the mojo is executed for the first time.
   *
   * @throws MojoExecutionException if an error is occuring in this mojo.
   * @throws MojoFailureException   if an error is occuring in this mojo.
   */
  // @Override
  // public void execute() throws MojoExecutionException, MojoFailureException {
  // if (this.skip) {
  // getLog().info("maven-download-plugin:wget skipped");
  // return;
  // }
  // NOTE FYI: Always returns a list of size() == 1 or empty()
  public Optional<List<IBResource>> downloadIt() /* throws MojoExecutionException, MojoFailureException */ {
//      if (/*StringUtils.isNotBlank(serverId) && */ (StringUtils.isNotBlank(username)
//          || StringUtils.isNotBlank(password))) {
//        throw new MojoExecutionException("Specify either serverId or username/password, not both");
//      }

    // if (settings == null) {
    // getLog().warn("settings is null");
    // }
    // getLog().debug("Got settings");
    if (retries < 1) {
//      throw new MojoFailureException("retries must be at least 1");
      throw new IBException("retries must be at least 1");
    }

    // PREPARE
    // if (this.outputFileName == null) {
    // try {
    // this.outputFileName = new File(this.uri.toURL().getFile()).getName();
    // } catch (Exception ex) {
    // throw new MojoExecutionException("Invalid URL", ex);
    // }
    // }
    // if (this.cacheDirectory == null) {
    // this.cacheDirectory = new File(this.session.getLocalRepository()
    // .getBasedir(), ".cache/download-maven-plugin");
    // }
    getLog().debug("Cache is: " + this.cacheDirectory.getAbsolutePath());
    DownloadCache cache = new DownloadCache(this.cacheDirectory);
    Path outputDirectory = outputPath;
    String outputFileName = UUID.randomUUID().toString();
    IBException.cet.translate(() -> Files.createDirectories(outputDirectory));
    // this.outputDirectory.mkdirs();
    File outputFile = outputDirectory.resolve(outputFileName).toFile();
    // File outputFile = new File(this.outputDirectory, this.outputFileName);
    final Lock fileLock = FILE_LOCKS.computeIfAbsent(outputFile.getAbsolutePath(), ignored -> new ReentrantLock());

    final Checksums checksums = new Checksums(/* this.md5, this.sha1, this.sha256, */null, null, null, this.sha512,
        this.getLog());

    // DO
    boolean lockAcquired = false;
    try {
      lockAcquired = fileLock.tryLock(this.maxLockWaitTime, TimeUnit.MILLISECONDS);
      if (!lockAcquired) {
        final String message = String.format("Could not acquire lock for File: %s in %dms", outputFile,
            this.maxLockWaitTime);
        if (this.failOnError) {
//            throw new MojoExecutionException(message);
          throw new IBException(message);
        } else {
          getLog().warn(message);
          return Optional.empty();
        }
      }
      boolean haveFile = outputFile.exists();
      if (haveFile) {
        boolean checksumMatch = true;
        if (this.alwaysVerifyChecksum || this.checkSignature) {
          try {
            checksums.validate(outputFile);
          } catch (final MojoFailureException e) {
            getLog().warn("The local version of file " + outputFile.getName() + " doesn't match the expected checksum. "
                + "You should consider checking the specified checksum is correctly set.");
            checksumMatch = false;
          }
        }
        if (!checksumMatch || overwrite) {
          outputFile.delete();
          haveFile = false;
        } else {
          getLog().info("File already exist, skipping");
        }
      }

      if (!haveFile) {
        File cached = cache.getArtifact(this.uri, checksums);
        if (!this.skipCache && cached != null && cached.exists()) {
          getLog().debug("Got from cache: " + cached.getAbsolutePath());
          Files.copy(cached.toPath(), outputFile.toPath());
        } else {
          if (/* this.settings.isOffline() */ false) {
            if (this.failOnError) {
//              throw new MojoExecutionException("No file in cache and maven is in offline mode");
              throw new IBException("No file in cache and maven is in offline mode");
            } else {
              getLog().warn("Ignoring download failure.");
            }
          }
          boolean done = false;
          while (!done && this.retries > 0) {
            try {
              this.doGet(outputFile);
              checksums.validate(outputFile);
              done = true;
            } catch (Exception ex) {
              getLog().warn("Could not get content", ex);
              this.retries--;
              if (this.retries > 0) {
                getLog().warn("Retrying (" + this.retries + " more)");
              }
            }
          }
          if (!done) {
            if (this.failOnError) {
//              throw new MojoFailureException("Could not get content");
              throw new MojoFailureException("Could not get content");
            } else {
              getLog().warn("Ignoring download failure.");
//              return;
              return Optional.empty();
            }
          }
        }
      }
      cache.install(this.uri, outputFile, checksums);
      if (/* this.unpack */ false) {
//        unpack(outputFile);
//        buildContext.refresh(outputDirectory);
      } else {
//        buildContext.refresh(outputFile);
      }
    } catch (final Exception ex) {
      throw new IBException("IO Error", ex);
    } finally {
      if (lockAcquired) {
        fileLock.unlock();
      }
    }

    Path outputPath = outputFile.toPath();
    Checksum finalChecksum = (this.sha512 == null ? new Checksum(outputFile.toPath()) : new Checksum(this.sha512));

    IBResource pVal = IBResourceFactory.from(outputFile.toPath(), finalChecksum, toType.apply(outputPath));
    String computedType = pVal.getType();
    if (this.mimeType == null)
      this.mimeType = computedType;
    var csum = new Checksums(null, null, null, this.sha512, getLog());
    return IBException.cet.returns(() -> {
//      cache.install(this.uri, outputFile, checksums);
//      /* Get the "final name" */
      String finalFileName = finalChecksum.asUUID().get().toString() + t2e.getExtensionForType(this.mimeType);
      Path newTarget = outputPath.resolve(finalFileName);
      try {
        IBUtils.moveAtomic(outputFile.toPath(), newTarget);
      } finally {
        outputFile.delete();
      }

      Path outPath = newTarget;

      IBResource retVal = IBResourceFactory.from(outPath, finalChecksum, this.mimeType,
          this.uri.toURL().toExternalForm());
      return Optional.of(List.of(retVal));
    });
  }
  // private void unpack(File outputFile) throws NoSuchArchiverException {
//    UnArchiver unarchiver = this.archiverManager.getUnArchiver(outputFile);
//    unarchiver.setSourceFile(outputFile);
//    if (isFileUnArchiver(unarchiver)) {
//      unarchiver
//          .setDestFile(new File(this.outputDirectory, outputFileName.substring(0, outputFileName.lastIndexOf('.'))));
//    } else {
//      unarchiver.setDestDirectory(this.outputDirectory);
//    }
//    unarchiver.setFileMappers(this.fileMappers);
//    unarchiver.extract();
//    outputFile.delete();
//  }

//  private boolean isFileUnArchiver(final UnArchiver unarchiver) {
//    return unarchiver instanceof BZip2UnArchiver || unarchiver instanceof GZipUnArchiver
//        || unarchiver instanceof SnappyUnArchiver || unarchiver instanceof XZUnArchiver;
//  }

  private void doGet(final File outputFile) throws Exception {
    final RequestConfig requestConfig;
    if (readTimeOut > 0) {
      getLog().info(String.format("Read Timeout is set to %d milliseconds (apprx %d minutes)", readTimeOut,
          Math.round(readTimeOut * 1.66667e-5)));
      requestConfig = RequestConfig.custom().setConnectTimeout(readTimeOut).setSocketTimeout(readTimeOut)
          .setRedirectsEnabled(followRedirects).build();
    } else {
      requestConfig = RequestConfig.DEFAULT;
    }

    CredentialsProvider credentialsProvider = null;
    if (StringUtils.isNotBlank(username)) {
      getLog().debug("providing custom authentication");
      getLog().debug("username: " + username + " and password: ***");

      credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(new AuthScope(this.uri.getHost(), this.uri.getPort()),
          new UsernamePasswordCredentials(username, password));

    }
    final HttpRoutePlanner routePlanner;
    final ProxyInfo proxyInfo = this.wagonManager.getProxy(this.uri.getScheme());
    if (this.useHttpProxy(proxyInfo)) {
      routePlanner = new DefaultProxyRoutePlanner(new HttpHost(proxyInfo.getHost(), proxyInfo.getPort()));
      if (proxyInfo.getUserName() != null) {
        final Credentials creds;
        if (proxyInfo.getNtlmHost() != null || proxyInfo.getNtlmDomain() != null) {
          creds = new NTCredentials(proxyInfo.getUserName(), proxyInfo.getPassword(), proxyInfo.getNtlmHost(),
              proxyInfo.getNtlmDomain());
        } else {
          creds = new UsernamePasswordCredentials(proxyInfo.getUserName(), proxyInfo.getPassword());
        }
        AuthScope authScope = new AuthScope(proxyInfo.getHost(), proxyInfo.getPort());
        if (credentialsProvider == null) {
          credentialsProvider = new BasicCredentialsProvider();
        }
        credentialsProvider.setCredentials(authScope, creds);
      }
    } else {
      routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
    }

    try (final CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(CONN_POOL)
        .setConnectionManagerShared(true).setRoutePlanner(routePlanner).build()) {
      final HttpFileRequester fileRequester = new HttpFileRequester(httpClient,
          /*
           * this.session.getSettings().isInteractiveMode() ? new
           * LoggingProgressReport(getLog()) :
           */ new SilentProgressReport(getLog()));

      final HttpClientContext clientContext = HttpClientContext.create();
      clientContext.setRequestConfig(requestConfig);
      if (credentialsProvider != null) {
        clientContext.setCredentialsProvider(credentialsProvider);
      }
      fileRequester.download(this.uri, outputFile, clientContext, getAdditionalHeaders());
    }
  }

  private List<Header> getAdditionalHeaders() {
    return headers.entrySet().stream().map(pair -> new BasicHeader(pair.getKey(), pair.getValue()))
        .collect(Collectors.toList());
  }

//  private void doGet2(final File outputFile) throws Exception {
//    final RequestConfig requestConfig;
//    if (readTimeOut > 0) {
//      getLog().info("Read Timeout is set to " + readTimeOut + " milliseconds (apprx "
//          + Math.round(readTimeOut * 1.66667e-5) + " minutes)");
//      requestConfig = RequestConfig.custom().setConnectTimeout(readTimeOut).setSocketTimeout(readTimeOut).build();
//    } else {
//      requestConfig = RequestConfig.DEFAULT;
//    }
//
//    CredentialsProvider credentialsProvider = null;
//    if (StringUtils.isNotBlank(username)) {
//      getLog().debug("providing custom authentication");
//      getLog().debug("username: " + username + " and password: ***");
//
//      credentialsProvider = new BasicCredentialsProvider();
//      credentialsProvider.setCredentials(new AuthScope(this.uri.getHost(), this.uri.getPort()),
//          new UsernamePasswordCredentials(username, password));
//
//      // } else if (StringUtils.isNotBlank(serverId)) {
//      // getLog().debug("providing custom authentication for " + serverId);
//      // Server server = settings.getServer(serverId);
//      // if (server == null) {
//      // throw new MojoExecutionException(String.format("Server %s not found",
//      // serverId));
//      // }
//      // getLog().debug(String.format("serverId %s supplies username: %s and password:
//      // ***", serverId, server.getUsername() ));
//      //
//      // credentialsProvider = new BasicCredentialsProvider();
//      // credentialsProvider.setCredentials(
//      // new AuthScope(this.uri.getHost(), this.uri.getPort()),
//      // new UsernamePasswordCredentials(server.getUsername(),
//      // decrypt(server.getPassword(), serverId)));
//
//    }
//
//    final HttpRoutePlanner routePlanner;
//    setProxyInfoFrom(this.uri.getScheme());
//    // ProxyInfo proxyInfo = this.wagonManager.getProxy(this.uri.getScheme());
//    // ProxyInfo proxyInfo = (ProxyInfo) this.proxyInfoSupplier.get();
//    if (proxyInfo != null && proxyInfo.getHost() != null && ProxyInfo.PROXY_HTTP.equals(proxyInfo.getType())) {
//      routePlanner = new DefaultProxyRoutePlanner(new HttpHost(proxyInfo.getHost(), proxyInfo.getPort()));
//      if (proxyInfo.getUserName() != null) {
//        final Credentials creds;
//        if (proxyInfo.getNtlmHost() != null || proxyInfo.getNtlmDomain() != null) {
//          creds = new NTCredentials(proxyInfo.getUserName(), proxyInfo.getPassword(), proxyInfo.getNtlmHost(),
//              proxyInfo.getNtlmDomain());
//        } else {
//          creds = new UsernamePasswordCredentials(proxyInfo.getUserName(), proxyInfo.getPassword());
//        }
//        AuthScope authScope = new AuthScope(proxyInfo.getHost(), proxyInfo.getPort());
//        if (credentialsProvider == null) {
//          credentialsProvider = new BasicCredentialsProvider();
//        }
//        credentialsProvider.setCredentials(authScope, creds);
//      }
//    } else {
//      routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
//    }
//
//    final CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(CONN_POOL)
//        .setConnectionManagerShared(true).setRoutePlanner(routePlanner).build();
//
//    final HttpFileRequester fileRequester = new HttpFileRequester(httpClient,
//        /* this.session.getSettings().isInteractiveMode() */
//
//        /*
//         * this.interactiveMode ? new LoggingProgressReport(getLog()) :
//         */new SilentProgressReport(getLog()));
//
//    final HttpClientContext clientContext = HttpClientContext.create();
//    clientContext.setRequestConfig(requestConfig);
//    if (credentialsProvider != null) {
//      clientContext.setCredentialsProvider(credentialsProvider);
//    }
//
//    fileRequester.download(this.uri, outputFile, clientContext);
//  }

  /**
   * Check if target host should be accessed via proxy.
   *
   * @param proxyInfo Proxy info to check for proxy config.
   * @return True if the target host will be requested via a proxy.
   */
  private boolean useHttpProxy(final ProxyInfo proxyInfo) {
    final boolean result;
    if (proxyInfo == null) {
      result = false;
    } else {
      if (proxyInfo.getHost() == null) {
        result = false;
      } else {
        if (proxyInfo.getNonProxyHosts() == null) {
          result = true;
          getLog().debug(String.format("%s is a proxy host", this.uri.getHost()));
        } else {
          result = !ProxyUtils.validateNonProxyHosts(proxyInfo, this.uri.getHost());
          getLog().debug(String.format("%s is a non-proxy host", this.uri.getHost()));
        }
      }
    }
    return result;
  }

  // private String decrypt(String str, String server) {
  // try {
  // return securityDispatcher.decrypt(str);
  // }
  // catch(SecDispatcherException e) {
  // getLog().warn(String.format("Failed to decrypt password/passphrase for server
  // %s, using auth token as is", server), e);
  // return str;
  // }
  // }

  // ************************ Adding Constructor and setters for private params so
  // that this is a component

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public void setOverwrite(boolean overwrite) {
    this.overwrite = overwrite;
  }

  // public void setOutputFileName(String outputFileName) {
  // this.outputFileName = outputFileName;
  // }

  public void setOutputPath(Path outputPath) {
    this.outputPath = outputPath;
  }

  public void setSha512(String sha512) {
    this.sha512 = sha512;
  }

  // public void setUnpack(boolean unpack) {
  // this.unpack = unpack;
  // }
  //

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setRetries(int retries) {
    this.retries = retries;
  }

  public void setReadTimeOut(int readTimeOut) {
    this.readTimeOut = readTimeOut;
  }

  public void setSkipCache(boolean skipCache) {
    this.skipCache = skipCache;
  }

  public void setCacheDirectory(File cacheDirectory) {
    this.cacheDirectory = cacheDirectory;
  }

  public void setFailOnError(boolean failOnError) {
    this.failOnError = failOnError;
  }

  public void setCheckSignature(boolean checkSignature) {
    this.checkSignature = checkSignature;
  }

  private boolean interactiveMode = false;

  private Map<String, String> headers = new HashMap<>();

//    public void setInteractiveMode(boolean interactiveMode) {
//      this.interactiveMode = interactiveMode;
//    }

  private WagonManager wagonManager;

  private ProxyInfo proxyInfo = null;

  private String mimeType = null;

  // We do not follow redirects
  private final boolean followRedirects = false;

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  private TypeToExtensionMapper t2e;
  private ArchiverManager archiverManager;

  public void setT2EMapper(TypeToExtensionMapper t2e) {
    this.t2e = t2e;
  }

  public void setWagonManager(WagonManager info) {
    this.wagonManager = info;
  }

  public void setProxyInfoFrom(String string) {
    this.proxyInfo = Optional.ofNullable(this.wagonManager).map(wm -> wm.getProxy(string)).orElse(null);
  }

  public void setLog(Log log) {
    this.log = log;
  }
  /*
   * log, proxyInfoSupplier are required!!!
   */

  public void setHeaders(Map<String, String> headers2) {
    this.headers.putAll(requireNonNull(headers2));
  }

  public void setArchiverManager(ArchiverManager archiverManager) {
    this.archiverManager = archiverManager;

  }

  public void setFileMappers(FileMapper[] m) {
    this.fileMappers = m;
  }

  // public void setLog(LoggerSupplier log) {
  // this.log = cet.withReturningTranslation(() -> ((Log)
  // requireNonNull(log).get()));
  // }
  //
  // public void setProxySupplier(ProxyInfoSupplier pi) {
  // this.proxyInfo = cet.withReturningTranslation(() -> ((ProxyInfo)
  // requireNonNull(pi).get()));
  // }
}
