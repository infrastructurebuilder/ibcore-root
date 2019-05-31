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

import static java.util.Optional.ofNullable;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.settings.Settings;
import org.infrastructurebuilder.util.ServerProxy;
import org.infrastructurebuilder.util.ServerProxyListSupplier;

@Named
public class DefaultServerProxyListSupplier implements ServerProxyListSupplier {

  private final List<ServerProxy> servers;

  @Inject
  public DefaultServerProxyListSupplier(final Settings settings) {
    servers = settings.getServers().stream()
        .map(s -> new ServerProxy(s.getId(), ofNullable(s.getUsername()), ofNullable(s.getPassword()),
            ofNullable(s.getPassphrase()), ofNullable(s.getPrivateKey()).map(Paths::get),
            ofNullable(s.getFilePermissions()), ofNullable(s.getDirectoryPermissions()), Optional.empty()))
        .collect(Collectors.toList());
  }

  @Override
  public List<ServerProxy> get() {
    return servers;
  }

}
