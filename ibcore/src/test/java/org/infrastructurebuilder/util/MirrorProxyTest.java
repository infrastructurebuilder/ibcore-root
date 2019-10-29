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

import static org.infrastructurebuilder.util.Layout.DEFAULT;
import static org.infrastructurebuilder.util.Layout.LEGACY;
import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.infrastructurebuilder.IBException;
import org.junit.Before;
import org.junit.Test;

public class MirrorProxyTest {

  private static final String NAME = "name";
  private static final String B = "B";
  private static final String A = "A";
  private static final String ID = "id";
  private MirrorProxy m;
  String id = ID;
  Layout layout = DEFAULT;
  List<String> mirrorOf = Arrays.asList(A, B);
  List<Layout> mirrorOfLayouts = Arrays.asList(DEFAULT, LEGACY);
  Optional<String> name = Optional.of(NAME);
  String url = "http://www.example.com/somemirror";

  @Before
  public void setUp() throws Exception {
    m = new MirrorProxy(id, layout, mirrorOf, mirrorOfLayouts, name, new URL(url));
  }

  @Test
  public void test() {
    assertEquals(ID, m.getId());
    assertEquals(NAME, m.getName().get());
    assertEquals(mirrorOf, m.getMirrorOf().stream().collect(Collectors.toList()));
    assertEquals(mirrorOfLayouts, m.getMirrorOfLayouts().stream().collect(Collectors.toList()));
    assertEquals(url, m.getUrl().toExternalForm());
  }

  @Test(expected = IBException.class)
  public void failit() {
    m.isProxyOf("A");
  }

}
