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

import static org.infrastructurebuilder.util.IBUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.move;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
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
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.infrastructurebuilder.data.model.DataSet;
import org.infrastructurebuilder.data.model.DataSetInputSource;
import org.infrastructurebuilder.data.model.DataStream;
import org.infrastructurebuilder.data.model.io.xpp3.IBDataSourceModelXpp3ReaderEx;
import org.infrastructurebuilder.data.model.io.xpp3.IBDataSourceModelXpp3Writer;
import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.files.DefaultIBChecksumPathType;
import org.infrastructurebuilder.util.files.IBChecksumPathType;
import org.infrastructurebuilder.util.files.TypeToExtensionMapper;

public class IBDataModelUtils {
  public final static IBDataSourceModelXpp3Writer xpp3Writer = new IBDataSourceModelXpp3Writer();

  public final static void writeDataSet(DataSet ds, Path target) {
    try (Writer writer = Files.newBufferedWriter(target.resolve(IBDATA).resolve(IBDATASET_XML), UTF_8, CREATE_NEW)) {
      xpp3Writer.write(writer, ds.clone());
    } catch (Throwable e) { // Catch anything and translate it to an IBDataException
      throw new IBDataException(e);
    }
  }

  public final static void mutatingDataSetCloneHook(DataSet ds) {
    ds.getStreams().forEach(s -> s.setPath(relativizePath(ds, s)));
  }

  public final static void mutatingDataStreamCloneHook(DataStream s) {
    //    ds.getStreams().forEach(s -> s.setPath(relativizePath(ds, s)));
  }

  public final static String relativizePath(DataSet ds, DataStream s) {
    return nullSafeURLMapper.apply(ds.getPath()).map(u -> {
      String u1 = u.toExternalForm();
      String s2 = s.getPath();
      return (s2.startsWith(u1)) ? s2.substring(u1.length()) : s2;
    }).orElse(s.getPath());
  }

  public final static Function<String, Optional<UUID>> safeMapUUID = (s) -> cet
      .withReturningTranslation(() -> ofNullable(s).map(UUID::fromString));

  public final static Function<String, Optional<URL>> safeMapURL = (s) -> ofNullable(s)
      .map(u -> cet.withReturningTranslation(() -> new URL(u)));

  public final static Function<IBDataSetIdentifier, Checksum> dataSetIdentifierChecksum = (ds) -> {
    return ChecksumBuilder.newInstance()
        // Group
        .addString(ds.getGroupId())
        // artifact
        .addString(ds.getArtifactId())
        // version
        .addString(ds.getVersion())
        //
        .addString(ds.getName())
        //
        .addString(ds.getDescription())
        //
        .addDate(ds.getCreationDate())
        // fin
        .asChecksum();
  };

  public final static Checksum fromPathDSAndStream(Path workingPath, DataSet ds) {
    return ChecksumBuilder.newInstance(of(workingPath))
        // Checksum of data of streams
        .addChecksum(new Checksum(ds.getStreams().stream().map(s -> s.getChecksum()).collect(toList())))
        // Checksum of stream metadata
        .addChecksum(dataSetIdentifierChecksum.apply(ds)).asChecksum();
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
  public final static IBChecksumPathType forceToFinalizedPath(Date creationDate, Path workingPath, DataSet finalData,
      List<IBDataStreamSupplier> ibdssList, TypeToExtensionMapper t2e) throws IOException {

    // This archive is about to be created
    finalData.setCreationDate(requireNonNull(creationDate)); // That is now
    Path newWorkingPath = workingPath.getParent().resolve(UUID.randomUUID().toString());
    finalData
        .setPath(cet.withReturningTranslation(() -> newWorkingPath.toAbsolutePath().toUri().toURL().toExternalForm()));
    // We're moving everything to a new path
    Files.createDirectories(newWorkingPath);
    finalData.setStreams(
        // The list of streams
        ibdssList.stream()
            // Relocate the stream
            .map(dss -> dss.relocateTo(newWorkingPath, t2e))
            // Fetch the IBDS
            .map(IBDataStreamSupplier::get)
            // Map the IBDataStream to a DataStream object
            .map(toDataStream)
            // to list
            .collect(toList()));
//    finalData.getStreams().stream().forEach(dss -> dss.setPath(IBDataModelUtils.relativizePath(finalData, dss)));
    // The id of the archive is based on the checksum of the data within it
    Checksum dsChecksum = fromPathDSAndStream(newWorkingPath, finalData);
    finalData.setUuid(dsChecksum.asUUID().get().toString());
    // We're going to relocate the entire directory to a named UUID-backed directory
    Path newTarget = workingPath.getParent().resolve(finalData.getUuid());
    move(newWorkingPath, newTarget, ATOMIC_MOVE);
    finalData.setPath(newTarget.toAbsolutePath().toUri().toURL().toExternalForm());
    // Create the IBDATA dir so that we can write the metadata xml
    createDirectories(newTarget.resolve(IBDATA));
    // Clear the path so that it doesn't persist in the metadata xml
    DataSet finalData2 = finalData.clone(); // Executes the clone hook, including relativizing the path
    finalData2.setPath(null);
    // write the dataset to disk
    IBDataModelUtils.writeDataSet(finalData2, newTarget);
    // newTarget now points to a valid DataSet with metadata and referenced streams
    return DefaultIBChecksumPathType.from(newTarget, dsChecksum, IBMetadataUtils.APPLICATION_IBDATA_ARCHIVE);
  }

}
