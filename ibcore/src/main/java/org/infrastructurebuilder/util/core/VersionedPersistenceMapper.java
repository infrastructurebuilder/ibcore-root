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

import static java.util.Comparator.reverseOrder;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.util.core.IBVersion.IBVersionRange;

@Named
@Singleton
public class VersionedPersistenceMapper<T extends Modeled> {
  private final Map<IBVersion, VersionedPersistenceProvider<T>> readers = new HashMap<>();

  @Inject
  public VersionedPersistenceMapper(Set<VersionedPersistenceProvider<T>> providers) {
    providers.forEach(p -> readers.put(p.getVersion(), p));
  }

  public Optional<VersionedPersistenceProvider<T>> readerFor(IBVersionRange b) {
    return this.readers.keySet().stream().filter(v -> requireNonNull(b).isSatisfiedBy(v) && readers.get(v).isReader())
        .sorted(reverseOrder()).findFirst().map(this.readers::get);
  }

  public Optional<VersionedPersistenceProvider<T>> writerFor(IBVersionRange b) {
    return this.readers.keySet().stream().filter(v -> requireNonNull(b).isSatisfiedBy(v) && readers.get(v).isWriter())
        .sorted(reverseOrder()).findFirst().map(this.readers::get);
  }

  public Optional<VersionedPersistenceProvider<T>> readerFor(String b) {
    return this.readers.keySet().stream().filter(v -> v.satisfies(b) && readers.get(v).isReader())
        .sorted(reverseOrder()).findFirst().map(this.readers::get);
  }

  public Optional<VersionedPersistenceProvider<T>> writerFor(String b) {
    return this.readers.keySet().stream().filter(v -> v.satisfies(b) && readers.get(v).isWriter())
        .sorted(reverseOrder()).findFirst().map(this.readers::get);
  }
}
