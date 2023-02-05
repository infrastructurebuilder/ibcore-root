/*
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
package org.infrastructurebuilder.util.core;

import java.time.Instant;


public interface LastUpdated  {
  public final static String LAST_UPDATED = "lastUpdated";

  public static java.util.Comparator<LastUpdated> lastupdatedComparator = new java.util.Comparator<LastUpdated>() {
    @Override
    public int compare(LastUpdated o1, LastUpdated o2) {
      if (o1 == null)
        return -1;
      if (o2 == null)
        return 1;
      // Sort highest weights last
      return o1.getLastUpdated().compareTo(o2.getLastUpdated());
    }
  };

  Instant getLastUpdated();

}
