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
package org.infrastructurebuilder.util.readdetect.base.impls;

import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.OptionalReflectionLoadingTikaDetector;
import org.infrastructurebuilder.pathref.api.ConfigMap;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.pathref.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.readdetect.api.IBResource;
import org.infrastructurebuilder.util.readdetect.api.IBResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathRefPathIBResourceBuilderFactory extends AbstractPathRefPathIBResourceBuilderFactory {
  private final static Logger log = LoggerFactory.getLogger(PathRefPathIBResourceBuilderFactory.class);
  private static final long serialVersionUID = -7034230288330677232L;
  private ConfigMap config;

  public PathRefPathIBResourceBuilderFactory(PathRefFileSystem r) {
    super(Objects.requireNonNull(r));
  }

  @Override
  public Supplier<? extends AbstractPathIBResourceBuilder> getBuilder() {
    // Delivers a new builder from the relative root each time
    return () -> new PathRefPathIBResourceBuilder(getRelativeRoot());
  }

  public Logger getLog() {
    return log;
  }

  @SuppressWarnings("unchecked")
  public PathRefPathIBResourceBuilderFactory withConfig(ConfigMap config) {
    this.config = config;
    return this;
  }

  @Override
  protected Optional<ConfigMap> getConfig() {
    return Optional.ofNullable(this.config);
  }

  public static class PathRefPathIBResourceBuilder extends AbstractPathIBResourceBuilder {
    private final static Logger log = LoggerFactory.getLogger(PathRefPathIBResourceBuilder.class);
    private ConfigMap config;

    public PathRefPathIBResourceBuilder(PathRefFileSystem r) {
      super(r);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PathRefPathIBResourceBuilder withConfig(ConfigMap c) {
      this.config = c;
      return this;
    }

    @Override
    protected Optional<ConfigMap> getConfig() {
      return Optional.ofNullable(this.config);
    }

    @Override
    public Optional<IBResource> build(boolean hard) {
      try {
        validate(hard);
        return Optional.of(//
            new PathRefPathIBResource(//
                this.model, //
                this.sourcePath) //
        );
      } catch (IBException e) {
        log.error("Error building IBResource", e);
        return Optional.empty();
      }
    }

    @Override
    public boolean acceptable(PathRefPath p) {
      return p != null; // FIXME
    }

    /**
     * The AbsolutePathIBResource is an <link>IBResource</link> that has the following propertie
     * <ol>
     * <li>The output must be backed by an <b><i>ABSOLUTE</i></b> <code>java.nio.file.Path</code></li>
     * <li>It's <code>get()</code> method returns an <code>Optional InputStream</code>, which will probably be present
     * based on the availability of the FileSystem that backs the Path.</li>
     * <li>It's <code>PathRef</code> instance <i>may</i> be null, allowing for no relative paths. This could affect any
     * ability to persist the metadata.</li>
     *
     * </ol>
     */
    public static class PathRefPathIBResource extends AbstractIBResource {
      private final static Logger log = LoggerFactory.getLogger(PathRefPathIBResource.class);

      protected PathRefPathIBResource(IBResourceModel m, PathRefPath r) {
        super(m, r);
        this.m.setStreamSource(r.toFullString());
        this.m.setStreamChecksum(r.asChecksum().toString());

      }

      public PathRefPathIBResource(IBResourceModel m, Path sourcePath) {
        this(m, PathRefPath.fromUri(sourcePath.toUri())
            .orElseThrow(() -> new IBResourceException("PathRefPath {}".formatted(sourcePath))));
      }

      public PathRefPathIBResource(PathRefPath path, Checksum checksum, Optional<String> type,
          Optional<Properties> addlProps)
      {
        this(new IBResourceModel(), path);
        IBUtils.getAttributes.apply(path).ifPresent(bfa -> {
          this.m.setCreated(bfa.creationTime().toInstant());
          this.m.setLastUpdate(bfa.lastModifiedTime().toInstant());
          this.m.setMostRecentReadTime(bfa.lastAccessTime().toInstant());
          this.m.setStreamSize(bfa.size());
        });
        requireNonNull(type).ifPresent(t -> m.setStreamType(t));
        if (!validate(true))
          throw new IBResourceException("Resource did not pass validation");
      }

      @Override
      public PathRefPath get() {
        m.setMostRecentReadTime(now());
        return getPath().orElse(null);
      }
      @Override
      public boolean validate(boolean hard) {
        return getPath().map(p -> {
          if (!Files.exists(p)) {
            log.warn("validation: File %p does not exist", p.toString());
            return false;
          }
          if (hard) {
            Checksum s = this.getByteStreamChecksum();
            Checksum n = new Checksum(p); // Calculate new checksum
            if (!s.equals(n)) {
              log.warn(format("validation: expected checksum %s != actual checksum %s", s, n));
              return false;
            }
            var actualType = OptionalReflectionLoadingTikaDetector.toType.apply(p)
                .orElse(IBConstants.APPLICATION_OCTET_STREAM);
            if (!this.m.getStreamType().equals(actualType)) {
              log.warn(format("validation: expected type %s != actual type %", this.m.getStreamType(), actualType));
              return false;
            }
          }
          return true;
        }).orElseGet(() -> {
          log.warn("validate: path not present in model");
          return false;
        });
      }

    }

    }
}
