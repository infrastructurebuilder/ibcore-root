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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.util.constants.IBConstants.CACHEDIR;
import static org.infrastructurebuilder.util.constants.IBConstants.FILEMAPPERS;
import static org.infrastructurebuilder.util.constants.IBConstants.WORKINGDIR;
import static org.infrastructurebuilder.util.core.IBUtils.getJSONArrayAsListString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.HeadersSupplier;
import org.infrastructurebuilder.util.core.IBConfigurable;
import org.infrastructurebuilder.util.core.LoggerSupplier;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.WGetter;
import org.infrastructurebuilder.util.readdetect.WGetterSupplier;
import org.json.JSONObject;

@Named
public class DefaultWGetterSupplier implements WGetterSupplier, IBConfigurable<JSONObject> {

  @Inject
  private final ArchiverManager archiverManager;
  @Inject
  private WagonManager wagonManager;

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

      ArchiverManager archiverManager, Supplier<ProxyInfo> proxyInfoSupplier) {
    this.log = requireNonNull(log).get();
    this.t2e = requireNonNull(t2e);
    this.headers = requireNonNull(headerSupplier).get();
    this.pathSuppliers = requireNonNull(pathSuppliers);
    this.fileMappers = requireNonNull(fileMappers);
    this.archiverManager = requireNonNull(archiverManager);
  }

  @Override
  public Logger getLog() {
    return this.log;
  }

  @Override
  public WGetter get() {
    return new DefaultWGetter(log, t2e, headers, cacheDirectory.get(), workingDirectory.get(), this.archiverManager,
        ofNullable(this.wagonManager), ofNullable(mappers.get()));
  }

  @Override
  public WGetterSupplier configure(JSONObject config) {
    this.workingDirectory.compareAndSet(null,
        this.pathSuppliers.get(requireNonNull(config).getString(WORKINGDIR)).get());
    this.cacheDirectory.compareAndSet(null, this.pathSuppliers.get(requireNonNull(config).getString(CACHEDIR)).get());

    List<FileMapper> ls = getJSONArrayAsListString(config, FILEMAPPERS).stream()
        .map(key -> ofNullable(fileMappers.get(key))
            .orElseThrow(() -> new IBException(format("FileMapper {} not found", key))))
        .collect(toList());
    this.mappers.compareAndSet(null, ls.toArray(new FileMapper[ls.size()]));
    return this;
  }

}
