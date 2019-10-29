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

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.IBConstants;
import org.junit.Before;
import org.junit.Test;

public class IBDataStreamIdentifierTest extends AbstractModelTest {

  private UUID id;
  private URL url;
  private URL paU;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    url = new URL(SOURCE_URL);
  }

  @Test
  public void testGetMetadataAsDocument() {
    assertEquals(METADATA_CHECKSUM, IBMetadataUtils.asChecksum.apply(stream.getMetadataAsDocument()).toString());
  }

  @Test
  public void testIBDataSTreamIdentifierStuff() {
    assertEquals(STREAM_CHECKSUM, stream.asChecksum().toString());
    assertEquals(STREAM_METADATA_CHECKSUM, stream.getMetadataChecksum().toString());
    assertEquals(NAME, stream.getName().get());
    assertEquals(DESC, stream.getDescription().get());
    assertEquals(STREAM_CHECKSUM, stream.getChecksum().toString());
    assertEquals(now, stream.getCreationDate());
    assertEquals(url, stream.getURL().get());
    assertEquals(IBConstants.APPLICATION_OCTET_STREAM, stream.getMimeType());
    assertEquals(Optional.empty(), stream.pathAsURL(finalData));
    assertEquals(STREAM_ID, stream.getId().toString());
  }

}
