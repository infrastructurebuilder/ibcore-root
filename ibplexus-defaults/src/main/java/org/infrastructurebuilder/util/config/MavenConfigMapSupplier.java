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
package org.infrastructurebuilder.util.config;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.sisu.Nullable;
import org.infrastructurebuilder.IBException;

@Named(ConfigMapSupplier.MAVEN)
public class MavenConfigMapSupplier extends DefaultConfigMapSupplier {


  @Inject
  public MavenConfigMapSupplier(final MavenProject mavenProject, @Nullable MavenSession session,
      @Nullable MojoExecution execution) {

    final Build build = mavenProject.getBuild();
    super.overrideValueDefaultBlank("project.build.directory", build.getDirectory());
    super.overrideValueDefaultBlank("project.build.outputDirectory", build.getOutputDirectory());
    super.overrideValueDefaultBlank("project.build.testOutputDirectory", build.getTestOutputDirectory());
    super.overrideValueDefaultBlank("project.build.defaultGoal", build.getDefaultGoal());
    super.overrideValueDefaultBlank("project.build.finalName", build.getFinalName());
    super.overrideValueDefaultBlank("project.build.scriptSourceDirectory", build.getScriptSourceDirectory());
    super.overrideValueDefaultBlank("project.build.sourceDirectory", build.getSourceDirectory());
    super.overrideValueDefaultBlank("project.build.testOutputDirectory", build.getTestOutputDirectory());
    super.overrideValueDefaultBlank("project.build.testSourceDirectory", build.getTestSourceDirectory());

    super.overrideValueDefaultBlank("project.artifactId", mavenProject.getArtifactId());
    super.overrideValueDefaultBlank("project.groupId", mavenProject.getGroupId());
    super.overrideValueDefaultBlank("project.version", mavenProject.getVersion());

    ofNullable(session).ifPresent(s -> {
      super.overrideValueDefaultBlank("maven.session.goals.list", s.getGoals().stream().collect(joining()));
      super.overrideValueDefaultBlank("maven.session.start", s.getStartTime());
    });
    ofNullable(execution).ifPresent(e -> {
      super.overrideValueDefaultBlank("maven.execution.id", e.getExecutionId());
      super.overrideValueDefaultBlank("maven.execution.goal", e.getGoal());
      super.overrideValueDefaultBlank("maven.execution.phase", e.getLifecyclePhase());
    });

    final Path workingDir = Paths.get(build.getDirectory()).resolve(UUID.randomUUID().toString());
    IBException.cet.withTranslation(() -> Files.createDirectories(workingDir));
    super.overrideValue(IB_DATA_WORKING_DIR, workingDir.toString());
    Map<String, Object> i = requireNonNull(System.getenv()).entrySet().stream()
        .collect(toMap(k -> k.getKey(), v -> v.getValue()));
    super.addConfiguration(i);
    super.overrideConfiguration(System.getProperties());
    super.overrideConfiguration(Objects.requireNonNull(mavenProject).getProperties());
  }

}
