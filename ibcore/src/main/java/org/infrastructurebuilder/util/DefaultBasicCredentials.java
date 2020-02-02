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

import java.util.Objects;
import java.util.Optional;

public class DefaultBasicCredentials implements BasicCredentials {
  private final String key;
  private transient final Optional<String> secret;

  public DefaultBasicCredentials(final String principal, final Optional<String> password) {
    key = Objects.requireNonNull(principal);
    secret = Objects.requireNonNull(password);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final DefaultBasicCredentials other = (DefaultBasicCredentials) obj;
    if (!secret.equals(other.secret))
      return false;
    if (!key.equals(other.key))
      return false;
    return true;
  }

  @Override
  public String getKeyId() {
    return key;
  }

  @Override
  public Optional<String> getSecret() {
    return secret;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + secret.hashCode();
    result = prime * result + key.hashCode();
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DefaultBasicCredentials [key=").append(key).append(", secret=******]");
    return builder.toString();
  }




}
