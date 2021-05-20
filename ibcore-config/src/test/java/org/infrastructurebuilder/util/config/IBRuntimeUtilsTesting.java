/*
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
package org.infrastructurebuilder.util.config;

import java.lang.System.Logger;
import java.util.Collections;
import java.util.List;

import org.infrastructurebuilder.util.config.factory.FakeCredentialsFactory;
import org.infrastructurebuilder.util.core.DefaultGAV;
import org.infrastructurebuilder.util.core.GAV;
import org.infrastructurebuilder.util.core.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.infrastructurebuilder.util.core.TypeToExtensionMapper;
import org.infrastructurebuilder.util.credentials.basic.CredentialsFactory;
import org.infrastructurebuilder.util.versions.IBVersionsSupplier;

public class IBRuntimeUtilsTesting extends AbstractIBRuntimeUtils {
  public final static IBArtifactVersionMapper ibavm = new IBArtifactVersionMapper() {
    @Override
    public List<IBVersionsSupplier> getMatchingArtifacts(String groupId, String artifactId) {
      return Collections.emptyList();
    }
  };

  public final static IBRuntimeUtilsTesting from(String s) {
    return from(s, new FakeTypeToExtensionMapper());
  }

  public final static IBRuntimeUtilsTesting from(String s, TypeToExtensionMapper t2e) {
    return new IBRuntimeUtilsTesting(System.getLogger(s), t2e);
  }

  private final static Logger log = System.getLogger(IBRuntimeUtilsTesting.class.getName());

  public IBRuntimeUtilsTesting(PathSupplier wps, Logger ls, GAV gs, CredentialsFactory cf,
      IBArtifactVersionMapper avm) {
    super(wps, () -> ls, new FakeGAVSupplier(gs), cf, avm, new FakeTypeToExtensionMapper());
  }

  public IBRuntimeUtilsTesting(PathSupplier wps, Logger log) {
    this(wps, log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(), ibavm);
  }

  public IBRuntimeUtilsTesting(Logger log) {
    this(new TestingPathSupplier(), log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        ibavm);
  }

  public IBRuntimeUtilsTesting() {
    this(new TestingPathSupplier(), log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        ibavm);
  }

  public IBRuntimeUtilsTesting(Logger log, TypeToExtensionMapper t2e) {
    super(new TestingPathSupplier(), () -> log, new FakeGAVSupplier(new DefaultGAV(new FakeIBVersionsSupplier())),
        new FakeCredentialsFactory(), ibavm, t2e);
  }

  public IBRuntimeUtilsTesting(IBRuntimeUtilsTesting ibr) {
    super(ibr);
  }

  public Logger getLog() {
    return log;
  }

  public TestingPathSupplier getTestingPathSupplier() {
    return (TestingPathSupplier) ((super.wps instanceof TestingPathSupplier) ? super.wps : null);
  }

}
