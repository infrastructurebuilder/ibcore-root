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
package org.infrastructurebuilder.util.extensionmapper.basic;

import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_ACCESS;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_IBDATA;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_JAR;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_LIQUIBASE_CHANGELOG;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_MSWORD;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_MSWORDX;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_PDF;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_PPTX;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_VND_OASIS_SPREADSHEET;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_XLS;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_XLSX;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_XML;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_X_TIKA_MSOFFICE;
import static org.infrastructurebuilder.constants.IBConstants.APPLICATION_ZIP;
import static org.infrastructurebuilder.constants.IBConstants.AVI;
import static org.infrastructurebuilder.constants.IBConstants.AVRO;
import static org.infrastructurebuilder.constants.IBConstants.AVRO_BINARY;
import static org.infrastructurebuilder.constants.IBConstants.AVRO_SCHEMA;
import static org.infrastructurebuilder.constants.IBConstants.AVSC;
import static org.infrastructurebuilder.constants.IBConstants.BIN;
import static org.infrastructurebuilder.constants.IBConstants.CSV;
import static org.infrastructurebuilder.constants.IBConstants.DBUNIT_DTD;
import static org.infrastructurebuilder.constants.IBConstants.DBUNIT_FLATXML;
import static org.infrastructurebuilder.constants.IBConstants.DEFAULT_EXTENSION;
import static org.infrastructurebuilder.constants.IBConstants.DOC;
import static org.infrastructurebuilder.constants.IBConstants.DOCX;
import static org.infrastructurebuilder.constants.IBConstants.DTD;
import static org.infrastructurebuilder.constants.IBConstants.HTM;
import static org.infrastructurebuilder.constants.IBConstants.HTML;
import static org.infrastructurebuilder.constants.IBConstants.IBDATAARCHIVE;
import static org.infrastructurebuilder.constants.IBConstants.IBDATA_SCHEMA;
import static org.infrastructurebuilder.constants.IBConstants.IMAGE_JPG;
import static org.infrastructurebuilder.constants.IBConstants.JARARCHIVE;
import static org.infrastructurebuilder.constants.IBConstants.JAVA_LANG_STRING;
import static org.infrastructurebuilder.constants.IBConstants.JPEG;
import static org.infrastructurebuilder.constants.IBConstants.JPG;
import static org.infrastructurebuilder.constants.IBConstants.JSONSTRUCT;
import static org.infrastructurebuilder.constants.IBConstants.JSON_EXT;
import static org.infrastructurebuilder.constants.IBConstants.JSON_TYPE;
import static org.infrastructurebuilder.constants.IBConstants.MDB;
import static org.infrastructurebuilder.constants.IBConstants.NLD_JSON_EXT;
import static org.infrastructurebuilder.constants.IBConstants.NLD_JSON_TYPE;
import static org.infrastructurebuilder.constants.IBConstants.ODS;
import static org.infrastructurebuilder.constants.IBConstants.ORG_APACHE_AVRO_GENERIC_INDEXED_RECORD;
import static org.infrastructurebuilder.constants.IBConstants.ORG_W3C_DOM_NODE;
import static org.infrastructurebuilder.constants.IBConstants.PDF;
import static org.infrastructurebuilder.constants.IBConstants.PPT;
import static org.infrastructurebuilder.constants.IBConstants.PROTO;
import static org.infrastructurebuilder.constants.IBConstants.PROTOBUF0;
import static org.infrastructurebuilder.constants.IBConstants.PROTOBUF1;
import static org.infrastructurebuilder.constants.IBConstants.PROTOBUF2;
import static org.infrastructurebuilder.constants.IBConstants.PROTOBUF3;
import static org.infrastructurebuilder.constants.IBConstants.PSV;
import static org.infrastructurebuilder.constants.IBConstants.TEXT_CSV;
import static org.infrastructurebuilder.constants.IBConstants.TEXT_CSV_WITH_HEADER;
import static org.infrastructurebuilder.constants.IBConstants.TEXT_HTML;
import static org.infrastructurebuilder.constants.IBConstants.TEXT_PLAIN;
import static org.infrastructurebuilder.constants.IBConstants.TEXT_PSV;
import static org.infrastructurebuilder.constants.IBConstants.TEXT_PSV_WITH_HEADER;
import static org.infrastructurebuilder.constants.IBConstants.TEXT_TSV;
import static org.infrastructurebuilder.constants.IBConstants.TEXT_TSV_WITH_HEADER;
import static org.infrastructurebuilder.constants.IBConstants.TSV;
import static org.infrastructurebuilder.constants.IBConstants.TXT;
import static org.infrastructurebuilder.constants.IBConstants.VIDEO_AVI_1;
import static org.infrastructurebuilder.constants.IBConstants.XLS;
import static org.infrastructurebuilder.constants.IBConstants.XLSX;
import static org.infrastructurebuilder.constants.IBConstants.XML;
import static org.infrastructurebuilder.constants.IBConstants.ZIP;

import java.util.Arrays;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.pathref.TypeToExtensionMapper;
import org.infrastructurebuilder.util.core.IdentifiedAndWeighted;

@Named
public class DefaultTypeToExtensionMapper implements TypeToExtensionMapper {
  private final SortedSet<TypeMapTuple> list = new TreeSet<>(IdentifiedAndWeighted.comparator());

  private final String defaultExtension;

  @Inject
  public DefaultTypeToExtensionMapper() {
    this.defaultExtension = DEFAULT_EXTENSION;
    list.addAll(Arrays.asList(

        new TypeMapTuple(APPLICATION_OCTET_STREAM, BIN) // DEFAULT
        , new TypeMapTuple(IBDATA_SCHEMA, JSON_EXT) //
        , new TypeMapTuple(APPLICATION_IBDATA, IBDATAARCHIVE) //
        , new TypeMapTuple(APPLICATION_JAR, JARARCHIVE) //
        , new TypeMapTuple(APPLICATION_XML, XML, ORG_W3C_DOM_NODE) //
        , new TypeMapTuple(TEXT_HTML, HTM, JAVA_LANG_STRING) //
        , new TypeMapTuple(TEXT_HTML, HTML, JAVA_LANG_STRING) //
        , new TypeMapTuple(TEXT_PLAIN, TXT, JAVA_LANG_STRING) //
        , new TypeMapTuple(APPLICATION_ZIP, ZIP) //
        , new TypeMapTuple(TEXT_CSV, CSV, JAVA_LANG_STRING) //
        , new TypeMapTuple(TEXT_TSV, TSV, JAVA_LANG_STRING) //
        , new TypeMapTuple(TEXT_CSV_WITH_HEADER, CSV, JAVA_LANG_STRING) //
        , new TypeMapTuple(TEXT_TSV_WITH_HEADER, TSV, JAVA_LANG_STRING) //
        , new TypeMapTuple(APPLICATION_XLS, XLS) //
        , new TypeMapTuple(APPLICATION_XLSX, XLSX) //
        , new TypeMapTuple(APPLICATION_ACCESS, MDB) //
        , new TypeMapTuple(APPLICATION_MSWORD, DOC) //
        , new TypeMapTuple(APPLICATION_MSWORDX, DOCX) //
        , new TypeMapTuple(TEXT_PSV, PSV, JAVA_LANG_STRING) //
        , new TypeMapTuple(TEXT_PSV_WITH_HEADER, PSV, JAVA_LANG_STRING) //
        , new TypeMapTuple(APPLICATION_PDF, PDF) //
        , new TypeMapTuple(APPLICATION_PPTX, PPT) //
        , new TypeMapTuple(AVRO_BINARY, AVRO, ORG_APACHE_AVRO_GENERIC_INDEXED_RECORD) //
        , new TypeMapTuple(AVRO_SCHEMA, AVSC, JSONSTRUCT) //
        , new TypeMapTuple(APPLICATION_X_TIKA_MSOFFICE, DEFAULT_EXTENSION) //
        , new TypeMapTuple(APPLICATION_VND_OASIS_SPREADSHEET, ODS) //
        , new TypeMapTuple(APPLICATION_LIQUIBASE_CHANGELOG, XML) //
        , new TypeMapTuple(DBUNIT_FLATXML, XML) //
        , new TypeMapTuple(PROTOBUF0, PROTO) //
        , new TypeMapTuple(PROTOBUF1, PROTO) //
        , new TypeMapTuple(PROTOBUF2, PROTO) //
        , new TypeMapTuple(PROTOBUF3, PROTO) //
        , new TypeMapTuple(DBUNIT_DTD, DTD) //
        , new TypeMapTuple(JSON_TYPE, JSON_EXT, JSONSTRUCT) //
        , new TypeMapTuple(NLD_JSON_TYPE, NLD_JSON_EXT) //
        , new TypeMapTuple(IMAGE_JPG, JPG) //
        , new TypeMapTuple(IMAGE_JPG, JPEG) //
        , new TypeMapTuple(VIDEO_AVI_1, AVI) //
    ));
    ;
  }

  @Override
  public String getExtensionForType(String key) {
    return list.stream().filter(k -> k.getId().equals(key)).map(TypeMapTuple::getExtension).findFirst()
        .orElse(defaultExtension);
  }

  @Override
  public SortedSet<String> reverseMapFromExtension(String extension) {
    return list.stream().filter(k -> k.getExtension().equals(extension)).sorted(IdentifiedAndWeighted.comparator())
        .map(TypeMapTuple::getId).collect(Collectors.toCollection(TreeSet::new));
  }

  @Override
  public Optional<String> getStructuredSupplyTypeClassName(String key) {
    return list.stream().filter(k -> k.getId().equals(key)).map(TypeMapTuple::getStructuredType).findFirst()
        .orElse(Optional.empty());

  }

  /**
   * Default-weighted data tuple with the MIME type as the identifier
   *
   * @author mykel.alvis
   *
   */
  private class TypeMapTuple implements IdentifiedAndWeighted {

    private final String type;
    private final String extension;
    private final String structuredType;
    private final Integer weight;

    public TypeMapTuple(String type, String extension) {
      this(type, extension, null);
    }

    public TypeMapTuple(String type, String extension, String structuredType) {
      this(type, extension, structuredType, 0);
    }

    public TypeMapTuple(String type, String extension, String structuredType, Integer weight) {
      this.type = type;
      this.extension = extension;
      this.structuredType = structuredType;
      this.weight = ofNullable(weight).orElse(0);
    }

    public String getId() {
      return type;
    }

    public String getExtension() {
      return extension;
    }

    public Optional<String> getStructuredType() {
      return ofNullable(structuredType);
    }

    @Override
    public Integer getWeight() {
      return this.weight;
    }
  }

}
