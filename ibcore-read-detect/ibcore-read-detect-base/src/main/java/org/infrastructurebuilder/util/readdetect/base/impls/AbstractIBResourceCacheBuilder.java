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
package org.infrastructurebuilder.util.readdetect.base.impls;

import java.util.ArrayList;
import java.util.List;

import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.base.IBResourceCacheBuilder;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceCacheModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IbcoreReadDetectModelVersioning;

abstract public class AbstractIBResourceCacheBuilder implements IBResourceCacheBuilder {
  protected final IBResourceCacheModel model;
  protected final List<IBResource> resources = new ArrayList<>();

  public AbstractIBResourceCacheBuilder() {
    this.model = new IBResourceCacheModel();
    this.model.setModelVersion(IbcoreReadDetectModelVersioning.apiVersion());
  }

  @Override
  public IBResourceCacheBuilder withModelVersion(String modelVersion) {
    this.model.setModelVersion(modelVersion);
    return this;
  }

  @Override
  public IBResourceCacheBuilder withRoot(String root) {
    this.model.setRoot(root);
    return this;
  }

  @Override
  public IBResourceCacheBuilder withName(String name) {
    this.model.setName(name);
    return this;
  }

  @Override
  public IBResourceCacheBuilder withDescription(String desc) {
    this.model.setDescription(desc);
    return this;
  }

  @Override
  public IBResourceCacheBuilder withResources(List<IBResource> l) {
    this.resources.clear();
    this.resources.addAll(l);
    this.model.setResources(l.stream().map(m -> m.copyModel()).toList());
    return this;
  }

  @Override
  public IBResourceCacheBuilder withMetadata(IBMetadataModel j) {
    this.model.setMetadata(j);
    return this;
  }

}
