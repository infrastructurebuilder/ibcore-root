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
package org.infrastructurebuilder.util.relativeroot.classpath;

import static java.util.Optional.ofNullable;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

import javax.inject.Named;

import org.infrastructurebuilder.util.core.AbstractBaseRelativeRoot;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.RelativeRootSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(RelativeRootClasspathSupplier.NAME)
public class RelativeRootClasspathSupplier implements RelativeRootSupplier {
  private static final Logger log = LoggerFactory.getLogger(RelativeRootClasspathSupplier.class);

  public static final String NAME = "classpath:/";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Optional<RelativeRoot> getRelativeRoot() {
    return Optional.of(new AbstractBaseRelativeRoot(NAME) {
      @Override
      public RelativeRoot extendAsNewRoot(Path newPath) {
        return this; // "extend" doesn't work for classpath types
      }

      @Override
      public Optional<InputStream> getInputStreamFromExtendedPath(String path) {
        return ofNullable(this.getClass().getResourceAsStream(path)); // TODO is this OK?
      }
    });

  }
}
