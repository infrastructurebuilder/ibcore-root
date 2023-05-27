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
package org.infrastructurebuilder.util.vertx.base;

import java.util.Optional;

import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class DefaultVertxGAV extends DefaultGAV implements VertxGAV {

  private final static Logger log = LoggerFactory.getLogger(DefaultVertxGAV.class);

  public DefaultVertxGAV(JsonObject json, String classifier) {
    super(new JSONObject(json.encode()), classifier);
  }

  public DefaultVertxGAV(JsonObject json) {
    super(new JSONObject(json.encode()));
  }

  public DefaultVertxGAV(String groupId, String artifactId, String classifier, String version, String extension) {
    super(groupId, artifactId, classifier, version, extension);
  }

  public DefaultVertxGAV(String groupId, String artifactId, String version, String extension) {
    super(groupId, artifactId, version, extension);
  }

  public DefaultVertxGAV(String groupId, String artifactId, String version) {
    super(groupId, artifactId, version);
  }

  public DefaultVertxGAV(String from) {
    super(from);
  }

  @Override
  public JsonObject toJson() {
    return new JsonObject(asJSON().toString());
  }


}
