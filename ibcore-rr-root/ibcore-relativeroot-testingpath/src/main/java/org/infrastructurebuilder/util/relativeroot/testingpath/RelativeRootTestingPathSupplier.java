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
package org.infrastructurebuilder.util.relativeroot.testingpath;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.pathref.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.pathref.RelativeRoot;
import org.infrastructurebuilder.pathref.RelativeRootSupplier;
import org.infrastructurebuilder.pathref.TestingPathSupplier;

/**
 * This is primarily for testing purposes, as its source is a {@link TestingPathSupplier} and those are very IB
 * specific.
 */
@Named(RelativeRootTestingPathSupplier.NAME)
// Not a singleton, so be careful
public class RelativeRootTestingPathSupplier implements RelativeRootSupplier {
  public final static String NAME = "testing-path-supplier";
  private final TestingPathSupplier tps;

  @Inject
  public RelativeRootTestingPathSupplier() {
    this(new TestingPathSupplier());
  }

  public RelativeRootTestingPathSupplier(TestingPathSupplier p) {
    this.tps = requireNonNull(p);
  }

  public TestingPathSupplier getTps() {
    return tps;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Optional<RelativeRoot> getRelativeRoot() {
    return Optional.of(new AbsolutePathRelativeRoot(getTps().get()));
  }

  @Override
  protected void finalize() throws Throwable {
    this.tps.finalize();
  }
}
