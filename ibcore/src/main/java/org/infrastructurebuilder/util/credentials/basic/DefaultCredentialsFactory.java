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
package org.infrastructurebuilder.util.credentials.basic;

import static org.infrastructurebuilder.api.Weighted.weighted;
import static org.infrastructurebuilder.constants.IBConstants.DEFAULT;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

@Named(DEFAULT)
public class DefaultCredentialsFactory implements CredentialsFactory {

  private final List<CredentialsSupplier> ss;

  @Inject
  public DefaultCredentialsFactory(Map<String, CredentialsSupplier> credentialsSuppliers) {
    this.ss = credentialsSuppliers.values().stream().sorted(weighted).toList();
  }

  @Override
  public Optional<BasicCredentials> getCredentialsFor(String query) {
    return ss.stream().map(cs -> cs.getCredentialsFor(query)).flatMap(Optional::stream).findFirst();
  }

}
