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
package org.infrastructurebuilder.util.core;

import java.io.IOException;

public interface IBDirScanner {

  String INCLUDED = "included";
  String EXCLUDED = "excluded";
  String ERRORED = "errored";

  /**
   * Return a map of paths both included and excluded Instances of IBDirScanner may or may not cache scan results, so
   * subsequent calls to scan() or defaults that call scan() may or may not result in another tree walk.
   *
   * @return IBDirScan will entire result set
   */
  IBDirScan scan() throws IOException;
}
