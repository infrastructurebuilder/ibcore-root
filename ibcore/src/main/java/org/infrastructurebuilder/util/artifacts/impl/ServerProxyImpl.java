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
package org.infrastructurebuilder.util.artifacts.impl;

import static org.infrastructurebuilder.IBException.cet;
import static org.infrastructurebuilder.util.IBUtils.readFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.util.BasicCredentials;
import org.infrastructurebuilder.util.DefaultBasicCredentials;
import org.infrastructurebuilder.util.ServerProxy;

public class ServerProxyImpl implements ServerProxy {
  private final String id;
  private final Optional<Path> keyPath;
  private final Optional<String> passphrase;
  private final Optional<String> principal;
  private final Optional<String> secret;

  public ServerProxyImpl(final String id, final Optional<String> principal, final Optional<String> secret,
      final Optional<String> passphrase, final Optional<Path> keyPath) {
    super();
    this.id = Objects.requireNonNull(id);
    this.principal = Objects.requireNonNull(principal);
    this.secret = Objects.requireNonNull(secret);
    this.passphrase = Objects.requireNonNull(passphrase);
    this.keyPath = Objects.requireNonNull(keyPath);
  }

  @Override
  public BasicCredentials getBasicCredentials() {
    return new DefaultBasicCredentials(principal.orElse(null), secret);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Optional<Path> getKeyPath() {
    return keyPath.filter(Files::isReadable);
  }

  @Override
  public Optional<String> getPassphrase() {
    return passphrase;
  }

  @Override
  public Optional<String> getPrincipal() {
    return principal;
  }

  @Override
  public Optional<String> getSecret() {
    return secret;
  }

  @Override
  public Optional<String> readKey() {
    return getKeyPath().map(f -> cet.withReturningTranslation(() -> readFile(f)));
  }
}
