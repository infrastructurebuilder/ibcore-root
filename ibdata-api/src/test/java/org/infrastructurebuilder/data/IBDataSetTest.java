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

import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.data.IBMetadataUtilsTest.TEST_INPUT_0_11_XML;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.infrastructurebuilder.data.model.DataSet;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class IBDataSetTest extends AbstractModelTest {

  private IBDataSet ds;
  private List<String> idList = Arrays.asList("ABC");

  @Before
  public void setUp() throws Exception {
    super.setUp();
    try (InputStream t = getClass().getResourceAsStream(TEST_INPUT_0_11_XML);) {
      DataSet dsRead = IBDataModelUtils.mapInputStreamToDataSet.apply(t);
      dsRead.setPath(ibd.toAbsolutePath().toString());
      ds = new FakeIBDataSet(dsRead, o11Paths);
      assertEquals(2, ds.getStreamSuppliers().size());
    }
  }

  @Test
  public void testGetStreamSuppliers() {
    assertEquals("183d3030-6dae-4f33-acde-79eacbaa8c2d", ds.getStreamSuppliers().get(0).getId().toString());
  }

  @Ignore
  @Test
  public void testGetDataChecksum() {
    assertEquals("abc", ds.getDataChecksum().get().toString());
  }

  @Ignore
  @Test
  public void testGetDataSetMetadataChecksum() {
    assertEquals(
        "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
        ds.getDataSetMetadataChecksum().toString());
  }

  @Test
  public void testAsChecksumType() {
    Checksum v = ds.asChecksumType().getChecksum();
    assertEquals(new Checksum(), v);
  }

  @Test
  public void testAsStreamsList() {
    List<String> k =  ds.asStreamsList().stream().map(IBDataStream::getId).map(UUID::toString).collect(toList());
    List<String> expected = Arrays.asList("183d3030-6dae-4f33-acde-79eacbaa8c2d", "0dfb7bc9-73aa-4f7e-b735-cbccfa052733");
    assertEquals(expected,k);
  }

  @Test
  public void testGetGAV() {
    assertEquals(gav, ds.getGAV());
  }

}
