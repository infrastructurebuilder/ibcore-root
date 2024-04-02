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
import java.util.Map;
import java.util.Optional;

public class TestingPathSupplier extends WorkingPathSupplier implements RelativeRootSupplier {

  public TestingPathSupplier() {
    super();
  }

  public TestingPathSupplier(Map<String, String> params, IdentifierSupplier id) {
    this(params, id, true);
  }

  public TestingPathSupplier(Map<String, String> params, IdentifierSupplier id, boolean cleanup) {
    super(params, id, cleanup);
  }

  public TestingPathSupplier(PathSupplier root, IdentifierSupplier id, boolean cleanup) {
    super(root, id, cleanup);
  }

  public final Path getTestClasses() {
    return getRoot().resolve("test-classes");
  }

  public final Path getClasses() {
    return getRoot().resolve("classes");
  }

  @Override
  public String getName() {
    return "testing-path-supplier";
  }

  /**
   * Get a new RelativeRoot path every time
   */
  @Override
  public Optional<RelativeRoot> getRelativeRoot() {
    return Optional.of(new AbsolutePathRelativeRoot(get()));
  }

}
