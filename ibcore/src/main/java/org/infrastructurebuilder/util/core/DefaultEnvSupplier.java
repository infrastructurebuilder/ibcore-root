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

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DefaultEnvSupplier implements EnvSupplier {
  private static final String FORMAT = "{} = {}";
  private final static Logger log = LoggerFactory.getLogger(DefaultEnvSupplier.class);

  @Inject
  public DefaultEnvSupplier() {
  }

  @Override
  public Map<String, String> get() {
    Map<String, String> m = System.getenv();
    return System.getenv();
  }

}
