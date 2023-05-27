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
package org.infrastructurebuilder.util.vertx.base;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tag {

  private final static Logger log = LoggerFactory.getLogger(Tag.class);
  private final String tag;
  private final String value;
  private final Integer intValue;
  private final Double doubleValue;

  public final static Function<String, Integer> strToInt = (str) -> {
    try {
      return Integer.valueOf(str);
    } catch (Throwable t) {
      return null;
    }
  };

  public final static Function<String, Double> strToDouble = (str) -> {
    try {
      return Double.valueOf(str);
    } catch (Throwable t) {
      return null;
    }
  };

  public Tag(String tag) {
    this(tag, empty());
  }

  public Tag(String tag, String value) {
    this(tag, ofNullable(value));
  }

  private Tag(String tag, Optional<String> value) {
    this.tag = requireNonNull(tag).trim();
    this.value = requireNonNull(value).map(String::trim).orElse(null);
    this.intValue = strToInt.apply(this.value);
    this.doubleValue = strToDouble.apply(this.value);
  }

  public Tag(Entry<String, Object> e) {
    this(e.getKey(), Optional.ofNullable(e.getValue()).map(Object::toString).orElseGet(() -> null));
  }

  public String getTag() {
    return tag;
  }

  public Optional<String> getValue() {
    return ofNullable(this.value);
  }

  public Optional<Integer> getIntValue() {
    return ofNullable(intValue);
  }

  public Optional<Double> getDoubleValue() {
    return ofNullable(doubleValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tag);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Tag other = (Tag) obj;
    return Objects.equals(tag, other.tag);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(this.tag);
    getDoubleValue().ifPresentOrElse(dv -> sb.append("=").append(dv),

        () -> getIntValue().ifPresentOrElse(iv -> sb.append("=").append(iv),

            () -> getValue().ifPresent(v -> sb.append("=").append(v))));
    return sb.toString();
  }

}
