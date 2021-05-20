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
package org.infrastructurebuilder.util.dag;

import java.util.List;
import java.util.Set;

public interface MutableDAG<T extends Comparable<T>> {

  void addEdge(MutableVertex<T> from, MutableVertex<T> to) throws CycleDetectedException;

  void addEdge(T from, T to) throws CycleDetectedException;

  MutableVertex<T> addVertex(T label);

  List<T> getChildLabels(T label);

  Set<T> getLabels();

  List<T> getParentLabels(T label);

  List<T> getSuccessorLabels(T label);

  MutableVertex<T> getVertex(T label);

  Set<MutableVertex<T>> getVerticies();

  boolean hasEdge(T label1, T label2);

  boolean isConnected(T label);

  void removeEdge(MutableVertex<T> from, MutableVertex<T> to);

  void removeEdge(T from, T to);

}