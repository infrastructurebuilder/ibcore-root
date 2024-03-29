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
package org.infrastructurebuilder.util.artifacts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GithubIOGroupId2OrgMapperTest {

  private static final String TESTORG = "testorg";
  private static final String IO_GITHUB = "io.github.";
  private GithubIOGroupId2OrgMapper m;

  @BeforeEach
  public void setUp() throws Exception {
    this.m = new GithubIOGroupId2OrgMapper();
  }

  @Test
  public void testApply() {
    assertEquals(TESTORG, m.apply(IO_GITHUB + TESTORG).get());
    assertEquals(TESTORG, m.apply(IO_GITHUB + TESTORG + ".otherthingy").get());
    assertFalse(m.apply("com.github." + TESTORG).isPresent());

  }

}
