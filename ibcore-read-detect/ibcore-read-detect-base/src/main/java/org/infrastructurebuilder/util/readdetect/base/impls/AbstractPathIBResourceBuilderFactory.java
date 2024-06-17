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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_OCTET_STREAM;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.objectmapper.jackson.ObjectMapperUtils;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.RelativeRoot;
import org.infrastructurebuilder.util.core.DefaultPathAndChecksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.PathAndChecksum;
import org.infrastructurebuilder.util.readdetect.base.AbstractIBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.AbstractIBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

abstract public class AbstractPathIBResourceBuilderFactory extends AbstractIBResourceBuilderFactory<Path> {
  private final static Logger log = LoggerFactory.getLogger(AbstractPathIBResourceBuilderFactory.class);
  private static final long serialVersionUID = -8847933754124713375L;

  public AbstractPathIBResourceBuilderFactory() {
    this(null);
  }

  public AbstractPathIBResourceBuilderFactory(RelativeRoot root) {
    super(root);
  }

  public Supplier<? extends AbstractPathIBResourceBuilder> fromPath(Path p) {
    return () -> (AbstractPathIBResourceBuilder) getBuilder().get().accept(() -> p);
  }

//  public Optional<IBResourceBuilder<Path>> fromPath(Path p) {
//    if (p == null)
//      return Optional.empty();
//    if (!p.isAbsolute()) {
//      log.error("Path " + p + " is not absolute");
//      return Optional.empty();
//    }
//    return fromPathAndChecksum(new DefaultPathAndChecksum(p));
//  }

  abstract public Supplier<? extends AbstractPathIBResourceBuilder> getBuilder();

  @Override
  protected Optional<Path> extractFromJSON(JSONObject json) {
    return ofNullable(requireNonNull(json).optString("path")).map(Paths::get);
  }

  @Override
  protected Optional<Path> extractFromModel(IBResourceModel model) {
    return requireNonNull(model).getPath().map(Paths::get);
  }

  abstract public static class AbstractPathIBResourceBuilder extends AbstractIBResourceBuilder<Path> {
    private final static Logger log = LoggerFactory.getLogger(AbstractPathIBResourceBuilder.class);

    protected PathAndChecksum path = null;

    public AbstractPathIBResourceBuilder() {
      this(null);
    }

    public AbstractPathIBResourceBuilder(RelativeRoot root) {
      super(root);
    }

    protected Optional<PathAndChecksum> getPathAndChecksum() {
      return ofNullable(this.path);
    }

//    @Override
//    public Optional<IBResource> build(boolean hard) {
//      try {
//        validate(hard);
//        return Optional.of(new AbsolutePathIBResource(this.model, this.sourcePath));
//      } catch (IBException e) {
//        log.error("Error building IBResource", e);
//        return Optional.empty();
//      }
//    }

    @Override
    public Optional<IBResourceBuilder<Path>> validate(boolean hard) {
      this.sourcePath = requireNonNull(path.get());
      return super.validate(hard).map(builder -> {
        var op = requireNonNull(path.get());
        if (!op.isAbsolute()) {
          op = op.toAbsolutePath();
          log.warn("Path {} is not absolute.  Making absolute to {}", path, op);
        }

        // Attributes might not exist... :(
        path.getAttributes().ifPresent(attr -> {
          builder.withCreateDate(attr.creationTime().toInstant())

              .withLastUpdated(attr.lastModifiedTime().toInstant())

              .withMostRecentAccess(Instant.now())

              .withAcquired(Instant.now())

              .withSize(attr.size());
        });
        return builder

            .withFilePath(op.toString())

            .withChecksum(path.asChecksum())

            .withName(op.getFileName().toString())

            .withSource(op.toUri().toASCIIString())

        ;
      });
    }

    abstract public boolean acceptable(String p);

    @Override
    public IBResourceBuilder<Path> accept(Supplier<Path> path) {
      Optional<Path> supplied = ofNullable(path).map(Supplier::get);
      this.path = supplied.filter(p -> acceptable(p.toString()))
          .map(s -> new DefaultPathAndChecksum(getRoot(), s, null)).orElse(null); // mapped to a

      if (supplied.isPresent() && this.path == null) {
        log.warn("Supplied.Path.No.PandC|" + supplied.get().toString());
      }
      return this;
    }

//    /**
//     * The AbsolutePathIBResource is an <link>IBResource</link> that has the following propertie
//     * <ol>
//     * <li>The output must be backed by an <b><i>ABSOLUTE</i></b> <code>java.nio.file.Path</code></li>
//     * <li>It's <code>get()</code> method returns an <code>Optional InputStream</code>, which will probably be present
//     * based on the availability of the FileSystem that backs the Path.</li>
//     * <li>It's <code>RelativeRoot</code> instance <i>may</i> be null, allowing for no relative paths. This could affect
//     * any ability to persist the metadata.</li>
//     *
//     * </ol>
//     */
//    public static class AbsolutePathIBResource extends AbstractIBResource {
//      private final static Logger log = LoggerFactory.getLogger(AbsolutePathIBResource.class);
//
//      public AbsolutePathIBResource(IBResourceModel m, Path sourcePath) {
//        super(null, m);
//        this.m.setPath(IBResource.requireAbsolutePath(sourcePath).toString());
//      }
//
//      public AbsolutePathIBResource(IBResourceModel m) {
//        this(m, Paths.get(URI.create(m.getPath().orElseThrow(() -> new IBResourceException("No [required] file path")))));
//      }
//
//      public AbsolutePathIBResource(JSONObject j) {
//        super(null, IBResourceBuilder.modelFromJSON.apply(j).get()); // TODO Convert
//      }
//
//      public AbsolutePathIBResource(Path path, Checksum checksum, Optional<String> type, Optional<Properties> addlProps) {
//        this(new IBResourceModel());
//        m.setPath(requireNonNull(path).toAbsolutePath().toString());
//        m.setStreamChecksum(requireNonNull(checksum).toString());
//        IBUtils.getAttributes.apply(path).ifPresent(bfa -> {
//          this.m.setCreated(bfa.creationTime().toInstant());
//          this.m.setLastUpdate(bfa.lastModifiedTime().toInstant());
//          this.m.setMostRecentReadTime(bfa.lastAccessTime().toInstant());
//          this.m.setStreamSize(bfa.size());
//        });
//
//        requireNonNull(type).ifPresent(t -> m.setStreamType(t));
//      }
//
//      public AbsolutePathIBResource(Path path, Checksum checksum, Optional<String> type) {
//        this(path, checksum, type, empty());
//      }
//
//      @Override
//      public OptStream get() {
//        m.setMostRecentReadTime(now());
//        return new OptStream(getPath().map(path -> {
//          try {
//            return Files.newInputStream(path,
//                (path.getClass().getCanonicalName().contains("Zip")) ? ZIP_OPTIONS : OPTIONS);
//          } catch (Throwable t) {
//            log.error("Error opening " + path, t);
//            return null;
//          }
//        }).orElse(null));
//      }
//
//      @Override
//      public boolean validate(boolean hard) {
//        return getPath().map(p -> {
//          if (!Files.exists(p)) {
//            log.warn("validation: File %p does not exist", p.toString());
//            return false;
//          }
//          if (hard) {
//            Checksum s = this.getTChecksum();
//            Checksum n = new Checksum(p); // Calculate new checksum
//            if (!s.equals(n)) {
//              log.warn(format("validation: expected checksum %s != actual checksum %s", s, n));
//              return false;
//            }
//            var actualType = IBResourceBuilderFactory.toOptionalType.apply(p)
//                .orElse(IBConstants.APPLICATION_OCTET_STREAM);
//            if (!this.m.getStreamType().equals(actualType)) {
//              log.warn(format("validation: expected type %s != actual type %", this.m.getStreamType(), actualType));
//              return false;
//            }
//          }
//          return true;
//        }).orElseGet(() -> {
//          log.warn("validate: path not present in model");
//          return false;
//        });
//      }
//
//      @Override
//      public Checksum getTChecksum() {
//        return new Checksum(m.getStreamChecksum());
//      }
//
//    }
//
  }

  abstract public static class AbstractIBResource implements IBResource {
    private final static Logger log = LoggerFactory.getLogger(AbstractIBResource.class);
    protected final IBResourceModel m;

    protected final PathAndChecksum path;

    private Checksum checksum = null;

    public AbstractIBResource(IBResourceModel model, PathAndChecksum p) {
      this.m = model;
      this.path = requireNonNull(p);
      log.info("AbstractIBResource with " + this.path.getRoot() + " " + this.m);

    }

    public AbstractIBResource(PathAndChecksum p) {
      this(new IBResourceModel(), p);
    }

    public PathAndChecksum getPathAndChecksum() {
      return path;
    }

    @Override
    public Checksum getTChecksum() {
      return getPathAndChecksum().asChecksum();
    }

    @Override
    public Checksum getChecksum() {
      if (this.checksum == null)
        this.checksum = asChecksum();
      return this.checksum;
    }

    @Override
    public Optional<RelativeRoot> getRelativeRoot() {
      return this.path.getRoot();
    }

    @Override
    public String getType() {
      // The only way type would be null is if someone setType(null)
      if (m.getStreamType() == null) {
        getPath().ifPresent(path -> m.setStreamType(IBResourceBuilderFactory.toType.apply(path)));
      }
      return ofNullable(m.getStreamType()).orElse(APPLICATION_OCTET_STREAM);
    }

    @Override
    public Optional<URL> getSourceURL() {
      return ofNullable(m.getStreamSource()).map(u -> IBUtils.translateToWorkableArchiveURL(u));
    }

    @Override
    public Optional<String> getSourceName() {
      return ofNullable(m.getStreamName());
    }

    @Override
    public Optional<Instant> getMostRecentReadTime() {
      return this.m.getMostRecentReadTime();
    }

    @Override
    public Optional<Instant> getCreateDate() {
      return this.m.getCreated();
    }

    @Override
    public Optional<Instant> getAcquireDate() {
      return this.m.getAcquired();
    }

    @Override
    public Optional<Instant> getLastUpdateDate() {
      return this.m.getLastUpdate();
    }

    @Override
    public String getName() {
      return this.m.getStreamName();
    }

    @Override
    public Optional<String> getDescription() {
      return this.m.getDescription();
    }

    @Override
    public Optional<Long> size() {
//      return this.m.getStreamSize();
      var v = this.path.size();
      v.ifPresent(l -> this.m.setStreamSize(l));
      return v;
    }

    @Override
    public String getModelVersion() {
      return this.m.getModelVersion();
    }

    @Override
    public JSONObject getMetadata() {
      var mx = this.m.getMetadata().orElse(new IBMetadataModel());
      String x = "{}";
      try {
        x = ObjectMapperUtils.mapper.get().writeValueAsString(mx);
      } catch (JsonProcessingException | JSONException e) {
        log.error("Error with processing metadata" + x);
      }
      return new JSONObject(x);
    }

    protected void setName(String name) {
      this.m.setStreamName(name);
      this.checksum = null;
    }

    protected void setDescription(String desc) {
      this.m.setDescription(desc);
      this.checksum = null;
    }

    public void setSource(String source) {
      this.m.setStreamSource(requireNonNull(source));
      this.checksum = null;
    }

    @Override
    public IBResourceModel copyModel() {
      return new IBResourceModel(this.m);
    }

    @Override
    public int hashCode() {
      return IBResource.defaultHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
      return IBResource.defaultEquals(this, obj);
    }

    @Override
    public String toString() {
      return IBResource.defaultToString(this);
    }

    @Override
    public Optional<Path> getPath() {
      try {
        return ofNullable(getPathAndChecksum().get());
      } catch (Throwable e) {
        log.error("Failed to get path " + m.getPath());
        return Optional.empty();
      }
    }

  }

}
