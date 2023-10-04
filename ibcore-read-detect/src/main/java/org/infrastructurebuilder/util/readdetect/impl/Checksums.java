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
package org.infrastructurebuilder.util.readdetect.impl;

import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.googlecode.download.maven.plugin.internal.ChecksumUtils;

/**
 * Checksums supplied to verify file integrity.
 *
 * @author Paul Polishchuk
 */
public final class Checksums {

  private final Map<String, String> supplied;

  public Checksums(@Nullable final String md5, @Nullable final String sha1, @Nullable final String sha256,
      @Nullable final String sha512, Logger log)
  {
    this.supplied = Checksums.create(md5, sha1, sha256, sha512);
    if (this.supplied.isEmpty()) {
      log.debug("No checksums were supplied, skipping file validation");
    } else if (this.supplied.size() > 1) {
      log.warn("More than one checksum is supplied. This may be slow for big files. Consider using a single checksum.");
    }
  }

  /**
   * Validates the file with supplied checksums.
   *
   * @param file File to validate.
   * @return True if the file matches all supplied checksums or if no checksums were supplied.
   */
  public boolean isValid(final File file) {
    boolean valid = true;
    try {
      this.validate(file);
    } catch (final Exception ex) {
      valid = false;
    }
    return valid;
  }

  /**
   * Validates the file with supplied checksums.
   *
   * @param file File to validate.
   * @throws Exception If the file didn't match any supplied checksum.
   */
  public void validate(final File file) throws Exception {
    for (final Map.Entry<String, String> entry : this.supplied.entrySet()) {
      ChecksumUtils.verifyChecksum(file, entry.getValue(), MessageDigest.getInstance(entry.getKey()));
    }
  }

  /**
   * Fill the map of checksums.
   *
   * @param md5    Supplied md5 checksum, may be {@literal null}.
   * @param sha1   Supplied sha1 checksum, may be {@literal null}.
   * @param sha256 Supplied sha256 checksum, may be {@literal null}.
   * @param sha512 Supplied sha512 checksum, may be {@literal null}.
   * @return A map of a checksum type to a digest; {@literal null} digests are not included.
   */
  private static Map<String, String> create(@Nullable final String md5, @Nullable final String sha1,
      @Nullable final String sha256, @Nullable final String sha512) {
    final Map<String, String> digests = new HashMap<>();
//            digests.put(DownloadChecksum.MD5, md5);
//        }
//        if (sha1 != null) {
//            digests.put(DownloadChecksum.SHA1, sha1);
//        }
//        if (sha256 != null) {
//            digests.put(DownloadChecksum.SHA256, sha256);
//        }
    if (sha512 != null) {
      digests.put("SHA-512", sha512);
    }
    return digests;
  }
}
