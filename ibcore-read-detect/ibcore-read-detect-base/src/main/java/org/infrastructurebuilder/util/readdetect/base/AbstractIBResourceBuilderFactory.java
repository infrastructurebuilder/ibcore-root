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

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceCacheModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractIBResourceBuilderFactory<I> extends IBResourceCacheModel
    implements IBResourceBuilderFactory<I> {

  private static final long serialVersionUID = 1200177361527373141L;

  private final static Logger log = LoggerFactory.getLogger(AbstractIBResourceBuilderFactory.class);

  private final RelativeRoot _root;

  private final AtomicReference<TypeToExtensionMapper> t2e = new AtomicReference<>();

  public AbstractIBResourceBuilderFactory(RelativeRoot relRoot) {
    super();
    this._root = relRoot;
//    this.setModelEncoding(UTF_8);
    String r = Optional.ofNullable(relRoot)
        .map(rr -> rr.getPath().map(Path::toAbsolutePath).map(Path::toString).orElse(null)).orElse(null);
    this.setRoot(r);
    log.debug("Root is {}", this.getRoot());
  }

  abstract protected Supplier<? extends IBResourceBuilder<I>> getBuilder();

  @Override
  public final RelativeRoot getRelativeRoot() {
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
          .withFilePath(m.getPath().orElse(null)) // Null path must be filled later
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
