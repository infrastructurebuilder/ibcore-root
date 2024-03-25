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
package org.infrastructurebuilder.util.relativeroot.setpath;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.relativeroot.base.AbstractRelativeRootBasicPathPropertiesSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is (mostly) a testing instance of RelativeRootSupplier, allowing for the creation of RelativeRootSupplier
 * instances in the same manner as the Named instances
 */
@Named(RelativeRootSetPathSupplier.NAME)
// NOT a singleton
public class RelativeRootSetPathSupplier extends AbstractRelativeRootBasicPathPropertiesSupplier {
  private static final Logger log = LoggerFactory.getLogger(RelativeRootSetPathSupplier.class);
  public static final String NAME = "set-path";
  private final AtomicReference<String> path = new AtomicReference<>();

  @Override
  public String getName() {
    return NAME;
  }

  /**
   * Get a new one every time
   */
  @Inject
  public RelativeRootSetPathSupplier() {
  }

  public RelativeRootSetPathSupplier(Path p) {
    this.path.set(requireNonNull(p).toAbsolutePath().toString());
  }

  public final RelativeRootSetPathSupplier withPath(Path p) {
    boolean set = this.path.compareAndSet(null, ofNullable(p) //
        .map(Path::toAbsolutePath) //
        .map(Path::toString) //
        .orElse(null));
    getLog().info(set ? "Path set to " + p : "Path not reset");
    return this;
  }

  @Override
  public Optional<String> getProperty() {
    return ofNullable(this.path.get());
  }

  @Override
  protected Logger getLog() {
    return log;
  }

}
