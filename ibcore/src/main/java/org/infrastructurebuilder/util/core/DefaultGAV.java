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
package org.infrastructurebuilder.util.core;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.util.versions.DefaultGAVBasic;
import org.infrastructurebuilder.util.versions.IBVersionsSupplier;
import org.json.JSONObject;

public class DefaultGAV extends DefaultGAVBasic implements GAV {

  public static DefaultGAV copyFromSpec(final GAV hs) {
    return (DefaultGAV) new DefaultGAV(hs.getGroupId(), hs.getArtifactId(), hs.getClassifier().orElse(null),
        hs.getVersion().orElse(null), hs.getExtension()).withFile(hs.getFile().orElse(null));
  }

  private final Path path;

  private ChecksumBuilder builder;

  public DefaultGAV(final JSONObject json) {
    super(requireNonNull(json).getString(GAV_GROUPID), json.getString(GAV_ARTIFACTID),
        json.optString(GAV_CLASSIFIER, null), json.optString(GAV_VERSION, null),
        json.optString(GAV_EXTENSION, json.optString(GAV_TYPE, json.optString(GAV_PACKAGING, null))));
    path = ofNullable(json.optString(GAV_PATH, null)).map(Paths::get).orElse(null);
    this.builder = ChecksumBuilder.newInstance(empty());
  }

  public DefaultGAV(final JSONObject json, final String classifier) {
    this(json);
    setClassifier(classifier);
  }

  public DefaultGAV(final IBVersionsSupplier from) {
    this(requireNonNull(from).getArtifactDependency().get());
  }

  public DefaultGAV(final String from) {
    super(from);
    this.path = null;
    this.builder = ChecksumBuilder.newInstance(empty());
  }

  public DefaultGAV(final String groupId, final String artifactId, final String version) {
    this(groupId, artifactId, version, BASIC_PACKAGING);
  }

  public DefaultGAV(final String groupId, final String artifactId, final String version, final String extension) {
    this();
    setGroupId(groupId);
    setArtifactId(artifactId);
    setVersion(version);
    setExtension(extension);
  }

  public DefaultGAV(final String groupId, final String artifactId, final String classifier, final String version,
      final String extension)
  {
    this();
    setGroupId(groupId);
    setArtifactId(artifactId);
    setVersion(version);
    setClassifier(classifier);
    setExtension(extension);
  }

  private DefaultGAV() {
    super();
    path = null;
    this.builder = ChecksumBuilder.newInstance(empty());
  }

  private DefaultGAV(final GAV gav, final Path path) {
    setGroupId(gav.getGroupId());
    setArtifactId(gav.getArtifactId());
    setVersion(gav.getVersion().orElse(null));
    setClassifier(gav.getClassifier().orElse(null));
    setExtension(gav.getExtension());
    this.path = path;
    this.builder = ChecksumBuilder.newInstance(empty());
  }

  @Override
  public GAV copy() {
    return new DefaultGAV(asJSON());
  }

//  @Override
//  public boolean equals(final Object obj) {
//    if (this == obj)
//      return true;
//    if (obj == null)
//      return false;
//    if (getClass() != obj.getClass())
//      return false;
//    final DefaultGAV other = (DefaultGAV) obj;
//    return equalsIgnoreClassifier(other, false);
//  }

  @Override
  public int hashCode() {
    return Objects.hash(getArtifactId(), getExtension(), getGroupId(), getVersion(), getClassifier());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DefaultGAV other = (DefaultGAV) obj;
    return Objects.equals(getArtifactId(), other.getArtifactId())
        && Objects.equals(getExtension(), other.getExtension()) && Objects.equals(getGroupId(), other.getGroupId())
        && Objects.equals(getVersion(), other.getVersion()) && Objects.equals(getClassifier(), other.getClassifier());
  }

  public String getResourceType() {
    return this.getClass().getCanonicalName();
  }

  public DefaultGAV setArtifactId(final String artifactId) {
    super.setArtifactId(requireNonNull(artifactId));
    return this;
  }

  public DefaultGAV setClassifier(final String classifier) {
    super.setClassifier(classifier);
    return this;
  }

  public DefaultGAV setExtension(final String extension) {
    super.setExtension(extension);
    return this;
  }

  public DefaultGAV setGroupId(final String groupId) {
    super.setGroupId(requireNonNull(groupId));
    return this;
  }

  public DefaultGAV setVersion(final String stringVersion) {
    super.setVersion(stringVersion);
    return this;
  }

  @Override
  public String toString() {
    return asJSON().toString();
  }

  @Override
  public Optional<Path> getFile() {
    return Optional.ofNullable(this.path);
  }

  @Override
  public GAV withFile(final Path file) {
    return new DefaultGAV(this, file);
  }

  @Override
  public GAV withRelativeRoot(RelativeRoot r) {
    this.builder = ChecksumBuilder.newAlternateInstanceWithRelativeRoot(Optional.ofNullable(r));
    return this;
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return getDefaultChecksumBuilder();
  }
}
