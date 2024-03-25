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
package org.infrastructurebuilder.util.readdetect.base;

import static org.carlspring.cloud.storage.s3fs.S3Factory.ACCESS_KEY;
import static org.carlspring.cloud.storage.s3fs.S3Factory.SECRET_KEY;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Provider;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.infrastructurebuilder.util.settings.ServerProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3FileSystem implements Supplier<Optional<FileSystem>>, Provider<Optional<FileSystem>> {
  private static final Logger logger = LoggerFactory.getLogger(S3FileSystem.class);
  private String accessKey;
  private String secretKey;
  private String uri = "s3://s3.amazonaws.com/"; // s3:///

  public final S3FileSystem withBasicCredentials(BasicCredentials b) {
    this.accessKey = Objects.requireNonNull(b).getKeyId();
    this.secretKey = b.getSecret().orElseThrow(() -> new IBException("No available secret for S3FileSystem"));
    return this;
  }

  public final S3FileSystem withServerProxy(ServerProxy p) {
    this.accessKey = Objects.requireNonNull(p).getPrincipal()
        .orElseThrow(() -> new IBException("No available access key as principal for S3FileSystem"));
    this.secretKey = p.getSecret()
        .orElseThrow(() -> new IBException("No available secret key as secret for S3FileSystem"));
    return this;
  }

  public Optional<FileSystem> get() {
    Map<String, String> env = new HashMap<>();
    env.put(ACCESS_KEY, accessKey);
    env.put(SECRET_KEY, secretKey);

    try {
      return Optional
          .of(FileSystems.newFileSystem(URI.create(this.uri), env, Thread.currentThread().getContextClassLoader()));
    } catch (IOException e) {
      logger.error("Error getting filesystem with ACCESSKEY " + accessKey, e);
      return Optional.empty();
    }

  }
}
