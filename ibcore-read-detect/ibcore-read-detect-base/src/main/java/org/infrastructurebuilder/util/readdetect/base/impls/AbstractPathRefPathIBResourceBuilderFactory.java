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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.objectmapper.jackson.ObjectMapperUtils;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.ChecksumBuilder;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.pathref.fs.PathRefPathIF;
import org.infrastructurebuilder.pathref.fs.attribute.PathRefFileAttributeView;
import org.infrastructurebuilder.pathref.fs.attribute.PathRefFileAttributes;
import org.infrastructurebuilder.pathref.util.ibpathref.metadata.model.v1_0.IBMetadataModel;
import org.infrastructurebuilder.pathref.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.readdetect.api.IBResource;
import org.infrastructurebuilder.util.readdetect.api.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.api.IBResourceException;
import org.infrastructurebuilder.util.readdetect.base.AbstractIBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.AbstractIBResourceBuilderFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

abstract public class AbstractPathRefPathIBResourceBuilderFactory
    extends AbstractIBResourceBuilderFactory<PathRefPath> {
  private final static Logger log = LoggerFactory.getLogger(AbstractPathRefPathIBResourceBuilderFactory.class);
  private static final long serialVersionUID = -8847933754124713375L;

//  public AbstractPathRefPathIBResourceBuilderFactory() {
//    this(null);
//  }

  public AbstractPathRefPathIBResourceBuilderFactory(PathRefFileSystem root) {
    super(root);
  }

  public Supplier<? extends AbstractPathIBResourceBuilder> fromPath(PathRefPath p) {
    return () -> (AbstractPathIBResourceBuilder) getBuilder().get().accept(() -> p);
  }

  abstract public Supplier<? extends AbstractPathIBResourceBuilder> getBuilder();

  @Override
  protected Optional<PathRefPath> extractFromJSON(JSONObject json) {
    return ofNullable(requireNonNull(json).optString("path")).map(URI::create).map(uri -> (PathRefPath) Paths.get(uri));
  }

  @Override
  protected Optional<PathRefPath> extractFromModel(IBResourceModel model) {
    return PathRefPath.fromUri(URI.create(requireNonNull(model).getStreamSource()));
  }

  abstract public static class AbstractPathIBResourceBuilder extends AbstractIBResourceBuilder<PathRefPath> {
    private final static Logger log = LoggerFactory.getLogger(AbstractPathIBResourceBuilder.class);

    protected PathRefPath path = null;

    public AbstractPathIBResourceBuilder() {
      this(null);
    }

    public AbstractPathIBResourceBuilder(PathRefFileSystem root) {
      super(root);
    }

    protected Optional<PathRefPath> getPathAndChecksum() {
      return ofNullable(this.path);
    }

    @Override
    public Optional<IBResourceBuilder<PathRefPath>> validate(boolean hard) {
      this.sourcePath = requireNonNull(path);
      return super.validate(hard).map(builder -> {
        var op = requireNonNull(path);
        // Attributes might not exist... :(
        PathRefFileAttributes attr;
        try {

          attr = Files.getFileAttributeView(op, PathRefFileAttributeView.class).readAttributes();

          builder

              .withFileAttributes(attr)

              .withAcquired(Instant.now())

              .withName(op.getFileName().toString());
        } catch (IOException e) {
          log.error("Error validating {}", this.sourcePath, e);
          return null;
        }
        return builder;

      });
    }

    abstract public boolean acceptable(PathRefPath p);

    @Override
    public IBResourceBuilder<PathRefPath> accept(Supplier<Path> path) {
      Optional<String> fsKey = getConfig().map(c -> c.optString("fsKey", null));
      PathRefPath supplied = PathRefPathIF//
          .fromPath(Objects.requireNonNull(path).get(), Optional.empty())//
          .orElse(null);
      if (supplied != null) {
        this.path = (acceptable(supplied)) ? supplied : null;
        if (this.path == null) {
          log.warn("Supplied.Path.No.PandC|{}", supplied);
        }
      } else
        log.warn("Supplied.Path.Null");
      return this;
    }
  }

  abstract public static class AbstractIBResource implements IBResource {
    private final static Logger log = LoggerFactory.getLogger(AbstractIBResource.class);
    protected final IBResourceModel m;

    protected final Path path;

    private Checksum byteStreamChecksum = null;

    public AbstractIBResource(IBResourceModel model, PathRefPath p, Checksum c) {
      this.m = model;
      this.byteStreamChecksum = Optional.ofNullable(c).orElseGet(() -> new Checksum(p));
      this.path = requireNonNull(p);
      log.info("AbstractIBResource with " + this.path.getRoot() + " " + this.m);
    }

    public AbstractIBResource(IBResourceModel model, PathRefPath p) {
      this(model, p, null);
    }

    public AbstractIBResource(PathRefPath p) {
      this(new IBResourceModel(), p);
    }

    @Override
    public Checksum asChecksum() {
      return getChecksumBuilder() //
          .map(ChecksumBuilder::asChecksum) //
          .orElse(null);
    }

    @Override
    public Checksum getByteStreamChecksum() {
      return this.byteStreamChecksum;
    }

    @Override
    public Checksum getChecksum() {
      if (this.byteStreamChecksum == null)
        this.byteStreamChecksum = asChecksum();
      return this.byteStreamChecksum;
    }

    @Override
    public PathRefFileSystem getRelativeRoot() {
      return getPath() //
          .map(PathRefPath::getFileSystem) //
          .orElseThrow(() -> new IBResourceException("No path"));
    }

    @Override
    public String getType() {
      // The only way type would be null is if someone setType(null)
      if (m.getStreamType() == null) {
        m.setStreamType(getPath().map(path -> {
          String ret = null;
          try {
            ret = Files.getFileAttributeView(path, PathRefFileAttributeView.class).readAttributes().type().get();
          } catch (IOException e) {
            log.error("Error getting type", e);
          }
          return ret;
        }).orElse(APPLICATION_OCTET_STREAM));
      }
      return ofNullable(m.getStreamType()).orElse(APPLICATION_OCTET_STREAM);
    }

    @Override
    public URI getSourceURI() {
      return Optional.ofNullable(m.getStreamSource()).map(u -> IBUtils.translateToWorkableArchiveURI(u))
          .orElseThrow(() -> new IBResourceException("No source URI"));
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
    public Optional<String> getGroup() {
      return this.m.getGroup();
    }

    @Override
    public Optional<String> getOwner() {
      return this.m.getOwner();
    }

    @Override
    public Optional<String> getPermissionsAsString() {
      return this.m.getPermissions();
    }

    @Override
    public Optional<Long> size() {
      try {
        var v = this.getFileAttributes();
        return v.map(s -> {
          this.m.setStreamSize(s.size());
          return s.size();
        });
      } catch (Throwable t) {
        return Optional.empty();
      }
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
      this.byteStreamChecksum = null;
    }

    protected void setDescription(String desc) {
      this.m.setDescription(desc);
      this.byteStreamChecksum = null;
    }

    public void setSource(String source) {
      this.m.setStreamSource(requireNonNull(source));
      this.byteStreamChecksum = null;
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
    public Optional asPlexusIOResource() {
      return Optional.of(new IBURLPlexusIOResource(this));
    }

  }
}
