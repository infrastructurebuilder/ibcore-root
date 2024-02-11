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
package org.infrastructurebuilder.util.readdetect.impl;

import java.util.ArrayList;
import java.util.List;

import org.infrastructurebuilder.util.constants.IBConstants;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceCacheBuilder;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel.IBMetadataModelBuilderBase;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceCacheModel;
import org.json.JSONObject;

abstract public class AbstractIBResourceCacheBuilder<B, T> implements IBResourceCacheBuilder<B, T> {
  private final IBResourceCacheModel model;
  protected final List<IBResource<T>> resources = new ArrayList<>();

  public AbstractIBResourceCacheBuilder() {
    this.model = new IBResourceCacheModel();
  }

  @Override
  public IBResourceCacheBuilder<B, T> withModelVersion(String modelVersion) {
    this.model.setModelVersion(modelVersion);
    return this;
  }

  @Override
  public IBResourceCacheBuilder<B, T> withRoot(String root) {
    this.model.setRoot(root);
    return this;
  }

  @Override
  public IBResourceCacheBuilder<B, T> withName(String name) {
    this.model.setName(name);
    return this;
  }

  @Override
  public IBResourceCacheBuilder<B, T> withDescription(String desc) {
    this.model.setDescription(desc);
    return this;
  }

  @Override
  public IBResourceCacheBuilder<B, T> withResources(List<IBResource<T>> l) {
    this.resources.clear();
    this.resources.addAll(l);
    this.model.setResources(l.stream().map(m -> m.copyModel()).toList());
    return this;
  }

  @Override
  public IBResourceCacheBuilder<B, T> withMetadata(JSONObject j) {
    IBMetadataModelBuilderBase b = IBMetadataModel.builder();
    j.toMap().forEach((k, v) -> b.withAdditionalProperty(k, v));
    this.model.setMetadata(b.build());
    return this;
  }

}
