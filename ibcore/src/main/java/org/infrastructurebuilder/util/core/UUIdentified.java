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

import java.util.Comparator;
import java.util.UUID;

public interface UUIdentified  {
  public final static String ID = "id";
  UUID getId();

  public static Comparator<UUIdentified> uuidcomparator = new Comparator<>() {
    @Override
    public int compare(UUIdentified o1, UUIdentified o2) {
      if (o1 == null) return -1;
      if (o2 == null) return 1;
      return o1.getId().compareTo(o2.getId());
    }
  };

}
