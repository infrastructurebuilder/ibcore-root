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

import static io.vertx.core.Future.succeededFuture;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.constants.IBConstants.CREATE_DATE;
import static org.infrastructurebuilder.constants.IBConstants.DESCRIPTION;
import static org.infrastructurebuilder.constants.IBConstants.MIME_TYPE;
import static org.infrastructurebuilder.constants.IBConstants.MOST_RECENT_READ_TIME;
import static org.infrastructurebuilder.constants.IBConstants.ORIGINAL_PATH;
import static org.infrastructurebuilder.constants.IBConstants.PATH;
import static org.infrastructurebuilder.constants.IBConstants.SIZE;
import static org.infrastructurebuilder.constants.IBConstants.SOURCE_NAME;
import static org.infrastructurebuilder.constants.IBConstants.SOURCE_URL;
import static org.infrastructurebuilder.constants.IBConstants.UPDATE_DATE;
import static org.infrastructurebuilder.pathref.ChecksumEnabled.CHECKSUM;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.readdetect.base.IBResource;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;

public interface IBResourceVertx extends Supplier<FutureStream>, JsonOutputEnabled {
  static final OpenOptions oRead = new OpenOptions().setRead(true);

  default Vertx vertx() {
    return Vertx.vertx();
  }

  /**
   * @return Non-null Path to this result
   * @throws IBException (runtime) if not available
   */
  Optional<Path> getPath();

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

  default FutureStream get() {
    String filePath = getPath().toString();
    return new FutureStream(vertx().fileSystem().open(filePath, oRead));
  }

  default Future<String> defaultToString() {
    return Future.all(getChecksum(), getType()).compose(f -> {

      StringJoiner sj = new StringJoiner("|") //
          .add(getPath().toString()); // Path
      getSourceURL().ifPresent(u -> sj.add(u.toExternalForm()));
      getSourceName().ifPresent(sj::add);
      Checksum fc = f.resultAt(0);
      String ft = f.resultAt(1);
      return succeededFuture(sj.add(fc.asUUID().get().toString()).add(ft).toString());
    });
  }

  long size();

  default Optional<String> getName() {
    return empty();
  }

  default Optional<String> getDescription() {
    return empty();
  }

  @Override
  default Future<JsonObject> toFutureJson() {
    CompositeFuture cf = Future.all(getChecksum(), getType());
    return cf.compose(f -> {
      JsonBuilder jb = new JsonBuilder(empty()).addInstant(CREATE_DATE, getCreateDate())
          .addInstant(UPDATE_DATE, getLastUpdateDate()).addInstant(MOST_RECENT_READ_TIME, getMostRecentReadTime())
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
    return getPath().flatMap(path -> IBUtils.getAttributes.apply(path));
  }

  IBResource asIBResource();

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

//  default Optional<InputStream> get() {
//    if (getPath().isEmpty())
//      return Optional.empty();
//    List<OpenOption> o = new ArrayList<>(List.of(READ));
//    if (getPath().get().getClass().getCanonicalName().contains("Zip")) {
//    } else {
//      o.add(NOFOLLOW_LINKS);
//    }
//    return getPath().map(path -> cet.returns(() -> newInputStream(path, o.toArray(new OpenOption[o.size()]))));
//  }
//
  Optional<URL> getSourceURL();

  Optional<String> getSourceName();

}
