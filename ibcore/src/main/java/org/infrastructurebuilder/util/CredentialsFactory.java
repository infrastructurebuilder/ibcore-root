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
package org.infrastructurebuilder.util;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * The {@code CredentialsFactory} is injected with mechanisms for lookup up
 * creds
 *
 * @author mykel.alvis
 *
 */
public interface CredentialsFactory {
  /**
   * Get a {@link CredentialsSupplier} from a query
   *
   * @param query string query lookup
   * @return {@link CredentialsSupplier} if found
   */
  Optional<BasicCredentials> getCredentialsFor(String query);

}
