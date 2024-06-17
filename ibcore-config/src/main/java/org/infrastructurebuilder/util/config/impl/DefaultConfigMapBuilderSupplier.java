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
package org.infrastructurebuilder.util.config.impl;

import javax.inject.Named;

import org.infrastructurebuilder.constants.IBConstants;
import org.infrastructurebuilder.util.config.ConfigMapBuilder;
import org.infrastructurebuilder.util.config.ConfigMapBuilderSupplier;
import org.json.JSONObject;

@Named(IBConstants.DEFAULT) // FIXME Maybe? Dunno if this is a component or not
public class DefaultConfigMapBuilderSupplier implements ConfigMapBuilderSupplier {

  private ConfigMapBuilder builder = new DefaultConfigMapBuilder();

  @Override
  public ConfigMapBuilder get() {
    return this.builder;
  }

  public String getName() {
    return IBConstants.DEFAULT;
  }

  protected void resetConfigMapBuilder(ConfigMapBuilder l) {
    this.builder = l;

  }

  @Override
  public String toString() {
    return "DefaultConfigMapBuilderSupplier of :\n" + builder.toString();
  }

  protected void addASingle(JSONObject jsonObject) {
    ((DefaultConfigMapBuilder) this.builder).addASingle(jsonObject);
  }

}
