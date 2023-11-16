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
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.NO_PATH_SUPPLIED;
import static org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory.extracted;
import static org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory.toType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractIBResourceBuilder<B> implements IBResourceBuilder<B> {

  private final static Logger log = LoggerFactory.getLogger(AbstractIBResourceBuilder.class);
  protected IBResourceModel model = new IBResourceModel();
  private Checksum targetChecksum;
  protected Path sourcePath;
  private Path finalRestingPath;
  private RelativeRoot root;

  public AbstractIBResourceBuilder(Optional<RelativeRoot> root) {
    this.root = requireNonNull(root).orElse(null);
  }

  @Override
  public IBResourceBuilder<B> fromJSON(JSONObject j) {
    model = IBResourceModel.modelFromJSON(j);
    Path p = ofNullable(model.getFilePath())
        //
        .map(extracted)
        //
        .orElseThrow(() -> new IBResourceException(NO_PATH_SUPPLIED));
    this.sourcePath = p;
    return this;
  }

  @Override
  public IBResourceBuilder<B> withChecksum(Checksum csum) {
    this.targetChecksum = requireNonNull(csum);
    this.model.setFileChecksum(csum.toString());
    return this;
  }

  @Override
  public IBResourceBuilder<B> from(Path path) {
    this.sourcePath = requireNonNull(path);

    return this

        .withFilePath(path.toString())

        .withName(path.getFileName().toString())

        .withSource(path.toUri().toASCIIString());
  }

  @Override
  public IBResourceBuilder<B> withFilePath(String path) {
    this.model.setFilePath(path);
    return this;
  }

  @Override
  public IBResourceBuilder<B> cached(boolean cached) {
    this.model.setCached(cached);
    return this;
  }

  @Override
  public IBResourceBuilder<B> withName(String name) {
    this.model.setName(requireNonNull(name));
    return this;
  }

  @Override
  public IBResourceBuilder<B> withDescription(String desc) {
    this.model.setDescription(requireNonNull(desc));
    return this;
  }

  @Override
  public IBResourceBuilder<B> withType(String type) {
    this.model.setType(requireNonNull(type));
    return this;
  }

  @Override
  public IBResourceBuilder<B> withType(Optional<String> type) {
    return requireNonNull(type).map(t -> withType(t)).orElse(this);
  }

  @Override
  public IBResourceBuilder<B> withAdditionalProperties(Properties p) {
    Properties p1 = new Properties();
    p1.putAll(p);
    this.model.setAdditionalProperties(p1);
    return this;
  }

  @Override
  public IBResourceBuilder<B> withLastUpdated(Instant last) {
    this.model.setLastUpdate(requireNonNull(last).toString());
    return this;
  }

  @Override
  public IBResourceBuilder<B> withSource(String source) {
    this.model.setSource(requireNonNull(source));
    return this;
  }

  @Override
  public IBResourceBuilder<B> withCreateDate(Instant create) {
    this.model.setCreated(requireNonNull(create).toString());
    return this;
  }

  @Override
  public IBResourceBuilder<B> withSize(long size) {
    this.model.setSize(size);
    return this;
  }

  @Override
  public IBResourceBuilder<B> withMostRecentAccess(Instant access) {
    this.model.setMostRecentReadTime(requireNonNull(access).toString());
    return this;
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
  public IBResourceBuilder<B> validate(boolean hard) {
    if (this.sourcePath != null) {
      if (!Files.exists(sourcePath))
        throw new IBResourceException("unreadable.path");
      if (this.targetChecksum == null) {
        log.warn("Checksum not available");
        this.targetChecksum = Checksum.ofPath.apply(this.sourcePath).get();
      }
      if (this.model.getType() == null) {
        log.warn("Type not available");
        this.model.setType(toType.apply(this.sourcePath));
      }

      // There has been no source path set.
    } else {
      log.warn("No sourcePath set for resource");
      // TODO??
    }
    return this;
  }

  abstract public B build(boolean hard);

  /**
   * For IMDelegatedIBResource. Do not use for general construction of a resource. This method is not available outside
   * of this package.
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
    return Optional.of(root);
  }

}
