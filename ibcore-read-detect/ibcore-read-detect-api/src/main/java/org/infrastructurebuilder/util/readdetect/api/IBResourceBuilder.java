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
package org.infrastructurebuilder.util.readdetect.api;

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.objectmapper.jackson.ObjectMapperUtils.mapper;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;

import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.fs.attribute.PathRefFileAttributes;
import org.infrastructurebuilder.pathref.metadata.model.v0_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * An IBResourceBuilder is a type of object that allows for the setting of the underlying IBResourceModel's values
 * during IBResource creation.
 *
 * The values of the underlying model, as a rule, are not canonical. However, they are the values that ultimately get
 * used to indicate values about any given IBResource instance, once constructed.
 *
 * References to 'origin stream' are referring to the underlying byte stream that the IBResource points to.
 *
 * @param
 */
public interface IBResourceBuilder<I> {
  static final Logger log = LoggerFactory.getLogger(IBResourceBuilder.class);

  public final static Function<JSONObject, Optional<IBResourceModel>> modelFromJSON = (j) -> {
    requireNonNull(j);
    try {
      var o = mapper.get();
      IBResourceModel v = o.readValue(j.toString(), IBResourceModel.class);
      log.info("Read model:\n" + v.toString());
      return Optional.of(v);
    } catch (JsonProcessingException e) {
      log.error(String.format("Error processing JSON %s", j.toString()), e);
      // TODO Auto-generated catch block
      e.printStackTrace();
      return Optional.empty();
    }
  };

  IBResourceBuilder<I> accept(Supplier<Path> a);

  /**
   * Initialize a builder from a given JSONObject
   *
   * @param j
   * @return
   */
  IBResourceBuilder<I> fromJSON(JSONObject j);

  /**
   * Set the expected file checksum
   *
   * @param csum The expected checksum of the referenced stream
   * @return
   */
  IBResourceBuilder<I> withChecksum(Checksum csum);

  /**
   * Set the expected file checksum
   *
   * @param csum The expected checksum of the referenced stream
   * @return
   */
  IBResourceBuilder<I> withChecksumSupplier(Supplier<Checksum> csum);

  /**
   * Set the model to indicate that this is cached.
   *
   * Note that if the value is <i>not</i> cached, whatever that means to this particular resource, then validation
   * should fail.
   *
   * @param cached
   * @return
   */
  IBResourceBuilder<I> withAcquired(Instant acquired);

  /**
   * Sets the expected model name. This is, most often, the origin stream's filename
   *
   * @param name
   * @return
   */
  IBResourceBuilder<I> withName(String name);

  /**
   * Assigns a description to this resource
   *
   * @param desc
   * @return
   */
  IBResourceBuilder<I> withDescription(String desc);

  /**
   * Assigns an expected type to this resource
   *
   * @param type
   * @return
   */
  IBResourceBuilder<I> withType(String type);

  /**
   * Assigns an expected type to this resource
   *
   * @param type
   * @return
   */
  IBResourceBuilder<I> withTypeSupplier(Supplier<String> type);

  /**
   * Detect the type using Tika.
   *
   * @return
   */
  IBResourceBuilder<I> detectType();

  /**
   * Sets an expected type, if present, otherwise deetect type
   *
   * @param type
   * @return
   */
  default IBResourceBuilder<I> withType(Optional<String> type) {
    return requireNonNull(type) //
        .map(t -> withType(t)) //
        .orElseGet(() -> this.detectType());
  }

  /**
   * Sets "additional properties". This is additional metadata to allow more granular queries against resources
   *
   * FIXME This should be JSONObject or Map<String,Object>
   *
   * @param p
   * @return
   */
  IBResourceBuilder<I> withMetadata(JSONObject p);

  /**
   * Sets the date that the source was last updated
   *
   * @param last
   * @return
   */
  IBResourceBuilder<I> withLastUpdated(Instant last);

  /**
   * Sets the 'source' for this resource. This can be pretty much anything as long as there's an interpreter for what it
   * means.
   *
   * @param source
   * @return
   */
  IBResourceBuilder<I> withSource(URI source);

  /**
   * Sets the 'source' for this resource. This can be pretty much anything as long as there's an interpreter for what it
   * means.
   *
   * @param source
   * @return
   */
  default IBResourceBuilder<I> withSource(String source) {
    return withSource(URI.create(requireNonNull(source)));
  }

  /**
   * Sets the model create date for the underlying resource
   *
   * @param create
   * @return
   */
  IBResourceBuilder<I> withCreateDate(Instant create);

  /**
   * Sets the model original size of the origin stream
   *
   * @param size
   * @return
   */
  IBResourceBuilder<I> withSize(long size);

  /**
   * Sets the model most-recent value. At the builder's discretion, this can also be used to denote when the actual
   * resource stream was most-recently accessed.
   *
   * @param access
   * @return
   */
  IBResourceBuilder<I> withMostRecentAccess(Instant access);

  /**
   * NOTE: This may need to be rethought. If a later model needs some different method of handling this, then having a
   * default here could upset the balance of the Force.
   *
   * Attempts to use an instance of something extending BasicFileAttributes to set the model's attribute values
   *
   * @param a
   * @return
   */
  default IBResourceBuilder<I> withFileAttributes(BasicFileAttributes a) {
    if (a == null)
      return this;
//    if (a instanceof BasicFileAttributes bfa) {
    this.withCreateDate(a.creationTime().toInstant())

        .withSize(a.size())

        .withMostRecentAccess(a.lastAccessTime().toInstant())

        .withLastUpdated(a.lastModifiedTime().toInstant());
//    }
    if (a instanceof PosixFileAttributes pfa) {
      this

          .withGroup(pfa.group().getName())

          .withOwner(pfa.owner().getName())

          .withPermissions(PosixFilePermissions.toString(pfa.permissions()));

    }
    if (a instanceof PathRefFileAttributes prfa) {
      this

          .withChecksumSupplier(prfa.checksum())

          .withTypeSupplier(prfa.type())

      ;

    }
    return this;
  }

  IBResourceBuilder<I> withGroup(String groupName);

  IBResourceBuilder<I> withOwner(String ownerName);

  IBResourceBuilder<I> withPermissions(String perms);

  /**
   * validate checks the values provided so far and throws IBResourceException if anything is off.
   *
   * By contract, you should be able to call validate whenever you set any value and if it returns your data is still
   * possibly OK
   *
   * @param hard if true, then assume nothing and re-validate the existence and checksums of the paths and sources
   * @throws IBResourceException if validation fails
   * @return this builder
   */
  Optional<? extends IBResourceBuilder<I>> validate(boolean hard);

  /**
   * Performs a <code>validate(hard)</code> and then performs the build
   *
   * @param hard
   * @return
   */
  Optional<IBResource> build(boolean hard);

  default Optional<IBResource> build() {
    return build(false); // FixMe? Maybe there's no default?
  }

}
