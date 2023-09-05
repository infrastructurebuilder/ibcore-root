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
package org.infrastructurebuilder.util.readdetect.impl;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.util.constants.IBConstants.UTF_8;

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

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.IBResourceCacheFactory;
import org.infrastructurebuilder.util.readdetect.IBResourceException;
import org.infrastructurebuilder.util.readdetect.model.IBResourceCache;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class IBResourceCacheFactoryImpl implements IBResourceCacheFactory {
  private final static Logger log = LoggerFactory.getLogger(IBResourceCacheFactoryImpl.class);

  private RelativeRoot root;
  private final IBResourceCache cache;

  private Supplier<IBResourceBuilder> builder;

  @Inject
  public IBResourceCacheFactoryImpl(PathSupplier relRootSupplier) {
    this.root = ofNullable(relRootSupplier.get()).map(path -> RelativeRoot.from(path)).orElse(null);
    // Delivers a new builder from the relative root each time
    this.builder = () -> new IBResourceBuilderImpl();
    log.debug("Root is {}", this.root);
    this.cache = new IBResourceCache();

    cache.setModelEncoding(UTF_8);
    cache.setRoot(this.root.getPath().map(Path::toAbsolutePath).map(Path::toString).orElse(null));
  }

  public IBResourceCacheFactoryImpl() {
    this(() -> null); // No cache
  }

  public IBResourceCacheFactoryImpl(Path p) {
    this(() -> p);
  }

  @Override
  public final Optional<RelativeRoot> getRoot() {
    return ofNullable(this.root);
  }

  @Override
  public Optional<IBResource> fromPath(Path p, String type) {
    log.debug("Getting stream from {}", p);
    var r = getRoot().map(rr -> cacheIt(p, type)).orElseGet(() -> readIt(p, type));
    r.ifPresent(rr -> this.cache.addResource(rr.copyModel()));
    return r;
  }

  private Optional<IBResource> readIt(Path p, String type) {
    Optional<Checksum> c = Checksum.ofPath.apply(p);
    return Optional.ofNullable(c.map(checksum -> {
      var m = builderFromPathAndChecksum(p, checksum);
      Optional.ofNullable(type).ifPresent(t -> m.withType(t));
      return m.build();
    }).orElse(null));
  }

  private Optional<IBResource> cacheIt(Path p, String type) {
    var rootPath = getRoot().flatMap(RelativeRoot::getPath);

    if (rootPath.isEmpty()) {
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
      Optional.ofNullable(type).ifPresent(t -> builder.withType(t));
      return Optional.of(builder.build());
    } catch (IOException | NoSuchAlgorithmException e) {
      return empty();
    }
  }

  @Override
  public Optional<IBResource> fromURLLike(String u) {
    // TODO Auto-generated method stub
    return empty();
  }

  @Override
  public Optional<IBResource> fromJSON(JSONObject json) {
    return Optional.of(this.builder.get().fromJSON(json).build());
  }

  @Override
  public Optional<IBResource> fromURLLike(String u, String type) {
    // TODO Auto-generated method stub
    return empty();
  }

  // Package private
  @Override
  public IBResourceBuilder builderFromPathAndChecksum(Path p, Checksum checksum) {
    // We have a checksum, so we can read it, etc.
    Optional<BasicFileAttributes> bfa = IBResourceCacheFactory.getAttributes.apply(p);
    var m = this.builder.get()

        .from(p) // sets filepath and name (and source?)

        .withChecksum(checksum)

        .withType(IBResourceCacheFactory.toOptionalType.apply(p).orElse(APPLICATION_OCTET_STREAM))

        .withCreateDate(bfa.map(attrib -> attrib.creationTime()).map(FileTime::toInstant).orElse(null))

        .withLastUpdated(bfa.map(attrib -> attrib.lastModifiedTime()).map(FileTime::toInstant).orElse(null))

        .withSize(bfa.map(attrib -> attrib.size()).orElse(-1L))

        .withMostRecentAccess(bfa.map(attrib -> attrib.lastAccessTime()).map(FileTime::toInstant).orElse(null))

    ;

    return m;
  }

  @Override
  public Optional<IBResource> fromModel(IBResourceModel model) {
//    var m = Objects.requireNonNull(model);
    // TODO
    return empty();

  }

}
