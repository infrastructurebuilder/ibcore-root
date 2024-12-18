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
package org.infrastructurebuilder.util.vertx.base.impl;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.constants.IBConstants.NO_PATH_SUPPLIED;
import static org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory.extracted;
import static org.infrastructurebuilder.util.readdetect.base.IBResourceBuilderFactory.toType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.base.IBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.base.IBResourceException;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.vertx.base.VertxIBResource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class DefaultVertxIBResourceBuilder implements IBResourceBuilder<Future<VertxIBResource>> {

  private final static Logger log = LoggerFactory.getLogger(IBResourceBuilder.class);
  private IBResourceModel model = new IBResourceModel();
  private Checksum targetChecksum;
  private Path sourcePath;
  private Path finalRestingPath;
  private PathRef root;
  private final Vertx vertx;

  public DefaultVertxIBResourceBuilder(Vertx vertx, PathRef root) {
    this.vertx = requireNonNull(vertx);
    this.root = requireNonNull(root);
  }

//  private Optional<Path> getTargetDir() {
//    return root.getPath();
//  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> fromJSON(JSONObject j) {
    model = IBResourceBuilder.modelFromJSON.apply(j).get();
    Path p = model.getPath().map(extracted).orElseThrow(() -> new IBResourceException(NO_PATH_SUPPLIED));
    this.sourcePath = p;
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withChecksum(Checksum csum) {
    this.targetChecksum = requireNonNull(csum);
    this.model.setStreamChecksum(csum.toString());
    return this;
  }

  @Override
	public Optional<IBResource> build(boolean hard) {
		// TODO Auto-generated method stub
		throw new IBException("Not implemented");//return Optional.empty();
	}

//  @Override
//  public IBResourceBuilder<Future<VertxIBResource>> fromPathAndChecksum(PathAndChecksum path) {
//    this.sourcePath = requireNonNull(path).get();
//
//    return this
//
//        .withFilePath(path.toString())
//
//        .withName(this.sourcePath.getFileName().toString())
//
//        .withSource(this.sourcePath.toUri().toASCIIString());
//  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withFilePath(String path) {
    this.model.setPath(path);
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withAcquired(Instant acquired) {
    this.model.setAcquired(acquired);
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withName(String name) {
    this.model.setStreamName(requireNonNull(name));
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withDescription(String desc) {
    this.model.setDescription(requireNonNull(desc));
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withType(String type) {
    this.model.setStreamType(requireNonNull(type));
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withType(Optional<String> type) {
    return requireNonNull(type).map(t -> withType(t)).orElse(this);
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withMetadata(JSONObject p) {

    // TODO Auto-generated method stub
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withLastUpdated(Instant last) {
    this.model.setLastUpdate(requireNonNull(last));
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withSource(String source) {
    this.model.setStreamSource(requireNonNull(source));
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withCreateDate(Instant create) {
    this.model.setCreated(requireNonNull(create));
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withSize(long size) {
    this.model.setStreamSize(size);
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withMostRecentAccess(Instant access) {
    this.model.setMostRecentReadTime(requireNonNull(access));
    return this;
  }

  /**
   * validate checks the values provided so far and throws IBResourceException if anything is off. You can call validate
   * whenever you set any value and if it returns your data is still possibly OK
   *
   * @param hard if true, then assume nothing and re-validate the existence and checksums of the paths and sources
   * @throws IBResourceException if validation fails
   * @return this builder
   */
  @Override
  public Optional<IBResourceBuilder<Future<VertxIBResource>>> validate(boolean hard) {
    if (this.sourcePath != null) {
      if (!Files.exists(sourcePath)) {
        log.error("unreadable.path {}", this.sourcePath);
        return empty();
      }
      if (hard) {
        var aType = toType.apply(this.sourcePath);
        if (!this.model.getStreamType().equals(aType)) {
          log.error("Expected type {} does not equal actual type {}", this.model.getStreamType(), aType);
          return empty();
        }
        if (this.targetChecksum == null) {
          log.warn("target checksum not available.  Reading source path");
          this.targetChecksum = Checksum.ofPath.apply(this.sourcePath).get();
        }
      }
      if (!this.targetChecksum.equals(this.model.getStreamChecksum())) {
        log.error("Model checksum {} not equal to targeted checksum {}", this.model.getStreamChecksum(),
            this.targetChecksum);
        return empty();
      }
      if (this.model.getStreamType() == null) {
        log.warn("Type not available");
        this.model.setStreamType(toType.apply(this.sourcePath));
      }

      // There has been no source path set.
    } else {
      log.warn("No sourcePath set for resource");
      // TODO??
    }
    return Optional.of(this);
  }
////    getTargetDir().ifPresent(targetDir -> {
////      // there is a targetDir, so the file we're referencing should be in that
////      // directory
////      if (this.sourcePath != null)
////        if (!IBUtils.isParent(targetDir, this.sourcePath)) {
////          // We have a source, and a place it should be
////        }
////    });
//
//    if (this.sourcePath != null) {
//      if (!Files.exists(sourcePath))
//        throw new IBResourceException("unreadable.path");
//      if (this.targetChecksum == null) {
//        log.warn("Checksum not available");
//        this.targetChecksum = Checksum.ofPath.apply(this.sourcePath).get();
//      }
//      if (this.model.getType() == null) {
//        log.warn("Type not available");
//        this.model.setType(toType.apply(this.sourcePath));
//      }
//
//      // There has been no source path set.
//    } else {
//      log.warn("No sourcePath set for resource");
//      // TODO??
//    }
//    return this;
//  }

  public Future<VertxIBResource> buildFuture(boolean hard) {
    try {
      validate(hard);
      return Future.succeededFuture(new VertxDefaultIBResource(this.vertx, getRoot(), this.model, this.sourcePath));
    } catch (IBException e) {
      log.error("Error building IBResource");
      return Future.failedFuture("Error building IBResource");
    }
  }

  /**
   * For IMDeletedIBResource. Do not use for general construction of a resource. This method is not available outside of
   * this package.
   *
   * @param m model to replace existing model with
   * @return this builder.
   */
  IBResourceBuilder<Future<VertxIBResource>> fromModel(IBResourceModel m) {
    this.model = m;
    return this;
  }

  private IBResourceBuilder<Future<VertxIBResource>> movedTo(Path path) {
    this.finalRestingPath = path;
    return this;
  }

  public Optional<PathRef> getRoot() {
    return Optional.of(root);
  }

  public IBResourceBuilder<Future<VertxIBResource>> fromURL(String url) {
    // TODO Auto-generated method stub
    throw new IBException("unimplemented"); // return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> detectType() {
    // TODO Auto-generated method stub
    throw new IBException("unimplemented"); // return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> withBasicFileAttributes(BasicFileAttributes a) {
    // TODO Auto-generated method stub
    return this;
  }

  @Override
  public IBResourceBuilder<Future<VertxIBResource>> accept(Supplier<Future<VertxIBResource>> a) {
    // TODO Auto-generated method stub
    return null;
  }

}
