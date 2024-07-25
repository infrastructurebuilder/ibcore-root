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
package org.infrastructurebuilder.pathref;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.api.Weighted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RelativeRootFactory provides injected instances of {@link PathRefProducer}s, allowing a consumer to select the
 * instance desired by name using the get(String) method.
 *
 * Note that the key for obtaining the RelativeRootSupplier desired is by the getName() function on the supplier, not
 * necessarily the Named value.
 */
@Named
public class PathRefFactory {
  private final static Logger log = LoggerFactory.getLogger(PathRefFactory.class);

  private Map<String, PathRefProducer<? extends Object>> suppliers;

  @SuppressWarnings("unchecked")
  @Inject
  public PathRefFactory(Collection<PathRefProducer<? extends Object>> s) {
    log.info("Got list with names: {}", s.stream().map(PathRefProducer::getName).toList().toString());
    this.suppliers = requireNonNull(s).stream() //
        .filter(s2 -> String.class.isAssignableFrom(s2.withClass())) //
        .map(s3 -> (PathRefProducer<String>) s3) // Checked
        .collect(toMap(k -> k.getName(), identity()));
    log.info("Injected the following names: {}", this.suppliers.keySet().toString());
  }

  public final Set<String> getAvailableNames() {
    return suppliers.keySet();
  }

//  public final Optional<PathRef> get(String name) {
//    return get(name, null);
//  }

  public final Optional<PathRef> get(String name) {
    return get(name, null);
  }

  public final Optional<PathRef> get(String name, Object config) {
    if (!suppliers.keySet().contains(name))
      log.warn("PathRefProducer {} not available", name);
    PathRefProducer<? extends Object> q = suppliers.get(name);
    return q.with(config);
  }

  public final Optional<PathRef> getUnspecified(Object config) {
    Optional<PathRefProducer<? extends Object>> qq = suppliers.values().stream().sorted(Weighted.weighted)
        .filter(v -> v.withClass().equals(config.getClass())).findFirst();
    return qq.flatMap(ff -> ff.with(config));
  }

}
