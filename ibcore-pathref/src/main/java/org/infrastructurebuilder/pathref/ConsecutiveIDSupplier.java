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
package org.infrastructurebuilder.pathref;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.UUID;

import org.infrastructurebuilder.api.base.IdentifierSupplier;

/**
 * TODO This isn't correct. I think I forgot to implement this properly
 *
 * @author mykel.alvis
 *
 */
public class ConsecutiveIDSupplier implements IdentifierSupplier {

  private final Stack<String> s = new Stack<>();
  private final String id;

  public ConsecutiveIDSupplier() {
    id = UUID.randomUUID().toString();
    s.push(id);
  }

  @Override
  public String get() {
    try {
      return s.pop();
    } catch (final EmptyStackException e) {
      return UUID.randomUUID().toString();
    }
  }

  public String getId() {
    return id;
  }

}
