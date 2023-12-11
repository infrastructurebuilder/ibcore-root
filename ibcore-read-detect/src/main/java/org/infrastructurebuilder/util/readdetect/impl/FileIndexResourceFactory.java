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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.InputLimit;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;

/**
 * Generates {@link Resource} instances whose body is stored in a temporary file.
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class FileIndexResourceFactory implements ResourceFactory {
  private final Path cacheDir;

  public FileIndexResourceFactory(final Path cacheDir) {
    super();
    this.cacheDir = cacheDir;
  }

  protected Path generateUniqueCachePath(final String uri) {
    String resourcePart = !uri.isEmpty() ? uri.substring(uri.lastIndexOf('/') + 1) : uri;
    resourcePart = resourcePart.isEmpty() ? resourcePart : resourcePart + "_";
    // append a unique string based on timestamp
    return Paths.get(resourcePart + DigestUtils.md5Hex(UUID.randomUUID().toString()));
  }

  @Override
  public Resource generate(final String requestId, final InputStream inStream, final InputLimit limit)
      throws IOException {
    if (!Files.exists(cacheDir)) {
      Files.createDirectories(cacheDir);
    }
    final Path cachedFile = generateUniqueCachePath(requestId);
    Files.copy(inStream, cacheDir.resolve(cachedFile), REPLACE_EXISTING);
    return new FileIndexResource(cachedFile, cacheDir);
  }

  @Override
  public Resource copy(final String requestId, final Resource resource) throws IOException {
    final Path dst = generateUniqueCachePath(requestId);

    if (resource instanceof FileIndexResource) {
      Files.copy(cacheDir.resolve(((FileIndexResource) resource).getPath()), cacheDir.resolve(dst), REPLACE_EXISTING);
    } else {
      try (InputStream is = resource.getInputStream()) {
        Files.copy(is, cacheDir.resolve(dst), REPLACE_EXISTING);
      }
    }
    return new FileIndexResource(dst, cacheDir);
  }

}
