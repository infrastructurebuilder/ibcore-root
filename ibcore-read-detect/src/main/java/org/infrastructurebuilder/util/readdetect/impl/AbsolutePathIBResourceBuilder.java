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
package org.infrastructurebuilder.util.readdetect.impl;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.AbstractIBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.IBResourceIS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsolutePathIBResourceBuilder extends AbstractIBResourceBuilder<Optional<IBResourceIS>> {
  private final static Logger log = LoggerFactory.getLogger(AbsolutePathIBResourceBuilder.class);

  public AbsolutePathIBResourceBuilder() {
    super(null);
  }

  @Override
  public Optional<IBResourceIS> build(boolean hard) {
    try {
      validate(hard);
      return Optional.of(new AbsolutePathIBResource(this.model, this.sourcePath));
    } catch (IBException e) {
      log.error("Error building IBResource", e);
      return Optional.empty();
    }
  }

  @Override
  public IBResourceBuilder<Optional<IBResourceIS>> fromURL(String url) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IBResourceBuilder<Optional<IBResourceIS>> fromPath(Path path) {
    var op = requireNonNull(path);
    if (!op.isAbsolute()) {
      op = path.toAbsolutePath();
      log.warn("Path {} is not absolute.  Making absolute to {}", path, op);
    }
    this.sourcePath = requireNonNull(op);

    IBResourceBuilderFactory.getAttributes.apply(op).ifPresent(attr -> {
      this.withCreateDate(attr.creationTime().toInstant())

          .withLastUpdated(attr.lastModifiedTime().toInstant())

          .withMostRecentAccess(Instant.now())

          .withAcquired(Instant.now())

          .withSize(attr.size());
    });
    return this

        .withFilePath(op.toString())

        .withName(op.getFileName().toString())

        .withSource(op.toUri().toASCIIString())

    ;

  }

}
