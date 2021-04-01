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
package org.infrastructurebuilder.util.core;

import java.util.LinkedList;
import java.util.List;

import org.infrastructurebuilder.exceptions.IBException;

public interface RoseTree<A extends JSONAndChecksumEnabled> extends JSONAndChecksumEnabled {

  @Override
  default Checksum asChecksum() {
    return IBException.cet.withReturningTranslation(() -> {
      final ChecksumBuilder b = ChecksumBuilder.newInstance().addChecksumEnabled(getValue());
      getChildren().stream()

          .sorted((lhs, rhs) -> lhs.asChecksum().compareTo(rhs.asChecksum()))

          .forEach(b::addChecksumEnabled);
      return b.asChecksum();

    });
  }

  List<RoseTree<A>> getChildren();

  A getValue();

  default boolean hasChildren() {
    return getChildren().size() != 0;
  }

  default RoseTree<A> withChild(final RoseTree<A> child) {
    final List<RoseTree<A>> childList = new LinkedList<>();
    childList.add(child);
    return this.withChildren(childList);
  }

  RoseTree<A> withChildren(final List<RoseTree<A>> moreChildren);
}