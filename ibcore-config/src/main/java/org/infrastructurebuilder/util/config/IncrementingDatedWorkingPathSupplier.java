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
package org.infrastructurebuilder.util.config;

import static org.infrastructurebuilder.util.config.IncrementingDatedStringSupplier.INCREMENTING_DATED_STRING_SUPPLIER;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.infrastructurebuilder.util.core.IdentifierSupplier;
import org.infrastructurebuilder.util.core.WorkingPathSupplier;

@Named(IncrementingDatedWorkingPathSupplier.INCREMENTING_DATED_WPS)
@Singleton
public class IncrementingDatedWorkingPathSupplier extends WorkingPathSupplier {
  public static final String INCREMENTING_DATED_WPS = "incrementing-dated-wps";

  @Inject
  public IncrementingDatedWorkingPathSupplier(
      @Named(INCREMENTING_DATED_STRING_SUPPLIER) IdentifierSupplier idSupplier)
  {
    super(new HashMap<>(), idSupplier, false);
  }
}
