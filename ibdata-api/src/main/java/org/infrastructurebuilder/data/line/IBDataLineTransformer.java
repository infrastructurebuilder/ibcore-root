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
package org.infrastructurebuilder.data.line;

import java.util.Optional;
import java.util.function.Function;

/**
 * For ease of use making a component that does this. Some concrete class would be injected into an IBDataTransformer
 * @author mykel.alvis
 *
 * @param <I> Input type (line of text, array of strings, whatever, Avro record)
 *      The inbound value with ALWAYS be non-null
 * @param <O> Output type (target type (frequently Avro record)
 *      The result of the apply CAN be null, which should terminate processing for that record/row/whatever
 * @param <T>
 */
public interface IBDataLineTransformer<I, O> extends Function<I, O> {
  public String getName();
  /**
   * Get the specific keyed configuration for this transformer
   * @return
   */
  public Optional<String> getConfiguration(String key);

  public Optional<String> getConfiguration(String key, String defaultValue);

}
