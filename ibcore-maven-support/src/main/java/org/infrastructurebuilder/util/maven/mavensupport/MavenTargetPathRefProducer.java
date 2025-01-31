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

import static java.util.Optional.of;

import java.nio.file.Path;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.project.MavenProject;

@Named(MavenTargetPathRefProducer.MAVEN_TARGET)
public class MavenTargetPathRefProducer extends MavenBackedPathRefProducer {

  static final String MAVEN_TARGET = "maven-target";

  @Inject
  public MavenTargetPathRefProducer(MavenProjectSupplier project) {
    super(project);
  }

  @Override
  public String getName() {
    return MAVEN_TARGET;
  }

  @Override
  protected Optional<Path> getPathFromProject(MavenProject project) {
    return of(Path.of(project.getBuild().getDirectory()).toAbsolutePath());
  }

}
