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
package org.infrastructurebuilder.util.readdetect.base;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import org.apache.tika.Tika;
import org.apache.tika.metadata.TikaCoreProperties;
import org.infrastructurebuilder.api.base.ResponsiveToString;
import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.TypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An IBResourceBuilderFactory returns allows for the production of {@link IBResourceBuilder} instances from various
 * source locations, such as {@link Path}s, {@link URL}s, and URL-like strings (given appropriate processors within a
 * given builder).
 *
 * A given IBResourceBuilderFactory may or may not have a {@link PathRef}. If it does not, then all values are
 * considered to be purely reference values
 *
 * @param
 */

/*
 * IBResourceCacheFactor is the part of ibcore-read-detect that actually copies files from some remote location to it's
 * local copy. An IBResourceCache is expected to be inviolate from the time a cache is created until it is no longer
 * needed. A cache has a serialized representation of all the IBResource elements within it, and thus can be persisted.
 */
public interface IBResourceBuilderFactory<I> extends ResponsiveToString {
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
      log.warn("Unable to type " + path);
      return Optional.empty();
    }
  };

  /**
   * The PathRef is not optional
   *
   * @return
   */

  PathRef getRelativeRoot();

  Optional<IBResourceBuilder<I>> fromModel(IBResourceModel model);

  Optional<IBResourceBuilder<I>> fromJSON(JSONObject json);

  /**
   * Don't (at) me bro. The string needs to be a JSON string so that I can translate between types
   *
   * @param json
   * @return
   */
  default Optional<IBResourceBuilder<I>> fromJSONString(String json) {
    JSONObject j;
    try {
      j = new JSONObject(json);
    } catch (Throwable e) {
      log.error("Could not parse json from " + json, e);
      return Optional.empty();
    }
    return fromJSON(j);

  }

  IBResourceBuilderFactory<I> withTypeMapper(TypeToExtensionMapper m);

  Optional<TypeToExtensionMapper> getTypeMapper();
}
