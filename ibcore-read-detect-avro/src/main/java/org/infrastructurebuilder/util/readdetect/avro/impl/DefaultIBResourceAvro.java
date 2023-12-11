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
package org.infrastructurebuilder.util.readdetect.avro.impl;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.apache.avro.file.SeekableFileInput;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.avro.IBResourceAvro;
import org.infrastructurebuilder.util.readdetect.impl.AbsolutePathIBResource;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIBResourceAvro extends AbsolutePathIBResource implements IBResourceAvro {
  private final static Logger log = LoggerFactory.getLogger(DefaultIBResourceAvro.class.getName());

  public DefaultIBResourceAvro(Optional<RelativeRoot> root, IBResourceModel m, Path sourcePath) {
    super(root, m, sourcePath);
  }

  public DefaultIBResourceAvro(Optional<RelativeRoot> root, IBResourceModel m) {
    this(root, m, null);
  }

  public DefaultIBResourceAvro(Optional<RelativeRoot> root, JSONObject j) {
    this(Objects.requireNonNull(root), IBResourceBuilder.modelFromJSON.apply(j));
  }

  public DefaultIBResourceAvro(Optional<RelativeRoot> root, Path path, Checksum checksum, Optional<String> type,
      Optional<Properties> addlProps)
  {
    super(root, path, checksum, type, addlProps);
  }

  public DefaultIBResourceAvro(Optional<RelativeRoot> root, Path path, Checksum checksum) {
    this(root, path, checksum, empty(), empty());
  }

  public DefaultIBResourceAvro(Optional<RelativeRoot> root, Path p2, Optional<String> name, Optional<String> desc,
      Checksum checksum, Optional<Properties> addlProps)
  {
    this(root, p2, checksum, of(IBResourceBuilderFactory.toType.apply(p2)), addlProps);
    setName(requireNonNull(name).orElse(null));
    setDescription(requireNonNull(desc).orElse(null));
  }

  public DefaultIBResourceAvro(Optional<RelativeRoot> root, Path path, Checksum checksum, Optional<String> type) {
    this(root, path, checksum, type, empty());
  }

  @Override
  public Optional<SeekableFileInput> getSeekableFile() {
      try {
        return getPath().map(Path::toFile).map(f -> cet.returns(() -> new SeekableFileInput(f)));
      } catch (Throwable t) {
        log.error("Error getting seekable " + getPath(), t);
        return Optional.empty();
      }
  }

}
