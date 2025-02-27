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
package org.infrastructurebuilder.util.readdetect.avro;

import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

import org.apache.avro.file.SeekableInput;


public class SeekableInputFromPath implements SeekableInput {
  private final Supplier<InputStream> res;
  private transient InputStream ins = null;
  private long position = 0L;
  private long current = 0L;
  private final int bufferSize;
  private final long length;

  /**
   * Opens a Path, implementing SeekableInput for Avro
   *
   * @param res              the path open or create
   * @param options          options specifying how the file is opened
   * @param tempFileRequired true if a temp file wanted, false in case of a in-memory solution option.
   * @throws IOException if an I/O error occurs
   */
  public SeekableInputFromPath(final Path resource, int bufferSize) {
    this.res = () -> cet.returns( () -> Files.newInputStream(resource));
    this.length = cet.returns(() -> Files.size(resource));
    this.bufferSize = bufferSize;
    cet.translate(() -> reset());
  }

  public SeekableInputFromPath(final Path resource) {
    this(resource, 8192);
  }

  private void reset() throws IOException {
    if (this.ins != null)
      this.ins.close(); // Dunno if it closes underlying inputstream
    this.ins = res.get();
    this.current = 0L;
  }

  // Slow AF
  private void advanceTo(long p) throws IOException {
    if (p == 0 && this.current != 0) {
      reset();
    } else if (p >= this.current) {
      skipRead(p - current);
    } else {
      reset();
      skipRead(p);
    }
  }

  private void skipRead(long p) throws IOException {
    long count = p;
    while (count > 0) {
      byte[] buf = new byte[this.bufferSize];
      for (int i = 0; i < (p / this.bufferSize); ++i) {
        int read = this.ins.read(buf);
        if (read == -1)
          throw new IOException("Error.  Ran out of bytes.");
        this.current += read;
        count -= read;
      }
      byte[] one = new byte[(int) count];
      int read = this.ins.read(one);
      if (read == -1)
        throw new IOException("Error. Ran out of bytes.");
      this.current += read;
      count -= read;
    }
  }

  @Override
  public void close() throws IOException {
    try {
      if (this.ins != null)
        this.ins.close();
    } finally {
      this.ins = null;
    }
  }

  @Override
  public long length() throws IOException {
    return this.length;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return this.ins.read(b, off, len);
  }

  @Override
  public void seek(long newPosition) throws IOException {
    if (newPosition < 0 || newPosition > length())
      throw new IllegalArgumentException("Illegal new position %d".formatted(newPosition));
    this.position = newPosition;
    advanceTo(this.position);
  }

  @Override
  public long tell() throws IOException {
    return this.position;
  }

}
