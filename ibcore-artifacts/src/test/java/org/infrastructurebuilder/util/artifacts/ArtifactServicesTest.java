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
package org.infrastructurebuilder.util.artifacts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.GAV;
import org.infrastructurebuilder.util.impl.DefaultGAV;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArtifactServicesTest {
  private static class AS implements ArtifactServices {
    private final Collection<GAV> known;

    public AS(final Collection<GAV> knownArtifacts) {
      known = knownArtifacts;
    }

    @Override
    public GAV getArtifact(final GAV coords, final String scope) {

      return null;
    }

    @Override
    public List<GAV> getArtifacts(final GAV coords, final String scope, final boolean includeUnresolved) {
      return known.parallelStream().filter(a -> a.equalsIgnoreClassifier(coords, true)).collect(Collectors.toList());
    }

    @Override
    public String getClasspathOf(final GAV coords, final String scope, final boolean includeUnresolved) {
      return getDependencies(coords, scope).stream().map(f -> f.toAbsolutePath().toString())
          .collect(Collectors.joining(":"));
    }

    @Override
    public String getClasspathOf(final GAV coords, final String scope, final List<GAV> additional,
        final boolean eliminateEquivalentFiles) {
      final String cpRoot = getClasspathOf(coords, scope, false);
      final StringJoiner sj = new StringJoiner(":");
      final List<String> splits = Arrays.asList(cpRoot.split(":"));
      splits.forEach(i -> sj.add(i));
      for (final GAV gav : Objects.requireNonNull(additional)) {
        for (final String item : Arrays.asList(getClasspathOf(gav, scope, false).split(":"))) {
          if (!eliminateEquivalentFiles || !splits.contains(item)) {
            splits.add(item);
            sj.add(item);
          }
        }
      }
      return sj.toString();
    }

    @Override
    public List<Path> getDependenciesOfClassifiedTypeFor(final GAV coords, final String scope, final String classifier,
        final String type, final boolean throwOnFail) {

      return getArtifacts(coords, scope, true).stream().filter(a -> a.getFile().isPresent())
          .filter(a -> a.equalsIgnoreClassifier(coords, false)).map(a -> a.getFile().get())
          .collect(Collectors.toList());
    }

    @Override
    public Path getLocalRepo() {
      return FileSystems.getDefault().getRootDirectories().iterator().next();
    }

    @Override
    public Set<GAV> getResolvedDeployables(final GAV coords, final String scope, final boolean includeUnresolved,
        final String classifier, final String type) {
      return getArtifacts(coords, scope, includeUnresolved).stream().collect(Collectors.toSet());
    }

    @Override
    public Optional<URL> getRemoteRepo() {
      return Optional.of(ArtifactServices.CENTRAL_REPO_URL);
    }

  }

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  private ArtifactServices as;

  @BeforeEach
  public void setUp() throws Exception {
    as = new AS(Arrays.asList(new DefaultGAV("junit", "junit", "", "4.0", "jar").withFile(Paths.get("./junit1.jar")),
        new DefaultGAV("junit", "junitx", "", "4.0", "jar").withFile(Paths.get("./junit2.jar")),
        new DefaultGAV("junit", "junity", "", "4.0", "jar").withFile(Paths.get("./junit3.jar")),
        new DefaultGAV("junit", "junit", "", "5.0", "jar").withFile(Paths.get("./junit5.jar")),
        new DefaultGAV("junit", "junitx", "", "5.0", "jar").withFile(Paths.get("./junit6.jar")),
        new DefaultGAV("junit", "junity", "", "5.0", "jar").withFile(Paths.get("./junit4.jar")),
        new DefaultGAV("X", "Y", "", "1.0.0", "jar").withFile(Paths.get("./y-1.0.0.jar")),
        new DefaultGAV("Z:y:1.0.0")));

  }

  @Test
  public void testGetTrmeote() {
    assertEquals(ArtifactServices.CENTRAL_REPO_URL, as.getRemoteRepo().get());
  }

  @Test
  public void testGetArtifactsMatching() {
    List<GAV> d = as.getArtifactsMatching(new DefaultGAV("junit", "junit", null, "4.0", "jar"), GAV.PROVIDED_SCOPE,
        true, null, null);
    assertEquals(1, d.size(),"Size 1");
    d = as.getArtifactsMatching(new DefaultGAV("junit", "junit", null, "4.0", "jar"), GAV.PROVIDED_SCOPE, true, "x",
        null);
    assertEquals(0, d.size(),"Size 0");
    d = as.getArtifactsMatching(new DefaultGAV("junit", "junit", null, "4.0", "jar"), GAV.PROVIDED_SCOPE, true, null,
        "zip");
    assertEquals(0, d.size(),"Size 0");
    d = as.getArtifactsMatching(new DefaultGAV("junit", "junit", null, "4.0", "jar"), GAV.PROVIDED_SCOPE, true, "x",
        "jar");
    assertEquals(0, d.size(),"Size 0");
    d = as.getArtifactsMatching(new DefaultGAV("junit", "junit", null, "4.0", "zip"), GAV.PROVIDED_SCOPE, true, "x",
        null);
    assertEquals(0, d.size(),"Size 0");
  }

  @Test
  public void testGetArtifactsRuntime() {
    final List<GAV> d = as.getArtifactsRuntime(new DefaultGAV("junit", "junit", null, "4.0", "jar"));
    assertEquals(1, d.size(), "Size 1");
  }

  @Test
  public void testGetDependencies() {
    final List<Path> d = as.getDependencies(new DefaultGAV("junit", "junit", null, "4.0", "jar"), GAV.RUNTIME_SCOPE);
    assertEquals(1, d.size());
  }

  @Test
  public void testGetDependenciesRuntime() {
    final List<Path> d = as.getDependenciesRuntime(new DefaultGAV("junit", "junit", null, "4.0", "jar"));
    assertEquals(1, d.size());
  }

  @Test
  public void testGetDependencySourceJars() {
    final List<Path> d = as.getDependencySourceJars(new DefaultGAV("junit", "junit", null, "4.0", "jar"),
        GAV.PROVIDED_SCOPE);
    assertEquals(1, d.size());
  }

  @Test
  public void testGetDependencySourcJarsRuntime() {
    final List<Path> d = as.getDependencySourceJarsRuntime(new DefaultGAV("junit", "junit", null, "4.0", "jar"));
    assertEquals(1, d.size());
  };

  @Test
  public void testGetDeployables() {
    final Set<GAV> d = as.getResolvedDeployables(new DefaultGAV("junit", "junit", null, "4.0", "jar"),
        GAV.PROVIDED_SCOPE, true, null, null);
    assertEquals(1, d.size());

  }
}
