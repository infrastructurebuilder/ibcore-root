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

import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.IBConstants;

/**
 * As a [useful] example, this mapper extracts the org from the github.io username groupId mapping
 * that is common with Maven-generated resources from Github ('io.github.username').
 * Override matchIt or override and call it from within a subclass using another regex.
 * @author mykel.alvis
 *
 */
@Named(IBConstants.GITHUB)
@Singleton
public class GithubIOGroupId2OrgMapper implements GroupId2OrgMapper {
  public final static String DEFAULT_REGEX = "^io.github.(\\w+)(\\..*)?$";
  public final Pattern p = Pattern.compile(DEFAULT_REGEX);

  @Override
  public final Optional<String> apply(String t) {
    return matchIt(t);
  }

  protected Optional<String> matchIt(String t) {
    return Optional.ofNullable(t).map(res -> p.matcher(res)).filter(m -> m.matches()).map(m -> m.group(1));
  }

  @Override
  public String getId() {
    return IBConstants.GITHUB;
  }

}
