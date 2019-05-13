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
package org.infrastructurebuilder.util.dag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.dag.DAGBuilder.MutableDAGImpl;
import org.junit.Before;
import org.junit.Test;

public class DAG2UUIDTest {

  private UUID a, b, c, d, e;
  private MutableDAG<UUID> dag, dag1, dag2;
  private MutableVertex<UUID> dV;
  MutableVertex<UUID> cV;

  @Before
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

  @Test(expected = CycleDetectedException.class)
  public final void testAddEdgeVertexOfTVertexOfT() throws CycleDetectedException {
    cV = dag1.getVertex(c);
    dV = dag1.getVertex(d);
    dag1.addEdge(dV, cV);
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
      assertTrue("Message has c id", msg2.contains(c.toString()));
      assertTrue("Message has d id", msg2.contains(d.toString()));
      final String message = e.cycleToString();
      c.toString();
      d.toString();
      assertTrue("Message has c id", message.contains(c.toString()));
      assertTrue("Message has d id", message.contains(d.toString()));
    }
  }

  @Test
  public final void testEquals() throws CycleDetectedException {
    assertEquals("Self", dag1, dag1);
    assertNotEquals("Not null", dag1, null);
    assertNotEquals("Not String", dag1, "A");
    assertEquals("dag1 and dag2 are basically the same", dag1, dag2);
    final MutableDAG<UUID> dag3 = new DAGBuilder.MutableDAGImpl<>();
    dag3.addVertex(a);
    dag3.addVertex(b);
    dag3.addEdge(c, d);

  }

  @Test
  public final void testGetChildLabels() {
    final List<UUID> l = dag1.getChildLabels(a);
    assertEquals("There 2 childred of a", 2, l.size());
    assertTrue("b and d are those children", l.contains(b) && l.contains(d));
  }

  @Test
  public final void testGetLabels() {
    final Set<UUID> l = dag.getLabels();
    final Set<UUID> l1 = dag1.getLabels();
    assertEquals("No labels in dag", 0, l.size());
    assertEquals("4 labels in dag", 4, l1.size());
    assertTrue("dag1 labels contains d", l1.contains(d));
  }

  @Test
  public final void testGetParentLabels() {
    final List<UUID> l = dag1.getParentLabels(d);
    assertTrue("Parent labels contains a", l.contains(a));
    assertEquals("Has 2 elements", 2, l.size());
    dag1.addVertex(e);
    assertEquals("e has no parents", 0, dag1.getParentLabels(e).size());
  }

  @Test
  public final void testGetSuccessorLabels() {
    List<UUID> z = dag1.getSuccessorLabels(a);
    assertEquals("Successors is 3", 3, z.size());
    z = dag1.getSuccessorLabels(d);
    assertTrue("has self", z.contains(d));
    assertEquals("Successors is 1", 1, z.size());
  }

  @Test
  public final void testGetVerticies() {
    assertEquals("dag vertices = 0", 0, dag.getVerticies().size());
    assertEquals("dag1 vertices = 4", 4, dag1.getVerticies().size());
    assertFalse("dag1 does not contain e",
        dag1.getVerticies().stream().map(v -> v.getLabel()).collect(Collectors.toSet()).contains(e));
  }

  @Test
  public final void testHasEdge() {
    assertFalse("dag has no edge for b to d", dag.hasEdge(b, d));
    assertTrue("dag1 has b -> d", dag1.hasEdge(b, d));
  }

  @Test
  public final void testHash() {
    assertEquals("Empty dag hash is 961", 961, dag.hashCode());
  }

  @Test
  public final void testIsConnected() throws CloneNotSupportedException {
    dag2.addVertex(e);

    assertTrue("a is connected", dag2.isConnected(c));
    assertTrue("a is connected", dag2.isConnected(a));
    assertFalse("e is not connected", dag2.isConnected(e));
  }

  @Test
  public final void testIsRoot() {
    dag1.addVertex(e);
    assertTrue("C is a root", dag1.getVertex(c).isRoot());
    assertTrue("E is a root", dag1.getVertex(e).isRoot());
    assertFalse("A is not a root", dag1.getVertex(a).isRoot());
  }

  @Test
  public final void testRemoveEdgeTT() {
    assertTrue("dag1 has a->b", dag1.hasEdge(a, b));
    dag1.removeEdge(a, b);
    assertFalse("No longer an a->b edge", dag1.hasEdge(a, b));
  }

  @Test
  public final void testSortEntireGraph() throws CloneNotSupportedException {
    dag2.addVertex(e);
    final List<UUID> z = new DAGBuilder.MutableDAGImpl.MutableTopologicalSorterImpl<UUID>().sort(dag2);
    assertEquals("sorted output is size 5", 5, z.size());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public final void testVertexEquals() throws CycleDetectedException {
    cV = dag1.getVertex(c);
    assertEquals("c is c", cV, cV);
    assertNotEquals("c is not null", cV, null);
    assertNotEquals("c is not a String", cV, "abc");
    final MutableVertex c1V = new DAGBuilder.MutableDAGImpl.MutableVertexImpl(cV.getLabel(),
        new DAGBuilder.MutableDAGImpl.MutableTopologicalSorterImpl());
    assertNotEquals("Children aren't equal", c1V, cV);
    assertEquals("Equal dags have equal vertex", dag1.getVertex(c), dag2.getVertex(c));
    final MutableDAG<UUID> dag4 = new MutableDAGImpl<>();
    dag4.addEdge(a, b);
    dag4.addVertex(c);
    dag4.addEdge(c, a);
    dag4.addEdge(c, b);
    dag4.addEdge(a, d);
    dag4.addEdge(b, d);
    dag4.addEdge(e, c);
    assertNotEquals("Same children different parents is different", dag1.getVertex(c), dag4.getVertex(c));
  }

}
