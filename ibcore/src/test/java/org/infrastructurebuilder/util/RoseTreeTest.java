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
package org.infrastructurebuilder.util;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.infrastructurebuilder.util.IBUtils.deepCopy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.util.artifacts.Checksum;
import org.infrastructurebuilder.util.artifacts.ChecksumBuilder;
import org.infrastructurebuilder.util.artifacts.JSONAndChecksumEnabled;
import org.infrastructurebuilder.util.artifacts.impl.AbstractRoseTree;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class RoseTreeTest {

  public static class RoseTest extends AbstractRoseTree<X> {

    public static final String TREE_CHILDREN = "children";

    public RoseTest(final X value, final List<RoseTree<X>> children) {
      super(value, Optional.ofNullable(children).map(c -> Collections.unmodifiableList(c))
          .orElseThrow(() -> new IllegalArgumentException("Children can not be null.\n\t" + "Children : " + children)));
    }

    @Override
    public String getKeyForTreeInJSON() {
      return TREE_CHILDREN;
    }

    @Override
    public RoseTree<X> withChildren(final List<RoseTree<X>> moreChildren) {
      final List<RoseTree<X>> newChildren = new LinkedList<>(getChildren());
      newChildren.addAll(moreChildren);
      return new RoseTest(getValue(), newChildren);
    }

  }

  public static class X implements JSONAndChecksumEnabled {
    private final JSONObject a;
    private final JSONObject json;

    public X(final JSONObject a) {
      this.a = a;
      json = deepCopy.apply(this.a);
    }

    @Override
    public JSONObject asJSON() {
      return json;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final X other = (X) obj;
      if (a == null) {
        if (other.a != null)
          return false;
      } else {
        try {
          JSONAssert.assertEquals(a, other.a, true);
        } catch (final AssertionError e) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (a == null ? 0 : asChecksum().hashCode());
      return result;
    }

  }

  private RoseTest rose0, rose1, rose2;

  private JSONObject testJson0, testJson1, testJson2;

  private X testObject0, testObject1, testObject2;

  @Before
  public void setUp() throws Exception {
    testJson0 = new JSONObject().put("X", "1").put("Y", "2");
    testJson1 = new JSONObject().put("X", "2").put("Y", "4");
    testJson2 = new JSONObject().put("X", "3").put("Y", "6");
    testObject0 = new X(testJson0);
    testObject1 = new X(testJson1);
    testObject2 = new X(testJson2);
    rose2 = new RoseTest(testObject2, emptyList());
    rose1 = new RoseTest(testObject1, emptyList());
    rose0 = new RoseTest(testObject0, asList(rose1, rose2));
  }

  @Test
  public void testAsChecksum() {
    final String rose1checksum = "57be5e8db73583d93774c737de925b39e67cfa1ef03c0cdea0c34ebf41669aef87fab36c33225157f7fcfd34f34a50ac91d54aa2781bf884f6aa7f4e74968ced";
    assertEquals("rose1 checksum is " + rose1checksum, rose1checksum, rose1.asChecksum().toString());
    final String rose0Checksum = "49f9207ff5c6c339a3850ecce2172e327d6f8a8dc63f62fd2be277e8145f17d309167dabc9cf3650e1367f995ce90fe685113250a1226d089b7e42818ef1073f";
    assertEquals("rose0 checksum is " + rose0Checksum, rose0Checksum, rose0.asChecksum().toString());
  }

  @Test
  public void testEqualsNotSame() {
    final RoseTest rose3 = new RoseTest(new X(testJson0), emptyList());
    final RoseTest rose4 = new RoseTest(new X(testJson0), emptyList());
    assertEquals("Equals not same ", rose3, rose4);
    assertEquals("Different hash codes?", rose3.hashCode(), rose4.hashCode());
    assertNotEquals("Not equal to null?", rose3, null);
  }

  @Test
  public void testGetChildren() {
    assertEquals("rose2 has no children", 0, rose2.getChildren().size());
    assertEquals("rose0 has 2 children", 2, rose0.getChildren().size());
    final List<RoseTree<X>> x = rose0.getChildren();
    assertTrue("Rose0 contains rose1", x.contains(rose1));
    assertTrue("Rose0 contains rose2", x.contains(rose2));
  }

  @Test
  public void testGetValue() {
    assertEquals("Testing testObject0", testObject0, rose0.getValue());
    assertEquals("Testing testObject1", testObject1, rose1.getValue());
  }

  @Test
  public void testHasChildren() {
    assertTrue("Rose0 has children", rose0.hasChildren());
    assertFalse("Rose1 has no children", rose1.hasChildren());
  }

  @Test
  public void testJSON() {
    final RoseTree<X> b = rose1.withChild(rose2);
    assertTrue("B has a child of rose2", b.getChildren().contains(rose2));
    assertNotNull("JSON is valid", b.asJSON());

  }

  @Test
  public void testNotAString() {
    final RoseTest rose3 = new RoseTest(new X(testJson0), emptyList());
    assertNotEquals("Not a string", rose3, "ABC");
  }

  @Test
  public void testWithChild() {
    final RoseTree<X> b = rose1.withChild(rose2);
    assertTrue("B has a child of rose2", b.getChildren().contains(rose2));
    assertFalse("B does not have a child of rose0", b.getChildren().contains(rose0));
  }

  @Test
  public void testWithChildren() {
    final RoseTest rose3 = new RoseTest(new X(testJson0), emptyList());
    final RoseTree<X> b = rose1.withChildren(asList(rose2, rose3));
    assertEquals("b has 2 children", 2, b.getChildren().size());
  }

}
