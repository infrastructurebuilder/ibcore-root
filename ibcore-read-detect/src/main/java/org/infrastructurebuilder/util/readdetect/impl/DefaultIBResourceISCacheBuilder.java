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

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.objectmapper.jackson.ObjectMapperUtils;
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceIS;
import org.infrastructurebuilder.util.readdetect.IBResourceISBuilderFactorySupplier;
import org.infrastructurebuilder.util.readdetect.IBResourceISCache;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceCacheModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DefaultIBResourceISCacheBuilder extends AbstractIBResourceCacheBuilder<IBResourceISCache, InputStream> {

  public DefaultIBResourceISCacheBuilder() {
    super();
  }

  @Override
  public IBResourceISCache build(boolean hard) {
    return new DefaultIBResourceISCache(this.model);
  }

  private class DefaultIBResourceISCache implements IBResourceISCache {

    private IBResourceCacheModel model;

    public DefaultIBResourceISCache(IBResourceCacheModel model) {
      this.model = Objects.requireNonNull(model);
    }

    @Override
    public String getModelVersion() {
      return model.getModelVersion();
    }

    @Override
    public String getRoot() {
      return model.getRoot();
    }

    @Override
    public String getName() {
      return model.getName();
    }

    @Override
    public Optional<String> getDescription() {
      return model.getDescription();
    }

    @Override
    public Optional<List<IBResource<InputStream>>> getResources() {
      this.model.getResources().map(r -> {
        List<IBResourceModel> p = r;
        IBResourceIS x;
        return null;
      });
      // TODO Auto-generated method stub
      return Optional.empty();
    }

    @Override
    public Optional<JSONObject> getMetadata() {
      return model.getMetadata().map(md -> {
        String x = "{}";
        try {
          x = ObjectMapperUtils.mapper.get().writeValueAsString(md);
          return new JSONObject(x);
        } catch (JsonProcessingException j) {
          return null;
        }
      });
    }

  }

}
