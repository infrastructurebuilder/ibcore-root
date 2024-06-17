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
package org.infrastructurebuilder.util.readdetect.base.impls;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.constants.IBConstants.CACHEDIR;
import static org.infrastructurebuilder.constants.IBConstants.FILEMAPPERS;
import static org.infrastructurebuilder.constants.IBConstants.WORKINGDIR;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.PathSupplier;
import org.infrastructurebuilder.pathref.RelativeRoot;
import org.infrastructurebuilder.pathref.TypeToExtensionMapper;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.core.Configurable;
import org.infrastructurebuilder.util.core.HeadersSupplier;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.LoggerSupplier;
import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.infrastructurebuilder.util.mavendownloadplugin.WGetBuilderFactory;
import org.infrastructurebuilder.util.mavendownloadplugin.nonpublic.DefaultWGetBuilderFactory;
import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.base.IBResourceCollector;
import org.infrastructurebuilder.util.readdetect.base.IBResourceCollectorSupplier;
import org.json.JSONObject;
import org.slf4j.Logger;

abstract public class AbstractIBResourceCollectorSupplier<I>
    implements IBResourceCollectorSupplier, Configurable<ConfigMap> {

  @Inject
  private final ArchiverManager archiverManager;
  @Inject
  private ProxyInfoProvider proxyInfoProvider; // was wagonProvider

  private final Logger log;
  private final TypeToExtensionMapper t2e;
  private final Map<String, PathSupplier> pathSuppliers;

  private final Map<String, String> headers;
  private final Map<String, FileMapper> fileMappers;

  private final AtomicReference<Path> cacheDirectory = new AtomicReference<>();
  private final AtomicReference<Path> workingDirectory = new AtomicReference<>();
  private final AtomicReference<FileMapper[]> mappers = new AtomicReference<>();
  private final WGetBuilderFactory wgs;

  @Inject
  public AbstractIBResourceCollectorSupplier(LoggerSupplier log //
      , TypeToExtensionMapper t2e //
      , Map<String, PathSupplier> pathSuppliers //
      , Map<String, FileMapper> fileMappers //
      , HeadersSupplier headerSupplier //
      , ArchiverManager archiverManager //
      , ProxyInfoProvider pip //
  ) {
    this.log = requireNonNull(log).get();
    this.wgs = new DefaultWGetBuilderFactory(archiverManager);
    this.t2e = requireNonNull(t2e);
    this.headers = requireNonNull(headerSupplier).get();
    this.pathSuppliers = requireNonNull(pathSuppliers);
    this.fileMappers = requireNonNull(fileMappers);
    this.archiverManager = requireNonNull(archiverManager);
    this.proxyInfoProvider = requireNonNull(pip);
  }

  public Logger getLog() {
    return this.log;
  }

  @Override
  public IBResourceCollector get() {
    return new DefaultIBResourceCollector(log //
        , wgs //
            .withCacheDirectory(cacheDirectory.get()),
        t2e, headers, workingDirectory.get(), ofNullable(this.proxyInfoProvider), ofNullable(mappers.get()));
  }

  public IBResourceCollectorSupplier withProxyInfoProvider(ProxyInfoProvider proxyInfoProvider) {
    if (this.proxyInfoProvider == null)
      this.proxyInfoProvider = proxyInfoProvider;
    else
      log.warn("Cannot replace existing ProxyInfoProvider");
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public IBResourceCollectorSupplier withConfig(ConfigMap config) {
    this.workingDirectory.compareAndSet(null,
        this.pathSuppliers.get(requireNonNull(config).getString(WORKINGDIR)).get());
    this.cacheDirectory.compareAndSet(null, this.pathSuppliers.get(requireNonNull(config).getString(CACHEDIR)).get());

    List<FileMapper> ls = IBUtils.asStringStream(config.getJSONArray(FILEMAPPERS))
        .map(key -> ofNullable(fileMappers.get(key))
            .orElseThrow(() -> new IBException(String.format("FileMapper {} not found", key))))
        .toList();
    this.mappers.compareAndSet(null, ls.toArray(new FileMapper[ls.size()]));
    return this;
  }

  // ------
  private class DefaultIBResourceCollector implements IBResourceCollector {
    private static final String RES_BLD_ERR = "Could not produce resource builder from ";
    private static final String FROM_PATH_ERR = "Could not 'fromPath' resource from ";
    private final Logger log;
    private final Path workingDir;
    private final TypeToExtensionMapper t2e;
    private IBResourceBuilderFactory cf;
    private final WGetBuilderFactory wGetBldrFact;
    private final FileMapper[] mappers;

    public DefaultIBResourceCollector(Logger log //
        , WGetBuilderFactory wgs //
        , TypeToExtensionMapper t2e //
        , Map<String, String> headers //
        , Path workingDir //
        , Optional<ProxyInfoProvider> proxinfo //
        , Optional<FileMapper[]> fileMaprs) {
      this.log = log;
      this.t2e = requireNonNull(t2e);
      // this.wps = requireNonNull(wps);
      this.wGetBldrFact = Objects.requireNonNull(wgs);
      Objects.requireNonNull(proxinfo).ifPresent(p -> {
      });
      cf = getBuilderFactory(new AbsolutePathRelativeRoot(workingDir)).withTypeMapper(t2e);
      this.workingDir = requireNonNull(workingDir);
      this.mappers = fileMaprs.orElseGet(() -> new FileMapper[0]);
      log.info("Resource Collector created");
    }

    @Override
    synchronized public final Optional<List<IBResource>> collectCachedIBResources(//
        boolean deleteExistingCacheIfPresent // makes overwrite
        , Optional<BasicCredentials> creds // NOT proxy creds
        , String sourceString //
        , Optional<Checksum> checksum //
        , Optional<String> type //
        , int retries //
        , int readTimeOut //
        , boolean skipCache //
        , boolean expandArchives //
        , Optional<JSONObject> rootMetadata //
        , Optional<JSONObject> metadataForExpandedArchive //
    ) {

      return Optional.empty();
//
//      var uri = cet.returns(() -> IBUtils.translateToWorkableArchiveURL(requireNonNull(sourceString)).toURI());
//      var sha512 = requireNonNull(checksum).map(Checksum::toString).map(String::toLowerCase).orElse(null);
//      WGetBuilder q = this.wGetBldrFact.builder() //
//          .withOutputDirectory(this.workingDir) // FIXME
//          .withUnpack(expandArchives) //
//          .withUri(uri) //
//          .withFailOnError(true) //
//          .withOverwrite(deleteExistingCacheIfPresent) //
//          .withRetries(retries) //
//          .withReadTimeOut(readTimeOut) //
//          .withSkipCache(skipCache) //
//          .withSha512(sha512).withFileMappers(mappers) //
////          .withHeaders(headers) //
//      ;
//      requireNonNull(creds).ifPresent(bc -> {
//        q.withUsername(bc.getKeyId());
//        q.withPassword(bc.getSecret().orElse(null));
//      });
//      return q.wget().map(res -> {
//        List<IBResource> retVal = new ArrayList<>();
//        PathAndChecksum orig = res.getOriginal();
//        IBResourceBuilder<I> b = cf //
//
//            .fromPathAndChecksum(orig) //
//
//            .orElseThrow(() -> new IBResourceException(FROM_PATH_ERR + res.getOriginal())) //
//            .withType(type) // Was optionally supplied
//            .withAcquired(res.getAcquired()) // Was definitely obtained by the resource downloader
//            .withChecksum(checksum.orElseGet(() -> res.getOriginal().asChecksum())) // Obtainable
//            .withMetadata(rootMetadata.orElseGet(() -> new JSONObject()))
//
//        ;
//
//        IBResource qqq = b.build() //
//            .orElseThrow(() -> new IBResourceException(RES_BLD_ERR + res.getOriginal()));
//        // Add this resource to retVal
//        retVal.add(qqq);
//        // Check to see if we're adding expanded resources
//        String prefix;
//        // FIXME This might have additional edge cases that will come up
//        switch (qqq.getType()) {
//        case IBConstants.APPLICATION_JAR:
//          prefix = "jar:";
//          break;
//        case IBConstants.APPLICATION_ZIP:
//          prefix = "zip:";
//          break;
//        default:
//          prefix = "";
//          break;
//        }
//        res.getExpanded().ifPresent(expanded -> {
//          String rootOfDir = retVal.get(0).getSourceURL().map(URL::toExternalForm).map(n -> prefix + n).get();
//          // FIXME Do I need a new factory here with 'expanded' as the relative root or some such instead of 'cf'?
//          Path er = res.getExpandedRoot().get();
//          List<IBResource> qq = expanded.stream().map(pandc -> {
//            Path p = pandc.get();
//            Path path = er.resolve(p);
//            String pathString = String.format("%s%s", rootOfDir, p.toString());
//            IBResourceBuilder<I> builder = cf
//
//                .fromPathAndChecksum(pandc) //
//
//                .orElseThrow(() -> new IBResourceException(FROM_PATH_ERR + path));
//            IBResource file = builder //
//                .withName(p.getFileName().toString()) // FIXME
//                .withAcquired(res.getAcquired()) //
//                .withSource(pathString) //
//                .withDescription("Expanded from parent") //
//                .withSize(cet.returns(() -> Files.size(path))) //
//                .withChecksum(pandc.asChecksum()) //
//                .withLastUpdated(cet.returns(() -> Files.getLastModifiedTime(path).toInstant()))
//                .withMostRecentAccess(res.getAcquired()) // FIXME _Technically_ this is accurate
//                .withCreateDate(res.getAcquired()) // Also technically accurate
//                .withType(IBResourceBuilderFactory.toType.apply(path))
//                .withMetadata(metadataForExpandedArchive.orElseGet(() -> new JSONObject())).build() //
//                .orElseThrow(() -> new IBResourceException(RES_BLD_ERR + path));
//            return file;
//          }).toList();
//
//          retVal.addAll(qq);
//
//        });
//        return retVal;
//      });
    }
  }

  abstract public IBResourceBuilderFactory<I> getBuilderFactory(RelativeRoot rr);
}
