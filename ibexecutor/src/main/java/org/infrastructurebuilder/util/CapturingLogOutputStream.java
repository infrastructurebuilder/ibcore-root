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

import static org.infrastructurebuilder.IBException.cet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import org.zeroturnaround.exec.stream.LogOutputStream;

public abstract class CapturingLogOutputStream extends LogOutputStream {

  private final Optional<BufferedWriter> os;
  private final Optional<Path> path;

  public CapturingLogOutputStream(final Optional<Path> p) {
    path = Objects.requireNonNull(p);
    p.ifPresent(path -> {
      cet.withTranslation(() -> {
        Files.createDirectories(path.getParent());
      });
    });
    os = cet.withReturningTranslation(() -> {
      return Optional.ofNullable(p.isPresent() ? Files.newBufferedWriter(p.get()) : null);
    });
  }

  @Override
  public void close() throws IOException {
    super.close();
    os.ifPresent(p -> {
      cet.withTranslation(() -> {
        p.close();
      });
    });
  }

  public Optional<Path> getPath() {
    return path;
  }

  abstract public void secondaryProcessLine(String line);

  @Override
  protected void processLine(final String line) {
    if (os.isPresent()) {
      cet.withTranslation(() -> {
        os.get().write(line);
      });
    }
    secondaryProcessLine(line);
  }
}
