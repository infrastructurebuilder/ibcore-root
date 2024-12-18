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
package org.infrastructurebuilder.pathref.basicpathenv;

import java.util.Objects;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.pathref.base.AbstractBasicPathPropertiesPathRefSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(BasicPathEnvPathRefSupplier.NAME)
@Singleton
public class BasicPathEnvPathRefSupplier extends AbstractBasicPathPropertiesPathRefSupplier {
  private static final Logger log = LoggerFactory.getLogger(BasicPathEnvPathRefSupplier.class);
  public static final String ENV_VAR = "RELATIVE_ROOT_PATH";
  public static final String NAME = "basic-path-env";
  private final String name;

  @Inject
  public BasicPathEnvPathRefSupplier() {
    this(ENV_VAR);
  }

  public BasicPathEnvPathRefSupplier(String envVar) {
    this.name = Objects.requireNonNull(envVar);
    getLog().debug("Setting path to " + envVar);
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  protected Properties getProperties() {
    Properties p = new Properties();
    System.getenv().entrySet().forEach((w) -> p.setProperty(w.getKey(), w.getValue()));
    return p;
  }

  @Override
  protected Logger getLog() {
    return log;
  }

}
