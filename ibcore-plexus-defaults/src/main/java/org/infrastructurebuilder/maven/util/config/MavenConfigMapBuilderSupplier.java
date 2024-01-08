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
package org.infrastructurebuilder.maven.util.config;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.infrastructurebuilder.util.constants.IBConstants.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.sisu.Nullable;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.config.ConfigMapBuilderSupplier;
import org.infrastructurebuilder.util.config.impl.DefaultConfigMapBuilderSupplier;
import org.json.JSONObject;

@Named(MAVEN)
@Singleton
public class MavenConfigMapBuilderSupplier extends DefaultConfigMapBuilderSupplier {

  protected final static void overrideValueDefaultBlank(JSONObject j, String key, String o) {
    j.put(requireNonNull(key), ofNullable(o).orElse(""));
  }

  @Inject
  public MavenConfigMapBuilderSupplier(final MavenProject mavenProject, @Nullable MavenSession session,
      @Nullable MojoExecution execution)
  {
    super();
    final Build build = requireNonNull(mavenProject).getBuild();
    JSONObject proj = new JSONObject();
    JSONObject sess = new JSONObject();
    JSONObject exec = new JSONObject();

    overrideValueDefaultBlank(proj, "project.build.outputDirectory", build.getOutputDirectory());
    overrideValueDefaultBlank(proj, "project.build.directory", build.getDirectory());
    overrideValueDefaultBlank(proj, "project.build.testOutputDirectory", build.getTestOutputDirectory());
    overrideValueDefaultBlank(proj, "project.build.defaultGoal", build.getDefaultGoal());
    overrideValueDefaultBlank(proj, "project.build.finalName", build.getFinalName());
    overrideValueDefaultBlank(proj, "project.build.scriptSourceDirectory", build.getScriptSourceDirectory());
    overrideValueDefaultBlank(proj, "project.build.sourceDirectory", build.getSourceDirectory());
    overrideValueDefaultBlank(proj, "project.build.testOutputDirectory", build.getTestOutputDirectory());
    overrideValueDefaultBlank(proj, "project.build.testSourceDirectory", build.getTestSourceDirectory());

    overrideValueDefaultBlank(proj, "project.artifactId", mavenProject.getArtifactId());
    overrideValueDefaultBlank(proj, "project.groupId", mavenProject.getGroupId());
    overrideValueDefaultBlank(proj, "project.version", mavenProject.getVersion());

    ofNullable(session).ifPresent(s -> {
      overrideValueDefaultBlank(sess, "maven.session.goals.list", s.getGoals().stream().collect(joining()));
      overrideValueDefaultBlank(sess, "maven.session.start", dateFormatter.format(s.getStartTime().toInstant()));
    });
    ofNullable(execution).ifPresent(e -> {
      overrideValueDefaultBlank(exec, "maven.execution.id", e.getExecutionId());
      overrideValueDefaultBlank(exec, "maven.execution.goal", e.getGoal());
      overrideValueDefaultBlank(exec, "maven.execution.phase", e.getLifecyclePhase());
    });

    final Path workingDir = Paths.get(build.getDirectory()).resolve(UUID.randomUUID().toString());
    IBException.cet.translate(() -> Files.createDirectories(workingDir));
    super.get().withJSONObject(proj)

        .withJSONObject(sess)

        .withJSONObject(exec)

        .withMapStringString(System.getenv())

        .withProperties(System.getProperties())

        // FIXME Probably not doing this
        .withMapStringString(Map.of(ConfigMapBuilderSupplier.IB_DATA_WORKING_DIR, workingDir.toString()))

    // TODO The order and priority of these is important
//        .withProperties(mavenProject.getProperties())

    ;
  }

  @Override
  public String getName() {
    return MAVEN;
  }

}
