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
package org.infrastructurebuilder.util.dag.impl;

import static org.infrastructurebuilder.util.dag.DAGVisitResult.CONTINUE;
import static org.infrastructurebuilder.util.dag.DAGVisitResult.SKIP_SIBLINGS;
import static org.infrastructurebuilder.util.dag.DAGVisitResult.TERMINATE;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.infrastructurebuilder.util.dag.DAG;
import org.infrastructurebuilder.util.dag.DAGVisitResult;
import org.infrastructurebuilder.util.dag.DAGVisitor;
import org.infrastructurebuilder.util.dag.DAGWalker;
import org.infrastructurebuilder.util.dag.Vertex;

public class DAGWalkerPreTraversalDepthFirstImpl<T extends Comparable<T>> implements DAGWalker<T> {

  public DAGWalkerPreTraversalDepthFirstImpl() {
  }

  @Override
  public void close() throws Exception {

  }

  @Override
  public void walk(final DAG<T> dag, final List<DAGVisitor<T>> visitors) {
    Objects.requireNonNull(dag, "dag");
    Objects.requireNonNull(visitors, "visitors");
    final Set<Vertex<T>> roots = dag.getRoots();
    final Set<Vertex<T>> visited = Collections.synchronizedSet(new HashSet<>());
    final Set<Vertex<T>> entered = new HashSet<>();

    roots.parallelStream()
        .forEach(node -> threadedVisitation(true, visited, entered, node, node.getChildren(), visitors));
  }

  private DAGVisitResult threadedVisitation(final boolean isRoot, final Set<Vertex<T>> visited,
      final Set<Vertex<T>> entered, final Vertex<T> node, final List<Vertex<T>> children,
      final List<DAGVisitor<T>> visitors) {
    DAGVisitResult result = CONTINUE;
    synchronized (entered) {

      if (entered.contains(node))
        return CONTINUE;
      entered.add(node);
    }

    boolean skipSiblings = false;
    boolean skipChildren = false;

    for (final DAGVisitor<T> v : visitors) {
      result = v.preVisitNode(node);
      switch (result) {
      case CONTINUE:
        break;
      case SKIP_SIBLINGS:
        skipSiblings = true;
        break;
      case SKIP_SUBTREE:
        skipChildren = true;
        break;
      case TERMINATE:
        return result;
      }
    }

    if (!skipChildren) {
      for (final DAGVisitor<T> v : visitors) {
        result = v.visitNode(node);
        visited.add(node);
        switch (result) {
        case CONTINUE:
        case SKIP_SUBTREE:
          break;
        case SKIP_SIBLINGS:
          skipChildren = true;
          break;
        case TERMINATE:
          return result;
        }
      }
      for (final Vertex<T> child : node.getChildren()) {
        result = threadedVisitation(false, visited, entered, child, node.getChildren(), visitors);
        if (result == TERMINATE)
          return result;
        if (result == SKIP_SIBLINGS) {
          result = CONTINUE;
          break;
        }
      }
    }
    for (final DAGVisitor<T> v : visitors) {
      result = v.postVisitNode(node);
      switch (result) {
      case CONTINUE:
      case SKIP_SUBTREE:
      case SKIP_SIBLINGS:
        break;
      case TERMINATE:
        return result;
      }
    }
    return skipSiblings ? SKIP_SIBLINGS : result;
  }

}
