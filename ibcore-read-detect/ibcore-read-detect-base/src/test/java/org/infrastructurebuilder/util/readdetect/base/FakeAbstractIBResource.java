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
package org.infrastructurebuilder.util.readdetect.base;

import java.util.Optional;
import java.util.UUID;

import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.pathref.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.readdetect.api.IBResourceException;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractPathRefPathIBResourceBuilderFactory.AbstractIBResource;

public class FakeAbstractIBResource extends AbstractIBResource {
  private final static TestingPathSupplier tps = new TestingPathSupplier();

  private FakeAbstractIBResource(PathRefPath root, IBResourceModel model) {
    super(model,
        PathRefPath.fromPath(tps.getTestClasses().resolve("rick.jpg"), Optional.of(UUID.randomUUID().toString()))
            .orElseThrow(() -> new IBResourceException("Cannot create path")));
  }

  @Override
  public PathRefPath get() {
    return (PathRefPath) this.path;
  }

  @Override
  public boolean validate(boolean hard) {
    // TODO Auto-generated method stub
    return false;
  }

}
