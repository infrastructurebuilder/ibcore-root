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
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Optional;

public class DefaultPathAndChecksum implements PathAndChecksum {

  private final Path path;
  private final Checksum checksum;

  public DefaultPathAndChecksum(Path p) {
    this(p, new Checksum(p));
  }

  public DefaultPathAndChecksum(Path p, Checksum s) {
    this.path = Objects.requireNonNull(p);
    this.checksum = s;
  }

  @Override
  public Path get() {
    return this.path;
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilder.flatInstance(this.checksum);
  }

}
