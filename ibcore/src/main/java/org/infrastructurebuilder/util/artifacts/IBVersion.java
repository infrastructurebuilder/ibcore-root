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

import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion.RangeOperator;

import com.vdurmont.semver4j.Semver.SemverType;

public interface IBVersion extends Comparable<IBVersion> {

  public interface IBVersionRange {
    public boolean isSatisfiedBy(IBVersion version);

    public boolean isSatisfiedBy(String version);
  }

  public interface IBVersionRequirement {
    public boolean isSatisfiedBy(IBVersion version, IBVersionType type);

    public boolean isSatisfiedBy(String version, IBVersionType type);

  }

  // Straight from SemverType
  public enum IBVersionType {
    COCOAPODS, IVY, LOOSE, NPM, STRICT;

    public SemverType asSemVerType() {
      return SemverType.valueOf(name());
    }

  }

  public enum VersionDiff {
    BUILD, MAJOR, MINOR, NONE, PATCH, SUFFIX
  }

  IBVersion apiVersion();

  @Override
  int compareTo(IBVersion version);

  VersionDiff diff(IBVersion version);

  VersionDiff diff(String version);

  String getBuild();

  Integer getMajor();

  Integer getMinor();

  String getOriginalValue();

  Integer getPatch();

  String[] getSuffixTokens();

  IBVersionType getType();

  String getValue();

  boolean isEqualTo(IBVersion version);

  boolean isEqualTo(String version);

  boolean isEquivalentTo(IBVersion version);

  boolean isEquivalentTo(String version);

  boolean isGreaterThan(IBVersion version);

  boolean isGreaterThan(String version);

  boolean isLowerThan(IBVersion version);

  boolean isLowerThan(String version);

  boolean isStable();

  IBVersion nextMajor();

  IBVersion nextMinor();

  IBVersion nextPatch();

  boolean satisfies(IBVersionRequirement requirement);

  boolean satisfies(String requirement);

  IBVersion withClearedBuild();

  IBVersion withClearedSuffix();

  IBVersion withClearedSuffixAndBuild();

  IBVersion withIncMajor();

  IBVersion withIncMajor(int increment);

  IBVersion withIncMinor();

  IBVersion withIncMinor(int increment);

  IBVersion withIncPatch();

  IBVersion withIncPatch(int increment);

  IBVersionRange forRange(RangeOperator op);

}