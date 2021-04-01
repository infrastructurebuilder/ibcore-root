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
package org.infrastructurebuilder.util.core;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class ExplodingOutputStream extends FilterOutputStream {
  private int explodeAfter;
  private final int orig;

  public ExplodingOutputStream(final OutputStream out, final int explodeAfter) {
    super(out);
    if (explodeAfter < 1)
      throw new IllegalArgumentException("Must allow at least 1 byte written");

    this.explodeAfter = explodeAfter;
    orig = explodeAfter;
  }

  @Override
  public void write(final byte[] b, final int off, int len) throws IOException {
    len = Math.min(len, explodeAfter);
    explodeAfter -= len;
    super.write(b, off, len);
    if (explodeAfter <= 0)
      throw new IOException("Exploded after " + orig);
  }

  @Override
  public void write(final int b) throws IOException {
    explodeAfter--;
    super.write(b);
    if (explodeAfter <= 0)
      throw new IOException("Exploded after " + orig);
  }

}
