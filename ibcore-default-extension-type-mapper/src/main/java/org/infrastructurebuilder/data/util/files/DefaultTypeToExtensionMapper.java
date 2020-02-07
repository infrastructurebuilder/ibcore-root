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
package org.infrastructurebuilder.data.util.files;

import static java.util.Collections.emptySortedSet;
import static java.util.Collections.singleton;
import static org.infrastructurebuilder.IBConstants.APPLICATION_ACCESS;
import static org.infrastructurebuilder.IBConstants.APPLICATION_LIQUIBASE_CHANGELOG;
import static org.infrastructurebuilder.IBConstants.APPLICATION_MSWORD;
import static org.infrastructurebuilder.IBConstants.APPLICATION_MSWORDX;
import static org.infrastructurebuilder.IBConstants.APPLICATION_PDF;
import static org.infrastructurebuilder.IBConstants.APPLICATION_PPTX;
import static org.infrastructurebuilder.IBConstants.APPLICATION_VND_OASIS_SPREADSHEET;
import static org.infrastructurebuilder.IBConstants.APPLICATION_XLS;
import static org.infrastructurebuilder.IBConstants.APPLICATION_XLSX;
import static org.infrastructurebuilder.IBConstants.APPLICATION_XML;
import static org.infrastructurebuilder.IBConstants.APPLICATION_X_TIKA_MSOFFICE;
import static org.infrastructurebuilder.IBConstants.APPLICATION_ZIP;
import static org.infrastructurebuilder.IBConstants.AVI;
import static org.infrastructurebuilder.IBConstants.AVRO;
import static org.infrastructurebuilder.IBConstants.AVRO_BINARY;
import static org.infrastructurebuilder.IBConstants.AVRO_SCHEMA;
import static org.infrastructurebuilder.IBConstants.AVSC;
import static org.infrastructurebuilder.IBConstants.CSV;
import static org.infrastructurebuilder.IBConstants.DBUNIT_DTD;
import static org.infrastructurebuilder.IBConstants.DBUNIT_FLATXML;
import static org.infrastructurebuilder.IBConstants.DEFAULT_EXTENSION;
import static org.infrastructurebuilder.IBConstants.DOC;
import static org.infrastructurebuilder.IBConstants.DOCX;
import static org.infrastructurebuilder.IBConstants.DTD;
import static org.infrastructurebuilder.IBConstants.IBDATA_SCHEMA;
import static org.infrastructurebuilder.IBConstants.JSON_EXT;
import static org.infrastructurebuilder.IBConstants.JSON_TYPE;
import static org.infrastructurebuilder.IBConstants.MDB;
import static org.infrastructurebuilder.IBConstants.NLD_JSON_EXT;
import static org.infrastructurebuilder.IBConstants.NLD_JSON_TYPE;
import static org.infrastructurebuilder.IBConstants.ODS;
import static org.infrastructurebuilder.IBConstants.PDF;
import static org.infrastructurebuilder.IBConstants.PPT;
import static org.infrastructurebuilder.IBConstants.PROTO;
import static org.infrastructurebuilder.IBConstants.PROTOBUF0;
import static org.infrastructurebuilder.IBConstants.PROTOBUF1;
import static org.infrastructurebuilder.IBConstants.PROTOBUF2;
import static org.infrastructurebuilder.IBConstants.PROTOBUF3;
import static org.infrastructurebuilder.IBConstants.PSV;
import static org.infrastructurebuilder.IBConstants.TEXT_CSV;
import static org.infrastructurebuilder.IBConstants.TEXT_CSV_WITH_HEADER;
import static org.infrastructurebuilder.IBConstants.TEXT_PLAIN;
import static org.infrastructurebuilder.IBConstants.TEXT_PSV;
import static org.infrastructurebuilder.IBConstants.TEXT_PSV_WITH_HEADER;
import static org.infrastructurebuilder.IBConstants.TEXT_TSV;
import static org.infrastructurebuilder.IBConstants.TEXT_TSV_WITH_HEADER;
import static org.infrastructurebuilder.IBConstants.TSV;
import static org.infrastructurebuilder.IBConstants.TXT;
import static org.infrastructurebuilder.IBConstants.VIDEO_AVI_1;
import static org.infrastructurebuilder.IBConstants.XLS;
import static org.infrastructurebuilder.IBConstants.XLSX;
import static org.infrastructurebuilder.IBConstants.XML;
import static org.infrastructurebuilder.IBConstants.ZIP;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.files.TypeToExtensionMapper;

@Named
public class DefaultTypeToExtensionMapper implements TypeToExtensionMapper {
  @SuppressWarnings("serial")
  private final static Map<String, String> map = new HashMap<String, String>() {
    {
      put(IBDATA_SCHEMA, XML);
      put(APPLICATION_XML, XML);
      put(TEXT_PLAIN, TXT);
      put(APPLICATION_ZIP, ZIP);
      put(TEXT_CSV, CSV);
      put(TEXT_TSV, TSV);
      put(TEXT_CSV_WITH_HEADER, CSV);
      put(TEXT_TSV_WITH_HEADER, TSV);
      put(APPLICATION_XLS, XLS);
      put(APPLICATION_XLSX, XLSX);
      put(APPLICATION_ACCESS, MDB);
      put(APPLICATION_MSWORD, DOC);
      put(APPLICATION_MSWORDX, DOCX);
      put(TEXT_PSV, PSV);
      put(TEXT_PSV_WITH_HEADER, PSV);
      put(APPLICATION_PDF, PDF);
      put(APPLICATION_PPTX, PPT);
      put(AVRO_BINARY, AVRO);
      put(AVRO_SCHEMA, AVSC);
      put(APPLICATION_X_TIKA_MSOFFICE, DEFAULT_EXTENSION);
      put(APPLICATION_VND_OASIS_SPREADSHEET, ODS);
      put(APPLICATION_LIQUIBASE_CHANGELOG, XML);
      put(DBUNIT_FLATXML, XML);
      put(PROTOBUF0, PROTO);
      put(PROTOBUF1, PROTO);
      put(PROTOBUF2, PROTO);
      put(PROTOBUF3, PROTO);
      put(DBUNIT_DTD, DTD);
      put(JSON_TYPE, JSON_EXT);
      put(NLD_JSON_TYPE, NLD_JSON_EXT);
      put(VIDEO_AVI_1, AVI);
    }
  };

  @SuppressWarnings("serial")
  private final static Map<String, SortedSet<String>> reverseMap = new HashMap<String, SortedSet<String>>() {
    {
      for (Map.Entry<String, String> e : map.entrySet()) {
        if (containsKey(e.getValue())) {
          get(e.getValue()).add(e.getKey());
        } else {
          put(e.getValue(), new TreeSet<String>(singleton(e.getKey())));
        }
      }
    }
  };

  private final String defaultExtension;

  @Inject
  public DefaultTypeToExtensionMapper() {
    this.defaultExtension = DEFAULT_EXTENSION;
  }

  @Override
  public String getExtensionForType(String key) {
    return map.getOrDefault(key, defaultExtension);
  }

  @Override
  public SortedSet<String> reverseMapFromExtension(String extension) {
    return reverseMap.getOrDefault(extension, emptySortedSet());
  }

}