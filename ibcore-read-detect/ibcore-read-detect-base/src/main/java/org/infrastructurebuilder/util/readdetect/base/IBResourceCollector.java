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

import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.json.JSONObject;

public interface IBResourceCollector {

  Optional<List<IBResource>> collectCachedIBResources( //
      boolean deleteExistingCacheIfPresent //
      , Optional<BasicCredentials> creds //
      , String sourceString //
      , Optional<Checksum> checksum //
      , Optional<String> type //
      , int retries //
      , int readTimeOut //
      , boolean skipCache //
      , boolean expandArchives //
      , Optional<JSONObject> rootMetadata //
      , Optional<JSONObject> metadataForExpandedArchive //
  );

}
