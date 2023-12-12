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
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.constants.IBConstants.*;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;

public interface IBResourceBuilder<B> {
  public final static Function<JSONObject, IBResourceModel> modelFromJSON = (j) -> {
    requireNonNull(j);
    IBResourceModel model = new IBResourceModel();
    model.setCreated(ofNullable(j.optString(CREATE_DATE, null)).map(Instant::parse).orElse(null));
    model.setFileChecksum(j.getString(org.infrastructurebuilder.util.core.ChecksumEnabled.CHECKSUM));
    model.setSize(j.getLong(SIZE));
    model.setType(j.getString(MIME_TYPE));
    model.setFilePath(j.optString(PATH, null));
    model.setLastUpdate(ofNullable(j.optString(UPDATE_DATE, null)).map(Instant::parse).orElse(null));
    model.setMostRecentReadTime(ofNullable(j.optString(MOST_RECENT_READ_TIME, null)).map(Instant::parse).orElse(null));
    model.setName(j.optString(SOURCE_NAME, null));
    model.setSource(j.optString(SOURCE_URL, null));
    model.setDescription(j.optString(DESCRIPTION, null));
    java.util.Optional.ofNullable(j.optJSONObject(ADDITIONAL_PROPERTIES)).ifPresent(jo -> {
      jo.toMap().forEach((k, v) -> {
        model.setAdditionalProperty(k, v.toString());
      });
    });
    return model;
  };

  IBResourceBuilder<B> fromJSON(JSONObject j);

  IBResourceBuilder<B> withChecksum(Checksum csum);

  IBResourceBuilder<B> from(Path path);

  IBResourceBuilder<B> withFilePath(String path);

  IBResourceBuilder<B> cached(boolean cached);

  IBResourceBuilder<B> withName(String name);

  IBResourceBuilder<B> withDescription(String desc);

  IBResourceBuilder<B> withType(String type);

  IBResourceBuilder<B> withType(Optional<String> type);

  IBResourceBuilder<B> withAdditionalProperties(Properties p);

  IBResourceBuilder<B> withLastUpdated(Instant last);

  IBResourceBuilder<B> withSource(String source);

  IBResourceBuilder<B> withCreateDate(Instant create);

  IBResourceBuilder<B> withSize(long size);

  IBResourceBuilder<B> withMostRecentAccess(Instant access);

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
  Optional<IBResourceBuilder<B>> validate(boolean hard);

//  IBResourceBuilder<B> movedTo(Path path); // TODO Moved is for an IBResource

  /**
   * Performs a <code>validate(hard)</code> and then performs the build
   *
   * @param hard
   * @return
   */
  B build(boolean hard);

  default B build() {
    return build(false);
  }

}
