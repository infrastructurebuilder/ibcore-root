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
package org.infrastructurebuilder.util.artifacts.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.artifacts.TargetPlatform;

public class DefaultTargetPlatform implements TargetPlatform {

  private final String os;
  private final String platformIdentifier;

  public DefaultTargetPlatform() {
    this(UUID.randomUUID().toString(), null);
  }

  public DefaultTargetPlatform(String string, String os) {
    this.platformIdentifier = Objects.requireNonNull(string);
    this.os = os;
  }

  @Override
  public String getPlatformIdentifier() {
    return this.platformIdentifier;
  }

  @Override
  public Optional<String> getOperatingSystem() {
    return Optional.ofNullable(this.os);
  }

  @Override
  public String toString() {
    return "DefaultTargetPlatform [platformIdentifier=" + platformIdentifier
        + (getOperatingSystem().map(o -> String.format(", os=%s", o)).orElse(""));
  }

  @Override
  public int hashCode() {
    int result = 31 + ((os == null) ? 0 : os.hashCode());
    result = 31 * result + platformIdentifier.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!TargetPlatform.class.isAssignableFrom(obj.getClass()))
      return false;
    TargetPlatform other = (TargetPlatform) obj;
    return Objects.equals(getPlatformIdentifier(), other.getPlatformIdentifier())
        && Objects.equals(getOperatingSystem(), other.getOperatingSystem());
  }

}
