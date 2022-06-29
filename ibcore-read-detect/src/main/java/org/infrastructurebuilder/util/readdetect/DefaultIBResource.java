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
package org.infrastructurebuilder.util.readdetect;

import static java.nio.file.Files.createTempFile;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.core.IBUtils.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.System.Logger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import org.apache.tika.Tika;
import org.apache.tika.metadata.TikaCoreProperties;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;

public class DefaultIBResource implements IBResource {
  private final static Logger log = System.getLogger(DefaultIBResource.class.getName());
  private static final long serialVersionUID = 5978749189830232137L;
  private final static Tika tika = new Tika();
  private final IBResourceModel m;

  private Path p;

  DefaultIBResource() {
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

  public final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source,
      final Optional<String> oSource, final String pString) throws IOException {
    DefaultIBResource d = (DefaultIBResource) copyToTempChecksumAndPath(targetDir, source);
    requireNonNull(oSource).ifPresent(o -> {
      d.setSource(o + "!/" + pString);
    });
    return d;
  }

  void setSource(String source) {
    this.m.setSource(requireNonNull(source));
  }

  public final static IBResource copyToTempChecksumAndPath(Path targetDir, final Path source) throws IOException {

    String localType = toType.apply(requireNonNull(source));
    Checksum cSum = new Checksum(source);
    Path newTarget = targetDir.resolve(cSum.asUUID().get().toString());
    cet.withReturningTranslation(() -> copy(source, newTarget));
    return new DefaultIBResource(newTarget, cSum, Optional.of(localType));
  }

  public final static IBResource copyToDeletedOnExitTempChecksumAndPath(Path targetDir, String prefix, String suffix,
      final InputStream source) {
    return cet.withReturningTranslation(() -> {
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
    m.setFilePath(path.toAbsolutePath().toString());
    m.setFileChecksum(checksum.toString());
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
    m.setMostRecentReadTime(new java.util.Date());
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
      this.p = Paths.get(cet.withReturningTranslation(() -> getSourceURL().get().toURI()));
    }
    return ofNullable(this.p).orElseThrow(() -> new IBException("No available path"));
  }

  @Override
  public Optional<String> getSourceName() {
    return ofNullable(m.getName());
  }

  @Override
  public Optional<Date> getMostRecentReadTime() {
    return ofNullable(this.m.getMostRecentReadTime());
  }

}