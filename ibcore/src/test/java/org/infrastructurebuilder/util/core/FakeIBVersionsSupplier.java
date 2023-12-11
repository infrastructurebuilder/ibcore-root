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
package org.infrastructurebuilder.util.core;

import java.util.function.Supplier;

import org.infrastructurebuilder.util.versions.IBVersionsSupplier;

public class FakeIBVersionsSupplier implements IBVersionsSupplier {

  @Override
  public Supplier<String> getGroupId() {
    // TODO Auto-generated method stub
    return () -> "G";
  }

  @Override
  public Supplier<String> getArtifactId() {
    // TODO Auto-generated method stub
    return () -> "A";
  }

  @Override
  public Supplier<String> getVersion() {
    // TODO Auto-generated method stub
    return () -> "1.0.0";
  }

  @Override
  public Supplier<String> getExtension() {
    // TODO Auto-generated method stub
    return () -> "jar";
  }

  @Override
  public Supplier<String> getAPIVersion() {
    // TODO Auto-generated method stub
    return () -> "1.0";
  }

}
