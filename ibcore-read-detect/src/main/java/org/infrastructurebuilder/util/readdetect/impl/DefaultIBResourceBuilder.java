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
import static org.infrastructurebuilder.util.constants.IBConstants.ADDITIONAL_PROPERTIES;
import static org.infrastructurebuilder.util.constants.IBConstants.CREATE_DATE;
import static org.infrastructurebuilder.util.constants.IBConstants.DESCRIPTION;
import static org.infrastructurebuilder.util.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.util.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.util.constants.IBConstants.NAME;
import static org.infrastructurebuilder.util.constants.IBConstants.NO_PATH_SUPPLIED;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH_CHECKSUM;
import static org.infrastructurebuilder.util.constants.IBConstants.SIZE;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.util.constants.IBConstants.UPDATE_DATE;
import static org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory.extracted;
import static org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory.toType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceException;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIBResourceBuilder implements IBResourceBuilder {

  private final static Logger log = LoggerFactory.getLogger(IBResourceBuilder.class);
  private IBResourceModel model = new IBResourceModel();
  private Checksum targetChecksum;
  private Path sourcePath;
  private Path finalRestingPath;
  private RelativeRoot root;

  public DefaultIBResourceBuilder(Optional<RelativeRoot> root) {
    this.root = requireNonNull(root).orElse(null);
  }

  @Override
  public IBResourceBuilder fromJSON(JSONObject j) {
    model = new IBResourceModel();
    model.setFileChecksum(j.getString(PATH_CHECKSUM));
    model.setSize(j.getLong(SIZE));
    model.setSource(j.getString(SOURCE_URL));
    model.setType(j.getString(MIME_TYPE));
    model.setCreated(requireNonNull(j).optString(CREATE_DATE, null));
    model.setFilePath(j.optString(PATH, null)); // Nope!
    model.setLastUpdate(j.optString(UPDATE_DATE, null));
    model.setMostRecentReadTime(j.optString(MOST_RECENT_READ_TIME, null));
    model.setName(j.optString(NAME, null));
    model.setDescription(j.optString(DESCRIPTION, null));
    ofNullable(j.optJSONObject(ADDITIONAL_PROPERTIES)).ifPresent(jo -> {
      jo.toMap().forEach((k, v) -> {
        model.addAdditionalProperty(k, v.toString());
      });
    });

    var p = ofNullable(model.getFilePath())
        //
        .map(extracted)
        //
        .orElseThrow(() -> new IBResourceException(NO_PATH_SUPPLIED));
//    var originalPath = ofNullable(j.optString(ORIGINAL_PATH, null)).map(extracted).orElse(null);
    return this;
//    return this.fromModel(m).from(p).fromOriginalPath(originalPath);

  }

  @Override
  public IBResourceBuilder withChecksum(Checksum csum) {
    this.targetChecksum = requireNonNull(csum);
    this.model.setFileChecksum(csum.toString());
    return this;
  }

  @Override
  public IBResourceBuilder from(Path path) {
    this.sourcePath = requireNonNull(path);

    return this

        .withFilePath(path.toString())

        .withName(path.getFileName().toString())

        .withSource(path.toUri().toASCIIString());
  }

  @Override
  public IBResourceBuilder withFilePath(String path) {
    this.model.setFilePath(path);
    return this;
  }

  @Override
  public IBResourceBuilder cached(boolean cached) {
    this.model.setCached(cached);
    return this;
  }

  @Override
  public IBResourceBuilder withName(String name) {
    this.model.setName(requireNonNull(name));
    return this;
  }

  @Override
  public IBResourceBuilder withDescription(String desc) {
    this.model.setDescription(requireNonNull(desc));
    return this;
  }

  @Override
  public IBResourceBuilder withType(String type) {
    this.model.setType(requireNonNull(type));
    return this;
  }

  @Override
  public IBResourceBuilder withType(Optional<String> type) {
    return requireNonNull(type).map(t -> withType(t)).orElse(this);
  }

  @Override
  public IBResourceBuilder withAdditionalProperties(Properties p) {
    Properties p1 = new Properties();
    p1.putAll(p);
    this.model.setAdditionalProperties(p1);
    return this;
  }

  @Override
  public IBResourceBuilder withLastUpdated(Instant last) {
    this.model.setLastUpdate(requireNonNull(last).toString());
    return this;
  }

  @Override
  public IBResourceBuilder withSource(String source) {
    this.model.setSource(requireNonNull(source));
    return this;
  }

  @Override
  public IBResourceBuilder withCreateDate(Instant create) {
    this.model.setCreated(requireNonNull(create).toString());
    return this;
  }

  @Override
  public IBResourceBuilder withSize(long size) {
    this.model.setSize(size);
    return this;
  }

  @Override
  public IBResourceBuilder withMostRecentAccess(Instant access) {
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
  public IBResourceBuilder validate(boolean hard) {
//    getTargetDir().ifPresent(targetDir -> {
//      // there is a targetDir, so the file we're referencing should be in that
//      // directory
//      if (this.sourcePath != null)
//        if (!IBUtils.isParent(targetDir, this.sourcePath)) {
//          // We have a source, and a place it should be
//        }
//    });

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

  @Override
  public Optional<IBResource> build(boolean hard) {
    try {
      validate(hard);
      return Optional.of(new DefaultIBResource(getRoot(),this.model, this.sourcePath));
    } catch (IBException e) {
      log.error("Error building IBResource");
      return Optional.empty();
    }
  }

  /**
   * For IMDelegatedIBResource. Do not use for general construction of a resource. This method is not available outside of
   * this package.
   *
   * @param m model to replace existing model with
   * @return this builder.
   */
  DefaultIBResourceBuilder fromModel(IBResourceModel m) {
    this.model = m;
    return this;
  }

  @Override
  public IBResourceBuilder movedTo(Path path) {
    this.finalRestingPath = path;
    return this;
  }

  public Optional<RelativeRoot> getRoot() {
    return Optional.of(root);
  }

}
