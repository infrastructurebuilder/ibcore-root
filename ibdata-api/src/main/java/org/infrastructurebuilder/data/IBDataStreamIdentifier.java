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

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.util.IBUtils;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.ChecksumEnabled;
import org.w3c.dom.Document;

public interface IBDataStreamIdentifier extends ChecksumEnabled {

  String IBDATA_PREFIX = "IBDataTemp_";
  String IBDATA_SUFFIX = ".ibdata";

  UUID getId();

  Optional<URL> getURL();

  Optional<String> getName();

  Optional<String> getDescription();

  Checksum getChecksum();

  Date getCreationDate();

  Document getMetadata();

  String getMimeType();

  default Checksum getMetadataChecksum() {
    return ChecksumBuilder.newInstance()
        // URL
        .addString(getURL().map(URL::toExternalForm))
        // Name
        .addString(getName())
        // Desc
        .addString(getDescription())
        // Date
        .addInstant(getCreationDate().toInstant())
        // Mime type
        .addString(getMimeType())
        // metadata
        .addChecksum(IBMetadataUtils.asChecksum.apply(getMetadata()))
        //
        .asChecksum();
  }

  /**
   * Path to the URL of the stream (wherever it is) relative to the parent dataset's path.
   * @return possibly null version of toExternalForm version of the URL of the URI of the stream data
   */
  String getPath();

  /**
   * SLIGHLY DIFFERENT CHECKSUM!
   * This is a checksum of the data + metadata
   */
  @Override
  default Checksum asChecksum() {
    return new Checksum(Arrays.asList(getChecksum(), getMetadataChecksum()));
  }

  /**
   * This is tricky.  The parent URL must exist to be able to get the child URL (obvs).
   * @param parent non-null URL From IBDataSetIdentifier.pathAsURL().get()
   * @return Optional URL mapped to a string
   */

  default Optional<URL> pathAsURL(URL parent) {
    return IBUtils.nullSafeURLMapper.apply(Optional.ofNullable(getPath()).map(path -> {
      String y = Objects.requireNonNull(parent).toExternalForm();
      StringBuilder x = new StringBuilder(y);
      // URLS are paths into jar/zip files generally
      x.append((y.endsWith(".jar") || y.endsWith(".zip")) ? "!" : "");
      return x.append(path).toString();
    }).orElse(null));
  }
}