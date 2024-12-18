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
package org.infrastructurebuilder.util.artifacts;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.constants.IBConstants;

/**
 * Override matchIt or override and call it from within a subclass using another regex.
 *
 * @author mykel.alvis
 *
 */
@Named(IBConstants.AZUREDEVOPS)
@Singleton
public class AzureDevopsGroupId2OrgMapper implements GroupId2OrgMapper {
  public final static String DEFAULT_REGEX = "^https://dev.azure.com\\/(\\w+)\\/(\\..*)?$";
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
    return IBConstants.AZUREDEVOPS;
  }

}
