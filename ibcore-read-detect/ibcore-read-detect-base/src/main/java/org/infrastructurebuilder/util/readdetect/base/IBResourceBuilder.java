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
package org.infrastructurebuilder.util.readdetect.base;

import static java.util.Objects.requireNonNull;

import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
  final static AtomicReference<ObjectMapper> mapper = new AtomicReference<>();

  public static ObjectMapper getObjectMapper() {
    synchronized (mapper) {
      if (mapper.get() == null) {
        var o = new ObjectMapper();
        o.registerModule(new JavaTimeModule().enable(JavaTimeFeature.ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS))
            .registerModule(//
                new Jdk8Module() //
            );
        mapper.set(o);
      }
    }
    return mapper.get();
  }

  public final static Function<JSONObject, Optional<IBResourceModel>> modelFromJSON = (j) -> {
    requireNonNull(j);
    try {
      var o = getObjectMapper();
      IBResourceModel v = o.readValue(j.toString(), IBResourceModel.class);
      log.info("Read model:\n" + v.toString());
      return Optional.of(v);
    } catch (JsonProcessingException e) {
      log.error(String.format("Error processing JSON %s", j.toString()), e);
      // TODO Auto-generated catch block
      e.printStackTrace();
      return Optional.empty();
    }
//    model.setCreated(ofNullable(j.optString(CREATE_DATE, null)).map(Instant::parse).orElse(null));
//    model.setFileChecksum(j.getString(org.infrastructurebuilder.util.core.ChecksumEnabled.CHECKSUM));
//    model.setSize(j.getLong(SIZE));
//    model.setType(j.getString(MIME_TYPE));
//    model.setFilePath(j.optString(PATH, null));
//    model.setLastUpdate(ofNullable(j.optString(UPDATE_DATE, null)).map(Instant::parse).orElse(null));
//    model.setMostRecentReadTime(ofNullable(j.optString(MOST_RECENT_READ_TIME, null)).map(Instant::parse).orElse(null));
//    model.setName(j.optString(SOURCE_NAME, null));
//    model.setSource(j.optString(SOURCE_URL, null));
//    model.setDescription(j.optString(DESCRIPTION, null));
//    java.util.Optional.ofNullable(j.optJSONObject(ADDITIONAL_PROPERTIES)).ifPresent(jo -> {
//      jo.toMap().forEach((k, v) -> {
//        model.setAdditionalProperty(k, v.toString());
//      });
//    });
//    return model;
  };

  IBResourceBuilder<I> accept(Supplier<I> a);

  /**
   * Initialize a builder from a given JSONObject
   *
   * @param j
   * @return
   */
  IBResourceBuilder<I> fromJSON(JSONObject j);

//  /**
//   * Set a path that this builder will reference.
//   *
//   * @param path
//   * @return
//   */
//  default IBResourceBuilder<I> fromPath(Path path) {
//    return fromPathAndChecksum(new DefaultPathAndChecksum(path));
//  }
//
//  IBResourceBuilder<I> fromPathAndChecksum(PathAndChecksum p);
//
//  /**
//   * Set a URL or URL-like that this builder will reference.
//   *
//   * @param url
//   * @return
//   */
//  IBResourceBuilder<I> fromURL(String url);

  /**
   * Set the expected file checksum
   *
   * @param csum The expected checksum of the referenced stream
   * @return
   */
  IBResourceBuilder<I> withChecksum(Checksum csum);

  /**
   * Sets some file path of the origin stream.
   *
   * @param path
   * @return
   */
  IBResourceBuilder<I> withFilePath(String path);

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
  IBResourceBuilder<I> withSource(String source);

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

  IBResourceBuilder<I> withBasicFileAttributes(BasicFileAttributes a);

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
