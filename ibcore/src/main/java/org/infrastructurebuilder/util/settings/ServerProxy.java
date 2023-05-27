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
package org.infrastructurebuilder.util.settings;

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.exceptions.IBException.cet;
import static org.infrastructurebuilder.util.core.IBUtils.readFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.util.core.Identified;
import org.infrastructurebuilder.util.core.JSONBuilder;
import org.infrastructurebuilder.util.core.JSONOutputEnabled;
import org.infrastructurebuilder.util.credentials.basic.BasicCredentials;
import org.infrastructurebuilder.util.credentials.basic.DefaultBasicCredentials;
import org.json.JSONObject;

public class ServerProxy implements Identified, JSONOutputEnabled, Supplier<BasicCredentials> {
  public static final String ID_STRING = "id";
  private final String id;
  private final Optional<Path> keyPath;
  private final Optional<String> passphrase;
  private final Optional<String> principal;
  private final Optional<String> secret;
  private final Optional<String> filePermissions;
  private final Optional<String> directoryPermissions;
  private final Optional<String> configurationAsWrittenXMLString;

  public ServerProxy() {
    this("default", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty());
  }

  public ServerProxy(final String id, final Optional<String> principal, final Optional<String> secret,
      final Optional<String> passphrase, final Optional<Path> keyPath, final Optional<String> filePerms,
      final Optional<String> directoryPerms, final Optional<String> configAsXML)
  {
    super();
    this.id = requireNonNull(id);
    this.principal = requireNonNull(principal);
    this.secret = requireNonNull(secret);
    this.passphrase = requireNonNull(passphrase);
    this.keyPath = requireNonNull(keyPath);
    this.filePermissions = requireNonNull(filePerms);
    this.directoryPermissions = requireNonNull(directoryPerms);
    this.configurationAsWrittenXMLString = requireNonNull(configAsXML);
  }

  public BasicCredentials getBasicCredentials() {
    return new DefaultBasicCredentials(principal.orElse(null), secret);
  }

  public String getId() {
    return id;
  }

  public Optional<Path> getKeyPath() {
    return keyPath.filter(Files::isReadable);
  }

  public Optional<String> getPassphrase() {
    return passphrase;
  }

  public Optional<String> getPrincipal() {
    return principal;
  }

  public Optional<String> getSecret() {
    return secret;
  }

  public Optional<String> readKey() {
    return getKeyPath().map(f -> cet.returns(() -> readFile(f)));
  }

  public Optional<String> getConfiguration() {
    return configurationAsWrittenXMLString;
  }

  public Optional<String> getDirectoryPermissions() {
    return directoryPermissions;
  }

  public Optional<String> getFilePermissions() {
    return filePermissions;
  }

  @Override
  public JSONObject asJSON() {
    return JSONBuilder.newInstance()
        // id
        .addString(ID_STRING, getId())
        //
        .addString("key", readKey())
        //
        .addString("principal", getPrincipal())
        //
        .addString("secret", getSecret())
        //
        .addString("configuration", getConfiguration())
        //
        .addString("directoryPermissions", getDirectoryPermissions())
        //
        .addString("filePermissions", getFilePermissions())
        // fin
        .asJSON();
  }

  @Override
  public BasicCredentials get() {
    return new DefaultBasicCredentials(getPrincipal().orElse(null), getSecret());
  }
}
