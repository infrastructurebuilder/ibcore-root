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

import java.util.Optional;
import java.util.Set;

import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.RelativeRootFactory;

abstract public class AbstractIBResourceBuilderFactorySupplier implements IBResourceBuilderFactorySupplier {
  private final RelativeRootFactory root;

  public AbstractIBResourceBuilderFactorySupplier(RelativeRootFactory rrf) {
    super();
    this.root = requireNonNull(rrf);
  }

  @Override
  public Set<String> getAvailableNames() {
    return this.root.getAvailableNames();
  }

  @Override
  public Optional<RelativeRoot> getRoot(String name) {
    return root.get(name);
  }

}
