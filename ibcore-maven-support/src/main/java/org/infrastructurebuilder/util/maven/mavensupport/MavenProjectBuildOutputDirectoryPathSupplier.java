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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.util.core.PathSupplier;

@Named(MavenProjectBuildOutputDirectoryPathSupplier.NAME)
@Singleton
public class MavenProjectBuildOutputDirectoryPathSupplier implements PathSupplier {
  public final static String NAME = "maven-target";

  private final Path target;

  @Inject
  public MavenProjectBuildOutputDirectoryPathSupplier(MavenProject mp) {
    this.target = Paths.get(Objects.requireNonNull(mp).getBuild().getOutputDirectory()).toAbsolutePath();
  }

  @Override
  public Path get() {
    return this.target;
  }
}
