/**
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
 */
package org.infrastructurebuilder.data;

import static org.infrastructurebuilder.data.IBDataException.cet;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.data.model.DataStream;
import org.infrastructurebuilder.util.files.TypeToExtensionMapper;

public class FakeIBDataStream extends DataStream implements IBDataStream {

  private final Path gotten;
  private final Optional<Exception> throwMe;

  public FakeIBDataStream(Path p, Optional<Exception> throwMe) {
    this.gotten = p;
    this.throwMe = throwMe;
  }

  @Override
  public InputStream get() {
    return cet.withReturningTranslation(() -> Files.newInputStream(gotten));
  }

  @Override
  public void close() throws Exception {
    if (throwMe.isPresent())
      throw throwMe.get();
  }

  @Override
  public IBDataStream relocateTo(Path newWorkingPath, TypeToExtensionMapper t2e) {
    Path p = newWorkingPath.resolve(UUID.randomUUID().toString() + t2e.getExtensionForType(getMimeType()));
    cet.withReturningTranslation(() -> Files.move(Paths.get(getPath()), p));
    this.setPath(p.toString());
    return this;
  }
}
