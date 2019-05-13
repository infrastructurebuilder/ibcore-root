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
package org.infrastructurebuilder.util.artifacts;

import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.IBException;
import org.json.JSONObject;

public interface GAV extends JSONAndChecksumEnabled, Comparable<GAV> {
  String BASIC_PACKAGING = "jar";
  String COMPILE_SCOPE = "compile";

  String FAKE_AF_VALUE = "___@#!#!@#!@#!";
  String GAV_ARTIFACTID = "artifactId";
  String GAV_CLASSIFIER = "classifier";
  String GAV_EXTENSION = "extension";
  String GAV_GROUPID = "groupId";
  String GAV_VERSION = "version";
  String GAV_PATH = "path";
  String PROVIDED_SCOPE = "provided";
  String RUNTIME_SCOPE = "runtime";
  String SNAPSHOT_DESIGNATOR = "-SNAPSHOT";

  static String asPaxUrl(final GAV v) {
    final String cl = !v.getClassifier().isPresent() ? "" : "/" + v.getClassifier().orElse("");
    return String.format("mvn:%s/%s/%s/%s%s", v.getGroupId(), v.getArtifactId(), v.getVersion().orElse(""),
        v.getExtension(), cl);
  }

  @Override
  default Checksum asChecksum() {
    return ChecksumBuilder.newInstance().addString(getGroupId()).addString(getArtifactId()).addString(getClassifier())
        .addString(getVersion()).addString(getExtension()).asChecksum();
  }

  @Override
  default JSONObject asJSON() {
    return getJSONBuilder().asJSON();
  }

  default Optional<String> asMavenDependencyGet() {

    try {
      final String theVersion = getVersion().map(v -> v.toString())
          .orElseThrow(() -> new IllegalArgumentException("No version available"));
      final String theClassifier = getClassifier().map(c -> ":" + c).orElse("");

      final String theType = Optional.ofNullable(getExtension()).map(t -> ":" + t)
          .orElse("".equals(theClassifier) ? "" : ":jar");
      return Optional
          .of(String.format("%s:%s:%s%s%s", getGroupId(), getArtifactId(), theVersion, theType, theClassifier));
    } catch (final IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  default Optional<String> asModelId() {

    try {
      final String theVersion = getVersion().map(v -> v.toString())
          .orElseThrow(() -> new IllegalArgumentException("No version available"));
      return Optional.of(String.format("%s:%s:%s", getGroupId(), getArtifactId(), theVersion));
    } catch (final IllegalArgumentException e) {
      return Optional.empty();
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
        Optional.ofNullable(getExtension()).map(pp -> pp).orElse("jar"), getClassifier().map(c2 -> ":" + c2).orElse(""),
        getVersion().orElseThrow(() -> new IBException("No string version available")));
  }

  String getExtension();

  default Optional<Path> getFile() {
    return Optional.empty();
  }

  String getGroupId();

  default JSONBuilder getJSONBuilder() {
    return new JSONBuilder(Optional.empty())

        .addString(GAV_GROUPID, getGroupId())

        .addString(GAV_ARTIFACTID, getArtifactId())

        .addString(GAV_EXTENSION, getExtension())

        .addString(GAV_CLASSIFIER, getClassifier())

        .addString(GAV_VERSION, getVersion());

  }

  Optional<String> getVersion();

  default boolean isSnapshot() {
    return getVersion().orElse(FAKE_AF_VALUE).endsWith(SNAPSHOT_DESIGNATOR);
  }

  GAV withFile(Path file);

  boolean equalsIgnoreClassifier(GAV other, boolean ignoreClassifier);

}