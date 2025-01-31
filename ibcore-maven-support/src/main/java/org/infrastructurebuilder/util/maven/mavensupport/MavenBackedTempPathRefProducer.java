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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(MavenBackedTempPathRefProducer.NAME)
// NOT A SINGLETON, but returns an immutable value
public class MavenBackedTempPathRefProducer extends MavenBackedPathRefProducer {
  private static final Logger log = LoggerFactory.getLogger(MavenBackedTempPathRefProducer.class);

  static final String NAME = "maven-temp";

  @Inject
  public MavenBackedTempPathRefProducer(MavenProjectSupplier project) {
    super(project);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Logger getLog() {
    return log;
  }

  @Override
  protected Optional<Path> getPathFromProject(MavenProject project) {
    return super.getPathFromProject(project).map(p -> p.resolve(UUID.randomUUID().toString())).map(q -> {
      try {
        Files.createDirectories(q);
        return q;
      } catch (IOException e) {
        getLog().error("Error creating %s".formatted(q), e);
        return null;
      }
    });
  }
}
