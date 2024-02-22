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

import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.core.PathAndChecksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.AbstractIBResourceISBuilderFactory;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceIS;

public class RelativePathIBResourceISBuilderFactory extends AbstractIBResourceISBuilderFactory {

  private static final long serialVersionUID = -8847933754124713375L;

  public RelativePathIBResourceISBuilderFactory(RelativeRoot root) {
    super(requireNonNull(root));
  }

  @Override
  protected Supplier<IBResourceBuilder<Optional<IBResourceIS>>> getBuilder() {
    // Delivers a new builder from the relative root each time
    return () -> new RelativePathIBResourceBuilder(getRelativeRoot());
  }

  @Override
  public Optional<IBResourceBuilder<Optional<IBResourceIS>>> fromPathAndChecksum(PathAndChecksum p) {
    return Optional.of(getBuilder().get().fromPathAndChecksum(p));
  }

  @Override
  public Optional<IBResourceBuilder<Optional<IBResourceIS>>> fromURL(String u) {
    return Optional.of(getBuilder().get().fromURL(u));
  }

}
