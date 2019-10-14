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
package org.infrastructurebuilder.data.model;

import static org.infrastructurebuilder.data.IBMetadataUtils.IBDATA;
import static org.infrastructurebuilder.data.IBMetadataUtils.IBDATASET_XML;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.infrastructurebuilder.data.IBDataException;
import org.infrastructurebuilder.data.IBDataModelUtils;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.junit.Before;
import org.junit.Test;

public class IBDataModelUtilsTest {

  private WorkingPathSupplier wps;

  @Before
  public void setUp() throws Exception {
    wps = new WorkingPathSupplier();
  }

  @Test(expected = IBDataException.class)
  public void testWriteDataSet() {
    Path p = Paths.get(".").resolve(UUID.randomUUID().toString()).resolve(UUID.randomUUID().toString());
    IBDataModelUtils.writeDataSet(null, p);
  }

  @Test
  public void test() throws IOException {
    Path target = wps.get();
    Files.createDirectories(target.resolve(IBDATA));
    DataSet ds = new DataSet();
    IBDataModelUtils.writeDataSet(ds, target);
    assertTrue(Files.exists(target.resolve(IBDATA).resolve(IBDATASET_XML)));
  }

}
