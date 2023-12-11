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

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractIBResource<T> implements IBResource<T> {
  private final static Logger log = LoggerFactory.getLogger(AbstractIBResource.class);
  protected final IBResourceModel m;
  protected Checksum checksum;

  protected final RelativeRoot root;

  public AbstractIBResource(RelativeRoot root, IBResourceModel model) {
    this.root = root;
    this.m = model;
    log.info("AbstractIBResource with " + this.root + " " + this.m);
  }

  public AbstractIBResource(RelativeRoot root) {
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
    if (m.getType() == null) {
      getPath().ifPresent(path -> m.setType(IBResourceBuilderFactory.toType.apply(path)));
    }
    return m.getType();
  }

  @Override
  public Optional<URL> getSourceURL() {
    return ofNullable(m.getSource()).map(u -> IBUtils.translateToWorkableArchiveURL(u));
  }

  @Override
  public Optional<String> getSourceName() {
    return ofNullable(m.getName());
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
    return this.m.getName();
  }

  @Override
  public Optional<String> getDescription() {
    return this.m.getDescription();
  }

  @Override
  public long size() {
    return this.m.getSize();
  }

  @Override
  public Optional<Properties> getAdditionalProperties() {
    var p2 = m.getAdditionalProperties();
    Properties p = new Properties();
    m.getAdditionalProperties().forEach((k,v) -> p.setProperty(k,v.toString()));
    return (p.size() == 0) ? empty() : of(p);
  }

  protected void setName(String name) {
    this.m.setName(name);
  }

  protected void setDescription(String desc) {
    this.m.setDescription(desc);
  }

  public void setSource(String source) {
    this.m.setSource(requireNonNull(source));
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
      return m.getFilePath().map(URI::create).map(Paths::get);
    } catch (Throwable e) {
      log.error("Failed to get path " + m.getFilePath());
      return Optional.empty();
    }
  }

}
