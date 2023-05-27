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

import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultRepositoryRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.repository.RepositorySystem;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.maven.artifacts.InjectedResolver;
import org.infrastructurebuilder.util.maven.artifacts.ResolveOutcome;

@Named
public class DefaultInjectedResolver implements InjectedResolver {

  private final ArtifactRepository localRepository;

  private final RepositorySystem mavenRepositorySystem;

  private final List<ArtifactRepository> remoteArtifactRepos;

  @Inject
  public DefaultInjectedResolver(final ArtifactRepository localRepository, final RepositorySystem mavRepositorySystem,
      final List<ArtifactRepository> remoteArtifactRepositories)
  {
    this.localRepository = Objects.requireNonNull(localRepository);
    mavenRepositorySystem = Objects.requireNonNull(mavRepositorySystem);
    remoteArtifactRepos = Objects.requireNonNull(remoteArtifactRepositories);
  }

  @Override
  public final GAV fromArtifact(final Artifact a) {
    return new DefaultGAV(a.getGroupId(), a.getArtifactId(), a.getClassifier(), a.getVersion(), a.getType());
  }

  @Override
  public final GAV fromDependency(final Dependency a) {
    return new DefaultGAV(a.getGroupId(), a.getArtifactId(), a.getClassifier(), a.getVersion(), a.getType());
  }

  @Override
  public Artifact getArtifactFromDependency(final Dependency dep) {
    return mavenRepositorySystem.createArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(),
        Optional.ofNullable(dep.getScope()).orElse(JavaScopes.RUNTIME),
        Optional.ofNullable(dep.getType()).orElse("jar"));
  }

  @Override
  public Artifact getArtifactFromPlugin(final PluginDescriptor dep) {
    return mavenRepositorySystem.createArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(),
        JavaScopes.RUNTIME, "jar");
  }

  @Override
  public ResolveOutcome resolutionOutcomeFor(final Artifact artifact) {
    final ArtifactResolutionResult res = resolve(artifact);
    final List<URL> urls = res.getArtifacts().stream().map(a -> cet.returns(() -> a.getFile().toURI().toURL()))
        .collect(Collectors.toList());
    final URL origi = cet.returns(() -> res.getOriginatingArtifact().getFile().toURI().toURL());
    return new DefaultResolveOutcome(urls, res.getOriginatingArtifact(), origi);
  }

  @Override
  public ResolveOutcome resolutionOutcomeFor(final Dependency dep) {
    return resolutionOutcomeFor(getArtifactFromDependency(dep));
  }

  @Override
  public ResolveOutcome resolutionOutcomeFor(final PluginDescriptor p) {
    return resolutionOutcomeFor(getArtifactFromPlugin(p));
  }

  @Override
  public ArtifactResolutionResult resolve(final Artifact artifact) {
    return mavenRepositorySystem.resolve(new ArtifactResolutionRequest(
        new DefaultRepositoryRequest().setRemoteRepositories(remoteArtifactRepos).setLocalRepository(localRepository))
        .setArtifact(artifact).setResolveTransitively(true));
  }

}
