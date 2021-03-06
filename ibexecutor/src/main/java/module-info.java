/*
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
module org.infrastructurebuilder.util.executor {
  requires transitive org.infrastructurebuilder.util.core;
  requires transitive org.infrastructurebuilder.util.config;

  exports org.infrastructurebuilder.util.executor;
  exports org.infrastructurebuilder.util.executor.execution.model.v1_0_0;
  exports org.infrastructurebuilder.util.executor.execution.model.v1_0_0.io.xpp3;
  exports org.infrastructurebuilder.util.executor.plexus;
  requires  org.infrastructurebuilder.exceptions;
  requires org.slf4j;
  requires java.xml;
  requires plexus.utils;
  requires zt.exec;
  requires iblog.noop.component;
}