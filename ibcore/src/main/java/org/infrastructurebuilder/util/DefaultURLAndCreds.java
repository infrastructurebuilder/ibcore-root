/**
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
 */
package org.infrastructurebuilder.util;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.infrastructurebuilder.util.config.ConfigMap;

/**
 * Generally speaking, IBJDBCQuery is the more "extended" version of {@link URLAndCreds} for queries
 *
 * @author mykel.alvis
 *
 */
public class DefaultURLAndCreds implements URLAndCreds {

  private final String url;
  private final Optional<String> query;

//  public DefaultURLAndCreds(ConfigMap cfg) {
//    this(requireNonNull(cfg).get(SOURCE_URL), ofNullable(cfg.getOrDefault(CREDS, null)));
//  }
  public DefaultURLAndCreds(String url) {
    this(url, empty());
  }
  public DefaultURLAndCreds(String url, Optional<String> creds) {
    this.url = requireNonNull(url, "Source URL");
    this.query = requireNonNull(creds);
  }

  @Override
  public String getUrl() {
    return this.url;
  }

  @Override
  public Optional<String> getCredentialsQuery() {
    return this.query;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DefaultURLAndCreds [url=").append(url).append(", query=").append(query).append("]");
    return builder.toString();
  }


}
