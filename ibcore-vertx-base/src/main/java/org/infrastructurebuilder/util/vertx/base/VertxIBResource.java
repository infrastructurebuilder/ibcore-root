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
package org.infrastructurebuilder.util.vertx.base;

import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.util.readdetect.IBResource;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

public interface VertxIBResource extends IBResource<Future<AsyncFile>> {
  public static final OpenOptions OPTIONS = new OpenOptions().setRead(true);

  Vertx vertx();

  default Optional<Future<AsyncFile>> get() {
    return getPath() // Optional?
        .map(Path::toAbsolutePath) // Maybe we do this?
        .map(Path::toString) // have to do this
        .map(pStr -> vertx().fileSystem().open(pStr, OPTIONS));
  }

}
