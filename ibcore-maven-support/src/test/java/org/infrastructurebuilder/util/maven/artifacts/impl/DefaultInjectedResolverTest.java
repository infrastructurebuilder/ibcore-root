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

import static org.joor.Reflect.on;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.handler.manager.DefaultArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.repository.LocalArtifactRepository;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.repository.UserLocalArtifactRepository;
import org.apache.maven.repository.legacy.LegacyRepositorySystem;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.infrastructurebuilder.pathref.DefaultGAV;
import org.infrastructurebuilder.pathref.GAV;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultInjectedResolverTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
  }

  private ArtifactRepository remotes;
  private RepositorySystem repSystem;
  private LocalArtifactRepository local;
  private DefaultInjectedResolver dir;
  private Artifact a;
  private ArtifactHandler h;
  private GAV gav;
  private Dependency d;
  private PluginDescriptor dep;
  private ArtifactFactory af;
  private DefaultArtifactHandlerManager amd;
  private DefaultArtifactHandler ah;
  private DefaultPlexusContainer plexus;

  @BeforeEach
  void setUp() throws Exception {
//    var x = new DefaultContainerConfiguration();
//    plexus = new DefaultPlexusContainer(x);
    remotes = new MavenArtifactRepository();
    h = new DefaultArtifactHandler();
    local = new UserLocalArtifactRepository(remotes);
    repSystem = new LegacyRepositorySystem();
    af = new DefaultArtifactFactory();
    amd = new DefaultArtifactHandlerManager();
    ah = new DefaultArtifactHandler("jar");
    Map<String, ArtifactHandler> afm = Map.of(ah.getType(), ah);
    on(amd).set("artifactHandlers", afm);
    on(af).set("artifactHandlerManager", amd);
    on(repSystem).set("artifactFactory", af);
    on(repSystem).set("plexus", plexus);
    dir = new DefaultInjectedResolver(local, repSystem, List.of(remotes));
    gav = new DefaultGAV("a:b:1.0:jar");
    a = new DefaultArtifact(gav.getGroupId(), gav.getArtifactId(), gav.getVersion().get(), "compile",
        gav.getExtension().get(), null, h);
    d = new Dependency();
    d.setArtifactId(a.getArtifactId());
    d.setGroupId(a.getGroupId());
    d.setVersion(a.getVersion());
    d.setType(a.getType());
    d.setScope(a.getScope());
    d.setClassifier(a.getClassifier());
    dep = new PluginDescriptor();
    dep.setArtifactId(a.getArtifactId());
    dep.setGroupId(a.getGroupId());
    dep.setVersion(a.getVersion());

  }

  @Test
  void testFromArtifact() {
    assertEquals(gav, dir.fromArtifact(a));
  }

  @Test
  void testFromDependency() {
    assertEquals(gav, dir.fromDependency(d));
  }

  @Test
  void testGetArtifactFromDependency() {
    // TODO must set artifact factory in repSystem
    assertEquals(a, dir.getArtifactFromDependency(d));
  }

  @Test
  void testGetArtifactFromPlugin() {
    assertEquals(a, dir.getArtifactFromPlugin(dep));
  }

  @Test
  void testResolutionOutcomeForArtifact() {
//    var qq = dir.resolutionOutcomeFor(a);
//    assertNotNull(qq);
  }

  @Test
  void testResolutionOutcomeForDependency() {
  }

  @Test
  void testResolutionOutcomeForPluginDescriptor() {
  }

  @Test
  void testResolve() {
  }

}
