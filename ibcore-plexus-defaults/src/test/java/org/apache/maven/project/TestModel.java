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
package org.apache.maven.project;

import java.util.Properties;

import org.apache.maven.model.Model;
import org.infrastructurebuilder.util.executor.ProcessRunnerSupplier;

public class TestModel extends Model {

  /**
   *
   */
  private static final long serialVersionUID = 8170630890505544985L;
  private final Properties properties;

  public TestModel() {
    properties = new Properties();
    properties.setProperty("X", "Y");
    properties.setProperty(ProcessRunnerSupplier.PROCESS_NAMESPACE + "blah", "moreblah");
  }

  public Properties getProperties() {
    return properties;
  }
}
