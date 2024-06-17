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

import java.util.Arrays;
import java.util.List;

import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.GAV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AzureDevopsGenericReleaseVersionMapperTest {

  private static final String VER = "1.0.0";
  private static final String A = "y";
  private AzureDevopsGenericReleaseVersionMapper ads;
  private GAV gav;

  @BeforeEach
  public void setUp() throws Exception {
    ads = new AzureDevopsGenericReleaseVersionMapper();
    gav = new DefaultGAV("x:" + A + ":" + VER);
  }

  @Test
  public void testGetId() {
    assertEquals(IBConstants.AZUREDEVOPS, ads.getId());
  }

  @Test
  public void testGetWeight() {
    assertEquals(1, ads.getWeight().intValue());
  }

  @Test
  public void testApply() {
    List<String> v = ads.apply(gav);
    assertEquals(Arrays.asList("v" + VER, VER, A + "-" + VER), v);
  }

}
