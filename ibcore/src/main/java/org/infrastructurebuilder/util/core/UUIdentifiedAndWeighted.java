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

import java.util.Comparator;

import org.infrastructurebuilder.api.Weighted;

public interface UUIdentifiedAndWeighted extends UUIdentified, Weighted, Comparable<UUIdentifiedAndWeighted> {
  public static Comparator<UUIdentifiedAndWeighted> comparator = new Comparator<UUIdentifiedAndWeighted>() {
    @Override
    public int compare(UUIdentifiedAndWeighted o1, UUIdentifiedAndWeighted o2) {
      int retVal = Weighted.weighted.compare(o2, o1);
      if (retVal == 0)
        retVal = UUIdentified.uuidcomparator.compare(o1, o2);
      return retVal;
    }
  };

  @Override
  default int compareTo(UUIdentifiedAndWeighted o) {
    return comparator.compare(this, o);
  }

}
