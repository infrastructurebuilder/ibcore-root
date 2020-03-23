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
package org.infrastructurebuilder.util;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.IBException.cet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.zeroturnaround.exec.stream.LogOutputStream;

public abstract class CapturingLogOutputStream extends LogOutputStream {

  private final Optional<BufferedWriter> os;
  private final Optional<Path>           path;
  private final boolean                  flushEveryLine;

  public CapturingLogOutputStream(final Optional<Path> p) {
    this(p, true);
  }

  public CapturingLogOutputStream(final Optional<Path> p, boolean flushEveryLine) {
    path = requireNonNull(p).map(Path::toAbsolutePath);
    path.ifPresent(path -> cet.withTranslation(() -> createDirectories(path.getParent())));
    os = cet.withReturningTranslation(() -> ofNullable(p.isPresent() ? newBufferedWriter(p.get()) : null));
    this.flushEveryLine = flushEveryLine;
  }

  @Override
  public void close() throws IOException {
    super.close();
    os.ifPresent(p -> cet.withTranslation(() -> p.close()));
  }

  public Optional<Path> getPath() {
    return path;
  }

  abstract public void secondaryProcessLine(String line);

  @Override
  protected void processLine(final String line) {
    os.ifPresent(g -> cet.withTranslation(() -> {
      g.write(line);
      if (this.flushEveryLine)
        g.flush();
    }));
    secondaryProcessLine(line);
  }
}
