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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.util.constants.IBConstants.NO_PATH_SUPPLIED;
import static org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory.extracted;
import static org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory.toType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel.IBMetadataModelBuilderBase;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * builder base NOT THREADSAFE
 *
 * @param <B>
 */
abstract public class AbstractIBResourceBuilder<B> implements IBResourceBuilder<B> {

  private final static Logger log = LoggerFactory.getLogger(AbstractIBResourceBuilder.class);
  protected IBResourceModel model = new IBResourceModel();
  private Checksum targetChecksum;
  private boolean typeSet = false;
  protected Path sourcePath;
  private Path finalRestingPath;
  private final RelativeRoot root;

  public AbstractIBResourceBuilder(RelativeRoot root) {
    this.root = root;
  }

  @Override
  public IBResourceBuilder<B> fromJSON(JSONObject j) {
    model = IBResourceBuilder.modelFromJSON.apply(j)
        .orElseThrow(() -> new IBException("Unable to acquire model from json"));
    String fp = model.getPath().orElseThrow(() -> new IBResourceException(NO_PATH_SUPPLIED));
    log.debug("Got %s from model", fp);
    Path p = extracted.apply(fp);
    this.sourcePath = p;
    return this;
  }

  @Override
  public IBResourceBuilder<B> withChecksum(Checksum csum) {
    this.targetChecksum = requireNonNull(csum);
    this.model.setStreamChecksum(csum.toString());
    return this;
  }

  @Override
  public IBResourceBuilder<B> withFilePath(String path) {
    this.model.setPath(path);
    return this;
  }

  @Override
  public IBResourceBuilder<B> withBasicFileAttributes(BasicFileAttributes a) {
    return (a == null) ? this
        : this.withCreateDate(a.creationTime().toInstant())

            .withSize(a.size())

            .withMostRecentAccess(a.lastAccessTime().toInstant())

            .withLastUpdated(a.lastModifiedTime().toInstant());
  }

  @Override
  public IBResourceBuilder<B> withAcquired(Instant acquired) {
    this.model.setAcquired(acquired);
    return this;
  }

  @Override
  public IBResourceBuilder<B> withName(String name) {
    this.model.setStreamName(requireNonNull(name));
    return this;
  }

  @Override
  public IBResourceBuilder<B> withDescription(String desc) {
    this.model.setDescription(requireNonNull(desc));
    return this;
  }

  @Override
  public IBResourceBuilder<B> withType(String type) {
    this.model.setStreamType(requireNonNull(type));
    this.typeSet = true;
    return this;
  }

  @Override
  public IBResourceBuilder<B> withMetadata(JSONObject p) {
    IBMetadataModelBuilderBase b = IBMetadataModel.builder();
    if (p != null)
      p.toMap().forEach((k, v) -> b.withAdditionalProperty(k, v));
    this.model.setMetadata(b.build());
    ;
    return this;
  }

  @Override
  public IBResourceBuilder<B> withLastUpdated(Instant last) {
    this.model.setLastUpdate(requireNonNull(last));
    return this;
  }

  @Override
  public IBResourceBuilder<B> withSource(String source) {
    this.model.setStreamSource(requireNonNull(source));
    return this;
  }

  @Override
  public IBResourceBuilder<B> withCreateDate(Instant create) {
    this.model.setCreated(requireNonNull(create));
    return this;
  }

  @Override
  public IBResourceBuilder<B> withSize(long size) {
    this.model.setStreamSize(size);
    return this;
  }

  @Override
  public IBResourceBuilder<B> withMostRecentAccess(Instant access) {
    this.model.setMostRecentReadTime(requireNonNull(access));
    return this;
  }

  @Override
  public IBResourceBuilder<B> detectType() {
    if (this.typeSet) {
      // We've already set the type
      log.warn("Call to detectType() but type already set to {}", this.model.getStreamType());
      return this;
    }
    return withType(toType.apply(getActualFullPathToResource().orElseThrow(() -> new IBResourceException("No root"))));
  }

  protected Optional<Path> getActualFullPathToResource() {
    return (this.sourcePath.isAbsolute()) ? //
        Optional.of(this.sourcePath) //
        : //
        getRoot().flatMap(root -> {
          return root.getPath().map(rPath -> rPath.resolve(this.sourcePath));
        });
  }

  /**
   * validate checks the values provided so far and throws IBResourceException if anything is off. You can call validate
   * whenever you set any value and if it returns your data is still possibly OK
   *
   * @param hard if true, then assume nothing and re-validate the existence and checksums of the paths and sources
   * @throws IBResourceException if validation fails
   * @return this builder
   */
  @Override
  public Optional<IBResourceBuilder<B>> validate(boolean hard) {
    log.info("{} Validating {}", hard ? "Hard" : "Soft", this.sourcePath);
    if (this.sourcePath != null) {
      Optional<Path> fullPath = getActualFullPathToResource();
      if (fullPath.isPresent()) {
        if (!Files.exists(fullPath.get())) {
          log.error("unreadable.path {}", fullPath.get());
          return empty();
        }
        if (this.targetChecksum == null) {
          var c = Checksum.ofPath.apply(fullPath.get()).get();
          log.info("target checksum not available.  Reading source path checksum as {}", c);
          this.withChecksum(c);
        }
      }
      if (!this.typeSet)
        detectType();
      if (hard) {
        var aType = toType.apply(this.sourcePath);
        if (!this.model.getStreamType().equals(aType)) {
          log.error("Expected type {} does not equal actual type {}", this.model.getStreamType(), aType);
          return empty();
        }
      }
      if (!Objects.equals(this.targetChecksum.toString(), this.model.getStreamChecksum())) {
        log.error("Model checksum {} not equal to targeted checksum {}", this.model.getStreamChecksum(),
            this.targetChecksum);
        return empty();
      }
      if (this.model.getStreamType() == null) {
        log.warn("Type not available");
        this.model.setStreamType(toType.apply(this.sourcePath));
      }

      // There has been no source path set.
    } else {
      log.warn("No sourcePath set for resource");
      // TODO??
    }
    return Optional.of(this);
  }

  abstract public B build(boolean hard);

  /**
   * For IBResourceInMemoryDelegated. Do not use for general construction of a resource.
   *
   * @param m model to replace existing model with
   * @return this builder.
   */
  public IBResourceBuilder<B> fromModel(IBResourceModel m) {
    this.model = m;
    return this;
  }

//  @Override
  private IBResourceBuilder<B> movedTo(Path path) {
    this.finalRestingPath = path;
    return this;
  }

  public Optional<RelativeRoot> getRoot() {
    return Optional.ofNullable(root);
  }

}
