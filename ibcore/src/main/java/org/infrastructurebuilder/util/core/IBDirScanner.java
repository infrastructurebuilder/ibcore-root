/*
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
package org.infrastructurebuilder.util.core;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface IBDirScanner {
  /**
   * Return a map of paths both included and excluded
   *
   * @return Map. The "true" set is the included files. The "false" set is
   *         excluded
   */
  Map<Boolean, List<Path>> scan();

  default List<Path> getIncludedPaths() {
    return ofNullable(scan().get(true)).orElse(emptyList());
  }

  default List<Path> getExcludedPaths() {
    return ofNullable(scan().get(false)).orElse(emptyList());
  }
}
