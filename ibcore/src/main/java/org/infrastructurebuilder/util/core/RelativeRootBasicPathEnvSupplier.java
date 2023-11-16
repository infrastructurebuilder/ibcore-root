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

import java.util.Objects;
import java.util.Optional;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(RelativeRootBasicPathEnvSupplier.NAME)
@Singleton
public class RelativeRootBasicPathEnvSupplier extends RelativeRootBasicPathPropertiesSupplier {
  private static final Logger log = LoggerFactory.getLogger(RelativeRootBasicPathEnvSupplier.class);
  public static final String ENV_VAR = "RELATIVE_ROOT_PATH";
  public static final String NAME = "basic-path-env";
  private final String p;

  public RelativeRootBasicPathEnvSupplier() {
    this(ENV_VAR);
  }
  public RelativeRootBasicPathEnvSupplier(String envVar) {
    this.p = Objects.requireNonNull(envVar);
  }
  @Override
  public Optional<String> getProperty() {
    return Optional.ofNullable(System.getenv()).map(m -> m.get(this.p));
  }
  @Override
  protected Logger getLog() {
    return log;
  }
  @Override
  public String getName() {
    return NAME;
  }

}
