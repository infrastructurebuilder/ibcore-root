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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.move;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.data.IBDataException.cet;
import static org.infrastructurebuilder.data.IBMetadataUtils.IBDATA;
import static org.infrastructurebuilder.data.IBMetadataUtils.IBDATASET_XML;
import static org.infrastructurebuilder.data.IBMetadataUtils.toDataStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.infrastructurebuilder.data.IBDataException;
import org.infrastructurebuilder.data.IBDataStreamSupplier;
import org.infrastructurebuilder.data.IBMetadataUtils;
import org.infrastructurebuilder.data.model.io.xpp3.IBDataSourceModelXpp3ReaderEx;
import org.infrastructurebuilder.data.model.io.xpp3.IBDataSourceModelXpp3Writer;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.files.DefaultIBChecksumPathType;
import org.infrastructurebuilder.util.files.IBChecksumPathType;
import org.infrastructurebuilder.util.files.TypeToExtensionMapper;

public class IBDataModelUtils {

  public final static void writeDataSet(DataSet ds, Path target) {
    try (Writer writer = IBDataException.cet.withReturningTranslation(
        () -> Files.newBufferedWriter(target.resolve(IBDATA).resolve(IBDATASET_XML), UTF_8, CREATE_NEW))) {
      cet.withTranslation(() -> new IBDataSourceModelXpp3Writer().write(writer, ds.clone()));
    } catch (IOException e) {
      throw new IBDataException(e);
    }
  }

  public final static Checksum fromPathDSAndStream(Path workingPath, DataSet ds) {
    return ChecksumBuilder.newInstance(of(workingPath))
        // Checksum of data of streams
        .addChecksum(new Checksum(ds.getStreams().stream().map(s -> s.getChecksum()).collect(toList())))
        // Checksum of stream metadata
        .addChecksum(ds.getIdentifierChecksum()).asChecksum();
  }

  public final static Function<? super InputStream, ? extends DataSet> mapInputStreamToDataSet = (in) -> {
    IBDataSourceModelXpp3ReaderEx reader;
    DataSetInputSource dsis;

    reader = new IBDataSourceModelXpp3ReaderEx();
    dsis = new DataSetInputSource();
    try {
      return cet.withReturningTranslation(() -> reader.read(in, true, dsis));
    } finally {
      cet.withTranslation(() -> in.close());
    }
  };

  /**
   * Given the parameters, create a final data location (either through atomic moves or copies), that maps to a state that
   * will allow us to generate an archive
   *
   * @param workingPath  This is a current working path.
   * @param finalData This is as much of the metadata of the dataset as we currently know.  It has no streams attached at the moment
   * @param ibdssList These are the streams we will attach
   * @return A location suitable for archive generation
   * @throws IOException
   */
  public final static IBChecksumPathType forceToFinalizedPath(Path workingPath, DataSet finalData,
      List<IBDataStreamSupplier> ibdssList, TypeToExtensionMapper t2e) throws IOException {

    // This archive is about to be created
    finalData.setCreationDate(new Date()); // That is now
    Path newWorkingPath = workingPath.getParent().resolve(UUID.randomUUID().toString());
    // We're moving everything to a new path
    Files.createDirectories(newWorkingPath);
    finalData.setStreams(
        // The list of streams
        ibdssList.stream()
            .map(dss -> dss.relocateTo(newWorkingPath, t2e))
            // Fetch the IBDS
            .map(IBDataStreamSupplier::get)
            // Map the IBDataStream to a DataStream object
            .map(toDataStream)
            // to list
            .collect(toList()));
    // The id of the archive is based on the checksum of the data within it
    Checksum dsChecksum = IBDataModelUtils.fromPathDSAndStream(newWorkingPath, finalData);
    finalData.setUuid(dsChecksum.asUUID().get().toString());
    // We're going to relocate the entire directory to a named UUID-backed directory
    Path newTarget = workingPath.getParent().resolve(finalData.getUuid());
    move(newWorkingPath, newTarget, ATOMIC_MOVE);
    // Create the IBDATA dir so that we can write the metadata xml
    createDirectories(newTarget.resolve(IBDATA));
    // Clear the path so that it doesn't persist in the metadata xml
    finalData.setPath(null);
    // write the dataset to disk
    IBDataModelUtils.writeDataSet(finalData, newTarget);
    // newTarget now points to a valid DataSet with metadata and referenced streams
    return DefaultIBChecksumPathType.from(newTarget, dsChecksum, IBMetadataUtils.APPLICATION_IBDATA_ARCHIVE);
  }

}
