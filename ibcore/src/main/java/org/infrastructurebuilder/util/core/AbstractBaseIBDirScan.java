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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

import org.infrastructurebuilder.exceptions.IBException;

abstract public class AbstractBaseIBDirScan extends SimpleFileVisitor<Path> implements IBDirScan {
  TreeSet<Path> includedPaths = new TreeSet<>();
  TreeSet<Path> excludedPaths = new TreeSet<>();
  TreeMap<Path, Throwable> erroredPaths = new TreeMap<>();
  Map<Path, BasicFileAttributes> bfaMap = new HashMap<>();

  @Override
  public SortedSet<Path> getIncludedPaths() {
    return includedPaths;
  }

  @Override
  public SortedSet<Path> getExcludedPaths() {
    return excludedPaths;
  }

  @Override
  public SortedSet<Path> getErroredPaths() {
    return new TreeSet<Path>(erroredPaths.keySet());
  }

  @Override
  public Optional<BasicFileAttributes> getAttributesForPath(Path p) {
    return Optional.ofNullable(this.bfaMap.get(p));
  }

  private final void addIncluded(Path p) {
    this.includedPaths.add(requireNonNull(p));
  }

  private final void addExcluded(Path p) {
    this.excludedPaths.add(p);
  }

  private final void addAttributes(Path p, BasicFileAttributes attr) {
    this.bfaMap.put(p, attr);
  }

  @Override
  public Optional<Throwable> getExceptionForPath(Path p) {
    return Optional.ofNullable(this.erroredPaths.get(p));
  }

  private final void addErroredPath(Path p, Throwable exc) {
    this.erroredPaths.put(p, exc);
  }

  public final FileVisitResult addPath(Path p, BasicFileAttributes attr, Optional<Throwable> t) {
    requireNonNull(p);
    if (attr == null && t.isEmpty()) // yuck
      t = Optional.of(new IBException("Failed file visit"));
    AtomicBoolean excludeDir = new AtomicBoolean(false);
    requireNonNull(t).ifPresentOrElse(t1 -> addErroredPath(p, t1), () -> {
      if (getExclusionFunction().apply(p, attr))
        addIncluded(p);
      else {
        excludeDir.set(true);
        addExcluded(p);
      }
    });
    addAttributes(p, attr);

    return t.isPresent() ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    return addPath(dir, attrs, Optional.empty());
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
    return addPath(file, attr, empty()); // Might overwrite existing path
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) {
    return addPath(file, null, Optional.ofNullable(exc));
  }

  /**
   * The exclusion function determines if a given Path is included, excluded, or errored Not nullable path Nullable
   * BasicFileAttributes
   *
   * @return
   */
  abstract public BiFunction<Path, BasicFileAttributes, Boolean> getExclusionFunction();
}
