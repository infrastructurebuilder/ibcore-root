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
package org.infrastructurebuilder.util.vertx.base;

import static org.infrastructurebuilder.util.readdetect.IBResource.*;
import static org.infrastructurebuilder.util.readdetect.IBResourceFactory.getAttributes;
import static io.vertx.core.Future.succeededFuture;
import static java.nio.file.Files.readAttributes;
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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.readdetect.IBResource;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public interface IBResourceVertx extends Supplier<Future<Buffer>>, JsonOutputEnabled {

  default Vertx vertx() {
    return Vertx.vertx();
  }

  /**
   * @return Non-null Path to this result
   * @throws IBException (runtime) if not available
   */
  Path getPath();

  /**
   * @return Equivalent to calculated Checksum of the contents of the file at getPath()
   */
  Future<Checksum> getChecksum();

  /**
   * @return Non-null MIME type for the file at getPath()
   */
  Future<String> getType();

  /**
   * Relocate underlying path to new path. The target path is meant to be a normal filesystem, not a ZipFileSystem.
   *
   * @param target
   * @return
   * @throws IOException
   */
  Future<IBResourceVertx> moveTo(Path target) throws IOException;

  /**
   * Sub-types may, at their discretion, return a {@link Date} of the most recent "get()" call. The generated
   * IBResourceModel doesn't because it's really a persistence mechanism and that value isn't relevant.
   *
   * @return
   */
  Optional<Instant> getMostRecentReadTime();

  Optional<Instant> getCreateDate();

  Optional<Instant> getLateUpdateDate();

  default Future<Buffer> get() {
    return vertx().fileSystem().readFile(getPath().toString());
  }

  Optional<URL> getSourceURL();

  Optional<String> getSourceName();

  default Future<String> defaultToString() {
    return CompositeFuture.all(getChecksum(), getType()).compose(f -> {

      StringJoiner sj = new StringJoiner("|") //
          .add(getPath().toString()); // Path
      getSourceURL().ifPresent(u -> sj.add(u.toExternalForm()));
      getSourceName().ifPresent(sj::add);
      Checksum fc = f.resultAt(0);
      String ft = f.resultAt(1);
      return succeededFuture(sj.add(fc.asUUID().get().toString()).add(ft).toString());
    });
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
  default Future<JsonObject> toFutureJson() {
    CompositeFuture cf = CompositeFuture.all(getChecksum(), getType());
    return cf.compose(f -> {
      JsonBuilder jb = new JsonBuilder(empty()).addInstant(CREATE_DATE, getCreateDate())
          .addInstant(UPDATE_DATE, getLateUpdateDate()).addInstant(MOST_RECENT_READ_TIME, getMostRecentReadTime())
          .addString(SOURCE_NAME, getSourceName())
          .addString(SOURCE_URL, getSourceURL().map(java.net.URL::toExternalForm)).addPath(PATH, getPath())
          .addLong(SIZE, size()).addString(DESCRIPTION, getDescription())
          .addString(ORIGINAL_PATH, getOriginalPath().toString());
      Future<Checksum> fc = f.resultAt(0);
      Future<String> ft = f.resultAt(1);
      return succeededFuture(jb.addChecksum(CHECKSUM, fc.result()).addString(MIME_TYPE, ft.result()).toJson());
    });
  }

  Path getOriginalPath();

  default Optional<BasicFileAttributes> getBasicFileAttributes() {
    return getAttributes.apply(getPath());
  }

}
