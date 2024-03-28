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

import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;

public class DefaultPathAndChecksum implements PathAndChecksum {

  private final Path path;
  private final Checksum checksum;
  private RelativeRoot root;

  public DefaultPathAndChecksum(Path p) {
    this(p, null);
  }

  public DefaultPathAndChecksum(Path p, Checksum s) {
    this(Optional.empty(), p, s);
  }

  public DefaultPathAndChecksum(Optional<RelativeRoot> root, Path p) {
    this(root, p, null);
  }

  public DefaultPathAndChecksum(Optional<RelativeRoot> root, Path p, Checksum s) {
    this.path = Objects.requireNonNull(p);
    this.root = ofNullable(root).orElse(Optional.empty()).orElse(null);
    if (!this.path.isAbsolute() && this.root == null)
      throw new IBException("Relative.path.no.root|" + this.path);
    this.checksum = (s == null) ? new Checksum(resolvePath()) : s;
  }

  @Override
  public Path get() {
    return this.path;
  }

  @Override
  public final Optional<RelativeRoot> getRoot() {
    return ofNullable(this.root);
  }

  private boolean isAbsolute() {
    return this.path.isAbsolute();
  }

  private Path resolvePath() {
    return isAbsolute() ? get() : getRoot().get().resolvePath(get().toString()).get();
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilder.flatInstance(this.checksum);
  }

  @Override
  public OptStream asOptStream() {
    try {
      return new OptStream(Files.newInputStream(resolvePath(), StandardOpenOption.READ), this.checksum);
    } catch (IOException e) {
      return new OptStream();
    }
  }

  @Override
  public Optional<Long> size() {
    return ofNullable(resolvePath()).flatMap(IBUtils::size);
  }

}
