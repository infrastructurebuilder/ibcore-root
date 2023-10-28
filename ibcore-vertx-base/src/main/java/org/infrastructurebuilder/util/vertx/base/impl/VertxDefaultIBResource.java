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
package org.infrastructurebuilder.util.vertx.base.impl;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.util.constants.IBConstants.CREATE_DATE;
import static org.infrastructurebuilder.util.constants.IBConstants.DESCRIPTION;
import static org.infrastructurebuilder.util.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.util.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.util.constants.IBConstants.NO_PATH_SUPPLIED;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.SIZE;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_NAME;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.util.constants.IBConstants.UPDATE_DATE;
import static org.infrastructurebuilder.util.core.ChecksumEnabled.CHECKSUM;
import static org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory.extracted;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.ChecksumBuilder;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.infrastructurebuilder.util.vertx.base.VertxIBResource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;

public class VertxDefaultIBResource implements VertxIBResource {
  private final static Logger log = LoggerFactory.getLogger(VertxDefaultIBResource.class.getName());

  private final IBResourceModel m;

  private final Path cachedPath;
  private Checksum checksum;

  private final RelativeRoot root;

  private final Vertx vertx;

  public VertxDefaultIBResource(Vertx vertx, Optional<RelativeRoot> root, IBResourceModel m, Path sourcePath) {
    this.vertx = Objects.requireNonNull(vertx);
    this.root = Objects.requireNonNull(root).orElse(null);
    this.cachedPath = sourcePath;
    this.m = requireNonNull(m);
    String ps = m.getFilePath();
    Path path = null;
    try {
      path = Paths.get(ps);
      log.debug("{} path {}", path.isAbsolute() ? "Absolute" : "Relative", path);
    } catch (Throwable t) {
      log.error("Path was unavailable from {}", ps);
    } finally {
    }
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<RelativeRoot> root, IBResourceModel m) {
    this(vertx, root, m, null);
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<RelativeRoot> root, JSONObject j) {
    this.vertx = Objects.requireNonNull(vertx);
    this.root = Objects.requireNonNull(root).orElse(null);
    m = new IBResourceModel();
    m.setCreated(requireNonNull(j).optString(CREATE_DATE, null));
    m.setFileChecksum(j.getString(CHECKSUM));
    m.setSize(j.getLong(SIZE));
    m.setType(j.getString(MIME_TYPE));
    m.setFilePath(j.optString(PATH, null));
    m.setLastUpdate(j.optString(UPDATE_DATE, null));
    m.setMostRecentReadTime(j.optString(MOST_RECENT_READ_TIME, null));
    m.setName(j.optString(SOURCE_NAME, null));
    m.setSource(j.optString(SOURCE_URL, null));
    m.setDescription(j.optString(DESCRIPTION, null));
    ofNullable(j.optJSONObject(IBConstants.ADDITIONAL_PROPERTIES)).ifPresent(jo -> {
      jo.toMap().forEach((k, v) -> {
        m.addAdditionalProperty(k, v.toString());
      });
    });

    this.cachedPath = ofNullable(j.optString(IBConstants.PATH, null)).map(extracted)
        .orElseThrow(() -> new IBException(NO_PATH_SUPPLIED));
//    this.originalPath = ofNullable(j.optString(IBConstants.ORIGINAL_PATH, null)).map(extracted).orElse(null);
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<RelativeRoot> root, Path path, Checksum checksum,
      Optional<String> type, Optional<Properties> addlProps)
  {
    this.vertx = Objects.requireNonNull(vertx);
    this.root = Objects.requireNonNull(root).orElse(null);
    this.m = new IBResourceModel();
//    this.originalPath = requireNonNull(path);
//    m.setFilePath(this.originalPath.toAbsolutePath().toString());
    m.setFilePath(requireNonNull(path).toAbsolutePath().toString());
    m.setFileChecksum(requireNonNull(checksum).toString());
    IBResourceBuilderFactory.getAttributes.apply(path).ifPresent(bfa -> {
      this.m.setCreated(bfa.creationTime().toInstant().toString());
      this.m.setLastUpdate(bfa.lastModifiedTime().toInstant().toString());
      this.m.setMostRecentReadTime(bfa.lastAccessTime().toInstant().toString());
      this.m.setSize(bfa.size());
    });

    this.cachedPath = null;
    requireNonNull(type).ifPresent(t -> m.setType(t));
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<RelativeRoot> root, Path path, Checksum checksum) {
    this(vertx, root, path, checksum, empty(), empty());
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<RelativeRoot> root, Path p2, Optional<String> name,
      Optional<String> desc, Checksum checksum, Optional<Properties> addlProps)
  {
    this(vertx, root, p2, checksum, of(IBResourceBuilderFactory.toType.apply(p2)), addlProps);
    this.m.setName(requireNonNull(name).orElse(null));
    this.m.setDescription(requireNonNull(desc).orElse(null));
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<RelativeRoot> root, Path path, Checksum checksum,
      Optional<String> type)
  {
    this(vertx, root, path, checksum, type, empty());
  }

  @Override
  public Optional<RelativeRoot> getRelativeRoot() {
    return ofNullable(this.root);
  }

  @Override
  public Vertx vertx() {
    return this.vertx;
  }

  public void setSource(String source) {
    this.m.setSource(requireNonNull(source));
  }

  @Override
  public Checksum getChecksum() {
    if (this.checksum == null)
      this.checksum = new Checksum(m.getFileChecksum());
    return this.checksum;
  }

  @Override
  public String getType() {
    if (m.getType() == null) {
      getPath().ifPresent(path -> m.setType(IBResourceBuilderFactory.toType.apply(path)));
    }
    return m.getType();
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
  public Optional<URL> getSourceURL() {
    return ofNullable(m.getSource()).map(u -> IBUtils.translateToWorkableArchiveURL(u));
  }

  @Override
  public Optional<Path> getPath() {
    // FIXME Set cached path at creation time?
    return ofNullable(this.cachedPath);
  }

  @Override
  public Optional<String> getSourceName() {
    return ofNullable(m.getName());
  }

  @Override
  public Instant getMostRecentReadTime() {
    return ofNullable(this.m.getMostRecentReadTime()).map(Instant::parse).orElse(null);
  }

  @Override
  public Instant getCreateDate() {
    return ofNullable(this.m.getCreated()).map(Instant::parse).orElse(null);
  }

  @Override
  public Instant getLastUpdateDate() {
    return ofNullable(this.m.getLastUpdate()).map(Instant::parse).orElse(null);
  }

  @Override
  public Optional<String> getName() {
    return ofNullable(this.m.getName());
  }

  @Override
  public Optional<String> getDescription() {
    return ofNullable(this.m.getDescription());
  }

  @Override
  public long size() {
    return this.m.getSize();
  }

  @Override
  public Optional<Properties> getAdditionalProperties() {
    var p = m.getAdditionalProperties();
    return (p.size() == 0) ? empty() : of(p);
  }

  @Override
  public IBResourceModel copyModel() {
    return this.m.clone();
  }

  @Override
  public boolean validate(boolean hard) {
    if (!Files.exists(this.cachedPath))
      return false;
    Checksum s = this.getChecksum();
    IBResourceModel model = this.m;
    if (!s.equals(new Checksum(model.getFileChecksum())))
      return false;
    if (hard) {
      Checksum n = new Checksum(this.cachedPath);
      if (!s.equals(n))
        return false;
      if (!model.getType()
          .equals(IBResourceBuilderFactory.toOptionalType.apply(this.cachedPath).orElse(APPLICATION_OCTET_STREAM)))
        return false;
    }
    return true;
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilder.newInstance(getRelativeRoot().flatMap(RelativeRoot::getPath))
        .addChecksum(new Checksum(m.getFileChecksum()));
  }
}
