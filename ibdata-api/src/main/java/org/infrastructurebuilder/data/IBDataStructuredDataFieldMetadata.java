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
package org.infrastructurebuilder.data;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;

/**
 * This type's name is a mouthful.
 *
 * This type describes an optional piece of metadata about a field in a DataStream that contains
 * structured data.  These values are meant to be calculated at transformation time, so
 * the transformation should produce them.  They could change during the course of a transformation
 * so it's important that we be able to manipulate the list easily.
 * @author mykel.alvis
 *
 */
public interface IBDataStructuredDataFieldMetadata {

  public static final int NULL_INDICATOR = -1;

  /**
   * Required.  0-based index of this field
   * @return
   */
  int getIndex();

  /**
   * If we have a name, return it
   * @return
   */
  default Optional<String> getFieldName() {
    return empty();
  }

  /**
   * Returns the string representations of enumeration values (the "name()"s).
   * @return
   */
  List<String> getEnumerations();

  default boolean isEnumeration() {
    return ofNullable(getEnumerations()).orElse(emptyList()).size() > 0;
  }

  default Optional<Integer> getMaxLength() {
    return ofNullable(getMax() < 0 ? null : getMax());
  }

  default Optional<Integer> getMinLength() {
    return ofNullable(getMin() < 0 ? null : getMin());
  }

  /**
   * @return  actual byte length of the inputstream if known
   */
  default Optional<Long> getInputStreamLength() {
    return empty();
  }

  default Optional<Integer> getUniqueValuesCount() {
    return empty();
  }

  default boolean hasNull() {
    return getMinLength().map(l -> l == NULL_INDICATOR).orElse(false);
  }


  default Optional<IBDataStructuredDataMetadataType> getType() {
    return ofNullable(getIBDataStructuredDataMetadataType()).map(IBDataStructuredDataMetadataType::valueOf);
  }

  /**
   * Allows us to the the string type in the model from the enum
   * @param t
   */
  void setType(IBDataStructuredDataMetadataType t);

  /*
   * The following are implemented in the generated model directly
   */
  /**
   * Describes the "minimum length" of the data in the field.  Assume an actually null field has a -1 length.
              Length -2 means unset (for nullability purposes)
   * @return
   */
  int getMin();

  /**
   * Describes the "maximum length" of the data in the field.
              This is mostly useful for string (i.e. targeted to varchar) types.
              Length -2 means unset (for nullability purposes)
   * @return
   */
  int getMax();

  String getIBDataStructuredDataMetadataType();

}
