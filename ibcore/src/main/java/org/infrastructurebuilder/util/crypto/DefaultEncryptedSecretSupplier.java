/*
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
package org.infrastructurebuilder.util.crypto;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.exceptions.IBException;

@Named
public class DefaultEncryptedSecretSupplier implements EncryptedSecretSupplier {

  private final String secret;

  @Inject
  public DefaultEncryptedSecretSupplier(final String secret) {
    this.secret = Objects.requireNonNull(secret);
    if (this.secret.getBytes().length != 16)
      throw new IBException("Secret length must be 16 bytes");
  }

  @Override
  public String get() {
    return secret;
  }

}
