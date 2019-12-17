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

public enum IBDataStructuredDataMetadataType {
  // A limited subset of serialization framework types.

  // NOTE: At the present, we are not planning on supporting other types. If
  // necessary, you can write it as a string, y'all.  It's possible we will
  // implement Map and Array at a future date, but unlikely

  /* Not supported -> RECORD, ENUM, ARRAY, MAP, UNION, FIXED, NULL */
  STRING,       // UTF-* string
  BYTES,        // *LOB
  INT,          // small signed / sint32
  LONG,         // large signed / sint64
  FLOAT,        // float
  DOUBLE,       // double
  BOOLEAN,      // boolean
  DATE,         // Stored as a long
  TIMESTAMP     // Stored as a long
  ;
}
