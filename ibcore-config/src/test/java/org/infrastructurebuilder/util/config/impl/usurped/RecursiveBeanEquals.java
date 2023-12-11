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
package org.infrastructurebuilder.util.config.impl.usurped;

/** test class for verifying if recursively defined bean can be correctly identified */
public class RecursiveBeanEquals {
  private final String name;
  private Object reference;

  public RecursiveBeanEquals(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Object getRef() {
    return reference;
  }

  public void setRef(Object refObj) {
    reference = refObj;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof RecursiveBeanEquals && name.equals(((RecursiveBeanEquals) other).name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
