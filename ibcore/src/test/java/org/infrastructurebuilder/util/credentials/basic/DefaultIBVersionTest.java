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
package org.infrastructurebuilder.util.credentials.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.infrastructurebuilder.util.core.DefaultIBVersion;
import org.infrastructurebuilder.util.core.DefaultIBVersion.DefaultIBVersionBoundedRange;
import org.infrastructurebuilder.util.core.DefaultIBVersion.DefaultIBVersionRange;
import org.infrastructurebuilder.util.core.IBVersion;
import org.infrastructurebuilder.util.core.IBVersion.IBVersionBoundedRange;
import org.infrastructurebuilder.util.core.IBVersion.IBVersionRequirement;
import org.infrastructurebuilder.util.core.IBVersion.IBVersionType;
import org.infrastructurebuilder.util.core.IBVersion.VersionDiff;
import org.infrastructurebuilder.util.core.IBVersionException;
import org.infrastructurebuilder.util.core.RangeOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;

public class DefaultIBVersionTest {
  private static final String _2_0_0 = "2.0.0";
  private static final String _1_0_0 = "1.0.0";
  private static final String _2_2_2 = "2.2.2";
  private static final String _1_1 = "1.1";
  private static final String _0_1 = "0.1";
  private static final String _2_2_0 = "2.2.0";
  private static final String _1_0_0_ALPHA_1 = "1.0.0-alpha.1";
  private static final String RC3 = "-rc3";
  private static final String RC2 = "-rc2";
  private static final String RC1 = "-rc1";
  private static final String _1_2_2 = "1.2.2";
  private static final String FOFISIX = "4.5.6";
  private static final String UNODOSTRES = "1.2.3";
  IBVersion v, v1, v2;

  @Test
  public void compareTo_test() {

    final IBVersion[] array = new DefaultIBVersion[] {
        new DefaultIBVersion(UNODOSTRES), new DefaultIBVersion(UNODOSTRES + RC3),
        new DefaultIBVersion(UNODOSTRES + RC2), new DefaultIBVersion(UNODOSTRES + RC1), new DefaultIBVersion(_1_2_2),
        new DefaultIBVersion(_1_2_2 + RC2), new DefaultIBVersion(_1_2_2 + RC1), new DefaultIBVersion("1.2.0")
    };
    final int len = array.length;
    final List<IBVersion> list = new ArrayList<>(len);
    Collections.addAll(list, array);

    Collections.sort(list);

    for (int i = 0; i < list.size(); i++) {
      assertEquals(array[len - 1 - i], list.get(i));
    }
  }

  @Test
  public void testApi() {
    assertEquals("2.2", new DefaultIBVersion(_2_2_2).getApiVersion().toString());
  }

  @Test
  public void compareTo_test2() {
    assertTrue(new DefaultIBVersion(_2_0_0).compareTo(new DefaultIBVersion(_1_0_0)) > 0, "2.0.0 is > 1.0.0");
    assertTrue(new DefaultIBVersion(_1_0_0).compareTo(new DefaultIBVersion(_1_0_0)) == 0, "1.0.0 == 1.0.0");
  }

  @Test
  public void compareTo_without_path_or_minor() {
    assertTrue(new DefaultIBVersion(UNODOSTRES, IBVersionType.LOOSE).isGreaterThan("1.2"));
    assertTrue(new DefaultIBVersion("1.3", IBVersionType.LOOSE).isGreaterThan(UNODOSTRES));
    assertTrue(new DefaultIBVersion(UNODOSTRES, IBVersionType.LOOSE).isGreaterThan("1"));
    assertTrue(new DefaultIBVersion("2", IBVersionType.LOOSE).isGreaterThan(UNODOSTRES));
  }

  @Test
  public void diff() {
    final IBVersion sem = new DefaultIBVersion(UNODOSTRES + "-beta.4+sha899d8g79f87");
    assertEquals(VersionDiff.NONE, sem.diff(UNODOSTRES + "-beta.4+sha899d8g79f87"));
    assertEquals(VersionDiff.MAJOR, sem.diff("2.3.4-alpha.5+sha32iddfu987"));
    assertEquals(VersionDiff.MINOR, sem.diff("1.3.4-alpha.5+sha32iddfu987"));
    assertEquals(VersionDiff.PATCH, sem.diff("1.2.4-alpha.5+sha32iddfu987"));
    assertEquals(VersionDiff.SUFFIX, sem.diff(UNODOSTRES + "-alpha.4+sha32iddfu987"));
    assertEquals(VersionDiff.SUFFIX, sem.diff(UNODOSTRES + "-beta.5+sha32iddfu987"));
    assertEquals(VersionDiff.BUILD, sem.diff(UNODOSTRES + "-beta.4+sha32iddfu987"));
  }

  @Test
  public void getValue_returns_the_original_value_trimmed_and_with_the_same_case() {
    final String version = "  1.2.3-BETA.11+sHa.0nSFGKjkjsdf  ";
    final IBVersion ibVersionImpl = new DefaultIBVersion(version);
    assertEquals(UNODOSTRES + "-BETA.11+sHa.0nSFGKjkjsdf", ibVersionImpl.getValue());
  }

  @Test
  public void isEquivalentTo_isEqualTo_and_build() {
    final IBVersion semver = new DefaultIBVersion("1.0.0+ksadhjgksdhgksdhgfj");
    final String version2 = "1.0.0+sdgfsdgsdhsdfgdsfgf";
    assertFalse(semver.isEqualTo(version2));
    assertTrue(semver.isEquivalentTo(version2));
  }

  @Test
  public void isLowerThan_test() {

    assertFalse(new DefaultIBVersion(_1_0_0_ALPHA_1).isLowerThan("1.0.0-alpha"));
    assertFalse(new DefaultIBVersion("1.0.0-alpha.beta").isLowerThan(_1_0_0_ALPHA_1));
    assertFalse(new DefaultIBVersion("1.0.0-beta").isLowerThan("1.0.0-alpha.beta"));
    assertFalse(new DefaultIBVersion("1.0.0-beta.2").isLowerThan("1.0.0-beta"));
    assertFalse(new DefaultIBVersion("1.0.0-beta.11").isLowerThan("1.0.0-beta.2"));
    assertFalse(new DefaultIBVersion("1.0.0-rc.1").isLowerThan("1.0.0-beta.11"));
    assertFalse(new DefaultIBVersion(_1_0_0).isLowerThan("1.0.0-rc.1"));

    assertTrue(new DefaultIBVersion("1.0.0-alpha").isLowerThan(_1_0_0_ALPHA_1));
    assertTrue(new DefaultIBVersion(_1_0_0_ALPHA_1).isLowerThan("1.0.0-alpha.beta"));
    assertTrue(new DefaultIBVersion("1.0.0-alpha.beta").isLowerThan("1.0.0-beta"));
    assertTrue(new DefaultIBVersion("1.0.0-beta").isLowerThan("1.0.0-beta.2"));
    assertTrue(new DefaultIBVersion("1.0.0-beta.2").isLowerThan("1.0.0-beta.11"));
    assertTrue(new DefaultIBVersion("1.0.0-beta.11").isLowerThan("1.0.0-rc.1"));
    assertTrue(new DefaultIBVersion("1.0.0-rc.1").isLowerThan(_1_0_0));

    assertFalse(new DefaultIBVersion(_1_0_0).isLowerThan(_1_0_0));
    assertFalse(new DefaultIBVersion("1.0.0-alpha.12").isLowerThan("1.0.0-alpha.12"));
  }

  @Test
  public void isStable_test() {
    assertTrue(new DefaultIBVersion(UNODOSTRES + "+sHa.0nSFGKjkjsdf").isStable());
    assertTrue(new DefaultIBVersion(UNODOSTRES).isStable());
    assertFalse(new DefaultIBVersion(UNODOSTRES + "-BETA.11+sHa.0nSFGKjkjsdf").isStable());
    assertFalse(new DefaultIBVersion("0.1.2+sHa.0nSFGKjkjsdf").isStable());
    assertFalse(new DefaultIBVersion("0.1.2").isStable());
  }

  @BeforeEach
  public void setUp() throws Exception {
    v = new DefaultIBVersion(_1_0_0);
    v1 = new DefaultIBVersion("1.2.0");
    v2 = new DefaultIBVersion(_2_2_0);
  }

  @Test
  public void statisfies_works_will_all_the_types() {

    for (final IBVersionType type : IBVersionType.values()) {
      final String version = UNODOSTRES;
      final IBVersion semver = new DefaultIBVersion(version, type);
      assertTrue(semver.satisfies(UNODOSTRES));
      assertFalse(semver.satisfies(FOFISIX));
    }

    final IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildLoose(UNODOSTRES);

    assertTrue(new DefaultIBVersion(UNODOSTRES + "+123").satisfies(req), "CVR really satisfies, like a candy bar");
  }

  @Test
  public void strict_satisfies() {

    for (final IBVersionType type : IBVersionType.values()) {
      final String version = UNODOSTRES;
      final IBVersion semver = new DefaultIBVersion(version, type);
      assertTrue(semver.satisfies(UNODOSTRES));
      assertFalse(semver.satisfies(FOFISIX));
    }

    final IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildStrict(UNODOSTRES);

    assertTrue(new DefaultIBVersion(UNODOSTRES + "+123").satisfies(req), "CVR really satisfies, like a candy bar");
    final IBVersionRequirement req2 = DefaultIBVersion.DefaultIBVersionRequirement
        .buildStrict(new DefaultIBVersion(UNODOSTRES));

    assertTrue(new DefaultIBVersion(UNODOSTRES + "+123").satisfies(req2), "CVR really satisfies, like a candy bar");
  }

  @Test
  public void test() {

    assertTrue(v1.isGreaterThan(v), "1.2.0 > 1.0.0");
    assertTrue(v2.isGreaterThan(v1), "2.2.0 > 1.2.0");
    assertFalse(v1.isGreaterThan(v2), "1.2.0 > 2.2.0");
  }

  @Test
  public void testAPIVersion() {
    v = new DefaultIBVersion("2.0.999");
    assertEquals("2.0", v.apiVersion().toString());
  }

  @Test
  public void testIBVersionRangeImpl() {
    final DefaultIBVersionRange testRange = new DefaultIBVersion.DefaultIBVersionRange(new DefaultIBVersion(_1_0_0),
        RangeOperator.GTE);
    assertTrue(testRange.isSatisfiedBy(_1_0_0), "version range GTE 1.0.0 satisfied by String 1.0.0");
    assertFalse(testRange.isSatisfiedBy("0.9.900"), "version range GTE 1.0.0 not satisfied by String 0.9.900");
    assertTrue(testRange.isSatisfiedBy(v), "version range GTE 1.0.0 satisfied by IBVersion(1.0.0)");
    assertFalse(testRange.isSatisfiedBy(new DefaultIBVersion("0.9.9")),
        "version range GTE 1.0.0 not satisfied by IBVersion(0.9.9)");

    assertEquals(">=1.0", testRange.apiVersion().toString());
  }

  @Test
  public void testDEfaultIBVersionBoundedRange() {
    final IBVersionBoundedRange x = DefaultIBVersionBoundedRange.versionBoundedRangeFrom(_1_0_0, _2_2_2);
    assertEquals(">=1.0.0,<2.2.2", x.toString());
  }

  @Test
  public void testEquals() {
    assertThrows(IBVersionException.class,
        () -> assertNotEquals(new DefaultIBVersion(null), new DefaultIBVersion(_1_0_0), "Null semver != nonnull"));
//    assertNotEquals(new DefaultIBVersion(null), new DefaultIBVersion(null), "Null semver == null");
  }

  @Test
  public void testEquivalentTo() {
    assertTrue(new DefaultIBVersion("1.2.0+ABC").isEquivalentTo(v1.getValue()), "1.2.0 is equivalent to 1.2.1");
  }

  @Test
  public void testGetOriginal() {
    assertEquals("1.2.0", v1.getOriginalValue(), "Original is 1.2.0");
    assertNotEquals(v1, null, "vs null");
    assertNotEquals(v1, "Hi mom!", "vs string");
  }

  @Test
  public void testGetType() {
    assertEquals(IBVersionType.LOOSE, new DefaultIBVersion("1.0").getType(), "Type of IBVersion is ");
  }

  @Test
  public void testHashCode() {

    assertNotEquals(v.hashCode(), v1.hashCode(), "v1 != v");
  }

  @Test
  public void testIsEqualTo() {
    assertTrue(v1.isEqualTo(new DefaultIBVersion("1.2.0")), "v1 is equal to '1.2.0'");
  }

  @Test
  public void testIsGreaterThan() {
    assertTrue(v1.isGreaterThan(new DefaultIBVersion("1.1.0")), "v1 is equal to '1.1.0'");
    assertFalse(new DefaultIBVersion("1.1.0").isGreaterThan(v1), "1.1.0 is less than 1.2.0'");
    assertTrue(new DefaultIBVersion(_1_1).isGreaterThan(new DefaultIBVersion("1")), "1.1 is greater than 1");
    assertFalse(new DefaultIBVersion("1").isGreaterThan(new DefaultIBVersion(_1_1)), "1.1 is greater than 1");
    assertTrue(new DefaultIBVersion(UNODOSTRES).isGreaterThan(v1), UNODOSTRES + " is greater than 1.2.0");
  }

  @Test
  public void testIsStable() {
    assertFalse(new DefaultIBVersion("0.9.2").isStable(), "0.9.2 is not stable");
    assertTrue(new DefaultIBVersion(_1_0_0).isStable(), "1.0.0 is stable");
  }

  @Test
  public void testNexts() {
    assertEquals(new DefaultIBVersion(_2_0_0), v1.nextMajor(), "Next major from 1.2.0 is 2.0.0");
    assertEquals(new DefaultIBVersion("1.3.0"), v1.nextMinor(), "Next minor from 1.2.0 is 1.3.0");
    assertEquals(new DefaultIBVersion("1.2.1"), v1.nextPatch(), "Next patch from 1.2.0 is 1.2.1");

  }

  @Test
  public void testNullVersion() {
    Assertions.assertThrows(IBVersionException.class, () -> new DefaultIBVersion(null));
  }

  @Test
  public void testSatisfies() {
    assertTrue(v1.satisfies("1.2.0+abc"), "v1 satisfies 1");
  }

  @Test
  public void testWithIncs() {
    assertEquals(new DefaultIBVersion("1.2.1"), v1.withIncPatch(), "With Inc Path from 1.2.0 is 1.2.1");
    assertEquals(new DefaultIBVersion("1.3.1"), new DefaultIBVersion("1.2.1").withIncMinor(),
        "With Inc Minor from 1.2.1 is 1.3.0");
    assertEquals(new DefaultIBVersion("2.2.1"), new DefaultIBVersion("1.2.1").withIncMajor(),
        "With Inc Major from 1.2.1 is 2.2.1");

    assertEquals(new DefaultIBVersion("2"), new DefaultIBVersion("1").withIncMajor(), "With IncMajor from 1");
    assertEquals(new DefaultIBVersion(_1_1), new DefaultIBVersion("1.0").withIncMinor(), "With IncMinor from 1.0");

  }

  @Test
  public void withClearedBuild_test() {
    final IBVersion semver = new DefaultIBVersion(UNODOSTRES + "-Beta.4+sha123456789");
    semver.withClearedBuild().isEqualTo(UNODOSTRES + "-Beta.4");
  }

  @Test
  public void withClearedSuffix_test() {
    final IBVersion semver = new DefaultIBVersion(UNODOSTRES + "-Beta.4+SHA123456789");
    semver.withClearedSuffix().isEqualTo(UNODOSTRES + "+SHA123456789");
  }

  @Test
  public void withClearedSuffixAndBuild_test() {
    final IBVersion semver = new DefaultIBVersion(UNODOSTRES + "-Beta.4+SHA123456789");
    semver.withClearedSuffixAndBuild().isEqualTo(UNODOSTRES);
  }

  @Test
  public void testCocoapodsReq1() {
    IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildCocoapods("~> 1.2");
    DefaultIBVersion version = new DefaultIBVersion(UNODOSTRES);
    DefaultIBVersion version2 = new DefaultIBVersion(_1_1);
    assertTrue(req.isSatisfiedBy(version, IBVersionType.COCOAPODS));
    assertFalse(req.isSatisfiedBy(version2, IBVersionType.COCOAPODS));
  }

  @Test
  public void testIvyReq1() {
    IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildIvy("1.2.+");
    DefaultIBVersion version = new DefaultIBVersion(UNODOSTRES);
    DefaultIBVersion version2 = new DefaultIBVersion(_1_1);
    assertTrue(req.isSatisfiedBy(version, IBVersionType.IVY));
    assertFalse(req.isSatisfiedBy(version2, IBVersionType.IVY));
  }

  @Test
  public void testNPMReq1() {
    IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildNPM("1.x");
    DefaultIBVersion version = new DefaultIBVersion(UNODOSTRES);
    DefaultIBVersion version2 = new DefaultIBVersion(_0_1);
    assertTrue(req.isSatisfiedBy(version, IBVersionType.NPM));
    assertFalse(req.isSatisfiedBy(version2, IBVersionType.NPM));
  }

  @Test
  public void testLooseReq1() {
    IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildLoose("1");
    DefaultIBVersion version = new DefaultIBVersion("1");
    DefaultIBVersion version2 = new DefaultIBVersion(_0_1);
    assertTrue(req.isSatisfiedBy(version, IBVersionType.LOOSE));
    assertFalse(req.isSatisfiedBy(version2, IBVersionType.LOOSE));
    IBVersionRequirement sreq = DefaultIBVersion.DefaultIBVersionRequirement
        .buildLoose(new Semver("1", SemverType.LOOSE));
    DefaultIBVersion sversion = new DefaultIBVersion("1");
    DefaultIBVersion sversion2 = new DefaultIBVersion(_0_1);
    assertTrue(sreq.isSatisfiedBy(sversion, IBVersionType.LOOSE));
    assertFalse(sreq.isSatisfiedBy(sversion2, IBVersionType.LOOSE));
  }

  @Test
  public void testNull() {
    assertThrows(IBVersionException.class, () -> new DefaultIBVersion(null));
  }

  @Test
  public void testString() {
    assertEquals("1.2.3", new DefaultIBVersion("1.2.3").toString());
  }

}
