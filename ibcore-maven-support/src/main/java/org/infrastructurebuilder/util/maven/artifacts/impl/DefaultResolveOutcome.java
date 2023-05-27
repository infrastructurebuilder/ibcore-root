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
package org.infrastructurebuilder.util.maven.artifacts.impl;

import java.net.URL;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.infrastructurebuilder.util.maven.artifacts.ResolveOutcome;

public class DefaultResolveOutcome implements ResolveOutcome {
  private final URL url;
  private final Artifact originatingArtifact;
  private final List<URL> resolved;

  public DefaultResolveOutcome(final List<URL> resolved, final Artifact originatingArtifact, final URL url) {
    super();
    this.url = url;
    this.originatingArtifact = originatingArtifact;
    this.resolved = resolved;
  }

  @Override
  public final int getCount() {
    return resolved.size();
  }

  @Override
  public final Artifact getOriginatingArtifact() {
    return originatingArtifact;
  }

  @Override
  public final List<URL> getResolvedURLs() {
    return resolved;
  }

  @Override
  public final URL getURL() {
    return url;
  }
}
