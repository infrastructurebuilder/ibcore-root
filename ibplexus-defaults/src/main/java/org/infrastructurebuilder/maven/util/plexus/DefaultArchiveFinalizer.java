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
package org.infrastructurebuilder.maven.util.plexus;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.codehaus.plexus.archiver.AbstractArchiveFinalizer;

public class DefaultArchiveFinalizer<T> extends AbstractArchiveFinalizer {
  private final T config;

  public DefaultArchiveFinalizer(final T cffconfig) {
    super();
    this.config = Objects.requireNonNull(cffconfig, "DefaultArchiveFinalizer Finalizer Config");
  }

  public T getConfig() {
    return this.config;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List getVirtualFiles() {
    return Collections.emptyList();
  }

}
