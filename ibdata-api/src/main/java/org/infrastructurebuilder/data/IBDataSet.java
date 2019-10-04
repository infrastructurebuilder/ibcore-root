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
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.data.IBDataException.cet;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.infrastructurebuilder.data.model.DataSet;
import org.infrastructurebuilder.data.model.DataSetInputSource;
import org.infrastructurebuilder.data.model.io.xpp3.IBDataSourceModelXpp3ReaderEx;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.files.DefaultIBChecksumPathType;
import org.infrastructurebuilder.util.files.IBChecksumPathType;

/**
 * An IBDataSet is a logical grouping of D
 * @author mykel.alvis
 *
 */
public interface IBDataSet extends IBDataSetIdentifier {

  List<IBDataStreamSupplier> getStreamSuppliers();

  default List<UUID> getStreamIds() {
    return getStreamSuppliers().stream().map(ss -> ss.getId()).collect(toList());
  }

  /**
   * Get the aggregated checksum of all the checksums of all the data streams.
   * This is not a checksum of all the data bytes.  This is a checksum of
   * the list of data stream checksums
   *
   * @return Checksum of all the summary streams' data.  Empty if no streams
   */
  default Optional<Checksum> getDataChecksum() {
    return ofNullable(getStreamSuppliers().size() > 0
        ? new Checksum(
            getStreamSuppliers().stream().map(Supplier::get).map(ds -> ds.getChecksum()).collect(Collectors.toList()))
        : null);
  }

  /**
   * Get the aggregated checksum of all the
   * @return
   */
  default Checksum getDataSetMetadataChecksum() {
    return ChecksumBuilder.newInstance()
        // data checksum
        .addListChecksumEnabled(getStreamSuppliers().stream().map(Supplier::get).collect(Collectors.toList()))
        .asChecksum();

  }

  default IBChecksumPathType asChecksumType() {
    Checksum c = new Checksum();
    return DefaultIBChecksumPathType.from(Paths.get(getPath()), c, IBDataIngester.APPLICATION_IBDATA_ARCHIVE);
  }

}
