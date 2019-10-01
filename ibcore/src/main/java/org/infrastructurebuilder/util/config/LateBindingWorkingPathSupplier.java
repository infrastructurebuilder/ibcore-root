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

import java.nio.file.Path;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

@Named(LateBindingWorkingPathSupplier.LATE_BINDING_WORKING_PATH_SUPPLIER)
public class LateBindingWorkingPathSupplier extends WorkingPathSupplier {

  public static final String LATE_BINDING_WORKING_PATH_SUPPLIER = "late-binding-working-path-supplier";
  private final PathSupplier ps;

  @Inject
  public LateBindingWorkingPathSupplier(
      @Named(SingletonLateBindingPathSupplier.SINGLETON_LATE_BINDING_PATH_SUPPLIER) PathSupplier ps,
      @org.eclipse.sisu.Nullable final IdentifierSupplier id) {
    super(ps, id, true);    //Always cleanup
    this.ps = Objects.requireNonNull(ps);
  }

  @Override
  public Path getRoot() {
    return this.ps.get();
  }
}
