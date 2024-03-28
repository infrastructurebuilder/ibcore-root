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
package org.infrastructurebuilder.util.readdetect.avro;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.apache.avro.file.SeekableFileInput;
import org.apache.avro.file.SeekableInput;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.PathAndChecksum;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.readdetect.path.impls.relative.RelativePathIBResourceBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelativePathAvroIBResourceBuilderFactory extends RelativePathIBResourceBuilderFactory {
  private static final long serialVersionUID = 8394943566089224494L;

  public RelativePathAvroIBResourceBuilderFactory(RelativeRoot relRoot) {
    super(relRoot);
  }

  @Override
  public Supplier<? extends AbstractPathIBResourceBuilder> getBuilder() {
    return () -> new RelativePathAvroIBResourceBuilder(getRelativeRoot());
  }

  public static class RelativePathAvroIBResourceBuilder extends RelativePathIBResourceBuilder {
    private final static Logger log = LoggerFactory.getLogger(RelativePathAvroIBResourceBuilder.class);

    public RelativePathAvroIBResourceBuilder(RelativeRoot root) {
      super(root);
    }

    @Override
    public Optional<IBResource> build(boolean hard) {
      try {
        validate(hard);
        return Optional.of(new RelativePathIBResourceAvro(this.model, this.path));
      } catch (IBException e) {
        log.error("Error building IBResource", e);
        return Optional.empty();
      }
    }

    public static class RelativePathIBResourceAvro extends RelativePathIBResource implements IBResourceAvro {
      private final static Logger log = LoggerFactory.getLogger(RelativePathIBResourceAvro.class.getName());

      public RelativePathIBResourceAvro(IBResourceModel m, PathAndChecksum sourcePath) {
        super(m, sourcePath);
      }

      public RelativePathIBResourceAvro(RelativeRoot root, Path path, Checksum checksum, Optional<String> type,
          Optional<Properties> addlProps)
      {
        super(root, path, checksum, type, addlProps);
      }

      @Override
      public Optional<SeekableInput> getSeekableFile() {
        try {
          return getPath().map(Path::toFile).map(f -> IBException.cet.returns(() -> new SeekableFileInput(f)));
        } catch (Throwable t) {
          log.error("Error getting seekable " + getPath(), t);
          return Optional.empty();
        }
      }

    }
  }
}
