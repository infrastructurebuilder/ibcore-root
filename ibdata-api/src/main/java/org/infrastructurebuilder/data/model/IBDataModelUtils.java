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
package org.infrastructurebuilder.data.model;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static org.infrastructurebuilder.data.IBDataEngine.IBDATA;
import static org.infrastructurebuilder.data.IBDataEngine.IBDATASET_XML;
import static org.infrastructurebuilder.data.IBDataException.cet;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.infrastructurebuilder.data.IBDataException;
import org.infrastructurebuilder.data.model.io.xpp3.IBDataSourceModelXpp3Writer;

public class IBDataModelUtils {

  public final static void writeDataSet(DataSet ds, Path target) {
    try (Writer writer = IBDataException.cet.withReturningTranslation(
        () -> Files.newBufferedWriter(target.resolve(IBDATA).resolve(IBDATASET_XML), UTF_8, CREATE_NEW))) {
      cet.withTranslation(() -> new IBDataSourceModelXpp3Writer().write(writer, ds.clone()));
    } catch (IOException e) {
      throw new IBDataException(e);
    }

  }
}
