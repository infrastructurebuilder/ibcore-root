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
package org.infrastructurebuilder.util.artifacts;

import static java.util.stream.Collectors.toList;
import static org.infrastructurebuilder.util.constants.IBConstants.DEFAULT;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.util.core.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.versions.IBVersionsSupplier;

@Named(DEFAULT)
@Singleton
public class DefaultIBArtifactVersionMapper implements IBArtifactVersionMapper {

  private final Map<String, IBVersionsSupplier> ibvs;

  @Inject
  public DefaultIBArtifactVersionMapper(Map<String, IBVersionsSupplier> ibvs) {
    this.ibvs = Objects.requireNonNull(ibvs);
  }

  @Override
  public List<IBVersionsSupplier> getMatchingArtifacts(String groupId, String artifactId) {
    return this.ibvs.values().parallelStream()
        .filter(v -> v.getGroupId().get().equals(groupId) && v.getArtifactId().get().equals(artifactId))
        .collect(toList());
  }
}
