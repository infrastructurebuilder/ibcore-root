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
package org.infrastructurebuilder.pathref;

import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

import org.infrastructurebuilder.exceptions.IBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsolutePathRelativeRoot extends AbsoluteURLRelativeRoot {
  private static final Logger log = LoggerFactory.getLogger(AbsolutePathRelativeRoot.class);
  private final static Function<Path, String> toURL = (p) -> {
    log.info("toURL for " + Objects.requireNonNull(p));
    return cet.returns(() -> p.toUri().toURL().toExternalForm());
  };

  public AbsolutePathRelativeRoot(Path p) {
    super(toURL.apply(checkAbsolute(p).orElseThrow(() -> new IBException("Path provided is not valid: " + p))));
  }

  @Override
  public RelativeRoot extendAsNewRoot(Path newPath) {
    // TODO Unsure about how this should actually be
    return new AbsolutePathRelativeRoot(
        getPath().map(p -> p.resolve(newPath)).orElseThrow(() -> new IBException("Cannot extend " + toString())));
  }

}
