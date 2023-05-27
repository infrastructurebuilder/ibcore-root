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

import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.constants.IBConstants.CREATE_DATE;
import static org.infrastructurebuilder.util.constants.IBConstants.DESCRIPTION;
import static org.infrastructurebuilder.util.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.util.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.util.constants.IBConstants.NAME;
import static org.infrastructurebuilder.util.constants.IBConstants.NO_PATH_SUPPLIED;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.SIZE;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.util.constants.IBConstants.UPDATE_DATE;
import static org.infrastructurebuilder.util.core.ChecksumEnabled.CHECKSUM;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceFactory;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIBResource implements IBResource {
  private static final long serialVersionUID = 5978749189830232137L;
  private final static Logger log = LoggerFactory.getLogger(DefaultIBResource.class.getName());

  public final static Function<String, Path> extracted = (x) -> {
    try {
      Path p1 = Paths.get(x);
      URL u;
      if (Files.isRegularFile(p1)) {
        u = cet.returns(() -> p1.toUri().toURL());
      } else {
        u = cet.returns(() -> new URL(x));
      }
      // I know, right?
      return Paths.get(cet.returns(() -> cet.returns(() -> u.toURI())));
    } catch (Throwable t) {
      log.error( "Error converting to path", t);
      throw t;
    }
  };

  // User IBResourceFactory
  @Deprecated
  public final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source) throws IOException {
    return IBResourceFactory.copyToTempChecksumAndPath(targetDir, source);
  }

  // User IBResourceFactory
  @Deprecated
  public final static IBResource copyToDeletedOnExitTempChecksumAndPath(Path targetDir, String prefix, String suffix,
      final InputStream source) {
    return IBResourceFactory.copyToDeletedOnExitTempChecksumAndPath(targetDir, prefix, suffix, source);
  }

  // User IBResourceFactory
  @Deprecated
  public final static Function<Path, String> toType = IBResourceFactory.toType;

  // User IBResourceFactory
  @Deprecated
  public final static IBResource from(Path p, Checksum c, String type) {
    return IBResourceFactory.from(p, c, type);
  }

  // User IBResourceFactory
  @Deprecated
  public final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source,
      final Optional<String> oSource, final String pString) throws IOException {
    return IBResourceFactory.copyToTempChecksumAndPath(targetDir, source, oSource, pString);
  }

  @Deprecated
  public final static IBResource fromPath(Path path) {
    return new DefaultIBResource(path, new Checksum(path), empty(), empty());
  }

  private final IBResourceModel m;

  private Path p;
  private final Path originalPath;

  public DefaultIBResource(IBResourceModel m) {
    this.m = requireNonNull(m);
    String ps = m.getFilePath();
    Path path = null;
    try {
      path = Paths.get(ps);
      if (path.isAbsolute()) {
        log.debug( "Absolute path {}", path);
      } else {
        log.debug( "Relative path {}", path);
      }
    } catch (Throwable t) {
      log.error( "Path was unavailable from {}", ps);
    } finally {
    }
    this.originalPath = path;
  }

  public DefaultIBResource setCreateDate(Instant cdate) {
    this.m.setCreated(requireNonNull(cdate).toString());
    return this;
  }

  public DefaultIBResource setLastUpdated(Instant udate) {
    this.m.setLastUpdate(requireNonNull(udate).toString());
    return this;
  }

  /**
   * Magic deserializer :)
   *
   * @param j JSONObject produced by IBResource#asJSON
   */
  public DefaultIBResource(JSONObject j) {
    m = new IBResourceModel();
    m.setCreated(requireNonNull(j).optString(CREATE_DATE, null));
    m.setFileChecksum(j.getString(CHECKSUM));
    m.setSize(j.getLong(SIZE));
    m.setType(j.getString(MIME_TYPE));
    m.setFilePath(j.optString(PATH, null));
    m.setLastUpdate(j.optString(UPDATE_DATE, null));
    m.setModelEncoding("UTF-8");
    m.setMostRecentReadTime(j.optString(MOST_RECENT_READ_TIME, null));
    m.setName(j.optString(NAME, null));
    m.setSource(j.optString(SOURCE_URL, null));
    m.setDescription(j.optString(DESCRIPTION, null));
    Optional.ofNullable(j.optJSONObject(IBConstants.ADDITIONAL_PROPERTIES)).ifPresent(jo -> {
      jo.toMap().forEach((k, v) -> {
        m.addAdditionalProperty(k, v.toString());
      });
    });

    this.p = ofNullable(j.optString(IBConstants.PATH, null)).map(extracted)
        .orElseThrow(() -> new IBException(NO_PATH_SUPPLIED));
    this.originalPath = ofNullable(j.optString(IBConstants.ORIGINAL_PATH, null)).map(extracted).orElse(null);
  }

  public DefaultIBResource(Path path, Checksum checksum, Optional<String> type, Optional<Properties> addlProps) {
    this.m = new IBResourceModel();
    this.originalPath = requireNonNull(path);
    m.setFilePath(this.originalPath.toAbsolutePath().toString());
    m.setFileChecksum(requireNonNull(checksum).toString());
    IBResourceFactory.getAttributes.apply(path).ifPresent(bfa -> {
      this.m.setCreated(bfa.creationTime().toInstant().toString());
      this.m.setLastUpdate(bfa.lastModifiedTime().toInstant().toString());
      this.m.setMostRecentReadTime(bfa.lastAccessTime().toInstant().toString());
      this.m.setSize(bfa.size());
    });

    requireNonNull(type).ifPresent(t -> m.setType(t));
  }

  public DefaultIBResource(Path path, Checksum checksum) {
    this(path, checksum, empty(), empty());
  }

  public DefaultIBResource(Path p2, Optional<String> name, Optional<String> desc, Checksum checksum,
      Optional<Properties> addlProps) {
    this(p2, checksum, of(IBResourceFactory.toType.apply(p2)), addlProps);
    this.m.setName(requireNonNull(name).orElse(null));
    this.m.setDescription(requireNonNull(desc).orElse(null));
  }

  public DefaultIBResource(Path path, Checksum checksum, Optional<String> type) {
    this(path, checksum, type, empty());
  }

  public void setSource(String source) {
    this.m.setSource(requireNonNull(source));
  }

  @Override
  public Checksum getChecksum() {
    return new Checksum(m.getFileChecksum());
  }

  @Override
  public String getType() {
    if (m.getType() == null) {
      m.setType(IBResourceFactory.toType.apply(getPath()));
    }
    return m.getType();
  }

  @Override
  public IBResource moveTo(Path target) throws IOException {
    IBUtils.moveAtomic(getPath(), target);
    IBResourceModel m2 = m.clone();
    m2.setFilePath(target.toAbsolutePath().toString());
    return new DefaultIBResource(m2);
  }

  @Override
  public java.io.InputStream get() {
    m.setMostRecentReadTime(now().toString());
    return org.infrastructurebuilder.util.readdetect.IBResource.super.get();
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
  public Path getPath() {
    if (this.p == null && m.getFilePath() != null) {
      this.p = Paths.get(m.getFilePath());
    }
    if (this.p == null && getSourceURL().isPresent()) {
      this.p = Paths.get(cet.returns(() -> getSourceURL().get().toURI()));
    }
    return ofNullable(this.p).orElseThrow(() -> new IBException("No available path"));
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
  public Path getOriginalPath() {
    return originalPath;
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

}