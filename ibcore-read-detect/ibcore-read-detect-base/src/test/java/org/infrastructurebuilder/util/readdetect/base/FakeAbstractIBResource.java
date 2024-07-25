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

import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.PathRef;
import org.infrastructurebuilder.pathref.TestingPathSupplier;
import org.infrastructurebuilder.util.core.DefaultPathAndChecksum;
import org.infrastructurebuilder.util.core.OptStream;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractPathIBResourceBuilderFactory.AbstractIBResource;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;

public class FakeAbstractIBResource extends AbstractIBResource {
  private final static TestingPathSupplier tps = new TestingPathSupplier();

  public FakeAbstractIBResource(PathRef root, IBResourceModel model) {
    super(model, new DefaultPathAndChecksum(Optional.ofNullable(root), tps.getTestClasses().resolve("rick.jpg")));
  }

  @Override
  public OptStream get() {
    return getPathAndChecksum().asOptStream();
  }

  @Override
  public boolean validate(boolean hard) {
    // TODO Auto-generated method stub
    return false;
  }

}
