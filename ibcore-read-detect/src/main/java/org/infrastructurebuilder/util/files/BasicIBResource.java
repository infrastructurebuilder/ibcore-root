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

import org.infrastructurebuilder.util.files.model.IBResourceModel;

public class BasicIBResource extends IBResourceModel {

  /**
   *
   */
  private static final long serialVersionUID = 5579472434157113171L;
  protected final java.nio.file.Path path;

  public BasicIBResource(
      // Path
      java.nio.file.Path path
      // Checksum
      , org.infrastructurebuilder.util.artifacts.Checksum checksum
      // Type
      , String type) {
    this(path, checksum, type, java.util.Optional.empty(), java.util.Optional.empty());
  }

  public BasicIBResource(
      // Path
      java.nio.file.Path path
      // Checksum
      , org.infrastructurebuilder.util.artifacts.Checksum checksum
      // Type
      , String type
      // Source
      , java.util.Optional<java.net.URL> sourceURL
      // Name
      , java.util.Optional<String> name) {
    super(java.util.Objects.requireNonNull(path).toAbsolutePath().toString(),
        java.util.Objects.requireNonNull(checksum).toString(), java.util.Objects.requireNonNull(type),
        java.util.Objects.requireNonNull(sourceURL).map(java.net.URL::toExternalForm).orElse(null),
        java.util.Objects.requireNonNull(name).orElse(null));
    this.path = java.util.Objects.requireNonNull(path);
  }

  public BasicIBResource(
      // Path
      java.nio.file.Path path
      // Checksum
      , org.infrastructurebuilder.util.artifacts.Checksum checksum) {
    this(path, checksum, org.infrastructurebuilder.IBConstants.APPLICATION_OCTET_STREAM);
  }

  @Override
  public java.nio.file.Path getPath() {
    return path;
  }

}