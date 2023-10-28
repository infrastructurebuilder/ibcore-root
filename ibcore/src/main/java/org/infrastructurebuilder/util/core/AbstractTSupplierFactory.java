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

import static java.util.Optional.ofNullable;

import java.util.Optional;

public abstract class AbstractTSupplierFactory<T, C> implements TSupplierFactory<T, C> {
  private C config;
  private String name;
  private String desc;
  private int weight = 0;
  private String hint;
  private String displayName;

  public AbstractTSupplierFactory() {
    super();
    this.config = null;
  }

  public AbstractTSupplierFactory<T, C> withName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public String getName() {
    return this.name;
  }

  public AbstractTSupplierFactory<T, C> withDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public AbstractTSupplierFactory<T, C> withDescription(String desc) {
    this.desc = desc;
    return this;
  }

  public AbstractTSupplierFactory<T, C> withWeight(int weight) {
    this.weight = weight;
    return this;
  }

  @Override
  public Integer getWeight() {
    return this.weight;
  }

  public AbstractTSupplierFactory<T, C> withConfig(C config) {
    this.config = config;
    return this;
  }

  public AbstractTSupplierFactory<T, C> withHint(String hint) {
    this.hint = hint;
    return this;
  }

  @Override
  public String getHint() {
    return ofNullable(this.hint).orElse(this.name);
  }

  @Override
  public Optional<String> getDescription() {
    return ofNullable(this.desc);
  }

  @Override
  public Optional<String> getDisplayName() {
    return ofNullable(this.displayName);
  }

  protected C getConfig() {
    return this.config;
  }

}
