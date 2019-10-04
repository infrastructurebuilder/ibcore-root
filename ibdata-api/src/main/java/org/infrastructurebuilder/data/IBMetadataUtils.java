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

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.data.IBDataException.cet;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.infrastructurebuilder.data.model.DataStream;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class IBMetadataUtils {
  public final static String UNCONFIGURABLE = "<!-- UNCONFIGURABLE -->";
  public final static String TRANSFORMERSLIST = UNCONFIGURABLE + ".transformers";
  public final static String RECORD_SPLITTER = ",";
  public final static String MAP_SPLITTER = "|";
  public final static String WORKING_PATH_CONFIG_ITEM = UNCONFIGURABLE + ".workingPath";

  public static final String IBDATA = "IBDATA-INF";
  public static final String IBDATA_DIR = "/" + IBDATA + "/";
  public static final String IBDATASET_XML = "ibdataset.xml";
  public static final String IBDATA_IBDATASET_XML = IBDATA_DIR + IBDATASET_XML;
  private final static TransformerFactory tf = TransformerFactory.newInstance();
  private final static Supplier<Transformer> tfSupplier = () -> {
    return cet.withReturningTranslation(() -> tf.newTransformer());
  };
  public final static Function<Object, Document> fromXpp3Dom = (document) -> cet
      .withReturningTranslation(() -> (document instanceof Document) ?
      // Already a document
          (Document) document :
          // Not yet a document
          (DocumentBuilderFactory.newInstance().newDocumentBuilder()
              .parse(new InputSource(new StringReader(document.toString()))))
      // returned translation
      );
  public final static Function<Document, String> stringifyDocument = (document) -> cet.withReturningTranslation(() -> {
    StringWriter writer = new StringWriter();
    cet.withTranslation(() -> tfSupplier.get().transform(new DOMSource(document), new StreamResult(writer)));
    return writer.toString();
  });

  public final static Function<Object, Object> translateToXpp3Dom = (document) -> cet.withReturningTranslation(() -> {
    if (document instanceof Xpp3Dom)
      return (Xpp3Dom) document;
    else if (document instanceof Document) {
      Document d = (Document) document;
      if (d.hasAttributes() || d.hasChildNodes())
        return Xpp3DomBuilder.build(new StringReader(stringifyDocument.apply(d)));
      else
        return new Xpp3Dom("metadata");
    } else if (document instanceof XmlPlexusConfiguration) {
      return Xpp3DomBuilder.build(new StringReader(document.toString()), true);
    } else

      return null;
  });

  public final static Function<Document, Checksum> asChecksum = (metadata) -> {

    return new Checksum(stringifyDocument.apply(metadata).getBytes(StandardCharsets.UTF_8));
  };

  public final static Function<IBDataStream, DataStream> toDataStream = (ibds) -> {
    DataStream ds = new DataStream();
    requireNonNull(ibds).getName().ifPresent(ds::setDataStreamName);
    ibds.getDescription().ifPresent(ds::setDataStreamDescription);
    ibds.getURL().ifPresent(u -> ds.setSourceURL(u.toExternalForm()));
    ds.set_metadata(IBMetadataUtils.translateToXpp3Dom.apply((Object) ibds.getMetadata()));
    ds.setMimeType(ibds.getMimeType());
    ds.setSha512(ibds.getChecksum().toString());
    ds.setUuid(ibds.getChecksum().asUUID().get().toString());
    return ds;
  };
}
