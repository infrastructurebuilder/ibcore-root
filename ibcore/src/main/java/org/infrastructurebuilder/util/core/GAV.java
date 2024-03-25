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

import static java.util.Optional.empty;

import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.util.versions.GAVBasic;
import org.json.JSONObject;

public interface GAV extends GAVBasic, JSONAndChecksumEnabled {
  String COMPILE_SCOPE = "compile";

  String GAV_ARTIFACTID = "artifactId";
  String GAV_CLASSIFIER = "classifier";
  String GAV_EXTENSION = "extension";
  String GAV_GROUPID = "groupId";
  String GAV_VERSION = "version";
  String GAV_PATH = "path";
  String PROVIDED_SCOPE = "provided";
  String RUNTIME_SCOPE = "runtime";
  String GAV_PACKAGING = "packaging";
  String GAV_TYPE = "type";

  @Override
  default JSONObject asJSON() {
    return getJSONBuilder().asJSON();
  }

  default ChecksumBuilder getDefaultChecksumBuilder() {
    return ChecksumBuilder.newInstance() //
        .addString(getGroupId()) //
        .addString(getArtifactId()) //
        .addString(getVersion()) //
        .addString(getClassifier()) //
        .addString(getExtension());
  }

  default Optional<Path> getFile() {
    return empty();
  }

  default JSONBuilder getJSONBuilder() {
    return new JSONBuilder(empty())

        .addString(GAV_GROUPID, getGroupId())

        .addString(GAV_ARTIFACTID, getArtifactId())

        .addString(GAV_EXTENSION, getExtension())

        .addString(GAV_CLASSIFIER, getClassifier())

        .addString(GAV_VERSION, getVersion());

  }

  GAV copy();

  default GAV withFile(Path file) {
    return this;
  }
  
  default GAV withRelativeRoot(RelativeRoot r) {
    return this;
  }

}
