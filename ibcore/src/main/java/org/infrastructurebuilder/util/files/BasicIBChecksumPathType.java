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
package org.infrastructurebuilder.util.files;

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.IBException.cet;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.infrastructurebuilder.util.artifacts.Checksum;

public class BasicIBChecksumPathType implements IBChecksumPathType {

  protected final Checksum checksum;
  protected final Path path;
  protected final String type;

  public BasicIBChecksumPathType(Path path, Checksum checksum, String type) {
    super();
    this.path = requireNonNull(path);
    this.checksum = requireNonNull(checksum);
    this.type = requireNonNull(type);
  }

  public BasicIBChecksumPathType(Path path, Checksum checksum) {
    this(path, checksum, APPLICATION_OCTET_STREAM);
  }

  @Override
  public Path getPath() {
    return path;
  }

  @Override
  public Checksum getChecksum() {
    return checksum;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public InputStream get() {
    return cet.withReturningTranslation(
        () -> Files.newInputStream(this.path, StandardOpenOption.READ, LinkOption.NOFOLLOW_LINKS));
  }

}