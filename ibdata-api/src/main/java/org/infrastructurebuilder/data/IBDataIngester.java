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
package org.infrastructurebuilder.data;

import java.util.SortedMap;

import org.infrastructurebuilder.util.files.IBChecksumPathType;

/**
 * Instances of this need to Inject a  Named(SingletonLateBindingPathSupplier.SINGLETON_LATE_BINDING_PATH_SUPPLIER) WorkingPathSupplier wps
 * if they need working paths.  The ingester mojo will configure that path properly.
 * @author mykel.alvis
 *
 */
public interface IBDataIngester {
  /**
   * Ingestion returns a Path to a completed DataSet (i.e. a written IBDataEngine.IBDATA_IBDATASET_XML file).  That path can contain
   * no other files
   *
   * Sorted by natural order of "temporary id"
   * @param dss
   * @return
   */
  IBChecksumPathType ingest(IBDataSetIdentifier dsi, SortedMap<String,IBDataSourceSupplier> dss);
}
