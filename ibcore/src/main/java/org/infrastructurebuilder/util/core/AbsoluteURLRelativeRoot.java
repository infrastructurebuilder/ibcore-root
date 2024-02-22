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
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.exceptions.IBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsoluteURLRelativeRoot extends AbstractBaseRelativeRoot {
  public static final Logger log = LoggerFactory.getLogger(AbsoluteURLRelativeRoot.class);

  public AbsoluteURLRelativeRoot(String u) {
    this(cet.returns(() -> new URL(requireNonNull(u))));
  }

  public AbsoluteURLRelativeRoot(URL u) {
    super(requireNonNull(u).toExternalForm());
  }

  @Override
  public Optional<URL> getUrl() {
    return Optional.ofNullable(this.url);
  }

  @Override
  public RelativeRoot extendAsNewRoot(Path newPath) {
    if (requireNonNull(newPath).isAbsolute())
      throw new IBException("Extension " + newPath + " must be relative");
    return new AbsoluteURLRelativeRoot(toString().concat(newPath.toString()));
  }

  @Override
  public Optional<InputStream> getInputStreamFromExtendedPath(String path) {
    return getUrl().map(u -> {
      try {
        return u.openStream();
      } catch (IOException e) {
        log.error("Could not open " + this.url, e);
        return null;
      }
    });
  }

}
