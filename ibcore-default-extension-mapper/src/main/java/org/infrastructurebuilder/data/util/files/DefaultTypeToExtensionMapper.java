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
package org.infrastructurebuilder.data.util.files;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_ACCESS;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_MSWORD;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_MSWORDX;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_PDF;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_PPTX;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_VND_OASIS_SPREADSHEET;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_XLS;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_XLSX;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_XML;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_X_TIKA_MSOFFICE;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_ZIP;
import static org.infrastructurebuilder.util.constants.IBConstants.AVRO;
import static org.infrastructurebuilder.util.constants.IBConstants.AVRO_BINARY;
import static org.infrastructurebuilder.util.constants.IBConstants.CSV;
import static org.infrastructurebuilder.util.constants.IBConstants.DEFAULT_EXTENSION;
import static org.infrastructurebuilder.util.constants.IBConstants.DOC;
import static org.infrastructurebuilder.util.constants.IBConstants.DOCX;
import static org.infrastructurebuilder.util.constants.IBConstants.MDB;
import static org.infrastructurebuilder.util.constants.IBConstants.ODS;
import static org.infrastructurebuilder.util.constants.IBConstants.PDF;
import static org.infrastructurebuilder.util.constants.IBConstants.PPT;
import static org.infrastructurebuilder.util.constants.IBConstants.PSV;
import static org.infrastructurebuilder.util.constants.IBConstants.TEXT_CSV;
import static org.infrastructurebuilder.util.constants.IBConstants.TEXT_PLAIN;
import static org.infrastructurebuilder.util.constants.IBConstants.TEXT_PSV;
import static org.infrastructurebuilder.util.constants.IBConstants.TEXT_TSV;
import static org.infrastructurebuilder.util.constants.IBConstants.TSV;
import static org.infrastructurebuilder.util.constants.IBConstants.TXT;
import static org.infrastructurebuilder.util.constants.IBConstants.XLS;
import static org.infrastructurebuilder.util.constants.IBConstants.XLSX;
import static org.infrastructurebuilder.util.constants.IBConstants.XML;
import static org.infrastructurebuilder.util.constants.IBConstants.ZIP;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.infrastructurebuilder.util.core.TypeToExtensionMapper;

@Named
public class DefaultTypeToExtensionMapper implements TypeToExtensionMapper {

  private static final long serialVersionUID = -8394163203952496361L;

  private final static Map<String, String> map = new HashMap<String, String>() {
    {
      put(APPLICATION_XML, XML);
      put(TEXT_PLAIN, TXT);
      put(APPLICATION_ZIP, ZIP);
      put(TEXT_CSV, CSV);
      put(TEXT_TSV, TSV);
      put(APPLICATION_XLS, XLS);
      put(APPLICATION_XLSX, XLSX);
      put(APPLICATION_ACCESS, MDB);
      put(APPLICATION_MSWORD, DOC);
      put(APPLICATION_MSWORDX, DOCX);
      put(TEXT_PSV, PSV);
      put(APPLICATION_PDF, PDF);
      put(APPLICATION_PPTX, PPT);
      put(AVRO_BINARY, AVRO);
      put(APPLICATION_X_TIKA_MSOFFICE, DEFAULT_EXTENSION);
      put(APPLICATION_VND_OASIS_SPREADSHEET, ODS);
    }
  };

  private final String defaultExtension;

  @Inject
  public DefaultTypeToExtensionMapper() {
    this.defaultExtension = DEFAULT_EXTENSION;
  }

  @Override
  public String getExtensionForType(String key) {
    return ofNullable(map.get(key)).orElse(this.defaultExtension);
  }

  @Override
  public SortedSet<String> reverseMapFromExtension(String extension) {
    return map.entrySet().stream().filter(e -> e.getValue().equals(extension)).map(e -> e.getKey())
        .collect(toCollection(TreeSet::new));
  }

}
