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

import org.infrastructurebuilder.pathref.JSONOutputEnabled;
import org.json.JSONObject;

public class FakeModeled implements Modeled, JSONOutputEnabled {

  private JSONObject json;

  public FakeModeled() {
    this.json = new JSONObject().put("type", "FakeModeled");
  }

  public FakeModeled(JSONObject j) {
    this.json = j;

  }

  @Override
  public JSONObject asJSON() {
    return this.json;
  }

  @Override
  public String getModelVersion() {
    return "1.0";
  }

}
