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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.infrastructurebuilder.data.IBDataEngine;
import org.infrastructurebuilder.data.model.io.xpp3.IBDataSourceModelXpp3Reader;
import org.infrastructurebuilder.data.model.io.xpp3.IBDataSourceModelXpp3ReaderEx;
import org.infrastructurebuilder.data.model.io.xpp3.IBDataSourceModelXpp3Writer;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.junit.Before;
import org.junit.Test;

public class DataSetReadWriteModel0_11Test {

  private DataStream ds;
  private DataSet set;
  private Xpp3Dom d;

  @Before
  public void setUp() throws Exception {

    Map<String, String> meta = new HashMap<>();

    //    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //    DocumentBuilder builder = factory.newDocumentBuilder();
    //    d = builder.newDocument();
    d = new Xpp3Dom("metadata");
    meta.entrySet().forEach(entry -> {
      Xpp3Dom d2 = new Xpp3Dom(entry.getKey());
      d2.setValue(entry.getValue());
      d.addChild(d2);
      //      Element e = d.createElement(entry.getKey());
      //      e.setTextContent(Objects.requireNonNull(entry.getValue(), "setText value for " + entry.getKey() + " is null"));
      //      d.appendChild(e);
    });
    set = new DataSet();
    set.setCreationDate(new Date());
    set.setDataSetDescription("setDescription");
    set.setModelEncoding("UTF-8");
    set.setModelVersion(IBDataEngine.API_ENGINE_VERSION.toString());
    set.setUuid(UUID.randomUUID().toString());
    ds = new DataStream();
    ds.setDataStreamDescription("description 1");
    ds.setCreationDate(new Date());
    ds.setDataStreamName("one");
    ds.setSha512(new Checksum().toString());
    ds.setSourceURL("https://www.google.com");
    ds.setUuid(UUID.randomUUID().toString());
    ds.setMetadata(d);
    set.addStream(ds);
    ds.setCreationDate(new Date());
    ds.setDataStreamName("two");
    ds.setDataStreamDescription("description two");
    set.addStream(ds);

    StringWriter sw = new StringWriter();
    new IBDataSourceModelXpp3Writer().write(sw, set);

    String k = sw.toString();
    String v = k;
  }

  @Test
  public void testReaderEx() throws IOException, XmlPullParserException {
    IBDataSourceModelXpp3ReaderEx reader;
    DataSetInputSource dsis;

    reader = new IBDataSourceModelXpp3ReaderEx();
    dsis = new DataSetInputSource();
    try (InputStream in = getClass().getResourceAsStream("/test-input-0.11.xml")) {
      DataSet read = reader.read(in, true, dsis);
      assertEquals("310dc0e2-109d-4237-9729-e266176e1c7a", read.getId().toString());
    }
  }

  @Test
  public void testReader() throws IOException, XmlPullParserException {
    IBDataSourceModelXpp3Reader reader = new IBDataSourceModelXpp3Reader();
    try (InputStream in = getClass().getResourceAsStream("/test-input-0.11.xml")) {
      DataSet read = reader.read(in, true);
      assertEquals("310dc0e2-109d-4237-9729-e266176e1c7a", read.getId().toString());
    }
  }

}
