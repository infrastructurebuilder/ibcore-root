/**
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
module org.infrastructurebuilder.ibcore {
  exports org.infrastructurebuilder.util.config;
  exports org.infrastructurebuilder.util.crypto;
  exports org.infrastructurebuilder.util.config.factory;
  exports org.infrastructurebuilder.util.files;
  exports org.infrastructurebuilder.util.constants;
  exports org.infrastructurebuilder.util;
  exports org.infrastructurebuilder.util.dag;
  exports org.infrastructurebuilder.util.dag.impl;
  exports org.infrastructurebuilder.util.artifacts.impl;
  exports org.infrastructurebuilder.util.artifacts;

  requires transitive com.vdurmont.semver4j;
  requires java.xml;
  requires javax.inject;
  requires org.infrastructurebuilder.exceptions;
  requires org.json;
  requires  org.slf4j;
  requires org.eclipse.sisu.inject;
}