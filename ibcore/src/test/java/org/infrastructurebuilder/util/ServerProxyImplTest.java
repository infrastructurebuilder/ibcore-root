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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class ServerProxyImplTest {

  private static final String TESTCONFIGSTRING = "<configuration><abc>123</abc></configuration>";
  private ServerProxy p;
  private Path path;

  @Before
  public void setUp() throws Exception {
    final Path target = Paths.get(Optional.ofNullable(System.getProperty("target")).orElse("./target"));
    final Path testClasses = target.resolve("test-classes");
    path = testClasses.resolve("X.txt").toAbsolutePath();
    p = new ServerProxy("id", Optional.of("username"), Optional.of("password"), Optional.of("passphrase"),
        Optional.of(path), Optional.of("0666"), Optional.of("0777"), Optional.of(TESTCONFIGSTRING));
  }

  @Test
  public void testGetId() {
    assertEquals("id", p.getId());
  }

  @Test
  public void testGetKeyPath() {
    assertEquals(path, p.getKeyPath().get());
  }

  @Test
  public void testGetPassphrase() {
    assertEquals("passphrase", p.getPassphrase().get());
  }

  @Test
  public void testGetPrincipal() {
    assertEquals("username", p.getPrincipal().get());
  }

  @Test
  public void testGetSecret() {
    assertEquals("password", p.getSecret().get());
  }

  @Test
  public void testReadKey() {
    final String g = p.readKey().get();
    assertTrue(g.contains("ABC_123"));
  }

  @Test
  public void testServerProxyImpl() {
    assertNotNull(p);
  }

  @Test
  public void testPermsData() {
    assertEquals("0777", p.getDirectoryPermissions().get());
    assertEquals("0666", p.getFilePermissions().get());
  }

  @Test
  public void testConfiguration() {
    assertEquals(TESTCONFIGSTRING, p.getConfiguration().get());
  }

}
