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

import java.io.InputStream;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.util.core.RelativeRootFactory;
import org.infrastructurebuilder.util.readdetect.impl.AbsolutePathIBResourceBuilderFactory;

@Named(DefaultIBResourceBuilderFactorySupplier.NAME)
@Singleton
public class DefaultIBResourceBuilderFactorySupplier
    extends AbstractIBResourceBuilderFactorySupplier<Optional<IBResource<InputStream>>> {
  public final static String NAME = "default";

  @Inject
  public DefaultIBResourceBuilderFactorySupplier(RelativeRootFactory rrf) {
    super(rrf);
  }

  @Override
  public String getName() {
    return NAME;
  }

  public Optional<IBResourceBuilderFactory<Optional<IBResource<InputStream>>>> get(String id) {
    return getRoot(id).map(AbsolutePathIBResourceBuilderFactory::new);
  }

}
