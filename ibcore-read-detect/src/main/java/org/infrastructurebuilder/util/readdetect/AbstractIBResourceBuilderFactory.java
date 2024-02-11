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
package org.infrastructurebuilder.util.readdetect;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.util.constants.IBConstants.UNKNOWN_SIZE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceCacheModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractIBResourceBuilderFactory<B> extends IBResourceCacheModel
    implements IBResourceBuilderFactory<B> {

  private static final long serialVersionUID = 1200177361527373141L;

  private final static Logger log = LoggerFactory.getLogger(AbstractIBResourceBuilderFactory.class);

  private final RelativeRoot _root;

  public AbstractIBResourceBuilderFactory(RelativeRoot relRoot) {
    super();
    this._root = relRoot;
//    this.setModelEncoding(UTF_8);
    String r = Optional.ofNullable(relRoot)
        .map(rr -> rr.getPath().map(Path::toAbsolutePath).map(Path::toString).orElse(null)).orElse(null);
    this.setRoot(r);
    log.debug("Root is {}", this.getRoot());
  }

  abstract protected Supplier<IBResourceBuilder<B>> getBuilder();

  @Override
  public final RelativeRoot getRelativeRoot() {
    return this._root;
  }

  @Override
  public Optional<IBResourceBuilder<B>> fromJSON(JSONObject json) {
    return of(getBuilder().get().fromJSON(json));
  }

  @Override
  public Optional<IBResourceBuilder<B>> fromModel(IBResourceModel model) {
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

}
