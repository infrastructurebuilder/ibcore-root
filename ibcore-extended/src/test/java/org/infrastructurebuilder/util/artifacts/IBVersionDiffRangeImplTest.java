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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.infrastructurebuilder.util.artifacts.IBVersion;
import org.infrastructurebuilder.util.artifacts.IBVersionException;
import org.infrastructurebuilder.util.artifacts.IBVersion.IBVersionRequirement;
import org.infrastructurebuilder.util.artifacts.IBVersion.IBVersionType;
import org.infrastructurebuilder.util.artifacts.IBVersion.VersionDiff;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion.RangeOperator;
import org.junit.Test;

import com.vdurmont.semver4j.Semver;

public class IBVersionDiffRangeImplTest {

  @Test
  public void testDiff() {
    final VersionDiff v = IBVersion.VersionDiff.valueOf("BUILD");
    assertNotNull("BUILD is a thing", v);
  }

  @Test
  public void testRange() {
    final RangeOperator v = DefaultIBVersion.RangeOperator.valueOf("EQ");
    assertNotNull("EQ is a thing", v);
  }

  @Test
  public void testRequirementCocoapods() {
    final IBVersionRequirement v = DefaultIBVersion.DefaultIBVersionRequirement
        .buildCocoapods("1.0.1");
    assertNotNull("1.0.1 is a thing", v);
  }

  @Test
  public void testRequirementIvy() {
    final IBVersionRequirement v = DefaultIBVersion.DefaultIBVersionRequirement.buildIvy("1.0.1");
    assertNotNull("1.0.1 is a thing", v);
  }

  @Test
  public void testRequirementLoose() {
    final IBVersionRequirement v = DefaultIBVersion.DefaultIBVersionRequirement.buildLoose("1.0");
    assertNotNull("Loose is a thing", v);
    final IBVersionRequirement v1 = DefaultIBVersion.DefaultIBVersionRequirement
        .buildLoose(new Semver("1.0", Semver.SemverType.LOOSE));
    assertNotNull("Loose is still a thing", v1);
    assertTrue("Satisfies 1.0", v1.isSatisfiedBy(new DefaultIBVersion("1.0"), IBVersionType.LOOSE));

  }

  @Test(expected = IBVersionException.class)
  public void testRequirementLooseFail() {
    final IBVersionRequirement v1 = DefaultIBVersion.DefaultIBVersionRequirement
        .buildLoose(new Semver("1.0", Semver.SemverType.LOOSE));
    assertFalse("Satisfies 1.0 strict",
        v1.isSatisfiedBy(new DefaultIBVersion("1.0"), IBVersionType.STRICT));

  }

  @Test
  public void testRequirementNPM() {
    final IBVersionRequirement v = DefaultIBVersion.DefaultIBVersionRequirement.buildNPM("1.0.1");
    assertNotNull("1.0.1 is a thing", v);
  }

  @Test(expected = IBVersionException.class)
  public void testRequirementStrictFail() {
    final IBVersionRequirement v = DefaultIBVersion.DefaultIBVersionRequirement.buildStrict("1.0");
    assertNotNull("Loose is a thing", v);
  }

  @Test
  public void testRequirementStrictPass() {
    final IBVersionRequirement v = DefaultIBVersion.DefaultIBVersionRequirement
        .buildStrict("1.0.1");
    assertNotNull("1.0.1 is a thing", v);
  }

  @Test
  public void testType() {
    final IBVersionType v = IBVersion.IBVersionType.valueOf("NPM");
    assertNotNull("NPM is a thing", v);
  }

}
