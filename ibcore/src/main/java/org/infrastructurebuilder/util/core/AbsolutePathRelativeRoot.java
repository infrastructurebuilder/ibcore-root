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
package org.infrastructurebuilder.util.core;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import org.infrastructurebuilder.exceptions.IBException;

public class AbsolutePathRelativeRoot extends AbstractBaseRelativeRoot {

  public AbsolutePathRelativeRoot(Path p) {
    super(requireNonNull(checkAbsolute(p))
        .map(path -> IBException.cet.returns(() -> path.toUri().toURL().toExternalForm()))
        .orElseThrow(() -> new IBException("No path provided")));
  }
  
  @Override
  public RelativeRoot extend(String newPath) {
    return new AbsolutePathRelativeRoot(getPath().get().resolve(newPath));
  }

}
