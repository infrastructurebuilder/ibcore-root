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

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.util.core.RelativeRootFactory;
import org.infrastructurebuilder.util.readdetect.impl.AbsolutePathIBResourceBuilderFactory;

@Named(IBResourceBuilderFactorySupplier.NAME)
@Singleton
public class IBResourceBuilderFactorySupplier {
  public final static String NAME = "ibresource-builder-factory-supplier";
  private final RelativeRootFactory root;

  @Inject
  public IBResourceBuilderFactorySupplier(RelativeRootFactory rrf) {
    this.root = requireNonNull(rrf);
  }

  public Set<String> getAvailableIds() {
    return this.root.getNames();
  }

  public Optional<IBResourceBuilderFactory<Optional<IBResource<InputStream>>>> get(String id) {
    return this.root.get(id).map(AbsolutePathIBResourceBuilderFactory::new);
  }

}
