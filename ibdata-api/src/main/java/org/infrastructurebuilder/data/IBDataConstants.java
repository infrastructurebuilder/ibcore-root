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

public interface IBDataConstants {
  public static final String APPLICATION_IBDATA_ARCHIVE = "application/ibdata-archive";
  public final static String UNCONFIGURABLE = "<!-- UNCONFIGURABLE -->";
  public static final String PASS_THRU = UNCONFIGURABLE + "passthru";
  public final static String IBDATA_WORKING_PATH_SUPPLIER = "ibdata-working-path-supplier";
  public final static String CACHE_DIRECTORY_CONFIG_ITEM = UNCONFIGURABLE + ".cachePath";
  public final static String TRANSFORMERSLIST = UNCONFIGURABLE + ".transformers";
  public final static String RECORD_SPLITTER = ",";
  public final static String MAP_SPLITTER = "|";
  public final static String WORKING_PATH_CONFIG_ITEM = UNCONFIGURABLE + ".workingPath";

  public static final String IBDATA = "IBDATA-INF";
  public static final String IBDATA_DIR = "/" + IBDATA + "/";
  public static final String IBDATASET_XML = "ibdataset.xml";
  public static final String IBDATA_IBDATASET_XML = IBDATA_DIR + IBDATASET_XML;
  public final static String INGESTION_TARGET = "IBDATA_INGESTION_TARGET_@3123";
  public final static String TRANSFORMATION_TARGET = "IBDATA_TRANSFORMATION_TARGET_@3123";
  public static final String ANY_TYPE = "*ANY*";
  public static final String IBDATA_WORKING_DIRECTORY = "ibdata.working.directory";

  public final static String TIMESTAMP_FORMATTER = "timestamp.formatter";
  public final static String TIME_FORMATTER = "time.formatter";
  public final static String DATE_FORMATTER = "date.formatter";

  public final static String LOCALE_LANGUAGE_PARAM = "locale.language";
  public final static String LOCALE_REGION_PARAM = "locale.region";

}
