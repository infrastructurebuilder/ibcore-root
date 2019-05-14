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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.infrastructurebuilder.util.artifacts.IBVersion;
import org.infrastructurebuilder.util.artifacts.IBVersionException;
import org.infrastructurebuilder.util.artifacts.IBVersion.IBVersionRequirement;
import org.infrastructurebuilder.util.artifacts.IBVersion.IBVersionType;
import org.infrastructurebuilder.util.artifacts.IBVersion.VersionDiff;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion.DefaultIBVersionRange;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion.RangeOperator;
import org.junit.Before;
import org.junit.Test;

import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;

public class IBVersionImplTest {
  IBVersion v, v1, v2;

  @Test
  public void compareTo_test() {

    final IBVersion[] array = new DefaultIBVersion[] { new DefaultIBVersion("1.2.3"),
        new DefaultIBVersion("1.2.3-rc3"), new DefaultIBVersion("1.2.3-rc2"),
        new DefaultIBVersion("1.2.3-rc1"), new DefaultIBVersion("1.2.2"),
        new DefaultIBVersion("1.2.2-rc2"), new DefaultIBVersion("1.2.2-rc1"),
        new DefaultIBVersion("1.2.0") };
    final int len = array.length;
    final List<IBVersion> list = new ArrayList<>(len);
    Collections.addAll(list, array);

    Collections.sort(list);

    for (int i = 0; i < list.size(); i++) {
      assertEquals(array[len - 1 - i], list.get(i));
    }
  }

  @Test
  public void compareTo_test2() {
    assertTrue("2.0.0 is > 1.0.0",
        new DefaultIBVersion("2.0.0").compareTo(new DefaultIBVersion("1.0.0")) > 0);
    assertTrue("1.0.0 == 1.0.0",
        new DefaultIBVersion("1.0.0").compareTo(new DefaultIBVersion("1.0.0")) == 0);
  }

  @Test
  public void compareTo_without_path_or_minor() {
    assertTrue(new DefaultIBVersion("1.2.3", IBVersionType.LOOSE).isGreaterThan("1.2"));
    assertTrue(new DefaultIBVersion("1.3", IBVersionType.LOOSE).isGreaterThan("1.2.3"));
    assertTrue(new DefaultIBVersion("1.2.3", IBVersionType.LOOSE).isGreaterThan("1"));
    assertTrue(new DefaultIBVersion("2", IBVersionType.LOOSE).isGreaterThan("1.2.3"));
  }

  @Test
  public void diff() {
    final IBVersion sem = new DefaultIBVersion("1.2.3-beta.4+sha899d8g79f87");
    assertEquals(VersionDiff.NONE, sem.diff("1.2.3-beta.4+sha899d8g79f87"));
    assertEquals(VersionDiff.MAJOR, sem.diff("2.3.4-alpha.5+sha32iddfu987"));
    assertEquals(VersionDiff.MINOR, sem.diff("1.3.4-alpha.5+sha32iddfu987"));
    assertEquals(VersionDiff.PATCH, sem.diff("1.2.4-alpha.5+sha32iddfu987"));
    assertEquals(VersionDiff.SUFFIX, sem.diff("1.2.3-alpha.4+sha32iddfu987"));
    assertEquals(VersionDiff.SUFFIX, sem.diff("1.2.3-beta.5+sha32iddfu987"));
    assertEquals(VersionDiff.BUILD, sem.diff("1.2.3-beta.4+sha32iddfu987"));
  }

  @Test
  public void getValue_returns_the_original_value_trimmed_and_with_the_same_case() {
    final String version = "  1.2.3-BETA.11+sHa.0nSFGKjkjsdf  ";
    final IBVersion ibVersionImpl = new DefaultIBVersion(version);
    assertEquals("1.2.3-BETA.11+sHa.0nSFGKjkjsdf", ibVersionImpl.getValue());
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

    assertFalse(new DefaultIBVersion("1.0.0-alpha.1").isLowerThan("1.0.0-alpha"));
    assertFalse(new DefaultIBVersion("1.0.0-alpha.beta").isLowerThan("1.0.0-alpha.1"));
    assertFalse(new DefaultIBVersion("1.0.0-beta").isLowerThan("1.0.0-alpha.beta"));
    assertFalse(new DefaultIBVersion("1.0.0-beta.2").isLowerThan("1.0.0-beta"));
    assertFalse(new DefaultIBVersion("1.0.0-beta.11").isLowerThan("1.0.0-beta.2"));
    assertFalse(new DefaultIBVersion("1.0.0-rc.1").isLowerThan("1.0.0-beta.11"));
    assertFalse(new DefaultIBVersion("1.0.0").isLowerThan("1.0.0-rc.1"));

    assertTrue(new DefaultIBVersion("1.0.0-alpha").isLowerThan("1.0.0-alpha.1"));
    assertTrue(new DefaultIBVersion("1.0.0-alpha.1").isLowerThan("1.0.0-alpha.beta"));
    assertTrue(new DefaultIBVersion("1.0.0-alpha.beta").isLowerThan("1.0.0-beta"));
    assertTrue(new DefaultIBVersion("1.0.0-beta").isLowerThan("1.0.0-beta.2"));
    assertTrue(new DefaultIBVersion("1.0.0-beta.2").isLowerThan("1.0.0-beta.11"));
    assertTrue(new DefaultIBVersion("1.0.0-beta.11").isLowerThan("1.0.0-rc.1"));
    assertTrue(new DefaultIBVersion("1.0.0-rc.1").isLowerThan("1.0.0"));

    assertFalse(new DefaultIBVersion("1.0.0").isLowerThan("1.0.0"));
    assertFalse(new DefaultIBVersion("1.0.0-alpha.12").isLowerThan("1.0.0-alpha.12"));
  }

  @Test
  public void isStable_test() {
    assertTrue(new DefaultIBVersion("1.2.3+sHa.0nSFGKjkjsdf").isStable());
    assertTrue(new DefaultIBVersion("1.2.3").isStable());
    assertFalse(new DefaultIBVersion("1.2.3-BETA.11+sHa.0nSFGKjkjsdf").isStable());
    assertFalse(new DefaultIBVersion("0.1.2+sHa.0nSFGKjkjsdf").isStable());
    assertFalse(new DefaultIBVersion("0.1.2").isStable());
  }

  @Before
  public void setUp() throws Exception {
    v = new DefaultIBVersion("1.0.0");
    v1 = new DefaultIBVersion("1.2.0");
    v2 = new DefaultIBVersion("2.2.0");
  }

  @Test
  public void statisfies_works_will_all_the_types() {

    for (final IBVersionType type : IBVersionType.values()) {
      final String version = "1.2.3";
      final IBVersion semver = new DefaultIBVersion(version, type);
      assertTrue(semver.satisfies("1.2.3"));
      assertFalse(semver.satisfies("4.5.6"));
    }

    final IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement
        .buildLoose("1.2.3");

    assertTrue("CVR really satisfies, like Snickers", new DefaultIBVersion("1.2.3+123").satisfies(req));
  }

  @Test
  public void test() {

    assertTrue("1.2.0 > 1.0.0", v1.isGreaterThan(v));
    assertTrue("2.2.0 > 1.2.0", v2.isGreaterThan(v1));
    assertFalse("1.2.0 > 2.2.0", v1.isGreaterThan(v2));
  }

  @Test
  public void testAPIVersion() {
    v = new DefaultIBVersion("2.0.999");
    assertEquals("2.0", v.apiVersion().toString());
  }

  @Test
  public void testIBVersionRangeImpl() {
    final DefaultIBVersionRange testRange = new DefaultIBVersion.DefaultIBVersionRange(
        new DefaultIBVersion("1.0.0"), RangeOperator.GTE);
    assertTrue("version range GTE 1.0.0 satisfied by String 1.0.0", testRange.isSatisfiedBy("1.0.0"));
    assertFalse("version range GTE 1.0.0 not satisfied by String 0.9.900", testRange.isSatisfiedBy("0.9.900"));
    assertTrue("version range GTE 1.0.0 satisfied by IBVersion(1.0.0)", testRange.isSatisfiedBy(v));
    assertFalse("version range GTE 1.0.0 not satisfied by IBVersion(0.9.9)",
        testRange.isSatisfiedBy(new DefaultIBVersion("0.9.9")));

  }

  @Test(expected = IBVersionException.class)
  public void testEquals() {

    assertNotEquals("Null semver != nonnull", new DefaultIBVersion(null), new DefaultIBVersion("1.0.0"));
    assertNotEquals("Null semver == null", new DefaultIBVersion(null), new DefaultIBVersion(null));
  }

  @Test
  public void testEquivalentTo() {
    assertTrue("1.2.0 is equivalent to 1.2.1", new DefaultIBVersion("1.2.0+ABC").isEquivalentTo(v1.getValue()));
  }

  @Test
  public void testGetOriginal() {
    assertEquals("Original is 1.2.0", "1.2.0", v1.getOriginalValue());
    assertNotEquals("vs null", v1, null);
    assertNotEquals("vs string", v1, "Hi mom!");
  }

  @Test
  public void testGetType() {
    assertEquals("Type of IBVersion is ", IBVersionType.LOOSE,
        new DefaultIBVersion("1.0").getType());
  }

  @Test
  public void testHashCode() {

    assertNotEquals("v1 != v", v.hashCode(), v1.hashCode());
  }

  @Test
  public void testIsEqualTo() {
    assertTrue("v1 is equal to '1.2.0'", v1.isEqualTo(new DefaultIBVersion("1.2.0")));
  }

  @Test
  public void testIsGreaterThan() {
    assertTrue("v1 is equal to '1.1.0'", v1.isGreaterThan(new DefaultIBVersion("1.1.0")));
    assertFalse("1.1.0 is less than 1.2.0'", new DefaultIBVersion("1.1.0").isGreaterThan(v1));
    assertTrue("1.1 is greater than 1",
        new DefaultIBVersion("1.1").isGreaterThan(new DefaultIBVersion("1")));
    assertFalse("1.1 is greater than 1",
        new DefaultIBVersion("1").isGreaterThan(new DefaultIBVersion("1.1")));
    assertTrue("1.2.3 is greater than 1.2.0", new DefaultIBVersion("1.2.3").isGreaterThan(v1));
  }

  @Test
  public void testIsStable() {
    assertFalse("0.9.2 is not stable", new DefaultIBVersion("0.9.2").isStable());
    assertTrue("1.0.0 is stable", new DefaultIBVersion("1.0.0").isStable());
  }

  @Test
  public void testNexts() {
    assertEquals("Next major from 1.2.0 is 2.0.0", new DefaultIBVersion("2.0.0"), v1.nextMajor());
    assertEquals("Next minor from 1.2.0 is 1.3.0", new DefaultIBVersion("1.3.0"), v1.nextMinor());
    assertEquals("Next patch from 1.2.0 is 1.2.1", new DefaultIBVersion("1.2.1"), v1.nextPatch());

  }

  @Test(expected = IBVersionException.class)
  public void testNullVersion() {
    new DefaultIBVersion(null);
  }

  @Test
  public void testSatisfies() {
    assertTrue("v1 satisfies 1", v1.satisfies("1.2.0+abc"));
  }

  @Test
  public void testWithIncs() {
    assertEquals("With Inc Path from 1.2.0 is 1.2.1", new DefaultIBVersion("1.2.1"), v1.withIncPatch());
    assertEquals("With Inc Minor from 1.2.1 is 1.3.0", new DefaultIBVersion("1.3.1"),
        new DefaultIBVersion("1.2.1").withIncMinor());
    assertEquals("With Inc Major from 1.2.1 is 2.2.1", new DefaultIBVersion("2.2.1"),
        new DefaultIBVersion("1.2.1").withIncMajor());

    assertEquals("With IncMajor from 1", new DefaultIBVersion("2"),
        new DefaultIBVersion("1").withIncMajor());
    assertEquals("With IncMinor from 1.0", new DefaultIBVersion("1.1"),
        new DefaultIBVersion("1.0").withIncMinor());

  }

  @Test
  public void withClearedBuild_test() {
    final IBVersion semver = new DefaultIBVersion("1.2.3-Beta.4+sha123456789");
    semver.withClearedBuild().isEqualTo("1.2.3-Beta.4");
  }

  @Test
  public void withClearedSuffix_test() {
    final IBVersion semver = new DefaultIBVersion("1.2.3-Beta.4+SHA123456789");
    semver.withClearedSuffix().isEqualTo("1.2.3+SHA123456789");
  }

  @Test
  public void withClearedSuffixAndBuild_test() {
    final IBVersion semver = new DefaultIBVersion("1.2.3-Beta.4+SHA123456789");
    semver.withClearedSuffixAndBuild().isEqualTo("1.2.3");
  }
  @Test
  public void testCocoapodsReq1() {
    IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildCocoapods("~> 1.2");
    DefaultIBVersion version = new DefaultIBVersion("1.2.3");
    DefaultIBVersion version2 = new DefaultIBVersion("1.1");
    assertTrue(req.isSatisfiedBy(version, IBVersionType.COCOAPODS));
    assertFalse(req.isSatisfiedBy(version2, IBVersionType.COCOAPODS));
  }
  @Test
  public void testIvyReq1() {
    IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildIvy("1.2.+");
    DefaultIBVersion version = new DefaultIBVersion("1.2.3");
    DefaultIBVersion version2 = new DefaultIBVersion("1.1");
    assertTrue(req.isSatisfiedBy(version, IBVersionType.IVY));
    assertFalse(req.isSatisfiedBy(version2, IBVersionType.IVY));
  }
  @Test
  public void testNPMReq1() {
    IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildNPM("1.x");
    DefaultIBVersion version = new DefaultIBVersion("1.2.3");
    DefaultIBVersion version2 = new DefaultIBVersion("0.1");
    assertTrue(req.isSatisfiedBy(version, IBVersionType.NPM));
    assertFalse(req.isSatisfiedBy(version2, IBVersionType.NPM));
  }
  @Test
  public void testLooseReq1() {
    IBVersionRequirement req = DefaultIBVersion.DefaultIBVersionRequirement.buildLoose("1");
    DefaultIBVersion version = new DefaultIBVersion("1");
    DefaultIBVersion version2 = new DefaultIBVersion("0.1");
    assertTrue(req.isSatisfiedBy(version, IBVersionType.LOOSE));
    assertFalse(req.isSatisfiedBy(version2, IBVersionType.LOOSE));
    IBVersionRequirement sreq = DefaultIBVersion.DefaultIBVersionRequirement.buildLoose(new Semver("1",SemverType.LOOSE));
    DefaultIBVersion sversion = new DefaultIBVersion("1");
    DefaultIBVersion sversion2 = new DefaultIBVersion("0.1");
    assertTrue(sreq.isSatisfiedBy(sversion, IBVersionType.LOOSE));
    assertFalse(sreq.isSatisfiedBy(sversion2, IBVersionType.LOOSE));
  }
  @Test(expected=IBVersionException.class)
  public void testNull() {
    new DefaultIBVersion(null);
  }
}
