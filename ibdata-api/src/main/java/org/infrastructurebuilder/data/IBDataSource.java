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
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

import org.infrastructurebuilder.IBConstants;
import org.infrastructurebuilder.util.BasicCredentials;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.w3c.dom.Document;

/**
 * An IBDataSource understands where a data stream originates and how to acquire it.
 * Furthermore, it actually acquires that datastream.
 *
 * An IBDataSource always returns a Path pointer to some acquired tempfile.
 * The contract for IBDataSource:
 *          1. An IBData
 *          1. the Path supplied is to the same temp file every time.
 *
 * @author mykel.alvis
 *
 */
public interface IBDataSource extends Supplier<Path> {
  URL getSourceURL();
  Optional<BasicCredentials> getCredentials();
  Optional<Checksum> getChecksum();
  Optional<Document> getMetadata();
  Optional<String> getName();
  Optional<String> getDescription();
  /**
   * This is really a descriptive value, although it needs to be unique as well
   * @return
   */
  String getId();

  Checksum getActualChecksum();

  default String getMimeType() {
    return IBConstants.APPLICATION_OCTET_STREAM;
  }
}
