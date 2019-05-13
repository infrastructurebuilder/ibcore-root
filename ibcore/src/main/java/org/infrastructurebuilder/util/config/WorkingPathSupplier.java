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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Named;

import org.infrastructurebuilder.IBException;
import org.infrastructurebuilder.util.IBUtils;

@Named("working")
public class WorkingPathSupplier implements PathSupplier {

  private static final String WORKING_PATH_KEY = "working-root";
  private final Path root;
  private final IdentifierSupplier id;
  private final List<Path> paths = new ArrayList<>();
  private final boolean cleanup;

  public WorkingPathSupplier() {
    this(new HashMap<>(), null, true);
  }

  public WorkingPathSupplier(final Map<String, String> params, @org.eclipse.sisu.Nullable final IdentifierSupplier id) {
    this(params, id, false);
  }

  public WorkingPathSupplier(final Map<String, String> params, @org.eclipse.sisu.Nullable final IdentifierSupplier id,
      final boolean cleanup) {
    this.cleanup = cleanup;
    this.id = Optional.ofNullable(id).orElse(new DefaultIdentifierSupplier());
    final String prop = Optional.ofNullable(System.getProperty("target")).orElse("./target");
    root = IBException.cet.withReturningTranslation(
        () -> Paths.get(Optional.ofNullable(params.get(WORKING_PATH_KEY)).orElse(prop)).toRealPath().toAbsolutePath());
  }

  @Override
  public void finalize() {
    paths.forEach(p -> {
      if (cleanup) {
        IBUtils.deletePath(p);
      }
    });
  }

  @Override
  public Path get() {
    Path p = root.resolve(id.get());
    while (Files.exists(p)) {
      p = root.resolve(id.get());
    }
    final Path k = p;
    paths.add(k);
    IBException.cet.withTranslation(() -> Files.createDirectories(k));
    return k;
  }

  public Path getRoot() {
    return root;
  }

}
