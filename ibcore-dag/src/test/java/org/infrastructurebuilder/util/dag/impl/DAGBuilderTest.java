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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.infrastructurebuilder.util.dag.CycleDetectedException;
import org.infrastructurebuilder.util.dag.DAG;
import org.infrastructurebuilder.util.dag.DAGBuilder;
import org.infrastructurebuilder.util.dag.DAGVisitResult;
import org.infrastructurebuilder.util.dag.DAGVisitor;
import org.infrastructurebuilder.util.dag.MutableVertex;
import org.infrastructurebuilder.util.dag.Vertex;
import org.infrastructurebuilder.util.dag.impl.DAGBuilderImpl.MutableDAGImpl;
import org.infrastructurebuilder.util.dag.impl.DAGBuilderImpl.MutableDAGImpl.DAGImpl.DepthFirstTopologicalSorterImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DAGBuilderTest {

  private static final String FIVE = "5";
  private static final String FOUR = "4";
  private static final String ONE = "1";
  private static final String THREE = "3";
  private static final String TWO = "2";

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
  }

  private DAG<String> b;
  private DAG<String> c;
  private DAG<String> d;
//  private MutableDAGImpl<String> dag;
  private DAGBuilder<String> md;

  @BeforeEach
  public void setUp() throws Exception {
    b = new DAGBuilderImpl<String>().addEdge(ONE, TWO).addEdge(TWO, THREE).addEdge(ONE, FOUR).build();
    c = new DAGBuilderImpl<>(b).build();
    final DAGBuilder<String> d1 = new DAGBuilderImpl<>(b);
    d1.addVertex(FIVE);
    d = d1.build();

    md = new DAGBuilderImpl<>();
//    dag = Reflect.on(md).get("dag");
//    assertNotNull(dag);
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testAddEdgeMutableVertexOfTMutableVertexOfT() throws CycleDetectedException {
    final DAGBuilder<String> d1 = new DAGBuilderImpl<>(b);
    final MutableVertex<String> one = d1.addVertex(ONE);
    final MutableVertex<String> two = d1.addVertex(TWO);
    final MutableVertex<String> three = d1.addVertex(THREE);
    final MutableVertex<String> four = d1.addVertex(FOUR);
    d1.addEdge(one, two).addEdge(two, three).addEdge(one, four);
    final DAG<String> g = d1.build();
    assertEquals(b, g);
    final List<String> l = Arrays.asList(TWO, FOUR, TWO, FOUR);
    final List<String> k = g.getChildLabels(ONE);
    final Set<String> m = g.getLabels();
    final Set<String> expectedLab = new HashSet<>(Arrays.asList(ONE, TWO, THREE, FOUR));
    assertEquals(l, k);
    assertEquals(expectedLab, m);

    final List<String> p = g.getParentLabels(THREE);
    assertEquals(Arrays.asList(TWO, TWO), p);
  }

  @Test
  public void testDAGBuilderDAGOfT() throws CycleDetectedException {
    assertEquals(b, c);
    assertNotEquals(b, d);
    assertNotEquals(b, new DAGBuilderImpl<>(b).addEdge(ONE, THREE).build());

  }

  @Test
  public void testDAGImplEquals() throws CycleDetectedException {
    assertEquals(b, b);
    assertNotEquals(b, null);
    assertNotEquals(b, "X");

    final DAGBuilder<String> d1 = new DAGBuilderImpl<>();
    final MutableVertex<String> one = d1.addVertex(ONE);
    final MutableVertex<String> two = d1.addVertex(TWO);
    final MutableVertex<String> three = d1.addVertex(THREE);
    final DAGBuilder<String> d2 = new DAGBuilderImpl<>();
    d2.addVertex(ONE);
    d2.addVertex(TWO);
    d2.addVertex(THREE);
    d2.addVertex(FOUR);
    assertNotEquals(d2.build(), d1.build());
    final DAG<String> dag2 = d1.build();
    final MutableVertex<String> four = d1.addVertex(FOUR);
    d1.addEdge(one, two).addEdge(two, three).addEdge(one, four).addEdge(two, four);
    d1.addEdge(FIVE, TWO);
    final DAG<String> d2d = d2.build();
    final DAG<String> d1d = d1.build();
    d1d.equals(d2d);
    assertNotEquals(dag2, d1.build());
  }

  @Test
  public void testDagNotequals1() throws CycleDetectedException {
    final DAGBuilder<String> d1 = new DAGBuilderImpl<>();
    d1.addVertex(ONE);
    d1.addVertex(TWO);
    d1.addVertex(THREE);
    final DAGBuilder<String> d2 = new DAGBuilderImpl<>();
    d2.addVertex(ONE);
    d2.addVertex(TWO);
    d2.addVertex(FOUR);

    final MutableDAGImpl<String> s1 = new MutableDAGImpl<>(new MutableDAGImpl<>(d1.build()));
    MutableDAGImpl<String> s2 = new MutableDAGImpl<>(d2.build());
    assertNotEquals(s1, s2);
    d2.addVertex(THREE);
    s2 = new MutableDAGImpl<>(d2.build());
    assertNotEquals(s1, s2);
  }

//  @Test
//  public void testGetLabels() throws CycleDetectedException {
//    md.addEdge(ONE, TWO).addEdge(TWO, THREE).addEdge(TWO, FOUR);
//    assertEquals(Arrays.asList(ONE), dag.getParentLabels(TWO));
//    assertEquals(Arrays.asList(THREE, FOUR), dag.getChildLabels(TWO));
//    assertEquals(Arrays.asList(TWO), dag.getChildLabels(ONE));
//    assertEquals(dag.getSuccessorLabels(TWO), Arrays.asList(THREE, FOUR, TWO));
//    assertEquals(md.build().getSuccessorLabels(TWO), Arrays.asList(THREE, FOUR, TWO));
//
//  }

  @Test
  public void testHasEdge() {
    assertTrue(d.hasEdge(ONE, TWO));
    assertFalse(d.hasEdge(ONE, FIVE));
  }

  @Test
  public void testHash() {
    assertTrue(md.build().hashCode() != 0);
  }

  @Test
  public void testRemoveEdgeMutableVertexOfTMutableVertexOfT() throws CycleDetectedException {
    final DAGBuilder<String> d1 = new DAGBuilderImpl<>(b);
    final MutableVertex<String> one = d1.addVertex(ONE);
    final MutableVertex<String> two = d1.addVertex(TWO);
    final MutableVertex<String> three = d1.addVertex(THREE);
    final MutableVertex<String> four = d1.addVertex(FOUR);
    d1.addEdge(one, two).addEdge(two, three).addEdge(one, four).addEdge(two, four);

    d1.removeEdge(two, four);

    assertEquals(b, d1.build());
  }

  @Test
  public void testRemoveEdgeTT() throws CycleDetectedException {
    final DAGBuilder<String> d1 = new DAGBuilderImpl<>(b);
    final MutableVertex<String> one = d1.addVertex(ONE);
    final MutableVertex<String> two = d1.addVertex(TWO);
    final MutableVertex<String> three = d1.addVertex(THREE);
    final MutableVertex<String> four = d1.addVertex(FOUR);
    assertFalse(four.isRoot());
    assertTrue(four.toString().startsWith("Vertex{label="));
    assertFalse(four.getChildLabels().size() > 0);
    d1.addEdge(one, two).addEdge(two, three).addEdge(one, four).addEdge(two, four);

    d1.removeEdge(TWO, FOUR);

    assertEquals(b, d1.build());
  }

  @Test
  public void testSortTT() throws CycleDetectedException {
    final DAGBuilder<String> d1 = new DAGBuilderImpl<>(b);
    final MutableVertex<String> one = d1.addVertex(ONE);
    final MutableVertex<String> two = d1.addVertex(TWO);
    final MutableVertex<String> three = d1.addVertex(THREE);
    final MutableVertex<String> four = d1.addVertex(FOUR);
    d1.addEdge(one, two).addEdge(two, three).addEdge(one, four).addEdge(two, four);

    final DepthFirstTopologicalSorterImpl<String> s = new DepthFirstTopologicalSorterImpl<>();
    final List<String> a = s.sort(d1.build());
    assertEquals(Arrays.asList(THREE, FOUR, TWO, ONE), a);
  }

  @Test
  public void testVertexImplEquals() throws CycleDetectedException {

    final DAGBuilder<String> d1 = new DAGBuilderImpl<>();
    final DAGBuilder<String> d2 = new DAGBuilderImpl<>();
    d2.addVertex(ONE);
    d2.addVertex(TWO);
    d2.addVertex(THREE);
    d2.addVertex(FOUR);
    assertNotEquals(d2.build(), d1.build());
    d1.addVertex(TWO);
    final DAG<String> dag1 = d1.build();
    final DAG<String> dag2 = d2.build();

    final Vertex<String> v2 = dag2.getVertex(TWO);
    assertEquals("Vertex{label='2'}", v2.toString());
    assertTrue(v2.isRoot());
    assertTrue(v2.isLeaf());
    assertNotNull(UUID.fromString(v2.getId()));
    assertFalse(v2.equals(null));
    final Vertex<String> v1 = dag1.getVertex(TWO);
    assertEquals(v1, v2);

  }

  @Test
  public void testWalkMutable() throws CycleDetectedException {
    final DAGBuilder<String> d1 = new DAGBuilderImpl<>();
    d1.addVertex(ONE);
    d1.addVertex(TWO);
    d1.addVertex(THREE);
    final DAGBuilder<String> d2 = new DAGBuilderImpl<>();
    d2.addVertex(ONE);
    d2.addVertex(TWO);
    d2.addVertex(FOUR);

    new MutableDAGImpl<>(new MutableDAGImpl<>(d1.build()));

    final List<String> collected = new ArrayList<>();
    final DAGVisitor<String> v = new DAGVisitor<String>() {
      @Override
      public DAGVisitResult visitNode(final Vertex<String> node) {
        collected.add(node.getLabel());
        return DAGVisitResult.SKIP_SIBLINGS;
      }
    };
    final List<DAGVisitor<String>> visitors = Arrays.asList(v);
    d1.build().walk(new DAGWalkerPreTraversalDepthFirstImpl<>(), visitors);
    assertEquals(1, visitors.size());
  }
}
