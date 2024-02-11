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

public interface IBResourceCacheBuilder<B, T> {
  // TODO this might need to be set automatically
  IBResourceCacheBuilder<B, T> withModelVersion(String modelVersion);

  IBResourceCacheBuilder<B, T> withRoot(String root);

  IBResourceCacheBuilder<B, T> withName(String name);

  IBResourceCacheBuilder<B, T> withDescription(String desc);

  IBResourceCacheBuilder<B, T> withResources(List<IBResource<T>> l);

  IBResourceCacheBuilder<B, T> withMetadata(JSONObject j);

  B build(boolean hard);

}
