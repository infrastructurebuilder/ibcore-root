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
package org.infrastructurebuilder.util.readdetect.base;

import org.infrastructurebuilder.api.base.NameDescribed;
import org.infrastructurebuilder.api.base.ResponsiveTo;
import org.infrastructurebuilder.pathref.ChecksumBuilder;
import org.infrastructurebuilder.pathref.ChecksumBuilderFactory;
import org.infrastructurebuilder.pathref.JSONAndChecksumEnabled;
import org.infrastructurebuilder.util.core.IdentifiedAndWeighted;
import org.json.JSONObject;

public interface IBResourceSource
    extends IdentifiedAndWeighted, NameDescribed, JSONAndChecksumEnabled, ResponsiveTo<JSONObject> {
  static ChecksumBuilder defaultChecksumBuilder(IBResourceSource t) {
    return ChecksumBuilderFactory.newInstance()

        .addString(t.getName())

        .addString(t.getDescription())

        .addInteger(t.getWeight());
  }
}
