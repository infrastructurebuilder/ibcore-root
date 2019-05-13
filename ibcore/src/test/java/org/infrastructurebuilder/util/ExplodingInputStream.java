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
package org.infrastructurebuilder.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ExplodingInputStream extends FilterInputStream {

  private int explodeAfter;
  private final int orig;

  public ExplodingInputStream(final InputStream ins, final int explodeAfter) {
    super(ins);
    if (explodeAfter < 1)
      throw new IllegalArgumentException("Must allow at least 1 byte read");
    this.explodeAfter = explodeAfter;
    orig = explodeAfter;
  }

  @Override
  public int read() throws IOException {
    final int x = super.read();
    if (x != -1) {
      explodeAfter--;
    }
    if (explodeAfter <= 0)
      throw new IOException("Exploded after " + orig);
    return x;
  }

  @Override
  public int read(final byte[] b, final int off, int len) throws IOException {
    len = Math.min(len, explodeAfter);
    explodeAfter -= len;
    final int x = super.read(b, off, len);
    if (explodeAfter <= 0)
      throw new IOException("Exploded after " + orig);
    return x;
  }
}
