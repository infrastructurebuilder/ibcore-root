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

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class RandomStream extends InputStream {
  private long index;
  private final Random random;
  private final long size;

  public RandomStream(final long size) {
    this(new Random(), size);
  }

  public RandomStream(final long seed, final long size) {
    this(new Random(seed), size);
  }

  private RandomStream(final Random rand, final long size) {
    super();
    random = rand;

    if (size < 1)
      throw new IllegalArgumentException("Size must be > 1");
    this.size = size;
  }

  @Override
  public int read() throws IOException {
    if (index >= size)
      return -1;
    index++;
    return random.nextInt(255);
  }
}
