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

import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.data.IBDataException.cet;
import static org.infrastructurebuilder.util.IBUtils.nullSafeURLMapper;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.ChecksumEnabled;
import org.w3c.dom.Document;

/**
 * This is the top-level interface that describes a stream of data (i.e. a single InputStream/File/what have you)
 *
 * @author mykel.alvis
 *
 */
public interface IBDataStreamIdentifier extends ChecksumEnabled {

  /**
   * <i>Usually</i> this will return the "data stream id", which a UUID generated from the bytes of a Checksum of the contents of the stream in question.
   * This IS NULLABLE, but only temporarily.  It may not be available, as there might not have been a computed checksum for something that hasn't been
   * calculated yet.
   * @return A UUID from the Checksum of the contents of the stream or null.  Null simply means there has not been a calculation on the contents yet.
   */
  UUID getId();

  /**
   * The source of this stream.  Optional, but HIGHLY important
   * @return Optional URL of the underlying stream
   */
  Optional<URL> getURL();

  /**
   * @return Optional Name supplied at creation time
   */
  Optional<String> getName();

  /**
   *
   * @return Optional description supplied at creation time
   */
  Optional<String> getDescription();

  /**
   * This is a checksum of the underlying file (used to calculate the UUID in getId()).
   * It only contains a checksum for the file, not the metadata. See getMetadataChecksum() to get checksums of all elements
   *
   * This is expected to be a non-null value unless the underlying code handles an actual stream.  In that case the value needs
   * to be calculated.
   *
   * @return  Checksum of the contents of the underlying file or throw NullPointerException
   */
  Checksum getChecksum();

  /**
   * The "creation date", which is VERY CLOSE to when this file was downloaded.
   * @return Date accepted moment when this stream was read from the source and optionally subsequently verified
   */
  Date getCreationDate();

  /**
   * Xpp3Dom instance containing the metadata supplied for THIS stream.
   *
   * No extra metadata is supplied by the default ingester, although subtypes could easily introduce or require additional metadata.
   *
   * The DataSet has the capability of aggregating metadata.
   * You should probably use that.
   *
   * Use getMetadataAsDocument for W3c Document
   *
   * @return Xpp3Dom instance describing the metadata supplied at creation time.
   */
  Object getMetadata();

  /**
   * Non-nullable mime type of the contents of the stream.
   * @return Mime type of the contents of the stream, defaulting to application/octect-stream
   */
  String getMimeType();

  /**
   * REQUIRED Path to the URL of the stream (wherever it is) relative to the parent dataset's path.
   * See pathAsURL for a reasonable representation of how to calculate the URL based on this path.
   * @return Path relative to the path supplied in the enclosing DataSet.
   *
   */
  String getPath();

  /**
   * The proper method for calculating metadata checksum
   * @return Checksum instance consisting of a checksum of all relevant entries
   */
  default Checksum getMetadataChecksum() {
    return ChecksumBuilder.newInstance()
        // URL
        .addString(getURL().map(URL::toExternalForm))
        // Name
        .addString(getName())
        // Desc
        .addString(getDescription())
        // Date
        .addDate(getCreationDate())
        // Mime type
        .addString(getMimeType())
        // metadata
        .addChecksum(IBMetadataUtils.asChecksum.apply(getMetadataAsDocument()))
        //
        .asChecksum();
  }

  @Override
  default Checksum asChecksum() {
    return getChecksum();
  }

  /**
   * This is tricky.  The parent URL must exist to be able to get the child URL (obvs).
   * @param parent non-null URL From IBDataSetIdentifier.pathAsURL().get()
   *
   * The current version probably won't work on Windows because they REALLY needed to have a different path
   * separator than the rest of the computing world.
   *
   * @return Optional URL mapped to a string
   */

  default Optional<URL> pathAsURL(IBDataSetIdentifier pDataSet) {
    return nullSafeURLMapper.apply(ofNullable(getPath()).flatMap(path -> {
      Optional<String> v = ofNullable(pDataSet.getPath())
          .map(pPath -> cet.withReturningTranslation(() -> new URL(pPath))).map(parent -> {
            String y = Objects.requireNonNull(parent).toExternalForm();
            StringBuilder x = new StringBuilder(y);
            // URLS are paths into jar/zip files generally
            x.append((y.endsWith(".jar") || y.endsWith(".zip")) ? "!" : "");
            return x.append(path).toString();
          });
      return v;

    }).orElse(null));

  }

  default Document getMetadataAsDocument() {
    return IBMetadataUtils.fromXpp3Dom.apply(getMetadata());
  }

}