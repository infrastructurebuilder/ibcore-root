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
package org.infrastructurebuilder.util.readdetect.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.Resource;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class FileIndexResource implements Resource {
  private final Path path;

  private final Path cacheDir;

  public FileIndexResource(final Path path, Path cacheDir) {
    this.path = path;
    this.cacheDir = cacheDir;
  }

  public Path getPath() {
    return path;
  }

  public Path getFullPath() {
    return cacheDir.resolve(path);
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return Files.newInputStream(getFullPath());
  }

  @Override
  public long length() {
    try {
      return Files.size(getFullPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void dispose() {
    // do nothing
  }
}
