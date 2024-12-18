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
package org.infrastructurebuilder.api.base;

import static java.util.Optional.empty;

import java.util.Optional;

public interface NameDescribed {
  public final static String NAME = "name";
  public final static String DISPLAYNAME = "displayName";
  public final static String DESCRIPTION = "description";

  /**
   * It is possible for a NameDescribed to have a null name. This should be the exception, however.
   *
   * @return
   */
  public String getName();

  default Optional<String> getDisplayName() {
    return empty();
  }

  default Optional<String> getDescription() {
    return getDisplayName();
  }
}
