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
package org.infrastructurebuilder.util.core;

import static java.util.Optional.ofNullable;

import java.io.InputStream;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptStream implements AutoCloseable {
  private final InputStream stream;
  private Checksum checksum;

  public OptStream() {
    this(null, null);
  }
  
  public OptStream(@Nullable InputStream ins) {
    this(ins, null);
  }

  public OptStream(@Nullable InputStream ins, @Nullable Checksum csum) {
    this.checksum = csum;
    this.stream = ins;
  }

  public final Optional<InputStream> getStream() {
    return ofNullable(this.stream);
  }

  public Optional<Checksum> getChecksum() {
    return ofNullable(ofNullable(this.checksum).orElseGet(() -> {
      return getStream().map(Checksum::new).orElse(null);
    }));
  }

  @Override
  public void close() throws Exception {
    ofNullable(this.stream).ifPresent(s -> {
      try {
        s.close();
      } catch (Throwable t) {
      }
    });

  }
}
