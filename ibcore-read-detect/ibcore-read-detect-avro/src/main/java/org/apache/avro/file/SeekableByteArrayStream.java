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
package org.apache.avro.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SeekableByteArrayStream extends ByteArrayInputStream implements SeekableInput {

  public SeekableByteArrayStream(byte[] a) {
    super(a);
  }

  @Override
  public void seek(long p) throws IOException {
    reset();
    skip(p);
  }

  @Override
  public long tell() throws IOException {
    return Integer.toUnsignedLong(pos);
  }

  @Override
  public long length() throws IOException {
    return Integer.toUnsignedLong(buf.length);
  }

}
