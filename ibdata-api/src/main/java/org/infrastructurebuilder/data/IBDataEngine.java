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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.artifacts.IBVersion;
import org.infrastructurebuilder.util.artifacts.impl.DefaultIBVersion;

/**
 * Instances of IBDataEngine understand some set of data and can return instances of that data using supplier types
 * @author mykel.alvis
 *
 */
public interface IBDataEngine {
  // FIXME This won't work.  IbdataApiVersioning is loaded from the classpath, not localized
  public static final IBVersion API_ENGINE_VERSION = new DefaultIBVersion(IbdataApiVersioning.getApiVersion());
  // The final name of the metadata resources
  public static final String IBDATA = "IBDATA";
  public static final String IBDATA_DIR = "/" + IBDATA + "/";
  public static final String IBDATASET_XML = "ibdataset.xml";
  public static final String IBDATA_IBDATASET_XML = IBDATA_DIR + IBDATASET_XML;

  /**
   * Should be overriden in implementations because top-level erasure is a thingS
   * @return IBVersion instance of the implementations API
   */
  default IBVersion getEngineAPIVersion() {
    return API_ENGINE_VERSION;
  }

  List<UUID> getAvailableIds();

  Optional<IBDataSet> fetchDataSetById(UUID id);

  Optional<IBDataStream> fetchDataStreamById(UUID id);

  Optional<IBDataStream> fetchDataStreamByMetadataPatternMatcher(Map<String, Pattern> patternMap);

  default Optional<IBDataStream> fetchDataStreamByMetadataPatternMatcherFromStrings(Map<String, String> patternMap) {
    return this.fetchDataStreamByMetadataPatternMatcher(patternMap.entrySet().stream()
        .collect(Collectors.toMap(k -> k.getKey().toString(), v -> Pattern.compile(v.getValue()))));
  }

  /**
   * Transform a list of sources
   * @param sources There must be at least ONE!  This is the "original"
   * @param transformer
   * @return
   */
  Optional<IBTransformationResult> transform(List<UUID> sources, String transformer);

  Map<String, IBDataTransformer> getTransformers();

  /**
   * Execute a (probably extremely expensive) read of all available items in the classpath to acquire DOM objects for the metadata
   * @return number of Datasets found in the classpath
   */
  int prepopulate();
  /**
   * Return a list of UUIDs of Datasets currently available
   * @return
   */

}
