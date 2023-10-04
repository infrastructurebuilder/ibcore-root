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
package org.infrastructurebuilder.util.vertx.base;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.readdetect.PathBackedIBResourceRelativeRootSupplier;
import org.infrastructurebuilder.util.vertx.base.impl.VertxIBResourceBuilderFactoryImpl;

import io.vertx.core.Vertx;

@Named(VertxIBResourceBuilderFactorySupplier.NAME)
public class VertxIBResourceBuilderFactorySupplier
    implements Supplier<VertxIBResourceBuilderFactory>, Provider<VertxIBResourceBuilderFactory> {
  public final static String NAME = "VertxIBResourceBuilderFactorySupplier";
  private final PathSupplier root;
  private final Vertx vertx;
  private final TypeToExtensionMapper t2e;

  @Inject
  public VertxIBResourceBuilderFactorySupplier(Vertx vertx, PathSupplier root, TypeToExtensionMapper t2e) {
    this.vertx = requireNonNull(vertx);
    this.root = requireNonNull(root);
    this.t2e = requireNonNull(t2e);
  }

  @Override
  public VertxIBResourceBuilderFactory get() {
    return new VertxIBResourceBuilderFactoryImpl(this.vertx,
        new PathBackedIBResourceRelativeRootSupplier(this.root.get()), this.t2e);
  }

}
