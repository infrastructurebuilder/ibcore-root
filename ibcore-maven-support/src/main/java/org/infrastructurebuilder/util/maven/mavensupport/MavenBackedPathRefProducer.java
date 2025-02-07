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

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.pathref.base.AbstractBasicPathPropertiesPathRefProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(MavenBackedPathRefProducer.NAME)
@Singleton
public class MavenBackedPathRefProducer extends AbstractBasicPathPropertiesPathRefProducer {
  private static final Logger log = LoggerFactory.getLogger(MavenBackedPathRefProducer.class);

  public static final String NAME = "maven-basedir";

  private transient final Optional<Path> path;

  @Inject
  public MavenBackedPathRefProducer(MavenProjectSupplier project) {
    this.path = getPathFromProject(requireNonNull(project, "null.supplier").get());
  }

  @Override
  public Optional<String> getProperty() {
    return path.map(Path::toUri).map(URI::toString);
  }

  @Override
  public String getName() {
    return NAME;
  }

  protected Optional<Path> getPathFromProject(MavenProject project) {
    return ofNullable(requireNonNull(project, "null.project").getBasedir().toPath().toAbsolutePath());
  }

  public Logger getLog() {
    return log;
  }

}
