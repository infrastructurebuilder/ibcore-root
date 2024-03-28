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

import static io.vertx.core.Future.failedFuture;
import static java.util.Optional.ofNullable;

import javax.annotation.Nullable;

import org.infrastructurebuilder.util.core.OptStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.file.AsyncFile;

public class FutureStream {

  public final static Logger log = LoggerFactory.getLogger(OptStream.class);
  private final Future<AsyncFile> stream;

  public FutureStream(@Nullable Future<AsyncFile> ins) {
    this.stream = ins;
  }

  public final Future<AsyncFile> getStream() {
    return ofNullable(this.stream).orElse(failedFuture("no.stream.available"));
  }

}
