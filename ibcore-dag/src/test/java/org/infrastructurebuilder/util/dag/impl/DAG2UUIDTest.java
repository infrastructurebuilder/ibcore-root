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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.dag.CycleDetectedException;
import org.infrastructurebuilder.util.dag.MutableDAG;
import org.infrastructurebuilder.util.dag.MutableVertex;
import org.infrastructurebuilder.util.dag.impl.DAGBuilderImpl.MutableDAGImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DAG2UUIDTest {

  private UUID                a, b, c, d, e;
  private MutableDAG<UUID>    dag, dag1, dag2;
  private MutableVertex<UUID> dV;
  MutableVertex<UUID>         cV;

  @BeforeEach
  public void setUp() throws Exception {
    a = UUID.randomUUID();
    b = UUID.randomUUID();
    c = UUID.randomUUID();
    d = UUID.randomUUID();
    e = UUID.randomUUID();
    dag = new MutableDAGImpl<>();
    dag1 = new MutableDAGImpl<>();
    dag1.addEdge(a, b);
    dag1.addVertex(c);
    dag1.addEdge(c, a);
    dag1.addEdge(c, b);
    dag1.addEdge(a, d);
    dag1.addEdge(b, d);
    dag2 = new MutableDAGImpl<>();
    dag2.addEdge(a, b);
    dag2.addVertex(c);
    dag2.addEdge(c, a);
    dag2.addEdge(c, b);
    dag2.addEdge(a, d);
    dag2.addEdge(b, d);
  }

  @Test
  public final void testAddEdgeVertexOfTVertexOfT() throws CycleDetectedException {
    cV = dag1.getVertex(c);
    dV = dag1.getVertex(d);
    Assertions.assertThrows(CycleDetectedException.class, () -> dag1.addEdge(dV, cV));
  }

  @SuppressWarnings("unused")
  @Test
  public final void testAddEdgeVertexOfTVertexOfTCaughtCheckMessage() {
    cV = dag1.getVertex(c);
    dV = dag1.getVertex(d);
    try {
      dag1.addEdge(dV, cV);
    } catch (final CycleDetectedException e) {
      final String msg2 = e.getMessage();
      assertTrue(msg2.contains(c.toString()), "Message has c id");
      assertTrue(msg2.contains(d.toString()), "Message has d id");
      final String message = e.cycleToString();
      c.toString();
      d.toString();
      assertTrue(message.contains(c.toString()), "Message has c id");
      assertTrue(message.contains(d.toString()), "Message has d id");
    }
  }

  @Test
  public final void testEquals() throws CycleDetectedException {
    assertEquals(dag1, dag1);
    assertNotEquals(dag1, null);
    assertNotEquals(dag1, "A");
    assertEquals(dag1, dag2);
    final MutableDAG<UUID> dag3 = new DAGBuilderImpl.MutableDAGImpl<>();
    dag3.addVertex(a);
    dag3.addVertex(b);
    dag3.addEdge(c, d);

  }

  @Test
  public final void testGetChildLabels() {
    final List<UUID> l = dag1.getChildLabels(a);
    assertEquals(2, l.size());
    assertTrue(l.contains(b) && l.contains(d));
  }

  @Test
  public final void testGetLabels() {
    final Set<UUID> l  = dag.getLabels();
    final Set<UUID> l1 = dag1.getLabels();
    assertEquals(0, l.size());
    assertEquals(4, l1.size());
    assertTrue(l1.contains(d));
  }

  @Test
  public final void testGetParentLabels() {
    final List<UUID> l = dag1.getParentLabels(d);
    assertTrue(l.contains(a));
    assertEquals(2, l.size());
    dag1.addVertex(e);
    assertEquals(0, dag1.getParentLabels(e).size());
  }

  @Test
  public final void testGetSuccessorLabels() {
    List<UUID> z = dag1.getSuccessorLabels(a);
    assertEquals(3, z.size());
    z = dag1.getSuccessorLabels(d);
    assertTrue(z.contains(d));
    assertEquals(1, z.size());
  }

  @Test
  public final void testGetVerticies() {
    assertEquals(0, dag.getVerticies().size());
    assertEquals(4, dag1.getVerticies().size());
    assertFalse(dag1.getVerticies().stream().map(v -> v.getLabel()).collect(Collectors.toSet()).contains(e));
  }

  @Test
  public final void testHasEdge() {
    assertFalse(dag.hasEdge(b, d));
    assertTrue(dag1.hasEdge(b, d));
  }

  @Test
  public final void testHash() {
    assertEquals(961, dag.hashCode());
  }

  @Test
  public final void testIsConnected() throws CloneNotSupportedException {
    dag2.addVertex(e);

    assertTrue(dag2.isConnected(c));
    assertTrue(dag2.isConnected(a));
    assertFalse(dag2.isConnected(e));
  }

  @Test
  public final void testIsRoot() {
    dag1.addVertex(e);
    assertTrue(dag1.getVertex(c).isRoot());
    assertTrue(dag1.getVertex(e).isRoot());
    assertFalse(dag1.getVertex(a).isRoot());
  }

  @Test
  public final void testRemoveEdgeTT() {
    assertTrue(dag1.hasEdge(a, b));
    dag1.removeEdge(a, b);
    assertFalse(dag1.hasEdge(a, b));
  }

  @Test
  public final void testSortEntireGraph() throws CloneNotSupportedException {
    dag2.addVertex(e);
    final List<UUID> z = new DAGBuilderImpl.MutableDAGImpl.MutableTopologicalSorterImpl<UUID>().sort(dag2);
    assertEquals(5, z.size());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public final void testVertexEquals() throws CycleDetectedException {
    cV = dag1.getVertex(c);
    assertEquals(cV, cV);
    assertNotEquals(cV, null);
    assertNotEquals(cV, "abc");
    final MutableVertex c1V = new DAGBuilderImpl.MutableDAGImpl.MutableVertexImpl(cV.getLabel(),
        new DAGBuilderImpl.MutableDAGImpl.MutableTopologicalSorterImpl());
    assertNotEquals(c1V, cV);
    assertEquals(dag1.getVertex(c), dag2.getVertex(c));
    final MutableDAG<UUID> dag4 = new MutableDAGImpl<>();
    dag4.addEdge(a, b);
    dag4.addVertex(c);
    dag4.addEdge(c, a);
    dag4.addEdge(c, b);
    dag4.addEdge(a, d);
    dag4.addEdge(b, d);
    dag4.addEdge(e, c);
    assertNotEquals(dag1.getVertex(c), dag4.getVertex(c));
  }

}
