/**
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
package org.infrastructurebuilder.data.util.files;

import java.util.Optional;

import org.infrastructurebuilder.util.artifacts.IdentifiedAndWeighted;

/**
 * Default-weighted data tuple with the MIME type as the identifier
 *
 * @author mykel.alvis
 *
 */
public class TypeMapTuple implements IdentifiedAndWeighted {

  private final String type;
  private final String extension;
  private final Optional<String> structuredType;

  public TypeMapTuple(String type, String extension) {
    this(type, extension, null);
  }

  public TypeMapTuple(String type, String extension, String structuredType) {
    this.type = type;
    this.extension = extension;
    this.structuredType = Optional.ofNullable(structuredType);
  }

  public String getId() {
    return type;
  }

  public String getExtension() {
    return extension;
  }

  public Optional<String> getStructuredType() {
    return structuredType;
  }
}
