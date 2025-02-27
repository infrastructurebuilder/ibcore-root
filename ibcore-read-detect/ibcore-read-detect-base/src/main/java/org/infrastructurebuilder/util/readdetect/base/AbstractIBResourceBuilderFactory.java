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
package org.infrastructurebuilder.util.readdetect.base;

import static java.util.Optional.of;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.infrastructurebuilder.api.ConfigMap;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.TypeToExtensionMapper;
import org.infrastructurebuilder.pathref.util.ibpathref.metadata.model.v1_0.IBResourceCacheModel;
import org.infrastructurebuilder.pathref.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.readdetect.api.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.api.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.base.impls.IbcoreReadDetectBaseVersioning;
import org.infrastructurebuilder.util.version.DefaultIBVersion;
import org.infrastructurebuilder.util.version.IBVersion;
import org.infrastructurebuilder.util.version.IBVersion.VersionDiff;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractIBResourceBuilderFactory<I> //
    extends IBResourceCacheModel //
    implements IBResourceBuilderFactory<I> {

  private static final long serialVersionUID = 1200177361527373141L;

  private final static Logger log = LoggerFactory.getLogger(AbstractIBResourceBuilderFactory.class);

  private final PathRefFileSystem _root;

  private final AtomicReference<TypeToExtensionMapper> t2e = new AtomicReference<>();

  public AbstractIBResourceBuilderFactory(PathRefFileSystem relRoot) {
    super();
    IBVersion ver = new DefaultIBVersion(IbcoreReadDetectBaseVersioning.apiVersion());
    IBVersion modelVer = new DefaultIBVersion(getModelVersion());
    VersionDiff diff = ver.diff(modelVer);
//    if (diff == VersionDiff.MAJOR || diff == VersionDiff.MINOR)
//      throw new IBResourceException(
//          "Model version %s is not compatible with this implementation".formatted(getModelVersion()));
    this._root = relRoot;
    this.setRoot(relRoot.toString());
    log.debug("Root is {}", this.getRoot());
  }

  abstract protected Supplier<? extends IBResourceBuilder<I>> getBuilder();

  abstract protected Optional<ConfigMap> getConfig();

  @Override
  public int respondsTo(String input) {
    return getName().equals(input) ? 0 : -1;
  }

  @Override
  public final PathRefFileSystem getRelativeRoot() {
    return this._root;
  }

  @Override
  public final IBResourceBuilderFactory<I> withTypeMapper(TypeToExtensionMapper m) {
    this.t2e.compareAndExchange(null, m); // Only settable once
    return this;
  }

  @Override
  public Optional<TypeToExtensionMapper> getTypeMapper() {
    return Optional.ofNullable(t2e.get());
  }

  @Override
  public Optional<IBResourceBuilder<I>> fromJSON(JSONObject json) {
    return of(getBuilder().get().fromJSON(json));
  }

  @Override
  public Optional<IBResourceBuilder<I>> fromModel(IBResourceModel model) {
    return Optional.ofNullable(model).map(m -> {
      return getBuilder().get()
          // TODO am I losing metadata somehow?
          // Taking some liberties here
          .withAcquired(m.getAcquired().orElse(null)) //
          .withChecksum(new Checksum(m.getStreamChecksum())) //
          .withCreateDate(m.getCreated().orElse(null)) //
          .withDescription(m.getDescription().orElse(null)) //
          .withLastUpdated(m.getLastUpdate().orElse(null)) //
          .withMostRecentAccess(m.getMostRecentReadTime().orElse(null)) //
          .withName(m.getStreamName()) //
          .withSize(m.getStreamSize()) //
          .withSource(m.getStreamSource()) //
          .withType(m.getStreamType()); //
    });
  }

  protected abstract Optional<I> extractFromModel(IBResourceModel model);

  protected abstract Optional<I> extractFromJSON(JSONObject json);

}
