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
package org.infrastructurebuilder.data.transform;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.data.FakeIBDataStream;
import org.junit.Before;
import org.junit.Test;

public class DataStreamMatcherTest {

  private static final String A = "A";
  private DataStreamMatcher dsm;
  private FakeIBDataStream ds;
  private Date now = new Date();
  private final String id = UUID.randomUUID().toString();

  @Before
  public void setUp() throws Exception {
    dsm = new DataStreamMatcher();
    dsm.setUuid(id);
    dsm.setCreationDate(now);
    dsm.setDataStreamDescription(A);
    dsm.setDataStreamName(A);
    dsm.setMimeType(A);
    ds = new FakeIBDataStream(Paths.get("."), Optional.empty());
    ds.setCreationDate(now );
    ds.setDataStreamDescription(A);
    ds.setUuid(id);
    ds.setDataStreamName(A);
    ds.setMimeType(A);

  }

  @Test
  public void testMatches() {
    assertFalse(dsm.matches(null));
    assertTrue(dsm.matches(ds));
    dsm.setCreationDate(null);
    assertTrue(dsm.matches(ds));
    dsm.setDataStreamDescription("X");
    assertFalse(dsm.matches(ds));
  }

}