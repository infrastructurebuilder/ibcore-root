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
package org.infrastructurebuilder.util.readdetect;

import static java.util.Objects.hash;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.ADDITIONAL_PROPERTIES;
import static org.infrastructurebuilder.util.constants.IBConstants.CREATE_DATE;
import static org.infrastructurebuilder.util.constants.IBConstants.DESCRIPTION;
import static org.infrastructurebuilder.util.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.util.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH_CHECKSUM;
import static org.infrastructurebuilder.util.constants.IBConstants.SIZE;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_NAME;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.util.constants.IBConstants.UPDATE_DATE;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.ChecksumEnabled;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.infrastructurebuilder.util.core.JSONOutputEnabled;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;

public interface IBResourceBase extends JSONOutputEnabled , ChecksumEnabled {

  /**
   * @return Path to this result. If <code>empty()</code>, this is considered a reference resource.
   * @throws IBResourceException (runtime) if not available
   */
  Optional<Path> getPath();

  /**
   * @return Non-null. Equivalent to calculated Checksum of the contents of the file at getPath()
   */
  Checksum getPathChecksum();

  /**
   * @return Non-null. Equivalent to calculated Checksum of the model, not just the file
   */
  Checksum getChecksum();

  /**
   * @return Non-null MIME type for the file at getPath()
   */
  String getType();

  /**
   * Sub-types may, at their discretion, return a {@link Instant} of the most recent "get()" call. The generated
   * IBResourceModel doesn't because it's really a persistence mechanism and that value isn't relevant.
   *
   *
   * @return most recent read time or null
   */
  Instant getMostRecentReadTime();

  /**
   * Nullable (but probably not) create date
   *
   * @return create date or null
   */
  Instant getCreateDate();

  /**
   * @return last file update or null
   */
  Instant getLastUpdateDate();

  default Optional<Instant> getOptionalCreateDate() {
    return ofNullable(getCreateDate());
  }

  default Optional<Instant> getOptionalMostRecentReadTime() {
    return ofNullable(getMostRecentReadTime());
  }

  default Optional<Instant> getOptionalLastUpdateDate() {
    return ofNullable(getLastUpdateDate());
  }


  Optional<URL> getSourceURL();

  Optional<String> getSourceName();

  long size();

  Optional<String> getName();

  Optional<String> getDescription();

  default int defaultHashCode() {
    return hash(getChecksum(), getPath(), getSourceName(), getSourceURL(), getType());
  }

  default boolean defaultEquals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if ((obj instanceof IBResource )) {
      IBResource other = (IBResource)obj;
      return Objects.equals(getChecksum(), other.getChecksum()) // checksum
          && Objects.equals(getPath(), other.getPath()) // path
          && Objects.equals(getSourceName(), other.getSourceName()) // source
          && Objects.equals(getSourceURL(), other.getSourceURL()) // sourceURL
          && Objects.equals(getType(), other.getType()); // Type
    }
    return false;
  }

  default String defaultToString() {
    StringJoiner sj = new StringJoiner("|") //
        .add(getPathChecksum().asUUID().get().toString()) // Checksum
        .add(getType()) // type
        .add(getPath().toString()); // Path
    getSourceURL().ifPresent(u -> sj.add(u.toExternalForm()));
    getSourceName().ifPresent(sj::add);
    return sj.toString();
  }

  default JSONObject asJSON() {
    return new JSONBuilder(empty())

        .addChecksum(PATH_CHECKSUM, getPathChecksum())

        .addChecksum(CHECKSUM, getChecksum())

        .addInstant(CREATE_DATE, getCreateDate())

        .addInstant(UPDATE_DATE, getLastUpdateDate())

        .addInstant(MOST_RECENT_READ_TIME, ofNullable(getMostRecentReadTime()))

        .addString(SOURCE_NAME, getSourceName())

        .addString(SOURCE_URL, getSourceURL().map(java.net.URL::toExternalForm))

        .addString(MIME_TYPE, getType())

        .addPath(PATH, getPath())

//        .addString(ORIGINAL_PATH, getOriginalPath().toString())

        .addLong(SIZE, size())

        .addString(DESCRIPTION, getDescription())

        .addProperties(ADDITIONAL_PROPERTIES, getAdditionalProperties())

        .asJSON();
  }

//  Path getOriginalPath();

  /**
   * This method should not return an empty Properties object. If there are no additional properties then the return
   * should be Optional.empty()
   *
   * @return empty or a Properties with size() > 0
   */
  default Optional<Properties> getAdditionalProperties() {
    return empty();
  }

  default Optional<BasicFileAttributes> getBasicFileAttributes() {
    return getPath().flatMap(path -> IBResourceBuilderFactory.getAttributes.apply(path));
  }

  /**
   * @return true if this file was cached, which means the path is based on a RelativeRoot
   */
  default boolean isCached() {
    return false;
  }

  default Optional<RelativeRoot> getRelativeRoot() {
    return Optional.empty();
  }

  boolean validate(boolean hard);

  default boolean validate() {
    return validate(true);
  }

  IBResourceModel copyModel();

}