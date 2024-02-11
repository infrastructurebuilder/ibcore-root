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
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import javax.annotation.Nullable;

import org.infrastructurebuilder.objectmapper.jackson.ObjectMapperUtils;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

abstract public class AbstractIBResource<T> implements IBResource<T> {
  private final static Logger log = LoggerFactory.getLogger(AbstractIBResource.class);
  protected final IBResourceModel m;
  protected Checksum checksum;

  protected final RelativeRoot root;

  public AbstractIBResource(@Nullable RelativeRoot root, IBResourceModel model) {
    this.root = root;
    this.m = model;
    log.info("AbstractIBResource with " + this.root + " " + this.m);
  }

  public AbstractIBResource(@Nullable RelativeRoot root) {
    this(root, new IBResourceModel());
  }

  @Override
  public Checksum getChecksum() {
    if (this.checksum == null)
      this.checksum = asChecksum();
    return this.checksum;
  }

  @Override
  public Optional<RelativeRoot> getRelativeRoot() {
    return Optional.ofNullable(this.root);
  }

  @Override
  public String getType() {
    // The only way type would be null is if the code setType(null)
    if (m.getStreamType() == null) {
      getPath().ifPresent(path -> m.setStreamType(IBResourceBuilderFactory.toType.apply(path)));
    }
    return Optional.ofNullable(m.getStreamType()).orElse(IBConstants.APPLICATION_OCTET_STREAM);
  }

  @Override
  public Optional<URL> getSourceURL() {
    return ofNullable(m.getStreamSource()).map(u -> IBUtils.translateToWorkableArchiveURL(u));
  }

  @Override
  public Optional<String> getSourceName() {
    return ofNullable(m.getStreamName());
  }

  @Override
  public Optional<Instant> getMostRecentReadTime() {
    return this.m.getMostRecentReadTime();
  }

  @Override
  public Optional<Instant> getCreateDate() {
    return this.m.getCreated();
  }

  @Override
  public Optional<Instant> getAcquireDate() {
    return this.m.getAcquired();
  }

  @Override
  public Optional<Instant> getLastUpdateDate() {
    return this.m.getLastUpdate();
  }

  @Override
  public String getName() {
    return this.m.getStreamName();
  }

  @Override
  public Optional<String> getDescription() {
    return this.m.getDescription();
  }

  @Override
  public long size() {
    return this.m.getStreamSize();
  }

  @Override
  public String getModelVersion() {
    return this.m.getModelVersion();
  }

  @Override
  public JSONObject getMetadata() {
    var mx = this.m.getMetadata().orElse(new IBMetadataModel());
    String x = "{}";
    try {
      x = ObjectMapperUtils.mapper.get().writeValueAsString(mx);
    } catch (JsonProcessingException | JSONException e) {
      log.error("Error with processing metadata" + x);
    }
    return new JSONObject(x);
  }

  protected void setName(String name) {
    this.m.setStreamName(name);
  }

  protected void setDescription(String desc) {
    this.m.setDescription(desc);
  }

  public void setSource(String source) {
    this.m.setStreamSource(requireNonNull(source));
  }

  @Override
  public IBResourceModel copyModel() {
    return new IBResourceModel(this.m);
  }

  @Override
  public int hashCode() {
    return defaultHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return defaultEquals(obj);
  }

  @Override
  public String toString() {
    return defaultToString();
  }

  @Override
  public Optional<Path> getPath() {
    try {
      return m.getPath().map(URI::create).map(Paths::get);
    } catch (Throwable e) {
      log.error("Failed to get path " + m.getPath());
      return Optional.empty();
    }
  }

}
