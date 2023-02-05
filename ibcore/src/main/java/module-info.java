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
module org.infrastructurebuilder.util.core {
  exports org.infrastructurebuilder.util.crypto;
  exports org.infrastructurebuilder.util.constants;
  exports org.infrastructurebuilder.util.core;
  exports org.infrastructurebuilder.util.settings;
  exports org.infrastructurebuilder.util.credentials.basic;

  requires transitive com.vdurmont.semver4j;
  requires transitive org.infrastructurebuilder.exceptions;
  requires transitive org.json;
  requires transitive java.xml;
  requires javax.inject;
}