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

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.infrastructurebuilder.exceptions.IBException.cet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.infrastructurebuilder.util.readdetect.base.IBResourceIS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IBResourceAvro extends IBResourceIS {
  final static Logger log = LoggerFactory.getLogger(IBResourceAvro.class);
  public final static Function<InputStream, Optional<Stream<GenericRecord>>> genericStreamFromInputStream = (ins) -> {
    try (DataFileStream<GenericRecord> s = new DataFileStream<GenericRecord>(ins,
        new GenericDatumReader<GenericRecord>())) {
      return of(StreamSupport.stream(s.spliterator(), false));
    } catch (IOException e) {
      log.warn("Exception getting generic record stream", e);
      return empty();
    } finally {
      cet.translate(() -> ins.close());
    }
  };

  Optional<SeekableInput> getSeekableFile();
}
