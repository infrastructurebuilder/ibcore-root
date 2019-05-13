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
package org.infrastructurebuilder.util.dag.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.infrastructurebuilder.util.dag.CycleDetectedException;
import org.infrastructurebuilder.util.dag.DAG;
import org.infrastructurebuilder.util.dag.DAGBuilder;
import org.infrastructurebuilder.util.dag.DAGVisitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DAGWalkerPreTraversalDepthFirstImplTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  private DAGWalkerPreTraversalDepthFirstImpl<String> d;

  @Before
  public void setUp() throws Exception {
    d = new DAGWalkerPreTraversalDepthFirstImpl<>();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testClose() throws Exception {
    d.close();
  }

  @Test
  public void testDAGWalkerPreTraversalDepthFirstImpl() {
    assertNotNull(d);
  }

  @Test
  public void testWalk() throws CycleDetectedException {
    final DAG<String> dag = new DAGBuilder<String>().addEdge("A", "B").build();
    final List<DAGVisitor<String>> visitors = Arrays.asList(new DAGVisitor<String>() {
    });
    d.walk(dag, visitors);
    assertFalse(visitors.get(0).getVisitationState().isPresent());
  }

}
