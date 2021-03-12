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

import static org.infrastructurebuilder.util.constants.IBConstants.MAVEN;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;

@Named(MAVEN)
@Singleton
public final class MavenGAVSupplier implements GAVSupplier {

  private final GAV gav;
  private final MavenProject mp;

  @Inject
  public MavenGAVSupplier(MavenProject p) {
    this.mp = Objects.requireNonNull(p);
    this.gav = new DefaultGAV(p.getGroupId(), p.getArtifactId(), p.getVersion());
  }

  @Override
  public GAV getGAV() {
    return gav;
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable(mp.getDescription());
  }

}
