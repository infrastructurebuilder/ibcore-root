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

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIPathRef extends AbstractBasePathRef {
  public static final Logger log = LoggerFactory.getLogger(URIPathRef.class);

  public URIPathRef(String u) {
    this(cet.returns(() -> URI.create(u)));
  }

  public URIPathRef(URL u) {
    this(cet.returns(() -> requireNonNull(u).toURI()));
  }

  public URIPathRef(URI u) {
    super(requireNonNull(u));
  }

  @Override
  public Optional<Path> toResolvedPath(String p) {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public Optional<PathRef> extendAsPathRef(Path newPath) {
    return Optional.ofNullable(newPath).filter(p -> !p.isAbsolute()).map(Path::toString).map(this.getUri()::resolve)
        .map(URIPathRef::new);
  }

}
