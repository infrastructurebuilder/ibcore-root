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
package org.infrastructurebuilder.util.relativeroot.base;

import static java.util.Optional.ofNullable;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.util.core.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.util.core.AbstractBaseRelativeRoot;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.RelativeRootSupplier;
import org.slf4j.Logger;

abstract public class AbstractRelativeRootBasicPathPropertiesSupplier implements RelativeRootSupplier {

  @Override
  public Optional<RelativeRoot> getRelativeRoot() {
    return getProperty().flatMap(pStr -> {
      try {
        return AbstractBaseRelativeRoot.checkAbsolute(Paths.get(pStr)).map(ap -> new AbsolutePathRelativeRoot(ap));
      } catch (Throwable t) {
        getLog().warn("No RR created due to path failure of {}", pStr);
        return Optional.empty();
      }
    });
  }

  /**
   * Override for different property name
   *
   * @return
   */
  public String getPropertyName() {
    return getName();
  }

  abstract protected Logger getLog();

  public Optional<String> getProperty() {
    return ofNullable(getProperties().getProperty(getPropertyName()));
  }

  protected Properties getProperties() {
    return System.getProperties();
  }

}
