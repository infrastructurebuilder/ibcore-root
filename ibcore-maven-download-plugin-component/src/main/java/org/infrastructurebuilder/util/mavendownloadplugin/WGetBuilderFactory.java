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
package org.infrastructurebuilder.util.mavendownloadplugin;

import java.nio.file.Path;

import org.slf4j.Logger;

/**
 * Fluent factory for setting up builders of 
 */
public interface WGetBuilderFactory  {
  
  /**
   * 
   * @return a new {@link WGetBuilder} every time
   */
  WGetBuilder builder();
  
  WGetBuilderFactory withLogger(Logger log);

  WGetBuilderFactory withCacheDirectory(Path path);
}
