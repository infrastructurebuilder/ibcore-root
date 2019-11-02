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
package org.infrastructurebuilder.util.config;

import java.util.function.Supplier;

public class TSupplier<T> implements Supplier<T> {

  private T t;

  public TSupplier() {
    this.t = null;
  }

  public void setT(T t) {
    if (this.t == null) // You can only set it once
      this.t = t;
  }

  @Override
  public T get() {
    return this.t;
  }

}
