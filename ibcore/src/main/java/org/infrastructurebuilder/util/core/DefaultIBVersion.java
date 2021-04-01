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
package org.infrastructurebuilder.util.core;

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.util.core.IBVersionException.ibt;

import java.util.Objects;

import org.infrastructurebuilder.exceptions.IBException;

import com.vdurmont.semver4j.Range;
import com.vdurmont.semver4j.Requirement;
import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;

public final class DefaultIBVersion implements IBVersion {


  @Override
  public IBVersionRange forRange(RangeOperator op) {
    return new DefaultIBVersionRange((IBVersion)this, (RangeOperator)op);
  }

  public static class DefaultIBVersionRange implements IBVersionRange {
    private final Range range;
    private final IBVersion version;
    private final RangeOperator op;


    public DefaultIBVersionRange(final IBVersion version, final RangeOperator op) {
      this.version = Objects.requireNonNull(version);
      this.op = Objects.requireNonNull(op);

      range = new Range(new Semver(version.getValue(), SemverType.LOOSE), Range.RangeOperator.valueOf(op.name()));
    }

    @Override
    public boolean isSatisfiedBy(final IBVersion version) {
      return this.isSatisfiedBy(version.getValue());
    }

    @Override
    public boolean isSatisfiedBy(final String version) {
      return range.isSatisfiedBy(new Semver(version, SemverType.LOOSE));
    }
    @Override
    public String toString() {
        return range.toString();
    }

    @Override
    public IBVersionRange apiVersion() {
      return new DefaultIBVersionRange(this.version.apiVersion(),this.op);
    }

  }

  public static class DefaultIBVersionBoundedRange implements IBVersionBoundedRange {

    private final IBVersionRange lower, upper;

    public final static IBVersionBoundedRange versionBoundedRangeFrom (String lower, String upper) {
      return versionBoundedRangeFrom(new DefaultIBVersion(lower), new DefaultIBVersion(upper));
    }

    public final static IBVersionBoundedRange versionBoundedRangeFrom(IBVersion lower, IBVersion upper) {
      RangeOperator secnd = RangeOperator.LT;
      if (lower.equals(upper))
        secnd = RangeOperator.EQ;
      if (upper.isLowerThan(lower) )
        throw new IBException("Upper " + upper + " must be greater than " + lower);

      return new DefaultIBVersionBoundedRange(new DefaultIBVersionRange(lower, RangeOperator.GTE),
          new DefaultIBVersionRange(upper, secnd));
    }

    private DefaultIBVersionBoundedRange(IBVersionRange lower2, IBVersionRange upper2) {
      this.upper = requireNonNull(lower2);
      this.lower = requireNonNull(upper2);
    }

    @Override
    public boolean isSatisfiedBy(IBVersion version) {
      return lower.isSatisfiedBy(version) && upper.isSatisfiedBy(version);
    }

    @Override
    public boolean isSatisfiedBy(String version) {
      return lower.isSatisfiedBy(version) && upper.isSatisfiedBy(version);
    }

    @Override
    public String toString() {
      return this.lower.toString() + this.upper.toString();
    }

    @Override
    public IBVersionBoundedRange apiRange() {
      return new DefaultIBVersionBoundedRange(lower.apiVersion(), upper.apiVersion());
    }

  }

  public static class DefaultIBVersionRequirement implements IBVersionRequirement {
    public static IBVersionRequirement buildCocoapods(final String requirement) {
      return ibt.withReturningTranslation(() -> {
        return asIBVersionRequirement(com.vdurmont.semver4j.Requirement.buildCocoapods(requirement));
      });
    }

    public static IBVersionRequirement buildIvy(final String requirement) {
      return ibt.withReturningTranslation(() -> {
        return asIBVersionRequirement(Requirement.buildIvy(requirement));
      });
    }

    public static IBVersionRequirement buildLoose(final IBVersion requirement) {
      return ibt.withReturningTranslation(() -> {
        return asIBVersionRequirement(
            Requirement.buildLoose(new Semver(requirement.getValue(), SemverType.LOOSE).toString()));
      });
    }

    public static IBVersionRequirement buildLoose(final Semver requirement) {
      return ibt.withReturningTranslation(() -> {
        return asIBVersionRequirement(Requirement.buildLoose(requirement.toString()));
      });
    }

    public static IBVersionRequirement buildLoose(final String requirement) {
      return ibt.withReturningTranslation(() -> {
        return buildLoose(new DefaultIBVersion(requirement));
      });
    }

    public static IBVersionRequirement buildNPM(final String requirement) {
      return ibt.withReturningTranslation(() -> {
        return asIBVersionRequirement(Requirement.buildNPM(requirement));
      });
    }

    public static IBVersionRequirement buildStrict(final IBVersion requirement) {
      return ibt.withReturningTranslation(() -> {
        return asIBVersionRequirement(
            Requirement.buildStrict(new Semver(requirement.getValue(), SemverType.STRICT).toString()));
      });
    }

    public static IBVersionRequirement buildStrict(final String requirement) {
      return ibt.withReturningTranslation(() -> {
        return buildStrict(new DefaultIBVersion(requirement));
      });
    }

    private static IBVersionRequirement asIBVersionRequirement(final com.vdurmont.semver4j.Requirement req) {
      return ibt.withReturningTranslation(() -> {
        return new DefaultIBVersionRequirement(req);
      });
    }

    private final Requirement req;

    DefaultIBVersionRequirement(final com.vdurmont.semver4j.Requirement req) {
      this.req = req;
    }

    @Override
    public boolean isSatisfiedBy(final IBVersion version, final IBVersionType type) {
      return this.isSatisfiedBy(version.getValue(), type);
    }

    @Override
    public boolean isSatisfiedBy(final String version, final IBVersionType type) {
      return ibt.withReturningTranslation(() -> {
        return getReq().isSatisfiedBy(new Semver(version, type.asSemVerType()));
      });
    }

    Requirement getReq() {
      return req;
    }
  }


  private static com.vdurmont.semver4j.Semver.SemverType asSemverType(final IBVersionType type) {
    return com.vdurmont.semver4j.Semver.SemverType.valueOf(type.name());
  }

  private final Semver semver;

  public DefaultIBVersion(final String version) {
    if (version == null)
      throw new IBVersionException("Null version not allowed");
    semver = new Semver(version, com.vdurmont.semver4j.Semver.SemverType.LOOSE);
  }

  public DefaultIBVersion(final String orig, final IBVersionType type) {
    semver = new Semver(orig, asSemverType(type));
  }

  private DefaultIBVersion(final Semver semver) {
    if (semver == null)
      throw new IBVersionException("Null version not allowed");
    this.semver = semver;
  }

  @Override
  public IBVersion apiVersion() {
    return new DefaultIBVersion(getMajor() + "." + getMinor(), IBVersionType.LOOSE);
  }

  @Override
  public int compareTo(final IBVersion version) {
    if (this.isGreaterThan(version))
      return 1;
    else if (equals(version))
      return 0;
    return -1;
  }

  @Override
  public VersionDiff diff(final IBVersion version) {
    if (!Objects.equals(getMajor(), version.getMajor()))
      return VersionDiff.MAJOR;
    if (!Objects.equals(getMinor(), version.getMinor()))
      return VersionDiff.MINOR;
    if (!Objects.equals(getPatch(), version.getPatch()))
      return VersionDiff.PATCH;
    if (!areSameSuffixes(version.getSuffixTokens()))
      return VersionDiff.SUFFIX;
    if (!Objects.equals(getBuild(), version.getBuild()))
      return VersionDiff.BUILD;
    return VersionDiff.NONE;
  }

  @Override
  public VersionDiff diff(final String version) {
    return this.diff(new DefaultIBVersion(version));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final DefaultIBVersion other = (DefaultIBVersion) obj;

    if (!semver.equals(other.semver))
      return false;
    return true;
  }

  @Override
  public String getBuild() {
    return semver.getBuild();
  }

  @Override
  public Integer getMajor() {
    return semver.getMajor();
  }

  @Override
  public Integer getMinor() {
    return semver.getMinor();
  }

  @Override
  public String getOriginalValue() {
    return semver.getOriginalValue();
  }

  @Override
  public Integer getPatch() {
    return semver.getPatch();
  }

  @Override
  public String[] getSuffixTokens() {
    return semver.getSuffixTokens();
  }

  @Override
  public IBVersionType getType() {
    return IBVersionType.valueOf(semver.getType().name());
  }

  @Override
  public String getValue() {
    return semver.getValue();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (semver == null ? 0 : semver.hashCode());
    return result;
  }

  @Override
  public boolean isEqualTo(final IBVersion version) {
    return equals(version);
  }

  @Override
  public boolean isEqualTo(final String version) {
    return this.isEqualTo(new DefaultIBVersion(version));
  }

  @Override
  public boolean isEquivalentTo(final IBVersion version) {

    final IBVersion sem1 = getBuild() == null ? this
        : new DefaultIBVersion(new Semver(getValue().replace("+" + getBuild(), "")));
    final IBVersion sem2 = version.getBuild() == null ? version
        : new DefaultIBVersion(new Semver(version.getValue().replace("+" + version.getBuild(), "")));

    return sem1.isEqualTo(sem2);
  }

  @Override
  public boolean isEquivalentTo(final String version) {
    return this.isEquivalentTo(new DefaultIBVersion(version));
  }

  @Override
  public boolean isGreaterThan(final IBVersion version) {

    if (getMajor() > version.getMajor())
      return true;
    else if (getMajor() < version.getMajor())
      return false;

    final int otherMinor = version.getMinor() != null ? version.getMinor() : 0;
    if (getMinor() != null) {
      if (getMinor() > otherMinor)
        return true;
      if (getMinor() < otherMinor)
        return false;
    }

    final int otherPatch = version.getPatch() != null ? version.getPatch() : 0;
    if (getPatch() != null && getPatch() > otherPatch)
      return true;
    else if (getPatch() != null && getPatch() < otherPatch)
      return false;

    final String[] tokens1 = getSuffixTokens();
    final String[] tokens2 = version.getSuffixTokens();

    if (tokens1.length == 0 && tokens2.length > 0)
      return true;
    if (tokens2.length == 0 && tokens1.length > 0)
      return false;

    int i = 0;
    while (i < tokens1.length && i < tokens2.length) {
      int cmp;
      try {

        final int t1 = Integer.valueOf(tokens1[i]);
        final int t2 = Integer.valueOf(tokens2[i]);
        cmp = t1 - t2;
      } catch (final NumberFormatException e) {

        cmp = tokens1[i].compareToIgnoreCase(tokens2[i]);
      }
      if (cmp < 0)
        return false;
      else if (cmp > 0)
        return true;
      i++;
    }

    return tokens1.length > tokens2.length;
  }

  @Override
  public boolean isGreaterThan(final String version) {
    return semver.isGreaterThan(version);
  }

  @Override
  public boolean isLowerThan(final IBVersion version) {
    return !this.isGreaterThan(version) && !this.isEquivalentTo(version);
  }

  @Override
  public boolean isLowerThan(final String version) {
    return this.isLowerThan(new DefaultIBVersion(version));
  }

  @Override
  public boolean isStable() {
    return getMajor() != null && getMajor() > 0 && (getSuffixTokens() == null || getSuffixTokens().length == 0);
  }

  @Override
  public IBVersion nextMajor() {
    return with(getMajor() + 1, 0, 0, false, false);
  }

  @Override
  public IBVersion nextMinor() {
    return with(getMajor(), getMinor() + 1, 0, false, false);
  }

  @Override
  public IBVersion nextPatch() {
    return with(getMajor(), getMinor(), getPatch() + 1, false, false);
  }

  @Override
  public boolean satisfies(final IBVersionRequirement requirement) {

    return semver.satisfies(((DefaultIBVersionRequirement) requirement).getReq());
  }

  @Override
  public boolean satisfies(final String requirement) {
    return semver.satisfies(requirement);
  }

  @Override
  public String toString() {
    return semver.getValue();
  }

  @Override
  public IBVersion withClearedBuild() {
    return with(getMajor(), getMinor(), getPatch(), true, false);
  }

  @Override
  public IBVersion withClearedSuffix() {
    return with(getMajor(), getMinor(), getPatch(), false, true);
  }

  @Override
  public IBVersion withClearedSuffixAndBuild() {
    return with(getMajor(), getMinor(), getPatch(), false, false);
  }

  @Override
  public IBVersion withIncMajor() {
    return this.withIncMajor(1);
  }

  @Override
  public IBVersion withIncMajor(final int increment) {
    return withInc(increment, 0, 0);
  }

  @Override
  public IBVersion withIncMinor() {
    return this.withIncMinor(1);
  }

  @Override
  public IBVersion withIncMinor(final int increment) {
    return withInc(0, increment, 0);
  }

  @Override
  public IBVersion withIncPatch() {
    return this.withIncPatch(1);
  }

  @Override
  public IBVersion withIncPatch(final int increment) {
    return withInc(0, 0, increment);
  }

  private boolean areSameSuffixes(final String[] suffixTokens) {
    final String[] t = getSuffixTokens();
    if (t == null && suffixTokens == null)
      return true;
    else if (t == null || suffixTokens == null)
      return false;
    else if (t.length != suffixTokens.length)
      return false;
    for (int i = 0; i < t.length; i++) {
      if (!t[i].equals(suffixTokens[i]))
        return false;
    }
    return true;
  }

  private IBVersion with(final int major, final Integer minor, final Integer patch, final boolean suffix,
      final boolean build) {
    final StringBuilder sb = new StringBuilder().append(major);
    if (semver.getMinor() != null) {
      sb.append(".").append(minor);
    }
    if (getPatch() != null) {
      sb.append(".").append(patch);
    }
    if (suffix) {
      boolean first = true;
      for (final String suffixToken : getSuffixTokens()) {
        if (first) {
          sb.append("-");
          first = false;
        } else {
          sb.append(".");
        }
        sb.append(suffixToken);
      }
    }
    if (getBuild() != null && build) {
      sb.append("+").append(getBuild());
    }

    return new DefaultIBVersion(sb.toString(), IBVersionType.valueOf(semver.getType().name()));
  }

  private IBVersion withInc(final int majorInc, final int minorInc, final int patchInc) {
    Integer minor = getMinor();
    Integer patch = getPatch();
    if (getMinor() != null) {
      minor += minorInc;
    }
    if (getPatch() != null) {
      patch += patchInc;
    }
    return with(getMajor() + majorInc, minor, patch, true, true);
  }

  @Override
  public IBVersion getApiVersion() {
    return new DefaultIBVersion(getMajor() + "." + getMinor());
  }

}
