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

import java.net.Proxy;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

abstract public class AbstractBaseFileBackedPathRef extends AbstractBasePathRef {

  public AbstractBaseFileBackedPathRef(URI uri) {
    super(uri);
  }

  public AbstractBaseFileBackedPathRef(String u) {
    super(u);
  }

  public AbstractBaseFileBackedPathRef(String u, Proxy p) {
    super(u, p);
  }

  @Override
  public Optional<Path> toResolvedPath(String p) {
    return getPath().map(localPath -> {
      Path thePath = Optional.ofNullable(p).map(Path::of).orElse(Path.of("."));
      return localPath.resolve(thePath);
//      return Optional.ofNullable(p).flatMap(pStr -> {
//        Path v = null;
//        try {
//          Path resPath = Paths.get(pStr);
//          if (!resPath.isAbsolute())
//            v = thisPath.resolve(resPath);
//        } catch (Throwable t) {
//          // Do nothing
//          log.warn("{} not a path", pStr);
//        }
//        return ofNullable(v);
//      });
    });
  }

}
