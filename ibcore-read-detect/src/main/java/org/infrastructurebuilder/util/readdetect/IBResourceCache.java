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

import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

public interface IBResourceCache<T> {

  /**
   * Declares to which version of descriptor this configuration conforms. This value is the api version (major.minor)
   * for transportable schemas and conforms to semantic versioning (Required)
   *
   */
  String getModelVersion();

  /**
   * A URL-like for the RelativeRoot of the cache. (Required)
   *
   */
  String getRoot();

  /**
   * Queryable name for the cache. (Required)
   *
   */
  String getName();

  /**
   * Description for the cache.
   *
   */
  Optional<String> getDescription();

  Optional<List<IBResource<T>>> getResources();

  Optional<JSONObject> getMetadata();

}
