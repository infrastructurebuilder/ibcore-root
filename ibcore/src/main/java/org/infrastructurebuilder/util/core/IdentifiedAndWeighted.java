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

import org.infrastructurebuilder.api.Weighted;

/**
 * Merge identified and weighted for a type of convenience since they often go together.
 *
 * @author mykel.alvis
 *
 */
public interface IdentifiedAndWeighted extends Identified, Weighted {
  // TODO Make everythign a null safe comparator?
  public static java.util.Comparator<IdentifiedAndWeighted> comparator() {
    return new java.util.Comparator<IdentifiedAndWeighted>() {
      @Override
      public int compare(IdentifiedAndWeighted o1, IdentifiedAndWeighted o2) {
        int retVal = weighted.compare(o2, o1);
        if (retVal == 0)
          return Identified.comparator().compare(o1, o2);
        return retVal;
      }

    };
  }

}
