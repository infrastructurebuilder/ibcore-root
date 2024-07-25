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
import static org.infrastructurebuilder.util.constants.IBConstants.NO_PATH_SUPPLIED;
import static org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory.extracted;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.objectmapper.jackson.ObjectMapperUtils;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.ChecksumBuilder;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.OptStream;
import org.infrastructurebuilder.util.core.PathRef;
import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.vertx.base.VertxIBResource;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vertx.core.Vertx;

public class VertxDefaultIBResource implements VertxIBResource {
  private final static Logger log = LoggerFactory.getLogger(VertxDefaultIBResource.class.getName());

  private final IBResourceModel m;

  private final Path cachedPath;
  private Checksum checksum;

  private final PathRef root;

  private final Vertx vertx;

  public VertxDefaultIBResource(Vertx vertx, Optional<PathRef> root, IBResourceModel m, Path sourcePath) {
    this.vertx = Objects.requireNonNull(vertx);
    this.root = Objects.requireNonNull(root).orElse(null);
    this.cachedPath = sourcePath;
    this.m = requireNonNull(m);
    m.getPath().ifPresent(ps -> {
      Path path = null;
      try {
        path = Paths.get(ps);
        log.debug("{} path {}", path.isAbsolute() ? "Absolute" : "Relative", path);
      } catch (Throwable t) {
        log.error("Path was unavailable from {}", ps);
      } finally {
      }
    });
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<PathRef> root, IBResourceModel m) {
    this(vertx, root, m, null);
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<PathRef> root, JSONObject j) {
    this.vertx = Objects.requireNonNull(vertx);
    this.root = Objects.requireNonNull(root).orElse(null);
    m = IBResourceBuilder.modelFromJSON.apply(j).get();

    this.cachedPath = ofNullable(j.optString(IBConstants.PATH, null)).map(extracted)
        .orElseThrow(() -> new IBException(NO_PATH_SUPPLIED));
//    this.originalPath = ofNullable(j.optString(IBConstants.ORIGINAL_PATH, null)).map(extracted).orElse(null);
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<PathRef> root, Path path, Checksum checksum,
      Optional<String> type, Optional<Properties> addlProps)
  {
    this.vertx = Objects.requireNonNull(vertx);
    this.root = Objects.requireNonNull(root).orElse(null);
    this.m = new IBResourceModel();
//    this.originalPath = requireNonNull(path);
//    m.setFilePath(this.originalPath.toAbsolutePath().toString());
    m.setPath(requireNonNull(path).toAbsolutePath().toString());
    m.setStreamChecksum(requireNonNull(checksum).toString());
    IBUtils.getAttributes.apply(path).ifPresent(bfa -> {
      this.m.setCreated(bfa.creationTime().toInstant());
      this.m.setLastUpdate(bfa.lastModifiedTime().toInstant());
      this.m.setMostRecentReadTime(bfa.lastAccessTime().toInstant());
      this.m.setStreamSize(bfa.size());
    });

    this.cachedPath = null;
    requireNonNull(type).ifPresent(t -> m.setStreamType(t));
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<PathRef> root, Path path, Checksum checksum) {
    this(vertx, root, path, checksum, empty(), empty());
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<PathRef> root, Path p2, Optional<String> name,
      Optional<String> desc, Checksum checksum, Optional<Properties> addlProps)
  {
    this(vertx, root, p2, checksum, of(IBResourceBuilderFactory.toType.apply(p2)), addlProps);
    this.m.setStreamName(requireNonNull(name).orElse(null));
    this.m.setDescription(requireNonNull(desc).orElse(null));
  }

  public VertxDefaultIBResource(Vertx vertx, Optional<PathRef> root, Path path, Checksum checksum,
      Optional<String> type)
  {
    this(vertx, root, path, checksum, type, empty());
  }

  @Override
  public Optional<PathRef> getRelativeRoot() {
    return ofNullable(this.root);
  }

  @Override
  public Vertx vertx() {
    return this.vertx;
  }

  public void setSource(String source) {
    this.m.setStreamSource(requireNonNull(source));
  }

  @Override
  public Checksum getTChecksum() {
    return new Checksum(m.getStreamChecksum());
  }

  @Override
  public Checksum getChecksum() {
    if (this.checksum == null)
      this.checksum = new Checksum(m.getStreamChecksum());
    return this.checksum;
  }

  @Override
  public String getType() {
    if (m.getStreamType() == null) {
      getPath().ifPresent(path -> m.setStreamType(IBResourceBuilderFactory.toType.apply(path)));
    }
    return m.getStreamType();
  }

  @Override
  public int hashCode() {
    return IBResource.defaultHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return IBResource.defaultEquals(this, obj);
  }

  @Override
  public String toString() {
    return IBResource.defaultToString(this);
  }

  @Override
  public Optional<URL> getSourceURL() {
    return ofNullable(m.getStreamSource()).map(u -> IBUtils.translateToWorkableArchiveURL(u));
  }

  @Override
  public Optional<Path> getPath() {
    // FIXME Set cached path at creation time?
    return ofNullable(this.cachedPath);
  }

  @Override
  public String getModelVersion() {
    return m.getModelVersion();
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
  public Optional<Long> size() {
    return Optional.of( this.m.getStreamSize());
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

  @Override
  public IBResourceModel copyModel() {
    return new IBResourceModel(this.m);
  }

  @Override
  public boolean validate(boolean hard) {
    if (!Files.exists(this.cachedPath))
      return false;
    Checksum s = this.getChecksum();
    IBResourceModel model = this.m;
    if (!s.equals(new Checksum(model.getStreamChecksum())))
      return false;
    if (hard) {
      Checksum n = new Checksum(this.cachedPath);
      if (!s.equals(n))
        return false;
      if (!model.getStreamType()
          .equals(IBResourceBuilderFactory.toOptionalType.apply(this.cachedPath).orElse(APPLICATION_OCTET_STREAM)))
        return false;
    }
    return true;
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilder.newAlternateInstanceWithRelativeRoot(this.getRelativeRoot())
        .addChecksum(new Checksum(m.getStreamChecksum()));
  }
  
  @Override
  public OptStream get() {
    // TODO Auto-generated method stub
    return new OptStream(); // FIXME
  }
}
