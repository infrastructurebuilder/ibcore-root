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
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.util.constants.IBConstants.CACHEDIR;
import static org.infrastructurebuilder.util.constants.IBConstants.FILEMAPPERS;
import static org.infrastructurebuilder.util.constants.IBConstants.WORKINGDIR;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.infrastructurebuilder.util.core.Configurable;
import org.infrastructurebuilder.util.core.HeadersSupplier;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.LoggerSupplier;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.WGetter;
import org.infrastructurebuilder.util.readdetect.WGetterSupplier;
import org.slf4j.Logger;

@Named
public class DefaultWGetterSupplier implements WGetterSupplier, Configurable<ConfigMap, WGetterSupplier> {

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

  @Inject
  public DefaultWGetterSupplier(LoggerSupplier log, TypeToExtensionMapper t2e,

      Map<String, PathSupplier> pathSuppliers,

      Map<String, FileMapper> fileMappers,
//
//      @Named(IBDATA_WORKING_PATH_SUPPLIER) PathSupplier workingPathSupplier,
//      @Named(IBDATA_DOWNLOAD_CACHE_DIR_SUPPLIER) PathSupplier cacheDirSupplier,
      HeadersSupplier headerSupplier,

      ArchiverManager archiverManager, ProxyInfoProvider pip)
  {
    this.log = requireNonNull(log).get();
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
  public WGetter get() {
    return new DefaultWGetter(log, t2e, headers, cacheDirectory.get(), workingDirectory.get(), this.archiverManager,
        ofNullable(this.proxyInfoProvider), ofNullable(mappers.get()));
  }

  public DefaultWGetterSupplier withProxyInfoProvider(ProxyInfoProvider proxyInfoProvider) {
    if (this.proxyInfoProvider == null)
      this.proxyInfoProvider = proxyInfoProvider;
    else
      log.warn("Cannot replace existing ProxyInfoProvider");
    return this;
  }

  @Override
  public WGetterSupplier withConfig(ConfigMap config) {
    this.workingDirectory.compareAndSet(null,
        this.pathSuppliers.get(requireNonNull(config).getString(WORKINGDIR)).get());
    this.cacheDirectory.compareAndSet(null, this.pathSuppliers.get(requireNonNull(config).getString(CACHEDIR)).get());

    List<FileMapper> ls = IBUtils.asStringStream(config.getJSONArray(FILEMAPPERS))
        .map(key -> ofNullable(fileMappers.get(key))
            .orElseThrow(() -> new IBException(String.format("FileMapper {} not found", key))))
        .collect(toList());
    this.mappers.compareAndSet(null, ls.toArray(new FileMapper[ls.size()]));
    return this;
  }

}
