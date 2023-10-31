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
import static java.util.Optional.ofNullable;
//import static org.apache.maven.shared.utils.StringUtils.isBlank;
import static org.codehaus.plexus.util.StringUtils.isNotBlank;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

//import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.proxy.ProxyUtils;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.bzip2.BZip2UnArchiver;
import org.codehaus.plexus.archiver.gzip.GZipUnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.snappy.SnappyUnArchiver;
import org.codehaus.plexus.archiver.xz.XZUnArchiver;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.slf4j.Logger;

//import com.googlecode.download.maven.plugin.internal.FileNameUtils;

/**
 * Will download a file from a web site using the standard HTTP protocol.
 *
 * @author Marc-Andre Houle
 * @author Mickael Istria (Red Hat Inc)
 */
public final class WGet {
  public static String getOutputFileName(URI uri) {
    return uri.getPath().isEmpty() || uri.getPath().equals("/") ? uri.getHost()
        : uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
  }

  public static boolean isBlank(String str) {
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

  public final static Function<ProxyInfo, Proxy> mapPIPToProxy = (pip) -> {
    Authentication a = new AuthenticationBuilder().addNtlm(pip.getNtlmHost(), pip.getNtlmDomain())
        .addPassword(pip.getPassword()).addUsername(pip.getUserName())

        .build();
    return new Proxy(pip.getType(), pip.getHost(), pip.getPort(), a);
  };

  private static final PoolingHttpClientConnectionManager CONN_POOL;
  /**
   * A map of file caches by their location paths. Ensures one cache instance per path and enables safe execution in
   * parallel builds against the same cache.
   */
//  private static final Map<String, DownloadCache> DOWNLOAD_CACHES = new ConcurrentHashMap<>();

  private static final Map<String, Lock> FILE_LOCKS = new ConcurrentHashMap<>();

  static {
    CONN_POOL = new PoolingHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.getSocketFactory())
        .register("https", new SSLConnectionSocketFactory(SSLContexts.createSystemDefault(), new String[] {
            "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"
        }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier())).build(), null, null, null, 1,
        TimeUnit.MINUTES);
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
   * Represent the file name to use as output value. If not set, will use last segment of "url"
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
   * The md5 of the file. If set, file signature will be compared to this signature and plugin will fail.
   */
  // @Parameter
  // private String md5;

  /**
   * The sha1 of the file. If set, file signature will be compared to this signature and plugin will fail.
   */
  // @Parameter
  // private String sha1;

  /**
   * The sha512 of the file. If set, file signature will be compared to this signature and plugin will fail.
   */
  // @Parameter
  private String sha512;

  // /**
  // * Whether to unpack the file in case it is an archive (.zip)
  // */
  //// @Parameter(property = "download.unpack", defaultValue = "false")
  // private boolean unpack;

  /**
   * Server Id from settings file to use for authentication Only one of serverId or (username/password) may be supplied
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
   * The directory to use as a cache. Default is ${local-repo}/.cache/maven-download-plugin
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
   * By default, checksum verification only occurs after downloading a file. This option additionally enforces checksum
   * verification for already existing, previously downloaded (or manually copied) files. If the checksum does not
   * match, re-download the file.
   * <p>
   * Use this option in order to ensure that a new download attempt is made after a previously interrupted build or
   * network connection or some other event corrupted a file.
   */
  private boolean alwaysVerifyChecksum = true; // ALWAYS

  private Logger log;

  /**
   * Maximum time (ms) to wait to acquire a file lock.
   *
   * Customize the time when using the plugin to download the same file from several submodules in parallel build.
   */
//  @Parameter(property = "maxLockWaitTime", defaultValue = "30000")
  private long maxLockWaitTime = 30000L;

  /**
   * {@link FileMapper}s to be used for rewriting each target path, or {@code null} if no rewriting shall happen.
   *
   * @since 1.6.8
   */
//  @Parameter(property = "download.fileMappers")
  private FileMapper[] fileMappers;

  private Log mavenLog;

  private String ntlmDomain;

  private String ntlmHost;

  private Path localRepo;

  private String proxyUsername;

  private String proxyPassword;

  public void setProxyPassword(String proxyPassword) {
    this.proxyPassword = proxyPassword;
  }

  public void setProxyUsername(String proxyUsername) {
    this.proxyUsername = proxyUsername;
  }

  public Logger getLog() {
    return log;
  }

//  private Log getLogAsMavenLog() {
//    if (this.mavenLog == null)
//      this.mavenLog = new LoggingMavenComponent(getLog());
//    return this.mavenLog;
//  }

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
//      // throw new IBWGetException(String.format("Server %s not found",
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

  public void setLocalRespository(File localRepo) {
    this.localRepo = ofNullable(localRepo).map(File::toPath).map(Path::toAbsolutePath).orElse(null);
  }

  public Path getLocalRepository() {
    if (this.localRepo == null) {
      Properties p = getAvailableProperties();
      if (p.containsKey("maven.repo.local")) {
        this.localRepo = Paths.get(p.getProperty("maven.repo.local"));
      } else if (p.containsKey("user.home")) {
        this.localRepo = Paths.get(p.getProperty("user.home")).resolve(".m2").resolve("repository");
      } else {
        String home = System.getenv("HOME");
        this.localRepo = ofNullable(home).map(Paths::get).map(Path::toAbsolutePath)
            .map(p2 -> p2.resolve(".m2").resolve("repository")).orElse(null);
      }
    }
    return this.localRepo;
  }

  // public void setOutputFileName(String outputFileName) {
  // this.outputFileName = outputFileName;
  // }

  private Properties getAvailableProperties() {
    // TODO Auto-generated method stub
    return null;
  }

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

  public void setNtlmDomain(String domain) {
    this.ntlmDomain = domain;
  }

  public void setNtlmHost(String host) {
    this.ntlmHost = host;
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

  private boolean interactiveMode = false;

  private Map<String, String> headers = new HashMap<>();

//    public void setInteractiveMode(boolean interactiveMode) {
//      this.interactiveMode = interactiveMode;
//    }

  private ProxyInfoProvider wagonManager;

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

  public void setWagonManager(ProxyInfoProvider info) {
    this.wagonManager = info;
  }

  public void setProxyInfoFrom(String string) {
    this.proxyInfo = ofNullable(this.wagonManager).map(wm -> wm.getProxyInfo(string)).orElse(null);
  }

  public void setLog(Logger log) {
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

  private String outputFileName;

  private boolean unpack;

  private String serverId;

  /**
   * For transfers
   */

//  @Inject
//  private BuildContext buildContext;

  private boolean preemptiveAuth;

  private IBResourceBuilderFactory cf;

  /**
   * Method call when the mojo is executed for the first time.
   *
   * @return
   *
   * @throws IBWGetException      if an error is occuring in this mojo.
   * @throws MojoFailureException if an error is occuring in this mojo.
   */
  Optional<List<IBResource>> downloadIt() throws IBWGetException {

    if (this.proxyInfo == null) {
      this.proxyInfo = new ProxyInfo();
      this.proxyInfo.setHost(this.uri.getHost());
      this.proxyInfo.setNonProxyHosts(this.ntlmDomain);
      this.proxyInfo.setNtlmHost(this.ntlmHost);
      this.proxyInfo.setUserName(this.proxyUsername);
      this.proxyInfo.setPassword(this.proxyPassword);
    }

    if (isNotBlank(this.serverId) && (isNotBlank(this.username) || isNotBlank(this.password))) {
      throw new IBWGetException("Specify either serverId or username/password, not both");
    }
    if (this.retries < 1) {
      throw new IBException("retries must be at least 1");
    }

    // PREPARE
    if (this.outputFileName == null) {
      this.outputFileName = getOutputFileName(this.uri);
    }
    if (!this.skipCache) {
      if (this.cacheDirectory == null) {
        this.cacheDirectory = ofNullable(getLocalRepository())
            .map(repo -> repo.resolve(".cache").resolve("download-maven-plugin")).map(Path::toFile)
            .orElseThrow(() -> new IBException("No local repo"));
//        this.cacheDirectory = new File(this.session.getLocalRepository().getBasedir(), ".cache/download-maven-plugin");
      } else if (this.cacheDirectory.exists() && !this.cacheDirectory.isDirectory()) {
        throw new IBException(
            String.format("cacheDirectory is not a directory: " + this.cacheDirectory.getAbsolutePath()));
      }
      getLog().debug("Cache is: " + this.cacheDirectory.getAbsolutePath());

    } else {
      getLog().debug("Cache is skipped");
    }
    IBException.cet.translate(() -> Files.createDirectories(this.outputPath));
    final File outputFile = new File(this.outputPath.toFile(), this.outputFileName);
    final Lock fileLock = FILE_LOCKS.computeIfAbsent(outputFile.getAbsolutePath(), ignored -> new ReentrantLock());

    final Checksums checksums = new Checksums(null, null, null, this.sha512, this.getLog());
    // DO
    boolean lockAcquired = false;
    try {
      lockAcquired = fileLock.tryLock(this.maxLockWaitTime, TimeUnit.MILLISECONDS);
      if (!lockAcquired) {
        final String message = String.format("Could not acquire lock for File: %s in %dms", outputFile,
            this.maxLockWaitTime);
        if (this.failOnError) {
          throw new IBWGetException(message);
        } else {
          getLog().warn(message);
          return Optional.empty();
        }
      }
      boolean haveFile = outputFile.exists();
      if (haveFile) {
        boolean checksumMatch = true;
        if (true /* this.alwaysVerifyChecksum */) {
          try {
            checksums.validate(outputFile);
          } catch (final IBWGetException e) {
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
//              if (this.session.getRepositorySession().isOffline()) {
//                  if (this.failOnError) {
//                      throw new IBWGetException("No file in cache and maven is in offline mode");
//                  } else {
//                      getLog().warn("Ignoring download failure.");
//                  }
//              }
        boolean done = false;
        for (int retriesLeft = this.retries; !done && retriesLeft > 0; --retriesLeft) {
          try {
            this.doGet(outputFile);
            checksums.validate(outputFile);
            done = true;
          } catch (DownloadFailureException ex) {
            // treating HTTP codes >= 500 as transient and thus always retriable
            if (this.failOnError && ex.getHttpCode() < 500) {
              throw new IBWGetException(ex.getMessage(), ex);
            } else {
              getLog().warn(ex.getMessage());
            }
          } catch (IOException ex) {
            if (this.failOnError) {
              throw new IBWGetException(ex.getMessage(), ex);
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
            throw new IBWGetException("Could not get content after " + this.retries + " failed attempts.");
          } else {
            getLog().warn("Ignoring download failure(s).");
            return Optional.empty();
          }
        }
      }
      if (this.unpack) {
        unpack(outputFile);
//        this.buildContext.refresh(this.outputDirectory);
      } else {
//        this.buildContext.refresh(outputFile);
      }
    } catch (IBWGetException e) {
      throw e;
    } catch (IOException ex) {
      throw new IBWGetException("IO Error: ", ex);
    } catch (NoSuchArchiverException e) {
      throw new IBWGetException("No such archiver: " + e.getMessage());
    } catch (Exception e) {
      throw new IBWGetException("General error: ", e);
    } finally {
      if (lockAcquired) {
        fileLock.unlock();
      }
    }
    Path outputPath = outputFile.toPath();
    Checksum finalChecksum = (this.sha512 == null ? new Checksum(outputFile.toPath()) : new Checksum(this.sha512));

    var csum = new Checksums(null, null, null, this.sha512, getLog());
    return IBException.cet.returns(() -> {
//      cache.install(this.uri, outputFile, checksums);
//      /* Get the "final name" */

      getLog().error("OutputPath {}", outputPath.toString());
      String finalFileName = finalChecksum.asUUID().get().toString() + t2e.getExtensionForType(this.mimeType);
      getLog().info("Final file name {}", finalFileName);
      Path newTarget = outputPath.getParent().resolve(finalFileName);
      getLog().info("new Target is {}", newTarget.toString());
      try {
        getLog().info("Calling moveAtomic({},{})", outputFile.toPath(), newTarget);
        IBUtils.moveAtomic(outputFile.toPath(), newTarget);
      } finally {
        outputFile.delete();
      }

      Path outPath = newTarget;

      final IBResourceBuilder b = cf.builderFromPathAndChecksum(outPath, finalChecksum)

          .withSource(this.uri.toURL().toExternalForm())

          .withType(ofNullable(this.mimeType));

      return Optional.of(List.of(b.build().get())); // FIXME!!!!!
    });
  }

  private void unpack(File outputFile) throws NoSuchArchiverException {
    UnArchiver unarchiver = this.archiverManager.getUnArchiver(outputFile);
    unarchiver.setSourceFile(outputFile);
    if (isFileUnArchiver(unarchiver)) {
      unarchiver.setDestFile(
          new File(this.outputPath.toFile(), this.outputFileName.substring(0, this.outputFileName.lastIndexOf('.'))));
    } else {
      unarchiver.setDestDirectory(this.outputPath.toFile());
    }
    unarchiver.setFileMappers(this.fileMappers);
    unarchiver.extract();
    outputFile.delete();
  }

  private boolean isFileUnArchiver(final UnArchiver unarchiver) {
    return unarchiver instanceof BZip2UnArchiver || unarchiver instanceof GZipUnArchiver
        || unarchiver instanceof SnappyUnArchiver || unarchiver instanceof XZUnArchiver;
  }

  private static RemoteRepository createRemoteRepository(String serverId, URI uri) {
    return new RemoteRepository.Builder(isBlank(serverId) ? null : serverId, isBlank(serverId) ? uri.getScheme() : null,
        isBlank(serverId) ? uri.getHost() : null).build();
  }

  private void doGet(final File outputFile) throws IOException {
    final HttpFileRequester.Builder fileRequesterBuilder = new HttpFileRequester.Builder();

    final RemoteRepository repository = createRemoteRepository(null /* this.serverId */, this.uri);

    // set proxy if present

    ofNullable(this.wagonManager).map(pip -> pip.getProxyInfo(repository.getProtocol()))
//      Optional.ofNullable(this.session.getRepositorySession().getProxySelector())
//              .map(selector -> selector.getProxy(repository))
        .filter(pip -> !ProxyUtils.validateNonProxyHosts(pip, this.uri.getHost())).map(WGet.mapPIPToProxy)
        .ifPresent(proxy -> addProxy(fileRequesterBuilder, repository, proxy));

    // Optional.ofNullable(this.session.getRepositorySession().getAuthenticationSelector())
//        .map(selector -> selector.getAuthentication(repository))
//        .ifPresent(auth -> addAuthentication(fileRequesterBuilder, repository, auth));
    Authentication auth = new AuthenticationBuilder().addNtlm(this.ntlmHost, this.ntlmDomain).addUsername(this.username)
        .addPassword(this.password).build();
    addAuthentication(fileRequesterBuilder, repository, auth);

    if (!this.skipCache) {
      fileRequesterBuilder.withCacheDir(this.cacheDirectory);
    }

    try {
      final HttpFileRequester fileRequester = fileRequesterBuilder
          .withProgressReport(new LoggingProgressReport(getLog())).withConnectTimeout(this.readTimeOut)
          .withSocketTimeout(this.readTimeOut).withUri(this.uri).withUsername(this.username).withPassword(this.password)
          .withServerId(this.serverId).withPreemptiveAuth(this.preemptiveAuth)
//              .withMavenSession(this.session)
          .withRedirectsEnabled(this.followRedirects).withLog(this.getLog()).build();
      fileRequester.download(outputFile, getAdditionalHeaders());
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  private void addProxy(final HttpFileRequester.Builder fileRequesterBuilder, final RemoteRepository repository,
      final Proxy proxy) {
    fileRequesterBuilder.withProxyHost(proxy.getHost());
    fileRequesterBuilder.withProxyPort(proxy.getPort());

//    final RemoteRepository proxyRepo = new RemoteRepository.Builder(repository).setProxy(proxy).build();
    fileRequesterBuilder.withProxyUserName(this.proxyInfo.getUserName());
    fileRequesterBuilder.withProxyPassword(this.proxyInfo.getPassword());
    fileRequesterBuilder.withProxyPort(this.proxyInfo.getPort());

    fileRequesterBuilder.withNtlmDomain(this.proxyInfo.getNtlmDomain());
    fileRequesterBuilder.withNtlmHost(this.proxyInfo.getNtlmHost());

//    try (final AuthenticationContext ctx = AuthenticationContext.forProxy(this.session.getRepositorySession(),
//        proxyRepo)) {
//      fileRequesterBuilder.withProxyUserName(ctx.get(AuthenticationContext.USERNAME));
//      fileRequesterBuilder.withProxyPassword(ctx.get(AuthenticationContext.PASSWORD));
//      fileRequesterBuilder.withNtlmDomain(ctx.get(AuthenticationContext.NTLM_DOMAIN));
//      fileRequesterBuilder.withNtlmHost(ctx.get(AuthenticationContext.NTLM_WORKSTATION));
//    }
  }

  private void addAuthentication(final HttpFileRequester.Builder fileRequesterBuilder,
      final RemoteRepository repository, final Authentication authentication) {
//    final RemoteRepository authRepo = new RemoteRepository.Builder(repository).setAuthentication(authentication)
//        .build();
//    try (final AuthenticationContext authCtx = AuthenticationContext.forRepository(this.session.getRepositorySession(),
//        authRepo)) {
//      final String username = authCtx.get(AuthenticationContext.USERNAME);
//      final String password = authCtx.get(AuthenticationContext.PASSWORD);
//      final String ntlmDomain = authCtx.get(AuthenticationContext.NTLM_DOMAIN);
//      final String ntlmHost = authCtx.get(AuthenticationContext.NTLM_WORKSTATION);
//
//      getLog().debug("providing custom authentication");
//      getLog().debug("username: " + username + " and password: ***");

    fileRequesterBuilder.withUsername(this.username);
    fileRequesterBuilder.withPassword(this.password);
    fileRequesterBuilder.withNtlmDomain(this.ntlmDomain);
    fileRequesterBuilder.withNtlmHost(this.ntlmHost);
//    }
  }

  private List<Header> getAdditionalHeaders() {
    return headers.entrySet().stream().map(pair -> new BasicHeader(pair.getKey(), pair.getValue()))
        .collect(Collectors.toList());
  }

  public void setCacheFactory(IBResourceBuilderFactory cf) {
    this.cf = requireNonNull(cf);

  }

}
