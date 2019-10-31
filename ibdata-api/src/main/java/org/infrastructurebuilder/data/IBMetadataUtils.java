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
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import static org.infrastructurebuilder.data.IBDataException.cet;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.xml.parsers.DocumentBuilder;
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

  public final static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  public final static Supplier<DocumentBuilder> builderSupplier = () -> cet
      .withReturningTranslation(() -> factory.newDocumentBuilder());
  public final static Supplier<Document> emptyDocumentSupplier = () -> builderSupplier.get().newDocument();

  private final static TransformerFactory tf = TransformerFactory.newInstance();
  private final static Supplier<Transformer> tfSupplier = () -> {
    return cet.withReturningTranslation(() -> tf.newTransformer());
  };

  /**
   * Map an object to a W3C Document.  If null, returns an empty Document
   */
  public final static Function<Object, Document> fromXpp3Dom = (document) -> Optional
      .ofNullable(cet.withReturningTranslation(
          () -> (Optional.ofNullable(document).orElse(emptyDocumentSupplier.get()) instanceof Document) ? // Is it a Document
              (Document) document : // Already a document
              (newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(document.toString())))) // Make it a document
      ))

      .orElse(emptyDocumentSupplier.get());

  public final static Function<Document, String> stringifyDocument = (document) -> cet.withReturningTranslation(() -> {
    StringWriter writer = new StringWriter();
    cet.withTranslation(() -> tfSupplier.get().transform(new DOMSource(document), new StreamResult(writer)));
    return writer.toString();
  });

  public final static Function<Object, Xpp3Dom> translateToXpp3Dom = (document) -> cet.withReturningTranslation(() -> {
    if (document instanceof Xpp3Dom)
      return (Xpp3Dom) document;
    else if (document instanceof Document) {
      Document d = (Document) document;
      if (d.hasAttributes() || d.hasChildNodes())
        return Xpp3DomBuilder.build(new StringReader(stringifyDocument.apply(d)));
      else
        return new Xpp3Dom("metadata");
    } else if (document instanceof XmlPlexusConfiguration || document instanceof String) {
      return Xpp3DomBuilder.build(new StringReader(document.toString()), true);
    } else

      return null;
  });

  public final static Function<Document, Checksum> asChecksum = (metadata) -> {
    return new Checksum(new ByteArrayInputStream(stringifyDocument.apply(metadata).getBytes(StandardCharsets.UTF_8)));
  };

  public final static Function<IBDataStream, DataStream> toDataStream = (ibds) -> {
    DataStream ds = new DataStream();
    requireNonNull(ibds).getName().ifPresent(ds::setDataStreamName);
    ds.setCreationDate(ibds.getCreationDate());
    ibds.getDescription().ifPresent(ds::setDataStreamDescription);
    ibds.getURL().ifPresent(u -> ds.setSourceURL(u.toExternalForm()));
    ds.setMetadata(IBMetadataUtils.translateToXpp3Dom.apply((Object) ibds.getMetadata()));
    ds.setMimeType(ibds.getMimeType());
    ds.setSha512(ibds.getChecksum().toString());
    ds.setUuid(ibds.getChecksum().asUUID().get().toString());
    ds.setPath(ibds.getPath());
    return ds;
  };
  //public final static Document extractDatasetMetadataTheHardWay(final Xpp3Dom xpp) throws MojoFailureException {
  ////    Xpp3Dom xpp = getMojo().getConfiguration();
  //
  //final XPath xPath = XPathFactory.newInstance().newXPath();
  //final Document document = IBMetadataUtils.fromXpp3Dom.apply(xpp);
  //try {
  //  final NodeList nodeList = (NodeList) xPath.compile("/configuration/ingestionConfig/dataSet/metadata")
  //      .evaluate(document, XPathConstants.NODESET);
  //  switch (nodeList.getLength()) {
  //  case 0:
  //    return IBDataSetIdentifier.emptyDocumentSupplier.get();
  //  case 1:
  //    final Node nNode = nodeList.item(1);
  //    final Document d = IBDataSetIdentifier.emptyDocumentSupplier.get();
  //    d.appendChild(d.importNode(nNode, true));
  //    return d;
  //  default:
  //    throw new MojoFailureException("Too many metadata nodes for DataSet");
  //  }
  //} catch (final XPathExpressionException e) {
  //  throw new MojoFailureException("Failed to acquire metadata", e);
  //}
  //}
  //
  //public final static Map<String, Document> extractDatastreamsMetadataTheHardWay(final Xpp3Dom xpp)
  //  throws MojoFailureException {
  //final XPath xPath = XPathFactory.newInstance().newXPath();
  //final Document document = IBMetadataUtils.fromXpp3Dom.apply(xpp);
  //try {
  //  final NodeList nodeList = (NodeList) xPath.compile("/configuration/ingestionConfig/dataSet/streams/id")
  //      .evaluate(document, XPathConstants.NODESET);
  //  final Map<String, Document> dMap = new HashMap<>();
  //  for (int i = 0; i < nodeList.getLength(); ++i) {
  //    final Node nNode = nodeList.item(i);
  //
  //    final String key = "default";
  //    final Document d = IBDataSetIdentifier.emptyDocumentSupplier.get();
  //    d.appendChild(d.importNode(nNode, true));
  //    dMap.putIfAbsent(key, d);
  //  }
  //  return dMap;
  //} catch (final XPathExpressionException e) {
  //  throw new MojoFailureException("Failed to acquire metadata", e);
  //}
  //}
  /**
   * FIXME Move this to IBMetadataUtils
   * Function to compare W3c Dcoument instances (by string compare, like a filthy animal
   */
  public final static BiFunction<Document, Document, Boolean> w3cDocumentEqualser = (lhs, rhs) -> {
    return stringifyDocument.apply(lhs).equals(stringifyDocument.apply(rhs));
  };
}
