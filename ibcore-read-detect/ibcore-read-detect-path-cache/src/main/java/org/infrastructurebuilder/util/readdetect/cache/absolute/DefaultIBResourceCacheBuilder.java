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
package org.infrastructurebuilder.util.readdetect.cache.absolute;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.objectmapper.jackson.ObjectMapperUtils;
import org.infrastructurebuilder.util.readdetect.base.IBResource;
import org.infrastructurebuilder.util.readdetect.base.IBResourceCache;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractIBResourceCacheBuilder;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceCacheModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DefaultIBResourceCacheBuilder extends AbstractIBResourceCacheBuilder {

  public DefaultIBResourceCacheBuilder() {
    super();
  }

  @Override
  public Optional<IBResourceCache> build(boolean hard) {
    return Optional.of(new DefaultIBResourceCache(this.model));
  }

  private class DefaultIBResourceCache implements IBResourceCache {

    private IBResourceCacheModel model;

    public DefaultIBResourceCache(IBResourceCacheModel model) {
      this.model = requireNonNull(model);
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
    public Optional<List<IBResource>> getResources() {
      this.model.getResources().map(r -> {
        List<IBResourceModel> p = r;
        IBResource x;
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
          return new JSONObject(ObjectMapperUtils.mapper.get().writeValueAsString(md));
        } catch (JsonProcessingException j) {
          return null;
        }
      });
    }

  }

}
