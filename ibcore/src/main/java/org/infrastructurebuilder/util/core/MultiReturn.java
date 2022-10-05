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
package org.infrastructurebuilder.util.core;

public class MultiReturn<T, R, E extends Throwable> extends MultiType<T, E> {

  private R r;

  public MultiReturn(final E thrown) {
    super(thrown);
  }

  public MultiReturn(final R retVal, final E thrown) {
    this(null, retVal, thrown);
  }

  public MultiReturn(final T typed, final R retVal) {
    this(typed, retVal, null);
  }

  public MultiReturn(final T typed, final R retVal, final E thrown) {
    super(typed, thrown);
    this.r = retVal;
  }

  /**
   * @return Potentially null return value
   */
  public R getReturnValue() {
    return r;
  }

}
