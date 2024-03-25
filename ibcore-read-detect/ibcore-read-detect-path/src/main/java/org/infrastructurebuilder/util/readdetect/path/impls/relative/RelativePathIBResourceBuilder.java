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
package org.infrastructurebuilder.util.readdetect.path.impls.relative;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.infrastructurebuilder.util.core.PathAndChecksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.base.base.path.AbstractIBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.base.path.DefaultEmptyIBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.base.path.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.base.path.IBResourceIS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelativePathIBResourceBuilder extends AbstractIBResourceBuilder<Optional<IBResourceIS>> {
  private static final DefaultEmptyIBResourceBuilder<IBResourceIS> EMPTY_BUILDER = new DefaultEmptyIBResourceBuilder<IBResourceIS>(
      null);
  private final static Logger log = LoggerFactory.getLogger(RelativePathIBResourceBuilder.class);

  public RelativePathIBResourceBuilder(RelativeRoot root) {
    super(requireNonNull(root));
  }

  @Override
  public Optional<IBResourceIS> build(boolean hard) {
    try {
      return validate(hard).map(r -> new RelativePathIBResourceIS(this.getRoot().get(), this.model, this.sourcePath));
    } catch (Throwable e) {
      log.error("Error building IBResource", e);
      return empty();
    }
  }

  @Override
  public IBResourceBuilder<Optional<IBResourceIS>> fromURL(String url) {
    String op = requireNonNull(url);
    Path rel = Paths.get(getRoot().get().relativize(op));
    this.sourcePath = rel;
    return this

        .withFilePath(rel.toString())

        .withName(rel.getFileName().toString())

        .withSource(url);
  }

  @Override
  public IBResourceBuilder<Optional<IBResourceIS>> fromPathAndChecksum(PathAndChecksum pandc) {
    Path op = pandc.get();
    RelativeRoot rr = getRoot().get();
    if (requireNonNull(op).isAbsolute()) {
      if (rr.isParentOf(op)) {
        op = rr.relativize(op).get();
      } else {
        log.warn("Absolute path {} outside relative root {} is disallowed.  Builder will not produce a resource.",
            pandc, rr.toString());
        return EMPTY_BUILDER;
      }
    }
    Optional<String> k = rr.resolvePath(op);
    if (k.isEmpty()) {
      log.warn("Path {} unresolvable with {}", rr.toString());
      return EMPTY_BUILDER;
    }

    return fromURL(k.get()).withBasicFileAttributes(pandc.getAttributes().orElse(null));
  }

}
