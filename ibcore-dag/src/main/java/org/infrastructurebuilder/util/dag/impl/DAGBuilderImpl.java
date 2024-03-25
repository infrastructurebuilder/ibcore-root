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
package org.infrastructurebuilder.util.dag.impl;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.dag.CycleDetectedException;
import org.infrastructurebuilder.util.dag.CycleDetector;
import org.infrastructurebuilder.util.dag.DAG;
import org.infrastructurebuilder.util.dag.DAGBuilder;
import org.infrastructurebuilder.util.dag.DAGVisitor;
import org.infrastructurebuilder.util.dag.DAGWalker;
import org.infrastructurebuilder.util.dag.MutableDAG;
import org.infrastructurebuilder.util.dag.MutableVertex;
import org.infrastructurebuilder.util.dag.TopologicalSorter;
import org.infrastructurebuilder.util.dag.Vertex;

public class DAGBuilderImpl<T extends Comparable<T>> implements DAGBuilder<T> {
  public static final class MutableDAGImpl<T extends Comparable<T>> implements Serializable, MutableDAG<T> {
    /**
     *
     */
    private static final long serialVersionUID = 7477357523381006826L;

    public static final class DAGImpl<T extends Comparable<T>> implements Serializable, DAG<T> {
      /**
       *
       */
      private static final long serialVersionUID = 8128453028940561054L;

      public final static class DepthFirstTopologicalSorterImpl<T extends Comparable<T>>
          implements TopologicalSorter<T> {

        private final Integer NOT_VISTITED = 0;

        private final Integer VISITED = 2;

        private final Integer VISITING = 1;

        @Override
        public List<T> sort(final DAG<T> graph) {
          return dfs(graph);
        }

        @Override
        public List<T> sort(final Vertex<T> Vertex) {

          final List<T> retValue = new LinkedList<>();

          dfsVisit(Vertex, new HashMap<Vertex<T>, Integer>(), retValue);

          return retValue;
        }

        private List<T> dfs(final DAG<T> graph) {

          final List<T> retValue = new LinkedList<>();
          final Map<Vertex<T>, Integer> VertexStateMap = new HashMap<>();

          for (final Vertex<T> Vertex : graph.getVerticies())
            if (isNotVisited(Vertex, VertexStateMap)) {
              dfsVisit(Vertex, VertexStateMap, retValue);
            }

          return retValue;
        }

        private void dfsVisit(final Vertex<T> Vertex, final Map<Vertex<T>, Integer> VertexStateMap,
            final List<T> list) {
          VertexStateMap.put(Vertex, VISITING);

          for (final Vertex<T> v : Vertex.getChildren())
            if (isNotVisited(v, VertexStateMap)) {
              dfsVisit(v, VertexStateMap, list);
            }

          VertexStateMap.put(Vertex, VISITED);

          list.add(Vertex.getLabel());
        }

        private boolean isNotVisited(final Vertex<T> Vertex, final Map<Vertex<T>, Integer> VertexStateMap) {
          final Integer state = VertexStateMap.get(Vertex);

          return state == null || NOT_VISTITED.equals(state);
        }

      }

      @SuppressWarnings("hiding")
      private class VertexImpl<T extends Comparable<T>> implements Serializable, Vertex<T> {

        /**
         *
         */
        private static final long serialVersionUID = -7908862588481132800L;

        private final DAGImpl<T> dag;

        private final String id = UUID.randomUUID().toString();

        private final T label;

        final List<Vertex<T>> children = new ArrayList<>();
        final List<Vertex<T>> parents = new ArrayList<>();

        public VertexImpl(final DAGImpl<T> dagImpl, final T label) {
          this.dag = dagImpl;
          this.label = Objects.requireNonNull(label);
        }

        public void addEdgeTo(final Vertex<T> to) {
          children.add(to);
        }

        @Override
        public int compareTo(final Vertex<T> o) {
          final int retval = getLabel().compareTo(o.getLabel());
          return retval;
        }

        @SuppressWarnings({
            "rawtypes", "unchecked"
        })
        @Override
        public boolean equals(final Object obj) {
          if (this == obj)
            return true;
          if (obj == null)
            return false;
          if (!(obj instanceof Vertex))
            return false;
          final Vertex<T> other = (Vertex<T>) obj;
          if (!label.equals(other.getLabel()))
            return false;
          final List l1 = dag.sorter.sort(this);
          final List r1 = dag.sorter.sort(other);
          if (!Arrays.deepEquals(l1.toArray(), r1.toArray()))
            return false;
          if (!parents.equals(other.getParents()))
            return false;
          return true;
        }

        @Override
        public List<T> getChildLabels() {
          final List<T> retValue = new ArrayList<>(children.size());

          for (final Vertex<T> vertex : children) {
            retValue.add(vertex.getLabel());
          }
          return retValue;
        }

        @Override
        public List<Vertex<T>> getChildren() {
          return children;
        }

        @Override
        public String getId() {
          return this.id;
        }

        @Override
        public T getLabel() {
          return label;
        }

        @Override
        public List<T> getParentLabels() {
          final List<T> retValue = new ArrayList<>(parents.size());

          for (final Vertex<T> vertex : parents) {
            retValue.add(vertex.getLabel());
          }
          return retValue;
        }

        @Override
        public List<Vertex<T>> getParents() {
          return parents;
        }

        @Override
        public boolean isConnected() {
          return parents.size() > 0 || children.size() > 0;
        }

        @Override
        public boolean isLeaf() {
          return children.size() == 0;
        }

        @Override
        public boolean isRoot() {
          return parents.size() == 0;
        }

        @Override
        public String toString() {
          return "Vertex{" + "label='" + label + "'" + "}";
        }

        private void addEdgeFrom(final Vertex<T> from) {
          parents.add(from);
        }

      }

      private final TopologicalSorter<T> sorter = new DepthFirstTopologicalSorterImpl<>();

      private final NavigableMap<T, VertexImpl<T>> vertexMap = new TreeMap<>();

      private final NavigableSet<Vertex<T>> vertexTreeSet = new TreeSet<>();

      public DAGImpl(final MutableDAG<T> inDag) throws CycleDetectedException {
        super();
        for (final MutableVertex<T> v : inDag.getVerticies()) {
          addVertex(v.getLabel());
          if (v.isConnected()) {
            for (final T label : v.getChildLabels()) {
              addEdge(v.getLabel(), label);
            }
          }
        }
      }

      @SuppressWarnings({
          "unchecked"
      })
      @Override
      public boolean equals(final Object obj) {
        if (this == obj)
          return true;
        if (obj == null)
          return false;
        if (getClass() != obj.getClass())
          return false;
        final DAG<T> other = (DAG<T>) obj;

        final Set<Vertex<T>> others = new HashSet<>();
        others.addAll(other.getVerticies());

        for (final Vertex<T> vertex : this.getVerticies()) {
          final T label = vertex.getLabel();
          final Vertex<T> otherV = other.getVertex(label);
          if (otherV == null)
            return false;

          final List<T> thisParents = vertex.getParentLabels().stream().distinct().sorted().toList();
          final List<T> otherParents = otherV.getParentLabels().stream().distinct().sorted().toList();

          if (!thisParents.equals(otherParents))
            return false;

          final List<T> thisChildren = vertex.getChildLabels().stream().distinct().sorted().toList();
          final List<T> otherChildren = otherV.getChildLabels().stream().distinct().sorted().toList();

          if (!thisChildren.equals(otherChildren))
            return false;
          others.remove(otherV);
        }
        return others.size() == 0;
      }

      @Override
      public List<T> getChildLabels(final T label) {
        return getVertex(label).getChildLabels();
      }

      @Override
      public Set<T> getLabels() {
        return vertexMap.keySet();
      }

      @Override
      public List<T> getParentLabels(final T label) {
        return getVertex(label).getParentLabels();
      }

      @Override
      public Set<Vertex<T>> getRoots() {
        return vertexTreeSet.stream().filter(v -> v.getParentLabels().size() == 0).collect(Collectors.toSet());
      }

      @Override
      public List<T> getSuccessorLabels(final T label) {
        final Vertex<T> vertex = getVertex(label);

        final List<T> retValue;

        if (vertex.isLeaf()) {
          retValue = new ArrayList<>(1);

          retValue.add(label);
        } else {
          retValue = new DepthFirstTopologicalSorterImpl<T>().sort(vertex);
        }

        return retValue;
      }

      @Override
      public Vertex<T> getVertex(final T label) {
        final Vertex<T> retValue = vertexMap.get(label);

        return retValue;
      }

      @Override
      public Set<Vertex<T>> getVerticies() {
        return vertexTreeSet;
      }

      @Override
      public boolean hasEdge(final T label1, final T label2) {
        final Vertex<T> v1 = getVertex(label1);

        if (v1 != null) {

          final Vertex<T> v2 = getVertex(label2);

          final boolean retValue = v1.getChildren().contains(v2);

          return retValue;
        }
        return false;
      }

      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + vertexTreeSet.hashCode();
        result = prime * result + vertexMap.hashCode();
        return result;
      }

      @Override
      public boolean isConnected(final T label) {
        return getVertex(label).isConnected();
      }

      @Override
      public void walk(final DAGWalker<T> walker, final List<DAGVisitor<T>> visitors) {
        Objects.requireNonNull(walker, "DAGWalker").walk(this, Objects.requireNonNull(visitors, "DAGWalk Visitors"));
      }

      private void addEdge(final T from, final T to) throws CycleDetectedException {
        final VertexImpl<T> v1 = addVertex(from);

        final VertexImpl<T> v2 = addVertex(to);

        addEdge(v1, v2);
      }

      private void addEdge(final VertexImpl<T> from, final VertexImpl<T> to) {

        from.addEdgeTo(to);

        to.addEdgeFrom(from);
      }

      private VertexImpl<T> addVertex(final T label) {
        VertexImpl<T> retValue = null;

        if (vertexMap.containsKey(label)) {
          retValue = vertexMap.get(label);
        } else {
          retValue = new VertexImpl<>(this, label);

          vertexMap.put(label, retValue);

          vertexTreeSet.add(retValue);
        }

        return retValue;
      }

    }

    static class MutableTopologicalSorterImpl<T extends Comparable<T>> {

      private final Integer NOT_VISTITED = 0;

      private final Integer VISITED = 2;

      private final Integer VISITING = 1;

      public List<T> sort(final MutableDAG<T> graph) {
        return dfs(graph);
      }

      public List<T> sort(final MutableVertex<T> MutableVertex) {

        final List<T> retValue = new LinkedList<>();

        dfsVisit(MutableVertex, new HashMap<MutableVertex<T>, Integer>(), retValue);

        return retValue;
      }

      private List<T> dfs(final MutableDAG<T> graph) {

        final List<T> retValue = new LinkedList<>();
        final Map<MutableVertex<T>, Integer> MutableVertexStateMap = new HashMap<>();

        for (final MutableVertex<T> MutableVertex : graph.getVerticies())
          if (isNotVisited(MutableVertex, MutableVertexStateMap)) {
            dfsVisit(MutableVertex, MutableVertexStateMap, retValue);
          }

        return retValue;
      }

      private void dfsVisit(final MutableVertex<T> MutableVertex,
          final Map<MutableVertex<T>, Integer> MutableVertexStateMap, final List<T> list) {
        MutableVertexStateMap.put(MutableVertex, VISITING);

        for (final MutableVertex<T> v : MutableVertex.getChildren())
          if (isNotVisited(v, MutableVertexStateMap)) {
            dfsVisit(v, MutableVertexStateMap, list);
          }

        MutableVertexStateMap.put(MutableVertex, VISITED);

        list.add(MutableVertex.getLabel());
      }

      private boolean isNotVisited(final MutableVertex<T> MutableVertex,
          final Map<MutableVertex<T>, Integer> MutableVertexStateMap) {
        final Integer state = MutableVertexStateMap.get(MutableVertex);

        return state == null || NOT_VISTITED.equals(state);
      }

    }

    static class MutableVertexImpl<T extends Comparable<T>> implements Serializable, MutableVertex<T> {

      /**
       *
       */
      private static final long serialVersionUID = -5583117388101863872L;
      private final T label;
      private final MutableTopologicalSorterImpl<T> sorter;

      final List<MutableVertex<T>> children = new ArrayList<>();

      final List<MutableVertex<T>> parents = new ArrayList<>();

      MutableVertexImpl(final T label, final MutableTopologicalSorterImpl<T> sorter) {
        this.label = Objects.requireNonNull(label);
        this.sorter = Objects.requireNonNull(sorter);
      }

      @Override
      public void addEdgeFrom(final MutableVertex<T> vertex) {
        parents.add(vertex);
      }

      @Override
      public void addEdgeTo(final MutableVertex<T> vertex) {
        children.add(vertex);
      }

      @Override
      public int compareTo(final MutableVertex<T> o) {
        final int retval = getLabel().compareTo(o.getLabel());
        return retval;
      }

      @SuppressWarnings({
          "rawtypes", "unchecked"
      })
      @Override
      public boolean equals(final Object obj) {
        if (this == obj)
          return true;
        if (obj == null)
          return false;
        if (!(obj instanceof MutableVertex))
          return false;
        final MutableVertex<T> other = (MutableVertex<T>) obj;
        if (!label.equals(other.getLabel()))
          return false;
        final List l1 = this.sorter.sort(this);
        final List r1 = this.sorter.sort(other);
        if (!Arrays.deepEquals(l1.toArray(), r1.toArray()))
          return false;
        if (!parents.equals(other.getParents()))
          return false;
        return true;
      }

      @Override
      public List<T> getChildLabels() {
        final List<T> retValue = new ArrayList<>(children.size());

        for (final MutableVertex<T> vertex : children) {
          retValue.add(vertex.getLabel());
        }
        return retValue;
      }

      @Override
      public List<MutableVertex<T>> getChildren() {
        return children;
      }

      @Override
      public T getLabel() {
        return label;
      }

      @Override
      public List<T> getParentLabels() {
        final List<T> retValue = new ArrayList<>(parents.size());

        for (final MutableVertex<T> vertex : parents) {
          retValue.add(vertex.getLabel());
        }
        return retValue;
      }

      @Override
      public List<MutableVertex<T>> getParents() {
        return parents;
      }

      @Override
      public boolean isConnected() {
        return parents.size() > 0 || children.size() > 0;
      }

      @Override
      public boolean isLeaf() {
        return children.size() == 0;
      }

      @Override
      public boolean isRoot() {
        return parents.size() == 0;
      }

      @Override
      public void removeEdgeFrom(final MutableVertex<T> vertex) {
        parents.remove(vertex);
      }

      @Override
      public void removeEdgeTo(final MutableVertex<T> vertex) {
        children.remove(vertex);
      }

      @Override
      public String toString() {
        return "Vertex{" + "label='" + label + "'" + "}";
      }

    }

    private final CycleDetector<T> cycleDetector = new CycleDetectorImpl<>();

    private final MutableTopologicalSorterImpl<T> sorter = new MutableTopologicalSorterImpl<>();

    private final NavigableMap<T, MutableVertexImpl<T>> vertexMap = new TreeMap<>();

    private final NavigableSet<MutableVertex<T>> vertexTreeSet = new TreeSet<>();

    MutableDAGImpl() {
      super();
    }

    MutableDAGImpl(final DAG<T> inDag) throws CycleDetectedException {
      for (final Vertex<T> v : inDag.getVerticies()) {
        addVertex(v.getLabel());
        if (v.isConnected()) {
          for (final T label : v.getChildLabels()) {
            addEdge(v.getLabel(), label);
          }
        }
      }
    }

    MutableDAGImpl(final MutableDAG<T> inDag) throws CycleDetectedException {
      for (final MutableVertex<T> v : inDag.getVerticies()) {
        addVertex(v.getLabel());
        if (v.isConnected()) {
          for (final T label : v.getChildLabels()) {
            addEdge(v.getLabel(), label);
          }
        }
      }
    }

    @Override
    public void addEdge(final MutableVertex<T> from, final MutableVertex<T> to) throws CycleDetectedException {

      from.addEdgeTo(to);

      to.addEdgeFrom(from);

      final List<T> cycle = cycleDetector.introducesCycle(to);

      if (cycle != null) {

        removeEdge(from, to);

        final String msg = "Edge between '" + from + "' and '" + to + "' introduces to cycle in the graph";

        throw new CycleDetectedException(msg, cycle);
      }
    }

    @Override
    public void addEdge(final T from, final T to) throws CycleDetectedException {
      final MutableVertex<T> v1 = addVertex(from);

      final MutableVertex<T> v2 = addVertex(to);

      addEdge(v1, v2);
    }

    @Override
    public MutableVertex<T> addVertex(final T label) {
      MutableVertexImpl<T> retValue = null;

      if (vertexMap.containsKey(label)) {
        retValue = vertexMap.get(label);
      } else {
        retValue = new MutableVertexImpl<>(label, this.sorter);

        vertexMap.put(label, retValue);

        vertexTreeSet.add(retValue);
      }

      return retValue;
    }

    @SuppressWarnings({
        "unchecked"
    })
    @Override
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final MutableDAG<T> other = (MutableDAG<T>) obj;

      final List<T> l1 = sorter.sort(this);
      final List<T> r1 = sorter.sort(other);
      if (l1.size() != r1.size())
        return false;
      final Iterator<T> li = l1.iterator();
      final Iterator<T> ri = r1.iterator();
      while (li.hasNext())
        if (li.next().compareTo(ri.next()) != 0)
          return false;
      return true;
    }

    @Override
    public List<T> getChildLabels(final T label) {
      return getVertex(label).getChildLabels();
    }

    @Override
    public Set<T> getLabels() {
      return vertexMap.keySet();
    }

    @Override
    public List<T> getParentLabels(final T label) {
      return getVertex(label).getParentLabels();
    }

    @Override
    public List<T> getSuccessorLabels(final T label) {
      final MutableVertex<T> vertex = getVertex(label);

      final List<T> retValue;

      if (vertex.isLeaf()) {
        retValue = new ArrayList<>(1);

        retValue.add(label);
      } else {
        retValue = new MutableTopologicalSorterImpl<T>().sort(vertex);
      }

      return retValue;
    }

    @Override
    public MutableVertex<T> getVertex(final T label) {
      final MutableVertex<T> retValue = vertexMap.get(label);

      return retValue;
    }

    @Override
    public Set<MutableVertex<T>> getVerticies() {
      return vertexTreeSet;
    }

    @Override
    public boolean hasEdge(final T label1, final T label2) {
      final MutableVertex<T> v1 = getVertex(label1);

      if (v1 != null) {

        final MutableVertex<T> v2 = getVertex(label2);

        final boolean retValue = v1.getChildren().contains(v2);

        return retValue;
      }
      return false;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + vertexTreeSet.hashCode();
      result = prime * result + vertexMap.hashCode();
      return result;
    }

    @Override
    public boolean isConnected(final T label) {
      return getVertex(label).isConnected();
    }

    @Override
    public void removeEdge(final MutableVertex<T> from, final MutableVertex<T> to) {
      from.removeEdgeTo(to);

      to.removeEdgeFrom(from);
    }

    @Override
    public void removeEdge(final T from, final T to) {
      final MutableVertex<T> v1 = addVertex(from);

      final MutableVertex<T> v2 = addVertex(to);

      removeEdge(v1, v2);
    }

  }

  static class CycleDetectorImpl<T extends Comparable<T>> implements CycleDetector<T> {

    private final Integer NOT_VISTITED = 0;

    private final Integer VISITED = 2;

    private final Integer VISITING = 1;

    @Override
    public List<T> introducesCycle(final MutableVertex<T> vertex) {
      final Map<MutableVertex<T>, Integer> vertexStateMap = new HashMap<>();

      return introducesCycle(vertex, vertexStateMap);
    }

    @Override
    public List<T> introducesCycle(final MutableVertex<T> vertex, final Map<MutableVertex<T>, Integer> vertexStateMap) {
      final LinkedList<T> cycleStack = new LinkedList<>();

      final boolean hasCycle = dfsVisit(vertex, cycleStack, vertexStateMap);

      if (hasCycle) {

        final T label = cycleStack.getFirst();

        final int pos = cycleStack.lastIndexOf(label);

        final List<T> cycle = cycleStack.subList(0, pos + 1);

        Collections.reverse(cycle);

        return cycle;
      }

      return null;
    }

    private boolean dfsVisit(final MutableVertex<T> vertex, final LinkedList<T> cycle,
        final Map<MutableVertex<T>, Integer> vertexStateMap) {
      cycle.addFirst(vertex.getLabel());

      vertexStateMap.put(vertex, VISITING);

      for (final MutableVertex<T> v : vertex.getChildren())
        if (isNotVisited(v, vertexStateMap)) {
          final boolean hasCycle = dfsVisit(v, cycle, vertexStateMap);

          if (hasCycle)
            return true;
        } else if (isVisiting(v, vertexStateMap)) {
          cycle.addFirst(v.getLabel());

          return true;
        }
      vertexStateMap.put(vertex, VISITED);

      cycle.removeFirst();

      return false;
    }

    private boolean isNotVisited(final MutableVertex<T> vertex, final Map<MutableVertex<T>, Integer> vertexStateMap) {
      final Integer state = vertexStateMap.get(vertex);

      return state == null || NOT_VISTITED.equals(state);
    }

    private boolean isVisiting(final MutableVertex<T> vertex, final Map<MutableVertex<T>, Integer> vertexStateMap) {
      final Integer state = vertexStateMap.get(vertex);

      return VISITING.equals(state);
    }
  }

  private final MutableDAGImpl<T> dag;

  public DAGBuilderImpl() throws CycleDetectedException {
    this(new MutableDAGImpl<T>());
  }

  public DAGBuilderImpl(final DAG<T> inDag) throws CycleDetectedException {
    this(new MutableDAGImpl<>(inDag));
  }

  public DAGBuilderImpl(final MutableDAG<T> inDag) throws CycleDetectedException {
    dag = new MutableDAGImpl<>(inDag);
  }

  @Override
  public DAGBuilder<T> addEdge(final MutableVertex<T> from, final MutableVertex<T> to) throws CycleDetectedException {
    dag.addEdge(from, to);
    return this;
  }

  @Override
  public DAGBuilder<T> addEdge(final T from, final T to) throws CycleDetectedException {
    dag.addEdge(from, to);
    return this;
  }

  @Override
  public MutableVertex<T> addVertex(final T label) {
    return dag.addVertex(label);
  }

  @Override
  public DAG<T> build() {
    return IBException.cet.returns(() -> {
      return new MutableDAGImpl.DAGImpl<>(dag);
    });
  }

  @Override
  public DAGBuilder<T> removeEdge(final MutableVertex<T> from, final MutableVertex<T> to) {
    dag.removeEdge(from, to);
    return this;
  }

  @Override
  public DAGBuilder<T> removeEdge(final T from, final T to) {
    dag.removeEdge(from, to);
    return this;

  }
}
