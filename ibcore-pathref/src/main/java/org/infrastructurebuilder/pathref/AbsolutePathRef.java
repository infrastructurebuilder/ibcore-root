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

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

import org.infrastructurebuilder.exceptions.IBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsolutePathRef extends URLPathRef {
  public static final String NOT_ABSOLUTE_PATH = "NotAbsolutePath";
  private static final Logger log = LoggerFactory.getLogger(AbsolutePathRef.class);
  private final static Function<Path, URL> toURL = (p) -> {
    log.info("toURL for " + Objects.requireNonNull(p));
    if (!p.isAbsolute()) {
      log.error("Failed to provide absolute path to AbsolutePathRef {}",p);
      throw new IBException(NOT_ABSOLUTE_PATH);
    }
    return cet.returns(() -> p.toUri().toURL());
  };

  public AbsolutePathRef(Path p) {
    super(toURL.apply(p));
  }

}
