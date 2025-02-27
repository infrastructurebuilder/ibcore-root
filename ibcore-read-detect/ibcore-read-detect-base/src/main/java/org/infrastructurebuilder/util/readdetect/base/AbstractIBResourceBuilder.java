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
//import static org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory.extracted;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.pathref.OptionalReflectionLoadingTikaDetector.toType;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.infrastructurebuilder.api.ConfigMap;
import org.infrastructurebuilder.api.base.ConfigMapConfigurable;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.util.ibpathref.metadata.model.v1_0.IBMetadataModel;
import org.infrastructurebuilder.pathref.util.ibpathref.metadata.model.v1_0.IBMetadataModel.IBMetadataModelBuilderBase;
import org.infrastructurebuilder.pathref.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.readdetect.api.IBResource;
import org.infrastructurebuilder.util.readdetect.api.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.api.IBResourceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * builder base NOT THREADSAFE
 *
 * @param
 */
abstract public class AbstractIBResourceBuilder<I> //
    implements IBResourceBuilder<I>, ConfigMapConfigurable {

  private final static Logger log = LoggerFactory.getLogger(AbstractIBResourceBuilder.class);
  protected IBResourceModel model = new IBResourceModel();
  private Checksum targetChecksum;
  private boolean typeSet = false;
  protected Path sourcePath;
  private final PathRefFileSystem root;

  public AbstractIBResourceBuilder(PathRefFileSystem root) {
    this.root = root;
  }

  abstract protected Optional<ConfigMap> getConfig();

  @Override
  public IBResourceBuilder<I> fromJSON(JSONObject j) {
    model = IBResourceBuilder.modelFromJSON.apply(j)
        .orElseThrow(() -> new IBException("Unable to acquire model from json"));
    URI fp = URI.create(model.getStreamSource());
    log.debug("Got %s from model", fp);
    Path p = Paths.get(fp);
    this.sourcePath = p;
    return this;
  }

  /**
   * For IBResourceInMemoryDelegated. Do not use for general construction of a resource.
   *
   * @param m model to replace existing model with
   * @return this builder.
   */
  public IBResourceBuilder<I> fromModel(IBResourceModel m) {
    this.model = m;
    return this;
  }

  @Override
  public IBResourceBuilder<I> withChecksum(Checksum csum) {
    this.targetChecksum = requireNonNull(csum);
    this.model.setStreamChecksum(csum.toString());
    return this;
  }

  @Override
  public IBResourceBuilder<I> withAcquired(Instant acquired) {
    this.model.setAcquired(acquired);
    return this;
  }

  @Override
  public IBResourceBuilder<I> withName(String name) {
    this.model.setStreamName(requireNonNull(name));
    return this;
  }

  @Override
  public IBResourceBuilder<I> withDescription(String desc) {
    this.model.setDescription(requireNonNull(desc));
    return this;
  }

  @Override
  public IBResourceBuilder<I> withType(String type) {
    if (this.typeSet) {
      log.warn("Type already set to {}", this.model.getStreamType());
      return this;
    }
    this.model.setStreamType(requireNonNull(type));
    this.typeSet = true;
    return this;
  }

  @Override
  public IBResourceBuilder<I> withMetadata(JSONObject p) {
    IBMetadataModelBuilderBase<?> b = IBMetadataModel.builder();
    if (p != null)
      p.toMap().forEach((k, v) -> b.withAdditionalProperty(k, v));
    this.model.setMetadata(b.build());
    ;
    return this;
  }

  @Override
  public IBResourceBuilder<I> withLastUpdated(Instant last) {
    this.model.setLastUpdate(requireNonNull(last));
    return this;
  }

  @Override
  public IBResourceBuilder<I> withSource(URI source) {
    this.model.setStreamSource(requireNonNull(source).toString());
    return this;
  }

  @Override
  public IBResourceBuilder<I> withCreateDate(Instant create) {
    this.model.setCreated(requireNonNull(create));
    return this;
  }

  @Override
  public IBResourceBuilder<I> withSize(long size) {
    this.model.setStreamSize(size);
    return this;
  }

  @Override
  public IBResourceBuilder<I> withMostRecentAccess(Instant access) {
    this.model.setMostRecentReadTime(requireNonNull(access));
    return this;
  }

  @Override
  public IBResourceBuilder<I> withGroup(String groupName) {
    this.model.setGroup(groupName);
    return this;
  }

  @Override
  public IBResourceBuilder<I> withOwner(String ownerName) {
    this.model.setOwner(ownerName);
    return this;
  }

  @Override
  public IBResourceBuilder<I> withPermissions(Set<String> perms) {
    if (perms != null) {
      this.model.setPermissions(PosixFilePermissions.toString(new HashSet<>(perms
          .stream().map(PosixFilePermission::valueOf).toList())));
    }
    return this;
  }

  @Override
  public IBResourceBuilder<I> detectType() {
    if (this.typeSet) {
      // We've already set the type
      log.warn("Call to detectType() but type already set to {}", this.model.getStreamType());
      return this;
    }
    return withType(toType.apply(getActualFullPathToResource().orElseThrow(() -> new IBResourceException("No root"))));
  }

  protected Optional<Path> getActualFullPathToResource() {
    // FIXME. ALSO, don't use this
    return (this.sourcePath.isAbsolute()) ? //
        Optional.of(this.sourcePath) //
        : //
        getRoot().flatMap(root -> {
          return Optional.of(IBException.cet.returns(() -> root.getRoot().toRealPath()));
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
  public Optional<? extends IBResourceBuilder<I>> validate(boolean hard) {
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
        String aType = toType.apply(this.sourcePath).orElse(APPLICATION_OCTET_STREAM);
        if (this.typeSet) {
          if (!this.model.getStreamType().equals(aType)) {
            log.error("Expected type {} does not equal actual type {}", this.model.getStreamType(), aType);
            return empty();
          }
        } else {
          this.model.setStreamType(aType);
          this.typeSet = true;
        }
      }
      if (!Objects.equals(this.targetChecksum.toString(), this.model.getStreamChecksum())) {
        log.error("Model checksum {} not equal to targeted checksum {}", this.model.getStreamChecksum(),
            this.targetChecksum);
        return empty();
      }
      if (this.model.getStreamType() == null) {
        log.warn("Type not available");
        this.model.setStreamType(toType.apply(this.sourcePath).orElse(APPLICATION_OCTET_STREAM));
      }

      // There has been no source path set.
    } else {
      log.warn("No sourcePath set for resource");
      // TODO??
    }
    return Optional.of(this);
  }

  abstract public Optional<IBResource> build(boolean hard);

  public Optional<PathRefFileSystem> getRoot() {
    return Optional.ofNullable(root);
  }

}
