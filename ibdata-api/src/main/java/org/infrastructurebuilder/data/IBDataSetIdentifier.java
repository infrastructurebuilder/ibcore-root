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

import static org.infrastructurebuilder.data.IBDataException.cet;

import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.GAV;
import org.infrastructurebuilder.util.artifacts.impl.DefaultGAV;
import org.w3c.dom.Document;

/**
 * This is the top-level interface that describes a DataSet.
 *
 * A DataSet is a group of common metadata that holds a set of DataStreams
 *
 * Under nearly every circumstance, one might think of a DataSet as an archive (a jar or zip file),
 * since an archive can only have a single DataSet metadata file
 * @author mykel.alvis
 *
 */
public interface IBDataSetIdentifier {
  public final static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  public final static Supplier<DocumentBuilder> builderSupplier = () -> cet
      .withReturningTranslation(() -> factory.newDocumentBuilder());
  public final static Supplier<Document> emptyDocumentSupplier = () -> builderSupplier.get().newDocument();

  String getGroupId();
  String getArtifactId();
  String getVersion();

  default GAV getGAV() {
    return new DefaultGAV(getGroupId(), getArtifactId(), getVersion());
  }

  UUID getId();

  Optional<String> getName();

  Optional<String> getDescription();

  Date getCreationDate();

  Object getMetadata();

  /**
   * Possibly null representation of where this dataset currently exists.
   * This value should be nulled out prior to persisting the model, and must
   * be set by hand when deserializing it.
   *
   * @return
   */
  String getPath();

  default Checksum getIdentifierChecksum() {
    return ChecksumBuilder.newInstance()
        //
        .addString(getName())
        //
        .addString(getDescription())
        //
        .addInstant(getCreationDate().toInstant())
        //
        //        .addChecksum(IBMetadataUtils.asChecksum.apply((Document)getMetadata()))
        // fin
        .asChecksum();
  }

  default Optional<URL> pathAsURL() {
    return org.infrastructurebuilder.util.IBUtils.nullSafeURLMapper.apply(getPath());
  }

  default Document metdataAsDocument() {
    return IBMetadataUtils.fromXpp3Dom.apply(getMetadata());
  }
}