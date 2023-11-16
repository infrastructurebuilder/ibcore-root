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

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
/**
 * The RelativeRootFactory provides injected instances of RelativeRootSuppliers, allowing
 * a consumer to select the instance desired by name using the get(String) method.
 *
 * Note that the key for obtaining the RelativeRootSupplier desired is by the getName() function
 * on the supplier, not necessarily the Named value.
 */
@Named
public class RelativeRootFactory {

  private Map<String, RelativeRootSupplier> protocols;

  @Inject
  public RelativeRootFactory(Set<RelativeRootSupplier> protocols) {
    this.protocols = requireNonNull(protocols).stream() //
        .collect(toMap(k -> k.getName(), identity()));
  }

  public final Set<String> getNames() {
    return protocols.keySet();
  }

  public final Optional<RelativeRoot> get(String name) {
    return Optional.ofNullable(this.protocols.get(name)) //
        .flatMap(p -> p.get());
  }

}
