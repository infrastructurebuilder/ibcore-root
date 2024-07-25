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
package org.infrastructurebuilder.pathref.testingpath;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.pathref.AbsolutePathRef;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.PathRefProducer;
import org.infrastructurebuilder.pathref.TestingPathSupplier;

/**
 * This is primarily for testing purposes, as its source is a {@link TestingPathSupplier} and those are very IB
 * specific.
 */
@Named(TestingPathRefSupplier.NAME)
// Not a singleton, so be careful
public class TestingPathRefSupplier implements PathRefProducer<String> {
  public final static String NAME = "testing-path-supplier";
  private final TestingPathSupplier tps;

  @Inject
  public TestingPathRefSupplier() {
    this(new TestingPathSupplier());
  }

  public TestingPathRefSupplier(TestingPathSupplier p) {
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
  protected void finalize() throws Throwable {
    this.tps.finalize();
  }

  @Override
  public Optional<PathRef> with(Object arg0) {
    return Optional.of(new AbsolutePathRef(getTps().get()));
  }

  @Override
  public Class<? extends String> withClass() {
    return String.class;
  }
}
