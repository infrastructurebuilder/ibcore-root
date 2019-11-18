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

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.files.model.IBChecksumPathTypeModel;

public class BasicIBChecksumPathType extends IBChecksumPathTypeModel {

  /**
   *
   */
  private static final long serialVersionUID = 5579472434157113171L;
  protected final Path path;

  public BasicIBChecksumPathType(Path path, Checksum checksum, String type) {
    this(path, checksum, type, Optional.empty(), Optional.empty());
  }

  public BasicIBChecksumPathType(Path path, Checksum checksum, String type, Optional<URL> sourceURL,
      Optional<String> name) {
    super(requireNonNull(path).toAbsolutePath().toString(), requireNonNull(checksum).toString(), requireNonNull(type),
        requireNonNull(sourceURL).map(URL::toExternalForm).orElse(null), requireNonNull(name).orElse(null));
    this.path = requireNonNull(path);
  }

  public BasicIBChecksumPathType(Path path, Checksum checksum) {
    this(path, checksum, APPLICATION_OCTET_STREAM);
  }

  @Override
  public Path getPath() {
    return path;
  }

}