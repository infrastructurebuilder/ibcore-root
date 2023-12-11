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
package org.infrastructurebuilder.util.core;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AbstractRoseTree<A extends JSONAndChecksumEnabled>
    implements RoseTree<A>, JSONAndChecksumEnabled {
  private final List<RoseTree<A>> children;

  private final A value;

  public AbstractRoseTree(final A val, final List<RoseTree<A>> children) {
    this.value = val;
    this.children = children;
  }

  @Override
  public JSONObject asJSON() {
    return getValue().asJSON().put(getKeyForTreeInJSON(),
        new JSONArray(getChildren().stream().map(c -> c.asJSON()).collect(Collectors.toList())));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("unchecked")
    final AbstractRoseTree<A> other = (AbstractRoseTree<A>) obj;
    if (!children.equals(other.children))
      return false;
    if (!value.equals(other.value))
      return false;
    return true;
  }

  @Override
  public List<RoseTree<A>> getChildren() {
    return this.children;
  }

  abstract public String getKeyForTreeInJSON();

  @Override
  public A getValue() {
    return this.value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + children.hashCode();
    result = prime * result + value.hashCode();
    return result;
  }


}
