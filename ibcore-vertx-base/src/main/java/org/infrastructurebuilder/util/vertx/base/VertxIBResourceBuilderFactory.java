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

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
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

  default Future<IBResourceBuilder<Future<VertxIBResource>>> fromPath(Path p) {
    return fromPath(p, null);
  }

  default Future<IBResourceBuilder<Future<VertxIBResource>>> fromURL(URL u) {
    return fromURL(u, null);
  }

  default Future<IBResourceBuilder<Future<VertxIBResource>>> fromURLLike(String u) {
    return fromURLLike(u, null);
  }

  /**
   * Return method but force type
   *
   * @param p    path
   * @param type type to force. If null, type will be interpreted.
   * @return IBResource instance
   */
  Future<IBResourceBuilder<Future<VertxIBResource>>> fromPath(Path p, String type);

  default Future<IBResourceBuilder<Future<VertxIBResource>>> fromURL(URL u, String type) {
    return fromURLLike(requireNonNull(u).toExternalForm(), type);
  }

  Future<IBResourceBuilder<Future<VertxIBResource>>> fromURLLike(String u, String type);

  Future<IBResourceBuilder<Future<VertxIBResource>>> fromModel(IBResourceModel model);

  default IBResourceBuilder<Future<VertxIBResource>> builderFromPath(Path p) {
    return builderFromPathAndChecksum(requireNonNull(p), new Checksum(p));
  }

  /**
   * Dont (at) me bro. The string is a JSON string
   *
   * @param json
   * @return
   */
  default Optional<IBResourceBuilder<Future<VertxIBResource>>> fromJSONString(String json) {
    try {
      return fromJSON(cet.returns(() -> new JSONObject(json)));
    } catch (IBException e) {
      log.error("Could not build from JSONString", e);
      return Optional.empty();
    }

  }

  Optional<IBResourceBuilder<Future<VertxIBResource>>> fromJSON(JSONObject json);

  Optional<RelativeRoot> getRelativeRoot();

  IBResourceBuilder<Future<VertxIBResource>> builderFromPathAndChecksum(Path p, Checksum checksum);

}
