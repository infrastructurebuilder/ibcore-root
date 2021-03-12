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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.GAVMatcher;
import org.infrastructurebuilder.util.artifacts.IBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion.RangeOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultGAVMatcherTest {

  private GAVMatcher m, n;
  private GAV gav,gav2,gav3;
  private IBVersion i;

  @BeforeEach
  public void setUp() throws Exception {
    i = new DefaultIBVersion("1.0.0");
    m = new DefaultGAVMatcher(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty());
//    n = new DefaultGAVMatcher(Optional.of(Pattern.compile("x")), Optional.of(Pattern.compile("y")),
//        Optional.of(i.forRange(RangeOperator.GTE)), Optional.empty(), Optional.of(Pattern.compile("z")),
//        Optional.of(Pattern.compile("jar")));
    n = DefaultGAVMatcher.from(Optional.of("x"), Optional.of("y"), Optional.of(i.forRange(RangeOperator.GTE)), Optional.empty() , Optional.of("z"), Optional.of("jar"));
    gav = new DefaultGAV("x:y:1.0.0:jar:z");
    gav2 = new DefaultGAV("x:y:2.0.0:jar:z");
    gav3 = new DefaultGAV("x","y","z",null,"jar");
  }

  @Test
  public void testMatches() {
    assertTrue(m.matches(gav, true));
  }

  @Test
  public void testOptionals() {
    assertTrue(n.matches(gav, true));
    assertTrue(n.matches(gav3, true));
  }

}
