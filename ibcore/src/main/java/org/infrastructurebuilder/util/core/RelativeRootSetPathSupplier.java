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
package org.infrastructurebuilder.util.core;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is (mostly) a testing instance of RelativeRootSupplier, allowing for the creation of RelativeRootSupplier
 * instances in the same manner as the Named instances
 */
public class RelativeRootSetPathSupplier extends AbstractRelativeRootBasicPathPropertiesSupplier {
  private static final Logger log = LoggerFactory.getLogger(RelativeRootSetPathSupplier.class);
  public static final String NAME = "set-path";
  private final String path;

  @Override
  public String getName() {
    return NAME;
  }

  public RelativeRootSetPathSupplier(Path p) {
    this.path = Objects.requireNonNull(p).toAbsolutePath().toString();
  }

  @Override
  public Optional<String> getProperty() {
    return Optional.of(this.path);
  }

  @Override
  protected Logger getLog() {
    return log;
  }

}
