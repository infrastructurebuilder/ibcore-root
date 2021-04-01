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
package org.infrastructurebuilder.util.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.infrastructurebuilder.util.settings.ActivationFileProxy;
import org.infrastructurebuilder.util.settings.ActivationOSProxy;
import org.infrastructurebuilder.util.settings.ActivationPropertyProxy;
import org.infrastructurebuilder.util.settings.ActivationProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ActivationProxyTest {

  private static final String JDK = "jdk";
  private ActivationProxy ap;
  private boolean activeByDefault;
  private ActivationFileProxy afp;
  private ActivationOSProxy activationOsProperty;
  private ActivationPropertyProxy activa;
  private Optional<String> arch;
  private Optional<String> family;
  private Optional<String> name;
  private Optional<String> version;
  private String propertyName;
  private Optional<String> value;

  @BeforeEach
  public void setUp() throws Exception {
    activeByDefault = false;
    afp = new ActivationFileProxy(Optional.empty(), Optional.empty());
    activationOsProperty = new ActivationOSProxy(arch, family, name, version);
    activa = new ActivationPropertyProxy(propertyName, value);
    ap = new ActivationProxy(activeByDefault, Optional.of(afp), Optional.of(JDK), Optional.of(activationOsProperty),
        Optional.of(activa));
  }

  @Test
  public void testGetFile() {
    assertEquals(afp, ap.getFile().get());
  }

  @Test
  public void testGetJdk() {
    assertEquals(JDK, ap.getJdk().get());
  }

  @Test
  public void testGetOs() {
    assertEquals(activationOsProperty, ap.getOs().get());
  }

  @Test
  public void testGetProperty() {
    assertEquals(activa, ap.getProperty().get());
  }

  @Test
  public void testIsActiveByDefault() {
    assertFalse(ap.isActiveByDefault());
  }

}
