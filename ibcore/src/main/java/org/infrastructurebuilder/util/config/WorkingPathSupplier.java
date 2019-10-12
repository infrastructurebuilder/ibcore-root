/**
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
 */
package org.infrastructurebuilder.util.config;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.IBException.cet;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.IBUtils;

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

  public WorkingPathSupplier(final Map<String, String> params, @org.eclipse.sisu.Nullable final IdentifierSupplier id) {
    this(params, id, false);
  }

  public WorkingPathSupplier(final Map<String, String> params, @org.eclipse.sisu.Nullable final IdentifierSupplier id,
      final boolean cleanup) {
    this(
        () -> cet
            .withReturningTranslation(() -> Paths
                .get(ofNullable(params.get(WORKING_PATH_KEY))
                    .orElse(ofNullable(System.getProperty("target_dir")).orElse("./target")))
                .toRealPath().toAbsolutePath()),
        id, cleanup);
  }

  public WorkingPathSupplier(final PathSupplier root, final IdentifierSupplier id, final boolean cleanup) {
    this.cleanup = cleanup;
    this.id = ofNullable(id).orElse(new DefaultIdentifierSupplier());
    this.root = requireNonNull(root).get();
  }

  @Override
  public void finalize() {
    paths.forEach(p -> {
      if (cleanup) {
        IBUtils.deletePath(p);
      }
    });
    paths.clear();
  }

  @Override
  public Path get() {
    Path p = getRoot().resolve(id.get());
    while (Files.exists(p)) {
      p = getRoot().resolve(id.get());
    }
    final Path k = p;
    paths.add(k);
    cet.withTranslation(() -> Files.createDirectories(k));
    return k;
  }

  public Path getRoot() {
    return root;
  }

}
