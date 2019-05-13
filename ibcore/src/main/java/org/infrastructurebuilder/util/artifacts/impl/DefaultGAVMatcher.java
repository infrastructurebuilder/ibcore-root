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

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.GAVMatcher;
import org.infrastructurebuilder.util.artifacts.IBVersion.IBVersionRange;

public class DefaultGAVMatcher implements GAVMatcher {
  private final static Pattern allString = Pattern.compile(".*");

  public final static GAVMatcher from(final Optional<String> groupId, final Optional<String> artifactId,
      final Optional<IBVersionRange> versionRange, final Optional<String> versionByString,
      final Optional<String> classifier, final Optional<String> type) {
    return new DefaultGAVMatcher(groupId.map(g -> Pattern.compile(g)), artifactId.map(a -> Pattern.compile(a)),
        versionRange, versionByString.map(v -> Pattern.compile(v)), classifier.map(c -> Pattern.compile(c)),
        type.map(t -> Pattern.compile(t)));
  }

  private final Optional<Pattern> artifactId;
  private final Optional<Pattern> classifier;
  private final Optional<Pattern> groupId;
  private final Optional<Pattern> type;
  private final Optional<Pattern> versionByString;

  private final Optional<IBVersionRange> versionRange;

  public DefaultGAVMatcher(final Optional<Pattern> groupId, final Optional<Pattern> artifactId,
      final Optional<IBVersionRange> versionRange, final Optional<Pattern> versionByString,
      final Optional<Pattern> classifier, final Optional<Pattern> type) {
    this.groupId = Objects.requireNonNull(groupId, "matching groupId");
    this.artifactId = Objects.requireNonNull(artifactId, "matching artifactId`");

    this.versionRange = Optional.empty();
    this.versionByString = Objects.requireNonNull(versionByString, "matching versionByString");
    this.classifier = Objects.requireNonNull(classifier, "matching classifier");
    this.type = Objects.requireNonNull(type, "matching type");
  }

  @Override
  public Optional<Pattern> getArtifactId() {
    return artifactId;
  }

  @Override
  public Optional<Pattern> getClassifier() {
    return classifier;
  }

  @Override
  public Optional<Pattern> getExtension() {
    return type;
  }

  @Override
  public Optional<Pattern> getGroupId() {
    return groupId;
  }

  @Override
  public Optional<Pattern> getVersionByString() {
    return versionByString;
  }

  @Override
  public Optional<IBVersionRange> getVersionRange() {
    return versionRange;
  }

  @Override
  public boolean matches(final GAV target, final boolean strict) {
    boolean matches = true;
    if (target.getVersion().isPresent()) {
      matches = matches && getVersionByString().map(v -> v.matcher(target.getVersion().get()).matches()).orElse(true);
    } else if (strict) {
      matches = matches && !getVersionByString().isPresent();
    }
    if (target.getVersion().isPresent()) {
      matches = matches && getVersionRange().map(vr -> vr.isSatisfiedBy(target.getVersion().get())).orElse(true);
    } else if (strict) {
      matches = matches && !getVersionRange().isPresent();
    }
    if (target.getClassifier().isPresent()) {
      matches = matches && getClassifier().map(v -> v.matcher(target.getClassifier().get()).matches()).orElse(true);
    } else if (strict) {
      matches = matches && !getClassifier().isPresent();
    }
    matches = matches && getGroupId().map(g -> g.matcher(target.getGroupId()).matches()).orElse(true)
        && getArtifactId().map(a -> a.matcher(target.getArtifactId()).matches()).orElse(true)
        && getExtension().map(t -> t.matcher(target.getExtension()).matches()).orElse(true);

    return matches;
  }

}
