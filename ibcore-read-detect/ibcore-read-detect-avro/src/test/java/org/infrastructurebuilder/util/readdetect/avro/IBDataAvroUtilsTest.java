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

import static org.infrastructurebuilder.util.readdetect.avro.IBDataAvroUtils.avroSchemaFromString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecord;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.config.impl.DefaultConfigMapBuilder;
import org.infrastructurebuilder.util.core.AbsolutePathRelativeRoot;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.core.TestingPathSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IBDataAvroUtilsTest {
  private final static TestingPathSupplier wps = new TestingPathSupplier();
  private Schema schema;
  private Record r;
  private Map<String, Field> fields;
  private Schema schema2;
  private Map<String, Field> fields2;

  @BeforeEach
  public void setUp() throws Exception {
    Path p = wps.getTestClasses().resolve("ba.avsc");
    schema = avroSchemaFromString.apply(p.toAbsolutePath().toString());
    schema2 = BA.SCHEMA$;
    r = new GenericData.Record(schema);
    fields = schema.getFields().stream().collect(Collectors.toMap(Field::name, Function.identity()));
    fields2 = schema2.getFields().stream().collect(Collectors.toMap(Field::name, Function.identity()));
  }

  @AfterAll
  public static void afterClass() {
    wps.finalize();
  }

  @Test
  public void testFromSchemaAndPathAndTranslator() throws IOException {

    Path targetPath = wps.get();
    DataFileWriter<GenericRecord> d = IBDataAvroUtils.fromSchemaAndPathAndTranslator(
        targetPath.resolve(UUID.randomUUID().toString() + ".avro"), schema, Optional.empty());
    assertNotNull(d);
    d.close();
    d = IBDataAvroUtils.fromSchemaAndPathAndTranslator(targetPath.resolve(UUID.randomUUID().toString() + ".avro"),
        schema, Optional.of(new GenericData()));
    assertNotNull(d);
    d.close();

  }

  @Test
  public void testNotObvioyslyBrokenURLZip() {
    assertThrows(IBException.class, () -> avroSchemaFromString.apply("zip:file:/nope.jar"));
  }

  @Test
  public void testNotObvioyslyBrokenURLHttp() {
    assertThrows(IBException.class, () -> avroSchemaFromString.apply("http://www.example.com"));
  }

  @Test
  public void testNotObvioyslyBrokenURLHttps() {
    assertThrows(IBException.class, () -> avroSchemaFromString.apply("https://www.example.com"));
  }

  @Test
  public void testNotObvioyslyBrokenURLJar() {
    assertThrows(IBException.class, () -> avroSchemaFromString.apply("jar:file:/nope.zip"));
  }

  @Test
  public void testNotObvioyslyBrokenURLFile() {
    assertThrows(IBException.class, () -> avroSchemaFromString.apply("file:/nopw.www.example.com"));
  }

  @Test
  public void testNulled() {
    assertThrows(IBException.class, () -> avroSchemaFromString.apply(null));
  }

  @Test
  public void testBrokenURL() {
    assertThrows(IBException.class, () -> avroSchemaFromString.apply("noep:@3"));
  }

  @Test
  public void testFromMapAndWpNulled() {
    assertThrows(IBException.class, () -> {
      Path path = wps.getTestClasses();
      RelativeRoot rr = new AbsolutePathRelativeRoot(path);
      IBDataAvroUtils.fromMapAndWP.apply(rr, new DefaultConfigMapBuilder());
    });
  }
}
