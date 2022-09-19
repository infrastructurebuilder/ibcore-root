/*
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
 */
package org.infrastructurebuilder.util.readdetect.impl;

import static java.nio.file.Files.createTempFile;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.constants.IBConstants.CREATE_DATE;
import static org.infrastructurebuilder.util.constants.IBConstants.DESCRIPTION;
import static org.infrastructurebuilder.util.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.util.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.util.constants.IBConstants.NAME;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.util.constants.IBConstants.UPDATE_DATE;
import static org.infrastructurebuilder.util.core.ChecksumEnabled.CHECKSUM;
import static org.infrastructurebuilder.util.core.IBUtils.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import org.apache.tika.Tika;
import org.apache.tika.metadata.TikaCoreProperties;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;

public class DefaultIBResource implements IBResource {
  private static final long serialVersionUID = 5978749189830232137L;
  private final static Logger log = System.getLogger(DefaultIBResource.class.getName());
  private final static Tika tika = new Tika();
  private final IBResourceModel m;

  private Path p;

  public DefaultIBResource() {
    this.m = new IBResourceModel();
  }

//  public DefaultIBResource(
//      // Path
//      java.nio.file.Path path
//      // Checksum
//      , org.infrastructurebuilder.util.core.Checksum checksum
//      // Type
//      , String type
//      // Source
//      , java.util.Optional<java.net.URL> sourceURL
//      // Name
//      , java.util.Optional<String> name, java.util.Optional<java.util.Date> readDate) {
//    this.m = new IBResourceModel();
//    m.setFilePath(requireNonNull(path).toAbsolutePath().toString());
//    m.setFileChecksum(requireNonNull(checksum).toString());
//    m.setType(type);
//    m.setSource(requireNonNull(sourceURL).map(java.net.URL::toExternalForm).orElse(null));
//    m.setName(requireNonNull(name).orElse(null));
//    m.setMostRecentReadTime(readDate.orElse(null));
//  }

//  DefaultIBResource(String filePath, String checksum, String type, String source, String name, Date mostRecentRead) {
//    this.m = new IBResourceModel();
//    m.setFilePath(filePath);
//    m.setFileChecksum(checksum);
//    m.setType(type);
//    m.setSource(source);
//    m.setName(name);
//    m.setMostRecentReadTime(mostRecentRead);
//  }
//
  public DefaultIBResource(IBResourceModel m) {
    this.m = requireNonNull(m);
  }

  /**
   * Magic deserializer :)
   *
   * @param j JSONObject produced by IBResource#asJSON
   */
  public DefaultIBResource(JSONObject j) {

    // FIXME Some of these not being present should produce a runtime failure
    m = new IBResourceModel();
    m.setCreated(j.optString(CREATE_DATE, null));
    m.setFileChecksum(j.optString(CHECKSUM, null));
    m.setFilePath(j.optString(PATH, null));
    m.setLastUpdate(j.optString(UPDATE_DATE, null));
    m.setModelEncoding("UTF-8");
    m.setMostRecentReadTime(j.optString(MOST_RECENT_READ_TIME, null));
    m.setName(j.optString(NAME, null));
    m.setSource(j.optString(SOURCE_URL, null));
    m.setType(j.optString(MIME_TYPE, null));
    m.setDescription(j.optString(DESCRIPTION, null));

    String x = ofNullable(requireNonNull(j).optString(IBConstants.PATH, null))
        .orElseThrow(() -> new RuntimeException("No path supplied"));
    try {
      Path p = Paths.get(cet.returns(() -> cet.returns(() -> new URL(x).toURI())));
      this.p = p;
    } catch (Throwable t) {
      log.log(Level.ERROR, "Error converting to path", t);
      throw t;
    }

  }

  public final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source,
      final Optional<String> oSource, final String pString) throws IOException {
    DefaultIBResource d = (DefaultIBResource) copyToTempChecksumAndPath(targetDir, source);
    requireNonNull(oSource).ifPresent(o -> {
      d.setSource(o + "!/" + pString);
    });
    return d;
  }

  public void setSource(String source) {
    this.m.setSource(requireNonNull(source));
  }

  public final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source) throws IOException {

    String localType = toType.apply(requireNonNull(source));
    Checksum cSum = new Checksum(source);
    Path newTarget = targetDir.resolve(cSum.asUUID().get().toString());
    cet.returns(() -> copy(source, newTarget));
    return new DefaultIBResource(newTarget, cSum, Optional.of(localType));
  }

  public final static IBResource copyToDeletedOnExitTempChecksumAndPath(Path targetDir, String prefix, String suffix,
      final InputStream source) {
    return cet.returns(() -> {
      Path target = createTempFile(requireNonNull(targetDir), prefix, suffix);
      try (OutputStream outs = Files.newOutputStream(target)) {
        copy(source, outs);
        source.close();
      }
      return copyToTempChecksumAndPath(targetDir, target);
    });
  }

  public final static Function<Path, String> toType = (path) -> {
    synchronized (tika) {
      log.log(Logger.Level.DEBUG, "Detecting path " + path);
      org.apache.tika.metadata.Metadata md = new org.apache.tika.metadata.Metadata();
      md.set(TikaCoreProperties.RESOURCE_NAME_KEY, path.toAbsolutePath().toString());
      try (Reader p = tika.parse(path, md)) {
        log.log(Logger.Level.DEBUG, " Metadata is " + md);
        return tika.detect(path);
      } catch (IOException e) {
        throw new IBException("Failed during attempt to get tika type", e);
      }
    }
  };

  public final static IBResource from(Path p, Checksum c, String type) {
    IBResourceModel m = new IBResourceModel();
    m.setFilePath(requireNonNull(p).toAbsolutePath().toString());
    m.setFileChecksum(c.toString());
    m.setType(type);
    return new DefaultIBResource(p, c, Optional.of(type));
  }

  public DefaultIBResource(Path path, Checksum checksum, Optional<String> type) {
    this.m = new IBResourceModel();
    m.setFilePath(requireNonNull(path).toAbsolutePath().toString());
    m.setFileChecksum(requireNonNull(checksum).toString());
    IBResource.getAttributes.apply(path).ifPresent(bfa -> {
      this.m.setCreated(bfa.creationTime().toInstant().toString());
      this.m.setLastUpdate(bfa.lastModifiedTime().toInstant().toString());
      this.m.setMostRecentReadTime(bfa.lastAccessTime().toInstant().toString());
    });

    requireNonNull(type).ifPresent(t -> m.setType(t));
  }

  public DefaultIBResource(Path path, Checksum checksum) {
    this(path, checksum, Optional.empty());
  }

  public final static IBResource fromPath(Path path) {
    return new DefaultIBResource(path, new Checksum(path), empty());
  }

  @Override
  public Checksum getChecksum() {
    return new Checksum(m.getFileChecksum());
  }

  @Override
  public String getType() {
    if (m.getType() == null) {
      m.setType(toType.apply(getPath()));
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
    m.setMostRecentReadTime(Instant.now().toString());
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
      this.p = java.nio.file.Paths.get(m.getFilePath());
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
  public Optional<Instant> getMostRecentReadTime() {
    return ofNullable(this.m.getMostRecentReadTime()).map(Instant::parse);
  }

  @Override
  public Optional<Instant> getCreateDate() {
    return ofNullable(this.m.getCreated()).map(Instant::parse);
  }

  @Override
  public Optional<Instant> getLateUpdateDate() {
    return ofNullable(this.m.getLastUpdate()).map(Instant::parse);
  }

  @Override
  public Optional<String> getName() {
    return ofNullable(this.m.getName());
  }

  @Override
  public Optional<String> getDescription() {
    return ofNullable(this.m.getDescription());
  }
}