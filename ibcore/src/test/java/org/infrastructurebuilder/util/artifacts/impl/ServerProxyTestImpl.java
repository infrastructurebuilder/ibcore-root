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

import java.nio.file.Path;
import java.util.Optional;

import org.infrastructurebuilder.util.BasicCredentials;
import org.infrastructurebuilder.util.DefaultBasicCredentials;
import org.infrastructurebuilder.util.ServerProxy;

public class ServerProxyTestImpl implements ServerProxy {

  public ServerProxyTestImpl() {

  }

  @Override
  public BasicCredentials getBasicCredentials() {
    return new DefaultBasicCredentials(getPrincipal().orElse(null), getSecret());
  }

  @Override
  public String getId() {

    return null;
  }

  @Override
  public Optional<Path> getKeyPath() {

    return null;
  }

  @Override
  public Optional<String> getPassphrase() {

    return null;
  }

  @Override
  public Optional<String> getPrincipal() {

    return null;
  }

  @Override
  public Optional<String> getSecret() {

    return null;
  }

  @Override
  public Optional<String> readKey() {

    return null;
  }

}
