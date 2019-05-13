/**
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
package org.infrastructurebuilder.util.artifacts.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.json.JSONObject;

public class DefaultGAV implements GAV, Comparable<GAV> {

  public static DefaultGAV copyFromSpec(final GAV hs) {
    return (DefaultGAV) new DefaultGAV(hs.getGroupId(), hs.getArtifactId(), hs.getClassifier().orElse(null),
        hs.getVersion().orElse(null), hs.getExtension()).withFile(hs.getFile().orElse(null));
  }

  private String artifactId;

  private Optional<String> classifier = Optional.empty();

  private String extension;
  private String groupId;

  private String stringVersion = null;

  private final Optional<Path> path;

  public DefaultGAV(final JSONObject json) {
    setGroupId(Objects.requireNonNull(json).getString(GAV_GROUPID));
    setArtifactId(json.getString(GAV_ARTIFACTID));
    setVersion(json.optString(GAV_VERSION, null));
    setClassifier(json.optString(GAV_CLASSIFIER, null));
    setExtension(json.optString(GAV_EXTENSION, json.optString("type", json.optString("packaging", null))));
    path = Optional.ofNullable(json.optString(GAV_PATH, null)).map(Paths::get);
  }

  public DefaultGAV(final JSONObject json, final String classifier) {
    this(json);
    setClassifier(classifier);
  }

  public DefaultGAV(final String from) {
    this();
    final String[] l = Arrays.copyOf(from.split(":"), 5);

    for (int i = 0; i < 5; ++i) {
      switch (i) {
      case 0:
        setGroupId(l[i]);
        break;
      case 1:
        setArtifactId(l[i]);
        break;
      case 2:
        setVersion(l[i]);
        break;
      case 3:
        setExtension(l[i]);
        break;
      case 4:
        setClassifier(l[i]);
        break;
      }
    }
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
      final String extension) {
    this();
    setGroupId(groupId);
    setArtifactId(artifactId);
    setVersion(version);
    setClassifier(classifier);
    setExtension(extension);
  }

  private DefaultGAV() {
    super();
    path = Optional.empty();
  }

  private DefaultGAV(final GAV gav, final Path path) {
    setGroupId(gav.getGroupId());
    setArtifactId(gav.getArtifactId());
    setVersion(gav.getVersion().orElse(null));
    setClassifier(gav.getClassifier().orElse(null));
    setExtension(gav.getExtension());
    this.path = Optional.ofNullable(path);
  }

  @Override
  public int compareTo(final GAV o) {
    if (o == null)
      throw new NullPointerException("compareTo in DefaultGAV was passed a null");
    if (equals(o))
      return 0;
    int cmp = getGroupId().compareTo(o.getGroupId());
    if (cmp == 0) {
      cmp = getArtifactId().compareTo(o.getArtifactId());
      if (cmp == 0) {
        cmp = IBException.cet.withReturningTranslation(() -> {
          return compareVersion(o);
        });
        if (cmp == 0) {
          cmp = getExtension().compareTo(o.getExtension());
        }

      }
    }
    return cmp;
  }

  public int compareVersion(final GAV otherVersion) {
    final String v = otherVersion.getVersion().orElse(null);
    final String q = getVersion().orElse(null);
    if (q == null && v == null)
      return 0;
    if (q == null)
      return -1;
    return q.compareTo(v);

  }

  @Override
  public GAV copy() {
    return new DefaultGAV(asJSON());
  }

  @Override
  public boolean equalsIgnoreClassifier(final GAV other, boolean ignoreClassifier) {
    if (!extension.equals(Objects.requireNonNull(other).getExtension()))
      return false;
    if (!groupId.equals(other.getGroupId()))
      return false;
    if (!artifactId.equals(other.getArtifactId()))
      return false;
    if (!ignoreClassifier && !getClassifier().equals(other.getClassifier()))
      return false;
    if (!getVersion().equals(other.getVersion()))
        return false;
    return true;

  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final DefaultGAV other = (DefaultGAV) obj;
    return equalsIgnoreClassifier(other, false);
  }

  @Override
  public String getArtifactId() {
    return artifactId;
  }

  @Override
  public Optional<String> getClassifier() {
    return classifier;
  }

  @Override
  public String getExtension() {
    return extension;
  }

  @Override
  public String getGroupId() {
    return groupId;
  }

  public String getResourceType() {
    return this.getClass().getCanonicalName();
  }

  @Override
  public Optional<String> getVersion() {
    return Optional.ofNullable(stringVersion);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + artifactId.hashCode();
    result = prime * result + classifier.hashCode();
    result = prime * result + groupId.hashCode();
    result = prime * result + extension.hashCode();
    result = prime * result + getVersion().map(x -> x.hashCode()).orElse(0);
    return result;
  }

  public DefaultGAV setArtifactId(final String artifactId) {
    this.artifactId = Objects.requireNonNull(artifactId);
    return this;
  }

  public DefaultGAV setClassifier(final String classifier) {
    if (classifier != null && "".equals(classifier.trim())) {
      this.classifier = Optional.empty();
    } else {
      this.classifier = Optional.ofNullable(classifier);
    }
    return this;
  }

  public DefaultGAV setExtension(final String extension) {
    this.extension = Optional.ofNullable(extension).orElse(BASIC_PACKAGING);
    return this;
  }

  public DefaultGAV setGroupId(final String groupId) {
    this.groupId = Objects.requireNonNull(groupId);
    return this;
  }

  public DefaultGAV setVersion(final String stringVersion) {
    this.stringVersion = stringVersion;
    return this;
  }

  @Override
  public String toString() {
    return asJSON().toString();
  }

  @Override
  public Optional<Path> getFile() {
    return this.path;
  }

  @Override
  public GAV withFile(final Path file) {
    return new DefaultGAV(this, file);
  }
}