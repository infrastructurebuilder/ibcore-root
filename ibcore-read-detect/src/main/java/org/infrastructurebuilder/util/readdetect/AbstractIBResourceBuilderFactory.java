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
package org.infrastructurebuilder.util.readdetect;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_OCTET_STREAM;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.core.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceCache;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractIBResourceBuilderFactory<B> extends IBResourceCache implements IBResourceBuilderFactory<B> {
  private static final long serialVersionUID = 1200177361527373141L;

  private final static Logger log = LoggerFactory.getLogger(AbstractIBResourceBuilderFactory.class);

  private final RelativeRoot _root;

  public AbstractIBResourceBuilderFactory(RelativeRoot relRoot) {
    super();
    this._root = requireNonNull(relRoot);
//    this.setModelEncoding(UTF_8);
    this.setRoot(this._root.getPath().map(Path::toAbsolutePath).map(Path::toString).orElse(null));
    log.debug("Root is {}", this.getRoot());
  }

  public AbstractIBResourceBuilderFactory(Path p) {
    this(new AbsolutePathRelativeRoot(p));
  }

  abstract protected Supplier<IBResourceBuilder<B>> getBuilder();

  @Override
  public final Optional<RelativeRoot> getRelativeRoot() {
    return ofNullable(this._root);
  }

  @Override
  public Optional<IBResourceBuilder<B>> fromPath(Path p, String type) {
    log.debug("Getting stream from {}", p);
    var r = getRelativeRoot()

        .map(rr -> cacheIt(p, type))

        .orElseGet(() -> readIt(p, type));
    return r;
  }

  private Optional<IBResourceBuilder<B>> readIt(Path p, String type) {
    log.info("Reading from {}", p);
    return Checksum.ofPath.apply(p).map(checksum -> {
      var m = builderFromPathAndChecksum(p, checksum);
      ofNullable(type).ifPresent(t -> m.withType(t));
      return m;
    });
  }

  private Optional<IBResourceBuilder<B>> cacheIt(Path p, String type) {
    log.info("Cacheing from {}", p);
    var rootPath = getRelativeRoot().flatMap(RelativeRoot::getPath);

    if (getRelativeRoot().isEmpty()) {
      log.error("no.root.path");
      return empty();
    }

    var r = rootPath.get();
    var f = IBResourceException.cet.returns(() -> Files.createTempFile(r, "temp", ".bin"));
    f.toFile().deleteOnExit(); // Deletes file if things fail
    try (
        // File to read
        InputStream ins = Files.newInputStream(p);
        // Temp file to write
        OutputStream out = Files.newOutputStream(f)) {
      // Copy the file and get it's Checksum at the same time. Slightly efficient.
      Checksum d = IBUtils.copyAndDigest(ins, out);

      Path landing = r.relativize(IBUtils.moveFileToNewIdPath(f, d));

      var builder = builderFromPathAndChecksum(p, d)

          .withFilePath(landing.toString())

          .cached(true);
      ofNullable(type).ifPresent(t -> builder.withType(t));
      return of(builder);
    } catch (IOException | NoSuchAlgorithmException e) {
      return empty();
    }
  }

  @Override
  public Optional<IBResourceBuilder<B>> fromJSON(JSONObject json) {
    return of(getBuilder().get().fromJSON(json));
  }

  @Override
  public Optional<IBResourceBuilder<B>> fromURLLike(String u, String type) {
    // TODO Auto-generated method stub
    return empty();
  }

  // Package private
  @Override
  public IBResourceBuilder<B> builderFromPathAndChecksum(Path p, Checksum checksum) {
    // We have a checksum, so we can read it, etc.
    Optional<BasicFileAttributes> bfa = IBResourceBuilderFactory.getAttributes.apply(p);
    var m = getBuilder().get();
    m = m.from(p) // sets filepath and name (and source?)

        .withChecksum(checksum)

        .withType(IBResourceBuilderFactory.toOptionalType.apply(p).orElse(APPLICATION_OCTET_STREAM))

        .withCreateDate(bfa.map(attrib -> attrib.creationTime()).map(FileTime::toInstant).orElse(null))

        .withLastUpdated(bfa.map(attrib -> attrib.lastModifiedTime()).map(FileTime::toInstant).orElse(null))

        .withSize(bfa.map(attrib -> attrib.size()).orElse(-1L))

        .withMostRecentAccess(bfa.map(attrib -> attrib.lastAccessTime()).map(FileTime::toInstant).orElse(null))

    ;

    return m;
  }

  @Override
  public Optional<IBResourceBuilder<B>> fromModel(IBResourceModel model) {
//    var m = Objects.requireNonNull(model);
    // TODO
    return empty();

  }

}
