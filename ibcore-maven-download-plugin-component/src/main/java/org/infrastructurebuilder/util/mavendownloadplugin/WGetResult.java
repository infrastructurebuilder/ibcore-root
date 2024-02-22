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

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.util.core.PathAndChecksum;

public interface WGetResult {
  
  /** {@link Instant} the download of {@link WGetResult#getOriginal() the downloaded file} started
   * 
   * @return
   */
  Instant getAcquired();

  /**
   * Gets original file downloaded by Wget
   * @return
   */
  PathAndChecksum getOriginal();
  
  /**
   * Gets the "root" path of the expanded list
   * @return
   */
  Optional<Path> getExpandedRoot();

  /**
   * Get optional directory of expanded files from the archive
   * that is returned by {@link WGetResult#getOriginal() the downloaded file}
   * @return
   */
  Optional<List<PathAndChecksum>> getExpanded();

  /**
   * SIDE EFFECTS: Deletes everything that is in this result
   * 
   */
  void cleanup();

}
