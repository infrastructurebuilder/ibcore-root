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
import static java.util.Optional.ofNullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.pathref.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.pathref.RelativeRoot;
import org.infrastructurebuilder.pathref.RelativeRootSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(MavenBackedRelativeRootSupplier.NAME)
@Singleton
public class MavenBackedRelativeRootSupplier implements RelativeRootSupplier {
  private static final Logger log = LoggerFactory.getLogger(MavenBackedRelativeRootSupplier.class);

  public static final String NAME = "maven-basedir";

  transient private final Optional<Path> path;

  @Inject
  public MavenBackedRelativeRootSupplier(MavenProjectSupplier project) {
    this.path = getPathFromProject(requireNonNull(project).get());
  }

  @Override
  public String getName() {
    return NAME;
  }

  protected Optional<Path> getPathFromProject(MavenProject project) {
    Path t = null;
    try {
      t = Paths.get(requireNonNull(project, "null.project").getBasedir().toString()).toAbsolutePath();
    } catch (Throwable thr) {
      getLog().warn("Error getting " + NAME + " RelativeRootProtocol with value " + t, thr);
    }
    return ofNullable(t);

  }

  public Logger getLog() {
    return log;
  }

  @Override
  public Optional<RelativeRoot> getRelativeRoot() {
    return this.path.map(AbsolutePathRelativeRoot::new);
  }

}
