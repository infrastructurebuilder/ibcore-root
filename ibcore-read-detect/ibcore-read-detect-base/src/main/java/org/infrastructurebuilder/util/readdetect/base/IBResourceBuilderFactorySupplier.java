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

import java.util.Optional;
import java.util.Set;

import org.infrastructurebuilder.api.base.NameDescribed;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefFactory;

/**
 * An IBResourceBuilderFactorySupplier is used to map {@link PathRef} identifiers supplied by a {@link PathRefFactory}
 * to instances of an {@link IBResourceBuilderFactory}
 *
 * This may seem unnecessarily complex as a means to obtain a factory-of-builders, but being able to indicate the actual
 * location of a {@link PathRef} by its identifier rather than its path allows us to fit in between an immovable
 * location and an unknown identifier when attempting to validate values later.
 *
 *
 * @param <T> The type of {@link IBResourceBuilderFactory} being supplied
 */
public interface IBResourceBuilderFactorySupplier extends NameDescribed {

  Set<String> getAvailableNames();

  Optional<PathRef> getRoot(String id);

  /**
   * Get a builder with this relative root
   *
   * @param id
   * @return
   */
  Optional<IBResourceBuilderFactory<?>> get(String relativeRootName);

}
