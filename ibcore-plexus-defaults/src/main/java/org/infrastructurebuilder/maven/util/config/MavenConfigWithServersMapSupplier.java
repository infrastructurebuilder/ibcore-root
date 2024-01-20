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
package org.infrastructurebuilder.maven.util.config;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.Properties;
import java.util.StringJoiner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.eclipse.sisu.Nullable;
import org.infrastructurebuilder.util.config.ConfigMapBuilderSupplier;
import org.json.JSONObject;

@Named(ConfigMapBuilderSupplier.MAVEN_WITH_SERVERS)
@Singleton
public class MavenConfigWithServersMapSupplier extends MavenConfigMapBuilderSupplier {

  @Inject
  public MavenConfigWithServersMapSupplier(final MavenProject mavenProject, final Settings settings,
      @Nullable MavenSession session, @Nullable MojoExecution execution)
  {
    super(mavenProject, session, execution);
    requireNonNull(settings);
    final StringJoiner sb = new StringJoiner(",");
    Properties p = new Properties();
    requireNonNull(settings).getServers().stream().forEach(server -> {
      sb.add(server.getId());
      final String preKey = ConfigMapBuilderSupplier.MAVEN_SETTINGS_SERVER_NAMESPACE + server.getId() + ".";
      ofNullable(server.getUsername()).ifPresent(v -> p.setProperty(preKey + "username", v));
      ofNullable(server.getPassword()).ifPresent(v -> p.setProperty(preKey + "password", v));
      ofNullable(server.getPassphrase()).ifPresent(v -> p.setProperty(preKey + "passphrase", v));
      ofNullable(server.getPrivateKey()).ifPresent(v -> p.setProperty(preKey + "privatekey", v));
    });

    ofNullable(sb.toString().length() > 0 ? sb.toString() : null)
        .ifPresent(v -> p.setProperty(ConfigMapBuilderSupplier.MAVEN_SETTINGS_SERVER_NAMESPACE + "servers", v));
    super.addASingle(new JSONObject(p));
//    super.get().withProperties(p);
  }

  @Override
  public String getName() {
    return ConfigMapBuilderSupplier.MAVEN_WITH_SERVERS;
  }

}
