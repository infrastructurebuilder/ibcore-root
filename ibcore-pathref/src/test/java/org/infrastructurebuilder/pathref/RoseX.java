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
package org.infrastructurebuilder.pathref;

import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;

public class RoseX  implements JSONAndChecksumEnabled {
  private final JSONObject a;
  private final JSONObject json;

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilderFactory.newInstance()

        .addJSONObject(a)

        .addJSONObject(json);
  }

  public RoseX(final JSONObject a) {
    this.a = a;
    json = IBChecksumUtils.deepCopy.apply(this.a);
  }

  @Override
  public JSONObject asJSON() {
    return json;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final RoseX other = (RoseX) obj;
    if (a == null) {
      if (other.a != null)
        return false;
    } else {
      try {
        JSONAssert.assertEquals(a, other.a, true);
      } catch (final AssertionError e) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (a == null ? 0 : asChecksum().hashCode());
    return result;
  }

}
