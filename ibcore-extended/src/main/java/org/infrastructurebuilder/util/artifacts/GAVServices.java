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

import static org.infrastructurebuilder.util.IBUtils.p;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.version.VersionScheme;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;

import org.infrastructurebuilder.IBException;

public final class GAVServices {
  public static Optional<IBVersion> apiVersion(final GAV gav) {
    return Objects.requireNonNull(gav).getVersion().map(DefaultIBVersion::new).map(DefaultIBVersion::apiVersion);
  }

  public final static Function<Artifact, GAV> artifactToGAV = (art) -> {
    final Path p = Optional.ofNullable(art.getFile()).map(p2 -> p2.toPath()).orElse(null);
    return new DefaultGAV(art.getGroupId(), art.getArtifactId(), art.getClassifier(), art.getVersion(),
        art.getExtension()).withFile(p);
  };

  public static String toInternalSignaturePath(final GAV gav) {
    return gav.getGroupId() + ":" + gav.getArtifactId() + ":" + gav.getClassifier().orElse("") + ":"
        + gav.getVersion().orElse("___") + ":" + gav.getExtension();
  }

  public static String getArtifactFilenamePath(final GAV art) {
    return String.format("%s%s%s.%s", art.getArtifactId(), art.getVersion().map(sv -> "-" + sv).orElse(""),
        art.getClassifier().map(cls -> "-" + cls).orElse(""), art.getExtension());
  }

  public static boolean _matcher(final String pattern, final String value) {
    if (value == null)
      return true;
    if (pattern == null)
      return true;
    final boolean b = java.util.regex.Pattern.compile(pattern).matcher(value).matches();
    return b;
  }

  public static boolean _versionmatcher(final GAV art, final GAV range) {
    if (!art.getVersion().isPresent())
      return true;
    if (!range.getVersion().isPresent())
      return true;
    try {
      final boolean b = inRange(art, ((DefaultGAV) range).asRange());
      return b;
    } catch (final IBException e) {
      return false;
    }
  }

  public static Artifact asArtifact(final GAV art) {
    return new DefaultArtifact(art.getDefaultSignaturePath());
  }

  public static Dependency asDependency(final GAV art, final String scope) {
    return new Dependency(asArtifact(art), scope);
  }

  public static int compareVersion(final GAV art, final GAV otherVersion)
      throws org.eclipse.aether.version.InvalidVersionSpecificationException {
    return getVersionScheme().parseVersion(art.getVersion().get().toString())
        .compareTo(getVersionScheme().parseVersion(otherVersion.getVersion().get().toString()));
  }

  public static GAV fromArtifact(final Artifact a) {
    return new DefaultGAV(a.getGroupId(), a.getArtifactId(), a.getClassifier(), a.getVersion(), a.getExtension())
        .withFile(Optional.ofNullable(a.getFile()).map(File::toPath).orElse(null));
  }

  public static Optional<IBVersion> getVersion(final GAV art) {
    return art.getVersion().map(DefaultIBVersion::new);
  }

  public static VersionScheme getVersionScheme() {
    return new org.eclipse.aether.util.version.GenericVersionScheme();
  }

  public static boolean inRange(final GAV art, final String versionRange) {
    return IBException.cet.withReturningTranslation(() -> {
      return getVersionScheme().parseVersionRange(versionRange)
          .containsVersion(getVersionScheme().parseVersion(art.getVersion().orElse(null)));
    });
  }

  public static boolean matches(final GAV art, final GAV pattern) {
    return _matcher(pattern.getGroupId(), art.getGroupId()) && _matcher(pattern.getArtifactId(), art.getArtifactId())
        && _matcher(pattern.getClassifier().orElse(".*"), art.getClassifier().orElse(null))
        && _matcher(pattern.getExtension(), art.getExtension()) && _versionmatcher(art, pattern);
  }

}
