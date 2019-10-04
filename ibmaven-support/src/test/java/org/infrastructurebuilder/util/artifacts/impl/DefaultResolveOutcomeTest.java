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
package org.infrastructurebuilder.util.artifacts.impl;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.infrastructurebuilder.util.artifacts.ResolveOutcome;
import org.junit.Before;
import org.junit.Test;

public class DefaultResolveOutcomeTest {

  private static final String URL = "http://www.example.com";
  private URL url;
  private Artifact originatingArtifact;
  private List<URL> resolved;
  private ResolveOutcome r;

  @Before
  public void setUp() throws Exception {
    url = new URL(URL + "/somethingelse");
    URL u1 = new URL(URL + "/1"), u2 = new URL(URL + "/2");
    resolved = Arrays.asList(u1, u2);
    originatingArtifact = new DefaultArtifact("x", "y", "1.0.0", "test", "type", "classifier", null);
    r = new DefaultResolveOutcome(resolved, originatingArtifact, url);
  }

  @Test
  public void testGetCount() {
    assertEquals(2,r.getCount());
  }

  @Test
  public void testGetOriginatingArtifact() {
    assertEquals(originatingArtifact, r.getOriginatingArtifact());
  }

  @Test
  public void testGetResolvedURLs() {
    assertEquals(resolved, r.getResolvedURLs());
  }

  @Test
  public void testGetURL() {
    assertEquals(url, r.getURL());
  }

}
