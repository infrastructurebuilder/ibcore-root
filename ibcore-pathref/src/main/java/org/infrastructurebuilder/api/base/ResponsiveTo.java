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
package org.infrastructurebuilder.api.base;

/**
 * ResponsiveTo has been changed to provide a weight for a given response.
 *
 * For a given input, the spec is as-follows:
 * <ol>
 * <li>if respondsTo returns less-than-zero, then the given responder has indicated that it does not respond to the
 * given input</li>
 * <li>Otherwise, respondsTo returns an integer "weight"</li>
 * <li>Higher weights are higher priority, with zero being the lowest default priority</li>
 * <li>Ties must be reconciled individually, but are generally discouraged</li>
 * </ol>
 *
 * @param <T>
 */
public interface ResponsiveTo<T> {
  default int respondsTo(T input) {
    return -1;
  }

}
