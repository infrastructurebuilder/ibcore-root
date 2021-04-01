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
package org.infrastructurebuilder.util.credentials.basic;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class DefaultBasicCredentialsMapSupplier implements BasicCredentialsMapSupplier {

  private Map<String, BasicCredentials>       _map;
  private final Map<String, BasicCredentials> interimMap = new ConcurrentHashMap<>();

  @Inject
  public DefaultBasicCredentialsMapSupplier(final Map<String, BasicCredentials> map) {
    Objects.requireNonNull(map).forEach((k, v) -> addCredentials(k, v));
  }

  public void addCredentials(final String id, final BasicCredentials b) {
    if (interimMap.containsKey(Objects.requireNonNull(id)) || _map != null)
      throw new IBCredentialsException(
          "Cannot replace or extend a value in a DefaultBasicCredentialsMapSupplier " + id);
    interimMap.put(id, Objects.requireNonNull(b));
  }

  @Override
  public Map<String, BasicCredentials> get() {
    if (_map == null) {
      _map = Collections.unmodifiableMap(interimMap);
    }
    return _map;
  }

}
