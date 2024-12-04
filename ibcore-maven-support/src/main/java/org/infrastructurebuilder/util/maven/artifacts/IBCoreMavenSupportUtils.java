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
package org.infrastructurebuilder.util.maven.artifacts;

import static java.util.Optional.ofNullable;

import java.util.Optional;
import java.util.function.BiFunction;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.infrastructurebuilder.util.versions.GAVBasic;

public class IBCoreMavenSupportUtils {
  public final static BiFunction<GAVBasic, ArtifactHandler, Optional<Artifact>> gav2Artifact = (gab, h) -> {
    return ofNullable(gab).flatMap(g -> {
      return ofNullable(h).map(gav -> {
        return new DefaultArtifact(g.getGroupId(), g.getArtifactId(), g.getVersion().orElse(null), JavaScopes.COMPILE,
            g.getExtension().orElse(null), g.getClassifier().orElse(null), h);

      });
    });
  };
}
