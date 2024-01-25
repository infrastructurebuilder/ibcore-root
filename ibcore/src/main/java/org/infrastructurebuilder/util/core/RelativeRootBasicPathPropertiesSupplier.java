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

import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns a RelativeRoot instance if System.getProperties(RR_BASIC_PATH) (see below) contains a string representation
 * of an absolute path.
 *
 * As System.getProperties is mutable, and as this item returns a new RR instance each time, the results of the get()
 * call are not fixed.
 *
 */
@Singleton
@Named(RelativeRootBasicPathPropertiesSupplier.NAME)
public class RelativeRootBasicPathPropertiesSupplier extends AbstractRelativeRootBasicPathPropertiesSupplier {

  static final Logger log = LoggerFactory.getLogger(RelativeRootBasicPathPropertiesSupplier.class);
  public static final String RR_BASIC_PATH = "rr.basic.path";
  static final String NAME = "basic-path-properties";

  @Override
  public String getName() {
    return NAME;
  }

  public String getPropertyName() {
    return RR_BASIC_PATH;
  }

  @Override
  protected Logger getLog() {
    return log;
  }
}
