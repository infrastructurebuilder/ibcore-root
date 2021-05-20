/*
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
package org.infrastructurebuilder.util.settings;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ActivationOSProxyTest {

  private static final String _1_0_0 = "1.0.0";
  private static final String MYNAME = "myname";
  private static final String WINDOWS = "Windows";
  private static final String I386 = "i386";
  private ActivationOSProxy a;

  @BeforeEach
  public void setUp() throws Exception {
    a = new ActivationOSProxy(of(I386), of(WINDOWS), of(MYNAME), of(_1_0_0));
  }

  @Test
  public void test() {
    assertEquals(I386, a.getArch().get());
    assertEquals(WINDOWS, a.getFamily().get());
    assertEquals(MYNAME, a.getName().get());
    assertEquals(_1_0_0, a.getVersion().get());
  }

}
