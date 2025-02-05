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
package org.infrastructurebuilder.pathref.base;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(BasicPathEnvPathRefProducer.NAME)
@Singleton
public class BasicPathEnvPathRefProducer extends AbstractBasicPathPropertiesPathRefProducer {
  private static final Logger log = LoggerFactory.getLogger(BasicPathEnvPathRefProducer.class);
  public static final String ENV_VAR = "PATHREF";
  public static final String NAME = "basic-path-env";
  private final String name;

  @Inject
  public BasicPathEnvPathRefProducer() {
    this(ENV_VAR);
  }

  public BasicPathEnvPathRefProducer(String envVar) {
    this.name = Objects.requireNonNull(envVar);
    getLog().debug("Setting path property to " + envVar);
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  protected Optional<Properties> getProperties() {
    Properties p = new Properties();
    System.getenv().entrySet().forEach((w) -> p.setProperty(w.getKey(), w.getValue()));
    return Optional.of(p);
  }

  @Override
  protected Logger getLog() {
    return log;
  }

}
