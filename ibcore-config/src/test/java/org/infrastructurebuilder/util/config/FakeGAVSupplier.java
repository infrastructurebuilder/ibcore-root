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

import java.util.Optional;

import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.core.GAVSupplier;

public class FakeGAVSupplier implements GAVSupplier {

  private final String groupId;
  private final String artifactId;
  private final String version;
  private final String description;

  public FakeGAVSupplier(String g, String a, String v, String d) {
    this.groupId = g;
    this.artifactId = a;
    this.version = v;
    this.description = d;
  }

  public FakeGAVSupplier(GAV gs) {
    this(gs.getGroupId(), gs.getArtifactId(), gs.getVersion().get(), null);
  }

  @Override
  public GAV getGAV() {
    return new DefaultGAV(this.groupId, this.artifactId, this.version);
  }

  @Override
  public Optional<String> getDescription() {
    return Optional.ofNullable(this.description);
  }


}
