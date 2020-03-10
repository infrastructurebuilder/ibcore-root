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
package org.infrastructurebuilder.util.config.factory;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import java.util.Optional;

public class DefaultProcessableTyped<T> implements ProcessableTyped<T> {

  private final String           processableType;
  private final Optional<String> specificProcessor;
  private final boolean          inbound;
  private final T value;

  public DefaultProcessableTyped(String type, T value) {
    this(type, empty(), value);
  }

  public DefaultProcessableTyped(String type, Optional<String> processor, T value) {
    this(type, processor, true, value);
  }

  public DefaultProcessableTyped(String type, Optional<String> processor, boolean inbound, T value) {
    this.processableType = requireNonNull(type);
    this.specificProcessor = requireNonNull(processor);
    this.inbound = inbound;
    this.value = value;
  }

  @Override
  public boolean isInbound() {
    return this.inbound;
  }

  @Override
  public String getProcessableType() {
    return this.processableType;
  }

  @Override
  public Optional<String> getSpecificProcessor() {
    return this.specificProcessor;
  }

  @Override
  public T getValue() {
    return value;
  }
}
