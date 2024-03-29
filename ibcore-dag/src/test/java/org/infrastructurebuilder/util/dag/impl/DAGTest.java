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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.infrastructurebuilder.util.dag.CycleDetectedException;
import org.infrastructurebuilder.util.dag.MutableDAG;
import org.infrastructurebuilder.util.dag.MutableVertex;
import org.junit.jupiter.api.Test;

public class DAGTest {
  @Test
  public void testCycleException() {
    final CycleDetectedException e = new CycleDetectedException("Test Message", new ArrayList<String>());
    assertFalse(e.getCycle() == null);
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testDAG() throws CycleDetectedException {
    final MutableDAG<String> dag = new DAGBuilderImpl.MutableDAGImpl<>();

    dag.addVertex("a");
    final MutableVertex<String> v = dag.getVertex("a");
    assertFalse(v.equals("ABC"));
    assertEquals(1, dag.getVerticies().size());

    assertEquals("a", dag.getVertex("a").getLabel());

    dag.addVertex("a");

    assertEquals(1, dag.getVerticies().size());

    assertEquals("a", dag.getVertex("a").getLabel());

    dag.addVertex("b");
    assertFalse(dag.getVertex("b").isConnected());
    assertFalse(dag.isConnected("b"));
    assertEquals(2, dag.getVerticies().size());

    assertFalse(dag.hasEdge("a", "b"));

    assertFalse(dag.hasEdge("b", "a"));

    final MutableVertex a = dag.getVertex("a");

    final MutableVertex b = dag.getVertex("b");

    assertEquals("a", a.getLabel());

    assertEquals("b", b.getLabel());

    dag.addEdge("a", "b");
    assertTrue(dag.isConnected("b"));

    assertTrue(a.getChildren().contains(b));

    assertTrue(b.getParents().contains(a));

    assertTrue(dag.hasEdge("a", "b"));

    assertFalse(dag.hasEdge("b", "a"));

    dag.addEdge("c", "d");

    assertEquals(4, dag.getVerticies().size());

    final MutableVertex c = dag.getVertex("c");

    final MutableVertex d = dag.getVertex("d");

    assertEquals("a", a.getLabel());

    assertEquals("b", b.getLabel());

    assertEquals("c", c.getLabel());

    assertEquals("d", d.getLabel());

    assertFalse(dag.hasEdge("b", "a"));

    assertFalse(dag.hasEdge("a", "c"));

    assertFalse(dag.hasEdge("a", "d"));

    assertTrue(dag.hasEdge("c", "d"));

    assertFalse(dag.hasEdge("d", "c"));

    final Set<String> labels = dag.getLabels();

    assertEquals(4, labels.size());

    assertTrue(labels.contains("a"));

    assertTrue(labels.contains("b"));

    assertTrue(labels.contains("c"));

    assertTrue(labels.contains("d"));

    dag.addEdge("a", "d");

    assertTrue(a.getChildren().contains(d));

    assertTrue(d.getParents().contains(a));

    assertEquals(2, a.getChildren().size());

    assertTrue(a.getChildLabels().contains("b"));

    assertTrue(a.getChildLabels().contains("d"));

    assertEquals(2, d.getParents().size());

    assertTrue(d.getParentLabels().contains("a"));

    assertTrue(d.getParentLabels().contains("c"));

  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  @Test
  public void testGetPredessors() throws CycleDetectedException {
    final MutableDAG dag = new DAGBuilderImpl.MutableDAGImpl<>();

    dag.addEdge("a", "b");

    dag.addVertex("c");

    dag.addVertex("d");

    dag.addEdge("a", "b");

    dag.addEdge("b", "c");

    dag.addEdge("b", "d");

    dag.addEdge("c", "d");

    dag.addEdge("c", "e");

    dag.addEdge("f", "d");

    dag.addEdge("e", "f");

    dag.addEdge("f", "g");

    final List<String> actual = dag.getSuccessorLabels("b");

    final List<String> expected = new ArrayList<>();

    expected.add("d");

    expected.add("g");

    expected.add("f");

    expected.add("e");

    expected.add("c");

    expected.add("b");

    assertEquals(expected, actual);
  }

}
