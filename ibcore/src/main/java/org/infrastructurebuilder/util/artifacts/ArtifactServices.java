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

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.infrastructurebuilder.IBException;

/**
 * ArtifactServices instances produce access to an artifact repository.
 *
 * Specifically, they currently can only reall manage a repository that stores
 * artifacts in a manner consistent with a {@link GAV}. This means that the
 * underlying repository has a namespace + name + version + [possible other
 * identifiers] + extension (not type, like in Maven). For the most part, this
 * is very straightforward. Nearly every repository manager does this, as it is
 * a proven pattern with literally billions of uses across the last few decades.
 *
 * Note that for some, and possibly even many, repository types, the concept of
 * scope simply doesn't exist. This value will generally be ignored if that's
 * the case.
 *
 * @author mykel.alvis
 *
 */
public interface ArtifactServices extends Weighted {
  final static String BASIC_PACKAGING = "jar";
  final static String CENTRAL_REPO_ID = "central";
  final static String CENTRAL_REPO_STRING_URL = "https://repo.maven.apache.org/maven2/";
  static final String LOCALREPO = "localRepo";

  static final String NORMALIZE_SNAPSHOTS = "normalizeSnapshots";
  static final String REMOTE_REPO_URL = "remoteRepoUrl";
  final static String SOURCE_CLASSIFIER = "sources";
  final static URL CENTRAL_REPO_URL = IBException.cet.withReturningTranslation(() -> new URL(CENTRAL_REPO_STRING_URL));

  GAV getArtifact(GAV coords, String scope);

  List<GAV> getArtifacts(GAV coords, String scope, boolean includeUnresolved);

  default List<GAV> getArtifactsMatching(final GAV coords, final String scope, final boolean includeUnresolved,
      final String classifier, final String type) {
    final List<GAV> s = getArtifacts(coords, scope, includeUnresolved);
    return s.stream().filter(a -> (classifier == null || classifier.equals(a.getClassifier())))
        .filter(a -> (type == null || type.equals(a.getExtension()))).collect(Collectors.toList());
  }

  default List<GAV> getArtifactsRuntime(final GAV coords) {
    return getArtifacts(coords, GAV.RUNTIME_SCOPE, false);
  }

  String getClasspathOf(GAV coords, String scope, boolean includeUnresolved);

  String getClasspathOf(GAV coords, String scope, List<GAV> additional, boolean eliminateEquivalentFiles);

  default List<Path> getDependencies(final GAV coords, final String scope) {
    List<GAV> q = getArtifacts(coords, scope, false).stream().filter(a -> a.getFile().isPresent())
        .collect(Collectors.toList());
    return q.stream().map(a -> a.getFile().get()).collect(Collectors.toList());
  }

  List<Path> getDependenciesOfClassifiedTypeFor(GAV coords, String scope, String classifier, String type,
      boolean throwOnFail);

  default List<Path> getDependenciesRuntime(final GAV coords) {
    return getDependencies(coords, GAV.RUNTIME_SCOPE);
  }

  default List<Path> getDependencySourceJars(final GAV coords, final String scope) {
    return getDependenciesOfClassifiedTypeFor(coords, scope, SOURCE_CLASSIFIER, BASIC_PACKAGING, false);
  }

  default List<Path> getDependencySourceJarsRuntime(final GAV coords) {
    return getDependencySourceJars(coords, GAV.RUNTIME_SCOPE);
  }

  /**
   * Defined as returning the absolute path to the user's local repository
   *
   * @return
   */
  Path getLocalRepo();

  Optional<URL> getRemoteRepo();

  default Set<GAV> getResolvedDeployables(final GAV coords, final String scope, final boolean includeUnresolved,
      final String classifier, final String type) {
    final String pre = getLocalRepo().toString().toLowerCase();
    return getArtifactsMatching(coords, scope, includeUnresolved, classifier, type).stream()
        .filter(a -> a.getFile().isPresent())
        .filter(a -> a.getFile().get().toAbsolutePath().toString().toLowerCase().startsWith(pre))
        .collect(Collectors.toSet());
  }

}