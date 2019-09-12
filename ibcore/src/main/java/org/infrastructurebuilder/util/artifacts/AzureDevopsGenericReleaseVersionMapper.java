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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import static org.infrastructurebuilder.IBConstants.*;
/**
 * This maps to a list of "release versions" for some known golang release types that generally
 * release in Github.  The most common is the first ("v1.0.0") but the others exist.
 *
 * @author mykel.alvis
 *
 */
@Named(AZUREDEVOPS)
public class AzureDevopsGenericReleaseVersionMapper implements VersionMapper {

  @Override
  public List<String> apply(GAV t) {
    return t.getVersion().map(v -> {
      return Arrays.asList("v" + v, v, t.getArtifactId() + "-" + v);
    }).orElse(Collections.emptyList());
  }

  @Override
  public String getId() {
    return AZUREDEVOPS;
  }

  @Override
  public Integer getWeight() {
    return 1; // slightly above the default weight
  }

}
