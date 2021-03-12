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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.infrastructurebuilder.util.constants.IBConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AzureDevopsGroupId2OrgMapperTest {

  private AzureDevopsGroupId2OrgMapper ads;

  private static final String VER = "1.0.0";
  private static final String A = "y";
  private GAV gav;

  @BeforeEach
  public void setUp() throws Exception {
    ads = new AzureDevopsGroupId2OrgMapper();
    gav = new DefaultGAV("x:" + A + ":" + VER);
  }

  @Test
  public void testApply() {
    assertEquals(Optional.of("Y"), ads.apply("https://dev.azure.com/Y/"));
  }

  @Test
  public void testGetId() {
    assertEquals(IBConstants.AZUREDEVOPS, ads.getId());
  }

}
