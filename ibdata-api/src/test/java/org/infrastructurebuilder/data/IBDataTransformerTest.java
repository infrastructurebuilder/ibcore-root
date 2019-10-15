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

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class IBDataTransformerTest {

  private IBDataTransformer i;

  @Before
  public void setUp() throws Exception {
    i = new IBDataTransformer() {

      @Override
      public Logger getLog() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public IBDataTransformationResult transform(IBDataSet ds, List<IBDataStream> suppliedStreams,
          boolean failOnError) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public String getHint() {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

  @Test
  public void test() {
    assertTrue(i.respondsTo(null));
    assertEquals(i, i.configure(new HashMap<>()));
  }

}