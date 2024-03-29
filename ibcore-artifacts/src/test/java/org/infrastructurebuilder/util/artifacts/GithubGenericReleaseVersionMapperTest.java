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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.IdentifiedAndWeighted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GithubGenericReleaseVersionMapperTest extends TestWeightedAndIdentified {

  private static final String x1_0_0 = "1.0.0";
  private GithubGenericReleaseVersionMapper vm;
  private DefaultGAV gav1;

  @BeforeEach
  public void setUp() throws Exception {
    this.vm = new GithubGenericReleaseVersionMapper();
    this.gav1 = new DefaultGAV("a", "b", x1_0_0, "zip");
  }

  @Override
  public IdentifiedAndWeighted getItemToTest() {
    return this.vm;
  }

  @Test
  public void testApply() {
    List<String> ret = this.vm.apply(this.gav1);
    assertEquals(3, ret.size());
    assertTrue(ret.contains("v" + x1_0_0));
    assertTrue(ret.contains(x1_0_0));
    assertTrue(ret.contains("b-" + x1_0_0));
  }

}
