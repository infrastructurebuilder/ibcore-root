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

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.readAttributes;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.constants.IBConstants.CREATE_DATE;
import static org.infrastructurebuilder.util.constants.IBConstants.DESCRIPTION;
import static org.infrastructurebuilder.util.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.util.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.util.constants.IBConstants.ORIGINAL_PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.PATH;
import static org.infrastructurebuilder.util.constants.IBConstants.SIZE;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_NAME;
import static org.infrastructurebuilder.util.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.util.constants.IBConstants.UPDATE_DATE;
import static org.infrastructurebuilder.util.core.ChecksumEnabled.CHECKSUM;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.infrastructurebuilder.util.core.JSONOutputEnabled;
import org.json.JSONObject;

public interface IBResource extends Supplier<InputStream>, JSONOutputEnabled {
  public final static Function<Path, Optional<BasicFileAttributes>> getAttributes = (i) -> {
    Optional<BasicFileAttributes> retVal = empty();
    try {
      retVal = of(readAttributes(requireNonNull(i), BasicFileAttributes.class));
    } catch (IOException e) {
      // Do nothing
    }
    return retVal;
  };

  public final static BiFunction<Path, Path, Optional<IBResource>> toIBResource = (targetDir, source) -> {
    try {
      return Optional.of(IBResourceFactory.copyToTempChecksumAndPath(targetDir, source));
    } catch (IOException e) {
      // TODO ??
    }
    return empty();
  };

  public final static BiFunction<Path, Optional<String>, String> nameMapper = (p, on) -> {
    var str = requireNonNull(p).toString();
    return requireNonNull(on).orElse(str.substring(0, str.lastIndexOf('.')));
  };



  /**
   * @return Non-null Path to this result
   * @throws IBException (runtime) if not available
   */
  Path getPath();

  /**
   * @return Equivalent to calculated Checksum of the contents of the file at
   *         getPath()
   */
  Checksum getChecksum();

  /**
   * @return Non-null MIME type for the file at getPath()
   */
  String getType();

  /**
   * Relocate underlying path to new path. The target path is meant to be a normal
   * filesystem, not a ZipFileSystem.
   *
   * @param target
   * @return
   * @throws IOException
   */
  IBResource moveTo(Path target) throws IOException;

  /**
   * Sub-types may, at their discretion, return a {@link Date} of the most recent
   * "get()" call. The generated IBResourceModel doesn't because it's really a
   * persistence mechanism and that value isn't relevant.
   *
   * @return
   */
  Optional<Instant> getMostRecentReadTime();
  Optional<Instant> getCreateDate();
  Optional<Instant> getLateUpdateDate();

  default InputStream get() {
    List<OpenOption> o = new ArrayList<>();
    o.add(READ);
    if (getPath().getClass().getCanonicalName().contains("Zip")) {

    } else {
      o.add(NOFOLLOW_LINKS);
    }
    OpenOption[] readOptions = o.toArray(new java.nio.file.OpenOption[o.size()]);

    return cet.returns(() -> newInputStream(getPath(), readOptions));
  }

  Optional<URL> getSourceURL();

  Optional<String> getSourceName();

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
    if ((obj instanceof IBResource other)) {
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
        .add(getChecksum().asUUID().get().toString()) // Checksum
        .add(getType()) // type
        .add(getPath().toString()); // Path
    getSourceURL().ifPresent(u -> sj.add(u.toExternalForm()));
    getSourceName().ifPresent(sj::add);
    return sj.toString();
  }

  default Long size() {
    return cet.returns(() -> Files.size(getPath()));

  }

  default Optional<String> getName() {
    return empty();
  }

  default Optional<String> getDescription() {
    return empty();
  }

  @Override
  default JSONObject asJSON() {
    return new JSONBuilder(empty())
        .addChecksum(CHECKSUM, getChecksum())
        .addInstant(CREATE_DATE, getCreateDate())
        .addInstant(UPDATE_DATE, getLateUpdateDate())
        .addInstant(MOST_RECENT_READ_TIME, getMostRecentReadTime())
        .addString(SOURCE_NAME, getSourceName())
        .addString(SOURCE_URL, getSourceURL().map(java.net.URL::toExternalForm))
        .addString(MIME_TYPE, getType())
        .addPath(PATH, getPath())
        .addLong(SIZE,size())
        .addString(DESCRIPTION, getDescription())
        .addString(ORIGINAL_PATH, getOriginalPath().toString())
        .asJSON();
  }

  Path getOriginalPath();

  default Optional<BasicFileAttributes> getBasicFileAttributes() {
    return getAttributes.apply(getPath());
  }


}