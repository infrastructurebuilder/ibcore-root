/*
 * @formatter:off
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
 * @formatter:on
 */
package org.infrastructurebuilder.util.mavendownloadplugin;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import javax.annotation.Nonnull;

import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.DefaultArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

public class FakeArchiverManager extends DefaultArchiverManager {

  public FakeArchiverManager() {
    super(Map.of("zip", () -> new ZipArchiver()), Map.of("zip", () -> new ZipUnArchiver()), Collections.emptyMap());
    // TODO Auto-generated constructor stub
  }

  private static String getFileExtension(final File file) {
    final String path = file.getAbsolutePath();

    String archiveExt = FileUtils.getExtension(path).toLowerCase(Locale.ENGLISH);

    if ("gz".equals(archiveExt) || "bz2".equals(archiveExt) || "xz".equals(archiveExt) || "snappy".equals(archiveExt)) {
      final String[] tokens = StringUtils.split(path, ".");

      if (tokens.length > 2 && "tar".equals(tokens[tokens.length - 2].toLowerCase(Locale.ENGLISH))) {
        archiveExt = "tar." + archiveExt;
      }
    }

    return archiveExt;

  }

  @Override

  public Archiver getArchiver(final File file) throws NoSuchArchiverException {
    return getArchiver(getFileExtension(file));
  }

  @Override

  public Archiver getArchiver(final String archiverName) throws NoSuchArchiverException {
    return new ZipArchiver();
  }

  @Override

  public UnArchiver getUnArchiver(final File file) throws NoSuchArchiverException {
    return getUnArchiver(getFileExtension(file));
  }

  @Override

  public UnArchiver getUnArchiver(final String unArchiverName) throws NoSuchArchiverException {
    return new ZipUnArchiver();
  }

  @Override
  public Collection<String> getAvailableArchivers() {
    // TODO Auto-generated method stub
    return List.of("zip");
  }

  @Override
  public Collection<String> getAvailableUnArchivers() {
    // TODO Auto-generated method stub
    return List.of("zip");
  }

  @Override
  public Collection<String> getAvailableResourceCollections() {
    // TODO Auto-generated method stub
    return List.of("zip");
  }

}
