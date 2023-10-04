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

import static java.nio.file.Files.readAttributes;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.function.Function;

import org.apache.tika.Tika;
import org.apache.tika.metadata.TikaCoreProperties;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;

/**
 * IBResourceCacheFactor is the part of ibcore-read-detect that actually copies files from some remote location to it's
 * local copy. An IBResourceCache is expected to be inviolate from the time a cache is created until it is no longer
 * needed. A cache has a serialized representation of all the IBResource elements within it, and thus can be persisted.
 */
public interface VertxIBResourceBuilderFactory {
  final static Logger log = LoggerFactory.getLogger(VertxIBResourceBuilderFactory.class.getName());

  public final static Function<Path, Optional<BasicFileAttributes>> getAttributes = (i) -> {
    Optional<BasicFileAttributes> retVal = empty();
    try {
      retVal = of(readAttributes(requireNonNull(i), BasicFileAttributes.class));
    } catch (IOException e) {
      // TODO Log an error, maybe?
    }
    return retVal;
  };

  default Future<VertxIBResourceBuilder> fromPath(Path p) {
    return fromPath(p, null);
  }

  default Future<VertxIBResourceBuilder> fromURL(URL u) {
    return fromURL(u, null);
  }

  default Future<VertxIBResourceBuilder> fromURLLike(String u) {
    return fromURLLike(u, null);
  }

  /**
   * Return method but force type
   *
   * @param p    path
   * @param type type to force. If null, type will be interpreted.
   * @return IBResource instance
   */
  Future<VertxIBResourceBuilder> fromPath(Path p, String type);

  default Future<VertxIBResourceBuilder> fromURL(URL u, String type) {
    return fromURLLike(requireNonNull(u).toExternalForm(), type);
  }

  Future<VertxIBResourceBuilder> fromURLLike(String u, String type);

  Future<VertxIBResourceBuilder> fromModel(IBResourceModel model);

  default VertxIBResourceBuilder builderFromPath(Path p) {
    return builderFromPathAndChecksum(requireNonNull(p), new Checksum(p));
  }

  /**
   * Dont (at) me bro. The string is a JSON string
   *
   * @param json
   * @return
   */
  default Future<VertxIBResourceBuilder> fromJSONString(String json) {
    try {
      return fromJSON(cet.returns(() -> new JSONObject(json)));
    } catch (IBException e) {
      // TODO?
      return Future.failedFuture("Could not build from JSONString");
    }

  }

  Future<VertxIBResourceBuilder> fromJSON(JSONObject json);

  Optional<RelativeRoot> getRoot();

  VertxIBResourceBuilder builderFromPathAndChecksum(Path p, Checksum checksum);

}
