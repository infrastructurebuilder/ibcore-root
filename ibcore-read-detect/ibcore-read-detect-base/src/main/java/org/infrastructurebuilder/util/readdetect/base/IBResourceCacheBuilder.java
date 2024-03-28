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

import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

public interface IBResourceCacheBuilder {
  // TODO this might need to be set automatically
  IBResourceCacheBuilder withModelVersion(String modelVersion);

  IBResourceCacheBuilder withRoot(String root);

  IBResourceCacheBuilder withName(String name);

  IBResourceCacheBuilder withDescription(String desc);

  IBResourceCacheBuilder withResources(List<IBResource> l);

  IBResourceCacheBuilder withMetadata(JSONObject j);

  Optional<IBResourceCache> build(boolean hard);

}
