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
package org.infrastructurebuilder.util.readdetect;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.core.RelativeRoot;

@Named
public class EnvBackedIBResourceRelativeRootSupplier extends AbstractIBResourceRelativeRootSupplier {

  public static final String RELATIVE_ROOT = "RELATIVE_ROOT";
  private final RelativeRoot rr;

  @Inject
  public EnvBackedIBResourceRelativeRootSupplier() {
    String s = Optional.ofNullable(System.getenv(RELATIVE_ROOT))
        .orElseThrow(() -> new IBResourceException("No " + RELATIVE_ROOT + " supplied in environment"));
    this.rr = RelativeRoot.from(s);
  }

  @Override
  public RelativeRoot get() {
    return rr;
  }

}
