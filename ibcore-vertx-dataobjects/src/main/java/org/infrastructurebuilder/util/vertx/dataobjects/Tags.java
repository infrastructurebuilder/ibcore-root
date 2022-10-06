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
package org.infrastructurebuilder.util.vertx.dataobjects;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class Tags {

  private final static Logger log = LoggerFactory.getLogger(Tags.class);

  private final Set<Tag> tags;

  public Tags() {
    tags = new HashSet<>();
  }

  public Tags(JsonObject j) {
    this();
    j.forEach(e -> tags.add(new Tag(e)));
  }

  public Tags(Optional<Tags> tags2) {
    this(requireNonNull(tags2).isPresent() ? tags2.get().toJson() : new JsonObject());
  }

  public boolean hasTag(String tag) {
    return fetchTag(tag).isPresent();
  }

  public boolean hasTag(String tag, Integer value) {
    return fetchTag(tag).filter(t -> t.getIntValue().isPresent())
        .filter(t -> (t.getIntValue().get()).equals(requireNonNull(value))).isPresent();
  }

  public Optional<Tag> remove(String tag) {
    return fetchTag(tag).map(t -> tags.remove(t) ? t : null); // FIXME ??
  }

  public Optional<Tag> addTag(Tag tag) {
    var orig = fetchTag(requireNonNull(tag).getTag());
    orig.ifPresent(t -> tags.remove(t));
    tags.add(tag);
    return orig;
  }

  public Set<Tag> getTags() {
    return unmodifiableSet(tags);
  }

  public boolean hasTag(String tag, Double value) {
    return fetchTag(tag).filter(t -> t.getDoubleValue().isPresent())
        .filter(t -> (t.getDoubleValue().get()).equals(requireNonNull(value))).isPresent();
  }

  public Optional<Tag> fetchTag(String tag) {
    return tags.stream().filter(i -> i.getTag().equals(tag)).findAny();
  }

  public JsonObject toJson() {
    var j = new JsonObject();
    tags.forEach(i -> j.put(i.getTag(), i.getValue().orElse(null)));
    return j;
  }
}
