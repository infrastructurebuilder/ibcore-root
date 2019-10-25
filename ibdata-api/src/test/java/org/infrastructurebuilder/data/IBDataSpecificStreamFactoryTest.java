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
package org.infrastructurebuilder.data;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class IBDataSpecificStreamFactoryTest {

  private IBDataSpecificStreamFactory i;

  @Before
  public void setUp() throws Exception {
    i = new IBDataSpecificStreamFactory() {

      @Override
      public List<String> getRespondTypes() {
        return Arrays.asList("A","B");
      }

      @Override
      public Optional<Stream<? extends Object>> from(IBDataStream ds) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

  @Test
  public void testGetWeight() {
    assertEquals(0,i.getWeight());
  }


  @Test
  public void testRespondsTo() {
    assertTrue(i.respondsTo("A"));
    assertTrue(i.respondsTo("B"));
    assertFalse(i.respondsTo("C"));
  }

}
