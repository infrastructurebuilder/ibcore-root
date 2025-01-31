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
package org.infrastructurebuilder.util.mavendownloadplugin.cache;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

//import com.googlecode.download.maven.plugin.internal.checksum.Checksums;
import org.apache.commons.codec.digest.DigestUtils;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.util.mavendownloadplugin.checksum.DLChecksums;
//import org.apache.maven.plugin.MojoFailureException;
//import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;

/**
 * A class representing a download cache
 *
 * @author Mickael Istria (Red Hat Inc)
 *
 */
public final class DownloadCache {

  private final PathRefFileSystem basedir;
  private final FileIndex index;

//    public DownloadCache(File cacheDirectory, Log log) {
  public DownloadCache(PathRefFileSystem prfs, Logger log) {
    this.index = new FileBackedIndex(prfs, log);
    this.basedir = prfs;
  }

  private String getEntry(URI uri, final DLChecksums checksums) {
    return Optional.ofNullable(this.index.get(uri)).map(res -> {
      final PathRefPath resFile = this.basedir.getPath(res);
      return (checksums.isValid(resFile)) ? res : null;
    }).orElse(null);
  }

  /**
   * Get a File in the download cache. If no cache for this URL, or if expected checksums don't match cached ones,
   * returns null. available in cache,
   *
   * @param uri       URL of the file
   * @param checksums Supplied checksums.
   * @return A File when cache is found, null if no available cache
   */
  public PathRefPath getArtifact(URI uri, final DLChecksums checksums) {
    final String res;
    this.index.getLock().lock();
    try {
      res = this.getEntry(uri, checksums);
    } finally {
      this.index.getLock().unlock();
    }
    if (res != null) {
      return this.basedir.getPath(res);
    }
    return null;
  }

  public void install(URI uri, Path outputFile, final DLChecksums checksums)
      throws /* MojoFailureException, */ IOException {
    // basedir is a PathRefFileSystem and thus already exists
//		if (!Files.exists(basedir.getRoot()) {
//			if (!basedir.mkdirs()) {
////        throw new MojoFailureException("Could not create cache directory: " + basedir.getAbsolutePath());
//        throw new IBMavenDownloadPluginComponentException("Could not create cache directory: " + basedir.getAbsolutePath());
//			}
//		}
    this.index.getLock().lock();
    try {
      final String entry = this.getEntry(uri, checksums);
      if (entry != null) {
        return; // entry already here
      }
      final String fileName = String.format("%s_%s", outputFile.getFileName(), DigestUtils.md5Hex(uri.toString()));
      Files.copy(outputFile, this.basedir.getPath(fileName), StandardCopyOption.REPLACE_EXISTING);
      // update index
      this.index.put(uri, fileName);
    } finally {
      this.index.getLock().unlock();
    }
  }
}
