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
package org.infrastructurebuilder.pathref;

import static java.util.Objects.requireNonNull;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import org.infrastructurebuilder.constants.IBConstants;

/**
 * A slightly hacked reader to allow digestion without trying to read into memory
 * Designed to work mostly with UTF-8.  At present, untested with other encodings.
 *
 */
public class DigestReader extends FilterReader {

  private final Charset encoding;
  private MessageDigest digest;
  private boolean on = true;

  public DigestReader(Reader in, MessageDigest digest, Charset encoding) {
    super(Objects.requireNonNull(in));
    setMessageDigest(digest);
    this.encoding = Objects.requireNonNull(encoding);

  }

  public DigestReader(Reader in, MessageDigest digest) {
    this(in, digest, IBConstants.UTF8);
  }

  public DigestReader(Reader in, String digestType) throws NoSuchAlgorithmException {
    this(in, MessageDigest.getInstance(requireNonNull(digestType)));
  }

  public DigestReader(Reader in, String digestType, Charset encoding) throws NoSuchAlgorithmException {
    this(in, MessageDigest.getInstance(requireNonNull(digestType)), encoding);
  }

  public void setMessageDigest(MessageDigest digest) {
    this.digest = digest;
  }

  public MessageDigest getMessageDigest() {
    return this.digest;
  }

  /**
   * Turns the digest function on or off. The default is on. When it is on, a call to one of the {@code read} methods
   * results in an update on the message digest. But when it is off, the message digest is not updated.
   *
   * @param on true to turn the digest function on, false to turn it off.
   */
  public void on(boolean on) {
    this.on = on;
  }

  /**
   * Reads a byte, and updates the message digest (if the digest function is on). That is, this method reads a byte from
   * the input stream, blocking until the byte is actually read. If the digest function is on (see {@link #on(boolean)
   * on}), this method will then call {@code update} on the message digest associated with this stream, passing it the
   * byte read.
   *
   * @return the byte read.
   *
   * @throws IOException if an I/O error occurs.
   *
   * @see DigestInputStream#read()
   */
  public int read() throws IOException {
    char[] cb = new char[1];
    if (super.read(cb, 0, 1) == -1)
      return -1;
    else {
      updateDigest(cb,0,1);
      return cb[0];
    }
  }

  /**
     * Reads characters into a portion of an array.  This method will block
     * until some input is available, an I/O error occurs, or the end of the
     * stream is reached.
     *
     * <p> If {@code len} is zero, then no characters are read and {@code 0} is
     * returned; otherwise, there is an attempt to read at least one character.
     * If no character is available because the stream is at its end, the value
     * {@code -1} is returned; otherwise, at least one character is read and
     * stored into {@code cbuf}.
   * If the digest function is on (see
   * {@link #on(boolean) on}), this method will then call {@code update}
   * on the message digest associated with this stream, passing it
   * the data.
   *
   * @param b the array into which the data is read.
   *
   * @param off the starting offset into {@code b} of where the
   * data should be placed.
   *
   * @param len the maximum number of bytes to be read from the input
   * stream into b, starting at offset {@code off}.
   *
   * @return  the actual number of characters read. This is less than
   * {@code len} if the end of the stream is reached prior to
   * reading {@code len} chars. -1 is returned if no bytes were
   * read because the end of the stream had already been reached when
   * the call was made.
   *
   * @throws    IOException if an I/O error occurs.
   *
   * @see DigestInputStream#read(byte[], int, int)
   * @see FilterReader#read(char[], int, int)
   */
  public int read(char[] b, int off, int len) throws IOException {
      int result = in.read(b, off, len);
      if (on && result != -1) {
          updateDigest(b, off, result);
      }
      return result;
  }

  private void updateDigest(char[] ci, int off, int len) {
    if (!on)
      return;
    char[] ca = new char[len];
    for (int i = 0; i < len; ++i) {
      ca[i] = ci[i+off];
    }
    if (this.digest != null) {
      this.digest.update(String.valueOf(ca).getBytes(encoding), off, len);
    }
    //TODO Maybe blank the buffers afterwards
  }


}

