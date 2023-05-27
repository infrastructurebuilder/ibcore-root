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

public interface UUIdentifiedAndTimestamped extends UUIdentified, Timestamped, Comparable<UUIdentifiedAndTimestamped> {
  public static Comparator<UUIdentifiedAndTimestamped> comparator = new Comparator<UUIdentifiedAndTimestamped>() {
    @Override
    public int compare(UUIdentifiedAndTimestamped o1, UUIdentifiedAndTimestamped o2) {
      int retVal = Timestamped.timestamped.compare(o2, o1);
      if (retVal == 0)
        retVal = UUIdentified.uuidcomparator.compare(o1, o2);
      return retVal;
    }
  };

  @Override
  default int compareTo(UUIdentifiedAndTimestamped o) {
    return comparator.compare(this, o);
  }

}
