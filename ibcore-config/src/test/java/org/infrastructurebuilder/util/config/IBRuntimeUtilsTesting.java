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
package org.infrastructurebuilder.util.config;

import org.infrastructurebuilder.util.CredentialsFactory;
import org.infrastructurebuilder.util.GAV;
import org.infrastructurebuilder.util.IBArtifactVersionMapper;
import org.infrastructurebuilder.util.PathSupplier;
import org.infrastructurebuilder.util.TestingPathSupplier;
import org.infrastructurebuilder.util.config.factory.FakeCredentialsFactory;
import org.infrastructurebuilder.util.impl.DefaultGAV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IBRuntimeUtilsTesting extends AbstractIBRuntimeUtils {

  private final static Logger log = LoggerFactory.getLogger(IBRuntimeUtilsTesting.class);

  public IBRuntimeUtilsTesting(PathSupplier wps, Logger ls, GAV gs, CredentialsFactory cf,
      IBArtifactVersionMapper avm) {
    super(wps, () -> ls, new FakeGAVSupplier(gs), cf, avm, new FakeTypeToExtensionMapper());
  }

  public IBRuntimeUtilsTesting(PathSupplier wps, Logger log) {
    this(wps, log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        new IBArtifactVersionMapper() {
        });
  }

  public IBRuntimeUtilsTesting(Logger log) {
    this(new TestingPathSupplier(), log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        new IBArtifactVersionMapper() {
        });
  }

  public IBRuntimeUtilsTesting() {
    this(new TestingPathSupplier(), log, new DefaultGAV(new FakeIBVersionsSupplier()), new FakeCredentialsFactory(),
        new IBArtifactVersionMapper() {
        });
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
