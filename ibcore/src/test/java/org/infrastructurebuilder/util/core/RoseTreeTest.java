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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.infrastructurebuilder.pathref.ChecksumBuilder;
import org.infrastructurebuilder.pathref.ChecksumBuilderFactory;
import org.infrastructurebuilder.pathref.RoseX;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoseTreeTest {

  public static class RoseTest extends AbstractRoseTree<RoseX> {

    public static final String TREE_CHILDREN = "children";

    public RoseTest(final RoseX value, final List<RoseTree<RoseX>> children) {
      super(value, ofNullable(children).map(Collections::unmodifiableList)
          .orElseThrow(() -> new IllegalArgumentException("Children cannot be null")));
    }

    @Override
    public String getKeyForTreeInJSON() {
      return TREE_CHILDREN;
    }

    @Override
    public RoseTree<RoseX> withChildren(final List<RoseTree<RoseX>> moreChildren) {
      final List<RoseTree<RoseX>> newChildren = new LinkedList<>(getChildren());
      newChildren.addAll(moreChildren);
      return new RoseTest(getValue(), newChildren);
    }

    @Override
    public ChecksumBuilder getChecksumBuilder() {
      return ChecksumBuilderFactory.newInstance().addChecksumEnabled(getValue())
          // It's actually pretty irritating that this works ---vvv
          .addListChecksumEnabled((getChildren().stream().collect(Collectors.toList())));
    }

  }

  private RoseTest rose0, rose1, rose2;

  private JSONObject testJson0, testJson1, testJson2;

  private RoseX testObject0, testObject1, testObject2;

  @BeforeEach
  public void setUp() throws Exception {
    testJson0 = new JSONObject().put("X", "1").put("Y", "2");
    testJson1 = new JSONObject().put("X", "2").put("Y", "4");
    testJson2 = new JSONObject().put("X", "3").put("Y", "6");
    testObject0 = new RoseX(testJson0);
    testObject1 = new RoseX(testJson1);
    testObject2 = new RoseX(testJson2);
    rose2 = new RoseTest(testObject2, emptyList());
    rose1 = new RoseTest(testObject1, emptyList());
    rose0 = new RoseTest(testObject0, asList(rose1, rose2));
  }

  @Test
  public void testAsChecksum() {
    final String rose1checksum = "6042297c1460176ec572094ba0abe6d7abf2a2f80ad70127117137a93ad688acbe1eb9a4aadbda7df16529ae272621a6b49d27ac23f97eecf4485ba5aec1cd90";
    assertEquals(rose1checksum, rose1.asChecksum().toString());
    final String rose0Checksum = "041aa5306e3f6971814fa5141199044487a03378886ed43d922e4192b6e6c9da519368917746a7f60f835dcd865e12aba60eb14f4dce6b6903584d13ab8df058";
    assertEquals(rose0Checksum, rose0.asChecksum().toString());
  }

  @Test
  public void testEqualsNotSame() {
    final RoseTest rose3 = new RoseTest(new RoseX(testJson0), emptyList());
    final RoseTest rose4 = new RoseTest(new RoseX(testJson0), emptyList());
    assertEquals(rose3, rose4);
    assertEquals(rose3.hashCode(), rose4.hashCode());
    assertNotEquals(rose3, null);
  }

  @Test
  public void testGetChildren() {
    assertEquals(0, rose2.getChildren().size());
    assertEquals(2, rose0.getChildren().size());
    final List<RoseTree<RoseX>> x = rose0.getChildren();
    assertTrue(x.contains(rose1));
    assertTrue(x.contains(rose2));
  }

  @Test
  public void testGetValue() {
    assertEquals(testObject0, rose0.getValue());
    assertEquals(testObject1, rose1.getValue());
  }

  @Test
  public void testHasChildren() {
    assertTrue(rose0.hasChildren());
    assertFalse(rose1.hasChildren());
  }

  @Test
  public void testJSON() {
    final RoseTree<RoseX> b = rose1.withChild(rose2);
    assertTrue(b.getChildren().contains(rose2));
    assertNotNull(b.asJSON());

  }

  @Test
  public void testNotAString() {
    final RoseTest rose3 = new RoseTest(new RoseX(testJson0), emptyList());
    assertNotEquals("Not a string", rose3, "ABC");
  }

  @Test
  public void testWithChild() {
    final RoseTree<RoseX> b = rose1.withChild(rose2);
    assertTrue(b.getChildren().contains(rose2));
    assertFalse(b.getChildren().contains(rose0));
  }

  @Test
  public void testWithChildren() {
    final RoseTest rose3 = new RoseTest(new RoseX(testJson0), emptyList());
    final RoseTree<RoseX> b = rose1.withChildren(asList(rose2, rose3));
    assertEquals(2, b.getChildren().size());
  }

}
