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
package org.infrastructurebuilder.util.maven.mavensupport;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.core.PathSupplier;

@Named(DefaultMavenBackedTempBasedRelativeRoot.NAME)
public class DefaultMavenBackedTempBasedRelativeRoot extends DefaultMavenBackedRelativeRoot {

  final static String NAME = "maven-target-temp";

  @Inject
  public DefaultMavenBackedTempBasedRelativeRoot(
      @Named(MavenProjectBuildOutputDirectoryPathSupplier.NAME) PathSupplier mavenTargetPath)
  {
    super(() -> requireNonNull(mavenTargetPath).get().resolve(randomUUID().toString()));
  }

  @Override
  public String getName() {
    return NAME;
  }

}
