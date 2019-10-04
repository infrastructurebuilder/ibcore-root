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
package org.infrastructurebuilder.util.config;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

@Named(MavenConfigWithServersMapSupplier.MAVEN_WITH_SERVERS)
public class MavenConfigWithServersMapSupplier extends MavenConfigMapSupplier {

  public static final String MAVEN_WITH_SERVERS = "maven-with-servers";
  public static final String MAVEN_SETTINGS_SERVER_NAMESPACE = "maven.settings.server.";

  @Inject
  public MavenConfigWithServersMapSupplier(final MavenProject mavenProject, final Settings settings) {
    super(mavenProject);
    final StringJoiner sb = new StringJoiner(",");
    final List<Server> servers = Objects.requireNonNull(settings).getServers();
    servers.stream().forEach(server -> {
      sb.add(server.getId());
      final String preKey = MAVEN_SETTINGS_SERVER_NAMESPACE + server.getId() + ".";
      Optional.ofNullable(server.getUsername()).ifPresent(v -> addValue(preKey + "username", v));
      Optional.ofNullable(server.getPassword()).ifPresent(v -> addValue(preKey + "password", v));
      Optional.ofNullable(server.getPassphrase()).ifPresent(v -> addValue(preKey + "passphrase", v));
      Optional.ofNullable(server.getPrivateKey()).ifPresent(v -> addValue(preKey + "privatekey", v));
    });
    Optional.ofNullable(sb.toString().length() > 0 ? sb.toString() : null)
        .ifPresent(v -> addValue(MAVEN_SETTINGS_SERVER_NAMESPACE + "servers", v));

  }

}
