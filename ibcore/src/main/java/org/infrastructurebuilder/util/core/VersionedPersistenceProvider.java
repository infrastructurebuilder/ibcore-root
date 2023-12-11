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
import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

import org.infrastructurebuilder.util.core.IBVersion.IBVersionBoundedRange;

public interface VersionedPersistenceProvider<T extends Modeled> extends Comparable<VersionedPersistenceProvider<?>> {

  @Override
  default int compareTo(VersionedPersistenceProvider<?> o) {
    return getVersion().compareTo(o.getVersion());
  }

  default boolean isReader() {
    return true;
  }

  default boolean isWriter() {
    return true;
  }

  /**
   * 
   * The version of the T that THIS provider will produce
   * 
   */
  IBVersion getVersion();

  /**
   * The range of versions that THIS provider can accept to produce a version of T of version
   * <code>getVersion()</code>.
   * 
   * ALL provider instances must read AT LEAST one API version below their own to allow for upward
   * migration of models.
   * 
   * @return
   */
  IBVersionBoundedRange getVersionRange();

  void write(Writer w, T s) throws IOException;

  T read(Reader r) throws IOException;

  Optional<T> fromVersionedObject(Modeled o);
}
