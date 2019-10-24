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
package org.infrastructurebuilder.data.transform;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.infrastructurebuilder.data.DataSetEnabled;
import org.infrastructurebuilder.data.IBMetadataUtils;
import org.infrastructurebuilder.data.model.DataSet;
import org.infrastructurebuilder.util.config.ConfigMap;

public class Transformation implements DataSetEnabled {

  public static final String DEFAULT = "default";
  public static final String DEFAULT_TRANSFORM = "default-transform";
  private String id = DEFAULT;
  private List<Transformer> transformers = new ArrayList<>();
  private String finalizer = null;
  private Map<String, String> finalizerConfig = new HashMap<>();

  // Not set with plugin config
  private String groupId, artifactId, version, name, description;
  private Xpp3Dom metadata;


  public void setId(String id) {
    this.id = id;
  }

  public void setTransformers(List<Transformer> transformers) {
    this.transformers = transformers;
  }

  public void setFinalizer(String finalizer) {
    this.finalizer = finalizer;
  }

  public void setFinalizerConfig(Map<String, String> finalizerConfig) {
    this.finalizerConfig = finalizerConfig;
  }

  public void setMetadata(XmlPlexusConfiguration metadata) {
    this.metadata = (Xpp3Dom) IBMetadataUtils.translateToXpp3Dom.apply(metadata);
  }

  public String getId() {
    return id;
  }

  public List<Transformer> getTransformers() {
    return transformers.stream().map(t -> t.copy(this)).collect(Collectors.toList());
  }

  public String getFinalizer() {
    return ofNullable(finalizer).orElse(DEFAULT_TRANSFORM);
  }

  public ConfigMap getFinalizerConfig() {
    return new ConfigMap(finalizerConfig.entrySet().stream().collect(toMap(k -> k.getKey(), v -> v.getValue())));

  }

  public void injectRequird(String groupId, String artifactId, String version, String name, String description) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.name = name;
    this.description = description;
    this.metadata = new Xpp3Dom("metadata"); // FIXME Where do we make metadata happen for a transformer
  }

  @Override
  public DataSet asDataSet() {
    DataSet dsi = new DataSet();
    dsi.setGroupId(this.groupId);
    dsi.setArtifactId(this.artifactId);
    dsi.setVersion(this.version);
    dsi.setDataSetName(this.name);
    dsi.setDataSetDescription(this.description);
    dsi.setMetadata(metadata);
    return dsi;
  }
}