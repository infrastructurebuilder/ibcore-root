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
package org.infrastructurebuilder.util.maven.artifacts.impl;

import static java.util.Optional.ofNullable;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.settings.Settings;
import org.infrastructurebuilder.util.settings.ServerProxy;
import org.infrastructurebuilder.util.settings.ServerProxyMap;

@Named
public class DefaultServerProxyListSupplier implements ServerProxyMap {

  private final Map<String, ServerProxy> servers;

  @Inject
  public DefaultServerProxyListSupplier(final Settings settings) {
    servers = settings.getServers().stream()
        .map(s -> new ServerProxy(s.getId(), ofNullable(s.getUsername()), ofNullable(s.getPassword()),
            ofNullable(s.getPassphrase()), ofNullable(s.getPrivateKey()).map(Paths::get),
            ofNullable(s.getFilePermissions()), ofNullable(s.getDirectoryPermissions()), Optional.empty()))
        .collect(Collectors.toMap(k -> k.getId(), Function.identity()));
  }

  @Override
  public Set<String> getServerIds() {
    return servers.keySet();
  }

  @Override
  public Optional<ServerProxy> getServer(String id) {
    return Optional.ofNullable(servers.get(id));
  }

}
