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
            g.getExtension(), g.getClassifier().orElse(null), h);

      });
    });
  };
}
