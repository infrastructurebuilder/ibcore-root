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

import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.pathref.util.readdetect.model.v0_0.IBResourceModel;
import org.infrastructurebuilder.util.readdetect.api.IBResource;
import org.infrastructurebuilder.util.readdetect.base.impls.PathRefPathIBResourceBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathRefPathAvroIBResourceBuilderFactory extends PathRefPathIBResourceBuilderFactory {
  private static final long serialVersionUID = 8394943566089224494L;

  public PathRefPathAvroIBResourceBuilderFactory(PathRefFileSystem relRoot) {
    super(relRoot);
  }

  @Override
  public Supplier<? extends AbstractPathIBResourceBuilder> getBuilder() {
    return () -> new PathRefPathAvroIBResourceBuilder(getRelativeRoot());
  }

  public static class PathRefPathAvroIBResourceBuilder extends PathRefPathIBResourceBuilder {
    private final static Logger log = LoggerFactory.getLogger(PathRefPathAvroIBResourceBuilder.class);

    public PathRefPathAvroIBResourceBuilder(PathRefFileSystem root) {
      super(root);
    }

    @Override
    public Optional<IBResource> build(boolean hard) {
      try {
        validate(hard);
        return Optional.of(new PathRefPathIBResourceAvro(this.model, this.path));
      } catch (IBException e) {
        log.error("Error building IBResource", e);
        return Optional.empty();
      }
    }

    public static class PathRefPathIBResourceAvro extends PathRefPathIBResource implements IBResourceAvro {
      private final static Logger log = LoggerFactory.getLogger(PathRefPathIBResourceAvro.class.getName());

      public PathRefPathIBResourceAvro(IBResourceModel m, PathRefPath sourcePath) {
        super(m, sourcePath);
      }

      public PathRefPathIBResourceAvro(PathRefPath path, Checksum checksum, Optional<String> type,
          Optional<Properties> addlProps)
      {
        super(path, checksum, type, addlProps);
      }

    }
  }
}
