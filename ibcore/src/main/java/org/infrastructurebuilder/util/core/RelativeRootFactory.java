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
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RelativeRootFactory provides injected instances of {@link RelativeRootSupplier}s, allowing a consumer to select
 * the instance desired by name using the get(String) method.
 *
 * Note that the key for obtaining the RelativeRootSupplier desired is by the getName() function on the supplier, not
 * necessarily the Named value.
 */
@Named
public class RelativeRootFactory {
  private final static Logger log = LoggerFactory.getLogger(RelativeRootFactory.class);

  private Map<String, RelativeRootSupplier> suppliers;

  @Inject
  public RelativeRootFactory(Collection<RelativeRootSupplier> s) {
    log.info("Got list with names: %s", s.stream().map(RelativeRootSupplier::getName).toList().toString());
    this.suppliers = requireNonNull(s).stream() //
        .collect(toMap(k -> k.getName(), identity()));
    log.info("Injected the following names: %s", this.suppliers.keySet().toString());
  }

  public final Set<String> getAvailableNames() {
    return suppliers.keySet();
  }

  public final Optional<RelativeRoot> get(String name) {
    if (!suppliers.keySet().contains(name))
      log.warn("RelativeRootSupplier %s not available", name);
    return ofNullable(this.suppliers.get(name)).flatMap(Supplier::get);
  }

}
