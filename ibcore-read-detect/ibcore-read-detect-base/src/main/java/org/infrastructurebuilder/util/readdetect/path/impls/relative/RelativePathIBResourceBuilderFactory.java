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
package org.infrastructurebuilder.util.readdetect.path.impls.relative;

import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.util.core.DefaultPathAndChecksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.OptStream;
import org.infrastructurebuilder.util.core.PathAndChecksum;
import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.base.IBResourceException;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractPathIBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelativePathIBResourceBuilderFactory extends AbstractPathIBResourceBuilderFactory {

  private static final long serialVersionUID = -7034230288330677232L;

  public RelativePathIBResourceBuilderFactory(PathRef r) {
    super(Objects.requireNonNull(r));
  }

  @Override
  public Supplier<? extends AbstractPathIBResourceBuilder> getBuilder() {
    // Delivers a new builder from the relative root each time
    return () -> new RelativePathIBResourceBuilder(getRelativeRoot());
  }

  public static class RelativePathIBResourceBuilder extends AbstractPathIBResourceBuilder {
    private final static Logger log = LoggerFactory.getLogger(RelativePathIBResourceBuilder.class);

    public RelativePathIBResourceBuilder(PathRef r) {
      super(r);
    }

    @Override
    public Optional<IBResource> build(boolean hard) {
      try {
        validate(hard);
        return of(new RelativePathIBResource(getRoot().get(), this.model, this.sourcePath));
      } catch (IBException e) {
        log.error("Error building IBResource", e);
        return Optional.empty();
      }
    }

    @Override
    public boolean acceptable(String p) {
      return !(Optional.ofNullable(p).map(Paths::get).map(Path::isAbsolute).orElse(false));
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
    public static class RelativePathIBResource extends AbstractIBResource {
      private final static Logger log = LoggerFactory.getLogger(RelativePathIBResource.class);

      protected RelativePathIBResource(IBResourceModel m, PathAndChecksum p) {
        super(m, p);
        this.m.setPath(p.get().toString());
        this.m.setStreamChecksum(getPathAndChecksum().asChecksum().toString());

      }

      public RelativePathIBResource(PathRef r, IBResourceModel m, Path sourcePath) {
        this(m, new DefaultPathAndChecksum(of(r), sourcePath));
      }

      public RelativePathIBResource(PathRef r, IBResourceModel m) {
        this(m,
            new DefaultPathAndChecksum(of(r),
                Paths
                    .get(URI.create(m.getPath().orElseThrow(() -> new IBResourceException("No [required] file path")))),
                new Checksum(m.getStreamChecksum())));
      }

      public RelativePathIBResource(PathRef r, JSONObject j) {
        this(r, IBResourceBuilder.modelFromJSON.apply(j).get()); // TODO Convert?
        if (!validate(true))
          throw new IBResourceException("Resource did not pass validation");
      }

      public RelativePathIBResource(PathRef r, Path path, Checksum checksum, Optional<String> type,
          Optional<Properties> addlProps)
      {
        this(new IBResourceModel(), new DefaultPathAndChecksum(of(r), path, checksum));
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

      public RelativePathIBResource(PathRef r, Path path, Checksum checksum, Optional<String> type) {
        this(r, path, checksum, type, empty());
      }

      @Override
      public OptStream get() {
        m.setMostRecentReadTime(now());
        return new OptStream(getPath().map(path -> {
          try {
            return Files.newInputStream(path,
                (path.getClass().getCanonicalName().contains("Zip")) ? ZIP_OPTIONS : OPTIONS);
          } catch (Throwable t) {
            log.error("Error opening " + path, t);
            return null;
          }
        }).orElse(null));
      }

      @Override
      public boolean validate(boolean hard) {
        return getPath().map(p -> {
          if (!Files.exists(p)) {
            log.warn("validation: File %p does not exist", p.toString());
            return false;
          }
          if (hard) {
            Checksum s = this.getTChecksum();
            Checksum n = new Checksum(p); // Calculate new checksum
            if (!s.equals(n)) {
              log.warn(format("validation: expected checksum %s != actual checksum %s", s, n));
              return false;
            }
            var actualType = IBResourceBuilderFactory.toOptionalType.apply(p)
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
