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
package org.infrastructurebuilder.pathref;

import static java.nio.file.Files.createDirectories;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.constants.IBConstants.MAVEN_TARGET_PATH;
import static org.infrastructurebuilder.constants.IBConstants.TARGET_DIR_PROPERTY;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.pathref.IBChecksumUtils.deletePath;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;


@Named(WorkingPathSupplier.WORKING)
public class WorkingPathSupplier implements PathSupplier {

  public static final String WORKING = "working";
  private static final String WORKING_PATH_KEY = "working-root";
  private final Path root;
  private final IdentifierSupplier id;
  private final List<Path> paths = new ArrayList<>();
  private final boolean cleanup;

  @Inject
  public WorkingPathSupplier() {
    this(new HashMap<>(), new DefaultIdentifierSupplier(), true);
  }

  public WorkingPathSupplier(final Map<String, String> params, /* @Nullable */ final IdentifierSupplier id) {
    this(params, id, false);
  }

  public WorkingPathSupplier(final Map<String, String> params, /* @Nullable */ final IdentifierSupplier id,
      final boolean cleanup)
  {
    this(() -> cet.returns(() -> Paths
        .get(ofNullable(params.get(WORKING_PATH_KEY))
            .orElse(ofNullable(System.getProperty(TARGET_DIR_PROPERTY)).orElse(MAVEN_TARGET_PATH)))
        .toRealPath().toAbsolutePath()), id, cleanup);
  }

  public WorkingPathSupplier(final PathSupplier root, final IdentifierSupplier id, final boolean cleanup) {
    this.cleanup = cleanup;
    this.id = ofNullable(id).orElse(new DefaultIdentifierSupplier());
    this.root = requireNonNull(root).get();
  }

  public WorkingPathSupplier(final PathSupplier newRoot, final WorkingPathSupplier overridden) {
    this.id = requireNonNull(overridden).id;
    this.cleanup = overridden.cleanup;
    this.root = requireNonNull(newRoot).get();
  }

  protected WorkingPathSupplier(WorkingPathSupplier workingPathSupplier) {
    this.id = requireNonNull(workingPathSupplier).id;
    this.cleanup = workingPathSupplier.cleanup;
    this.root = workingPathSupplier.root;
  }

  @Override
  public void finalize() {
    paths.forEach(p -> extracted(p));
    paths.clear();
  }

  private void extracted(Path p) {
    if (cleanup) {
      deletePath(p);
    }
  }

  @Override
  public Path get() {
    Path p = getRoot().resolve(id.get());
    while (Files.exists(p)) {
      p = getRoot().resolve(id.get());
    }
    final Path k = p;
    paths.add(k);
    return cet.returns(() -> createDirectories(k));
  }

  public Path getRoot() {
    return root;
  }

}
