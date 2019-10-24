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
package org.infrastructurebuilder.data.transform;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.infrastructurebuilder.data.model.DataSet;
import org.infrastructurebuilder.util.config.ConfigMap;
import org.junit.Before;
import org.junit.Test;

public class TransformationTest {

  private Transformation t;

  @Before
  public void setUp() throws Exception {
    t = new Transformation();
  }

  @Test
  public void testSimpleGetSet() {
    assertEquals(Transformation.DEFAULT_TRANSFORM, t.getFinalizer());
    t.setFinalizer("A");
    assertEquals("A", t.getFinalizer());
    ConfigMap m = t.getFinalizerConfig();
    m.put("A", new HashMap<String, Object>());
    HashMap<String, Object> mm = (HashMap<String, Object>) m.get((Object) "A");
    m = new ConfigMap();

    Map<String, String> q = new HashMap<>();
    q.put("A", "B");
    t.setFinalizerConfig(q);
    ConfigMap v = t.getFinalizerConfig();
    assertEquals("B", v.get("A"));
    List<Transformer> l1 = new ArrayList<>();
    assertEquals(l1, t.getTransformers());
    l1.add(new Transformer());
    t.setTransformers(l1);
    ;
    assertEquals(l1.get(0).getId(), t.getTransformers().get(0).getId());
    assertEquals(Transformation.DEFAULT, t.getId());
    t.setId("A");
    assertEquals("A", t.getId());

  }

  @Test
  public void testAsDataSet() {
    t.injectRequird("A", "B", "1.0.0", "name", "desc");
    XmlPlexusConfiguration x = new XmlPlexusConfiguration("A");
    t.setMetadata(x);
    DataSet s = t.asDataSet();
    assertEquals("A", s.getGroupId());
    assertEquals("B", s.getArtifactId());
    assertEquals("1.0.0", s.getVersion());
    assertEquals("name", s.getDataSetName());
    assertEquals("desc", s.getDataSetDescription());
    Object q = s.getMetadata();
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<A/>", q.toString());
  }

}