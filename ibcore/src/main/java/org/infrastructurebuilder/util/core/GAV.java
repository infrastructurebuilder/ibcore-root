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
package org.infrastructurebuilder.util.core;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.infrastructurebuilder.exceptions.IBException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface GAV extends JSONAndChecksumEnabled, Comparable<GAV> {
  String BASIC_PACKAGING = "jar";
  String COMPILE_SCOPE   = "compile";

  String GAV_ARTIFACTID      = "artifactId";
  String GAV_CLASSIFIER      = "classifier";
  String GAV_EXTENSION       = "extension";
  String GAV_GROUPID         = "groupId";
  String GAV_VERSION         = "version";
  String GAV_PATH            = "path";
  String PROVIDED_SCOPE      = "provided";
  String RUNTIME_SCOPE       = "runtime";
  String SNAPSHOT_DESIGNATOR = "-SNAPSHOT";
  String GAV_PACKAGING       = "packaging";
  String GAV_TYPE            = "type";

  static String asPaxUrl(final GAV v) {
    final String cl = !v.getClassifier().isPresent() ? "" : "/" + v.getClassifier().orElse("");
    return String.format("mvn:%s/%s/%s/%s%s", v.getGroupId(), v.getArtifactId(), v.getVersion().orElse(""),
        v.getExtension(), cl);
  }

  @Override
  default JSONObject asJSON() {
    return getJSONBuilder().asJSON();
  }

  default Document asDom() {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = IBException.cet.withReturningTranslation(() -> dbf.newDocumentBuilder());
    Document doc = builder.newDocument();
    Element root = doc.createElement("gav");
    root.setAttribute(GAV_GROUPID, getGroupId());
    root.setAttribute(GAV_ARTIFACTID, getArtifactId());
    getVersion().ifPresent(v -> root.setAttribute(GAV_VERSION, v));
    getClassifier().ifPresent(c -> root.setAttribute(GAV_CLASSIFIER, c));
    root.setAttribute(GAV_EXTENSION, getExtension());
    getFile().ifPresent(f -> root.setAttribute(GAV_PATH, f.toString()));
    return doc;
  }

  default Optional<String> asMavenDependencyGet() {

    try {
      final String theVersion = getVersion().map(v -> v.toString())
          .orElseThrow(() -> new IllegalArgumentException("No version available"));
      final String theClassifier = getClassifier().map(c -> ":" + c).orElse("");

      final String theType = ofNullable(getExtension()).map(t -> ":" + t)
          .orElse("".equals(theClassifier) ? "" : ":jar");
      return Optional
          .of(String.format("%s:%s:%s%s%s", getGroupId(), getArtifactId(), theVersion, theType, theClassifier));
    } catch (final IllegalArgumentException e) {
      return empty();
    }
  }

  default Optional<String> asModelId() {

    try {
      final String theVersion = getVersion().map(v -> v.toString())
          .orElseThrow(() -> new IllegalArgumentException("No version available"));
      return Optional.of(String.format("%s:%s:%s", getGroupId(), getArtifactId(), theVersion));
    } catch (final IllegalArgumentException e) {
      return empty();
    }

  }

  default String asPaxUrl() {
    return asPaxUrl(this);
  }

  default String asRange() {
    return "[" + getVersion().orElse("0.0.0,99999.99999.99999") + "]";
  }

  GAV copy();

  String getArtifactId();

  Optional<String> getClassifier();

  default String getDefaultSignaturePath() {
    return String.format("%s:%s:%s%s:%s", getGroupId(), getArtifactId(),
        ofNullable(getExtension()).map(pp -> pp).orElse("jar"), getClassifier().map(c2 -> ":" + c2).orElse(""),
        getVersion().orElseThrow(() -> new IBException("No string version available")));
  }

  String getExtension();

  default Optional<Path> getFile() {
    return empty();
  }

  String getGroupId();

  default JSONBuilder getJSONBuilder() {
    return new JSONBuilder(empty())

        .addString(GAV_GROUPID, getGroupId())

        .addString(GAV_ARTIFACTID, getArtifactId())

        .addString(GAV_EXTENSION, getExtension())

        .addString(GAV_CLASSIFIER, getClassifier())

        .addString(GAV_VERSION, getVersion());

  }

  Optional<String> getVersion();

  /**
   * Get the "API version" for semantic versions.
   *
   * Might blow up if you're not a semantic version
   *
   * @return String with Major.Minor verions
   */
  default Optional<String> getAPIVersion() {
    return getVersion().map(s -> {
      String[] splits = s.split("\\.");
      return splits[0] + "." + splits[1];
    });

  }

  default boolean isSnapshot() {
    return getVersion().map(v -> v.endsWith(SNAPSHOT_DESIGNATOR)).orElse(false);
  }

  default GAV withFile(Path file) {
    return this;
  }

  default boolean equalsIgnoreClassifier(final GAV other, boolean ignoreClassifier) {
    if (!this.getExtension().equals(requireNonNull(other).getExtension()))
      return false;
    if (!this.getGroupId().equals(other.getGroupId()))
      return false;
    if (!this.getArtifactId().equals(other.getArtifactId()))
      return false;
    if (!ignoreClassifier && !Objects.equals(getClassifier(), other.getClassifier()))
      return false;
    if (!Objects.equals(getVersion(), other.getVersion()))
      return false;
    return true;

  }

  @Override
  default int compareTo(final GAV o) {
    if (o == null)
      throw new NullPointerException("compareTo in DefaultGAV was passed a null");
    if (equals(o))
      return 0;
    int cmp = getGroupId().compareTo(o.getGroupId());
    if (cmp == 0) {
      cmp = getArtifactId().compareTo(o.getArtifactId());
      if (cmp == 0) {
        cmp = cet.withReturningTranslation(() -> {
          return compareVersion(o);
        });
        if (cmp == 0) {
          cmp = getExtension().compareTo(o.getExtension());
        }

      }
    }
    return cmp;
  }

  default int compareVersion(final GAV otherVersion) {
    final String v = otherVersion.getVersion().orElse(null);
    final String q = getVersion().orElse(null);
    if (q == null && v == null)
      return 0;
    if (q == null)
      return -1;
    return q.compareTo(v);

  }

}