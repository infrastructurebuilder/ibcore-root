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
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IBResourceCacheFactor is the part of ibcore-read-detect that actually copies files from some remote location to it's
 * local copy. An IBResourceCache is expected to be inviolate from the time a cache is created until it is no longer
 * needed. A cache has a serialized representation of all the IBResource elements within it, and thus can be persisted.
 */
public interface IBResourceBuilderFactory {
  final static Logger log = LoggerFactory.getLogger(IBResourceBuilderFactory.class.getName());
  final static Tika tika = new Tika();

  public final static Function<String, Path> extracted = (x) -> {
    try {
      Path p1 = Paths.get(x);
      URL u;
      if (Files.isRegularFile(p1)) {
        u = cet.returns(() -> p1.toUri().toURL());
      } else {
        u = cet.returns(() -> new URL(x));
      }
      // I know, right?
      return Paths.get(cet.returns(() -> u.toURI()));
    } catch (Throwable t) {
      log.error("Error converting to path", t);
      throw t;
    }
  };

  public final static Function<Path, String> toType = (path) -> {
    if (!Files.exists(requireNonNull(path)))
      throw new IBException("file.does.not.exist");
    if (!Files.isRegularFile(path))
      throw new IBException("file.not.regular.file");

    synchronized (tika) {
      log.debug("Detecting path " + path);
      org.apache.tika.metadata.Metadata md = new org.apache.tika.metadata.Metadata();
      md.set(TikaCoreProperties.RESOURCE_NAME_KEY, path.toAbsolutePath().toString());
      try (Reader p = tika.parse(path, md)) {
        log.debug(" Metadata is " + md);
        return tika.detect(path);
      } catch (IOException e) {
        log.error("Failed during attempt to get tika type", e);
        return IBConstants.APPLICATION_OCTET_STREAM;
      }
    }
  };

  public final static Function<Path, Optional<String>> toOptionalType = (path) -> {
    try {
      return ofNullable(toType.apply(path));
    } catch (Throwable t2) {
      return Optional.empty();
    }
  };

  public final static Function<Path, Optional<BasicFileAttributes>> getAttributes = (i) -> {
    Optional<BasicFileAttributes> retVal = empty();
    try {
      retVal = of(readAttributes(requireNonNull(i), BasicFileAttributes.class));
    } catch (IOException e) {
      // TODO Log an error, maybe?
    }
    return retVal;
  };

  default Optional<IBResourceBuilder> fromPath(Path p) {
    return fromPath(p, null);
  }

  default Optional<IBResourceBuilder> fromURL(URL u) {
    return fromURL(u, null);
  }

  default Optional<IBResourceBuilder> fromURLLike(String u) {
    return fromURLLike(u, null);
  }

  /**
   * Return method but force type
   *
   * @param p    path
   * @param type type to force. If null, type will be interpreted.
   * @return IBResource instance
   */
  Optional<IBResourceBuilder> fromPath(Path p, String type);

  default Optional<IBResourceBuilder> fromURL(URL u, String type) {
    return fromURLLike(u.toExternalForm(), type);
  }

  Optional<IBResourceBuilder> fromURLLike(String u, String type);

  Optional<IBResourceBuilder> fromModel(IBResourceModel model);

  default IBResourceBuilder builderFromPath(Path p) {
    return builderFromPathAndChecksum(p, new Checksum(p));
  }

  /**
   * Dont (at) me bro. The string is a JSON string
   *
   * @param json
   * @return
   */
  default Optional<IBResourceBuilder> fromJSONString(String json) {
    try {
      return fromJSON(cet.returns(() -> new JSONObject(json)));
    } catch (IBException e) {
      //TODO?
      return Optional.empty();
    }

  }

  Optional<IBResourceBuilder> fromJSON(JSONObject json);

  Optional<RelativeRoot> getRoot();

  IBResourceBuilder builderFromPathAndChecksum(Path p, Checksum checksum);

}
