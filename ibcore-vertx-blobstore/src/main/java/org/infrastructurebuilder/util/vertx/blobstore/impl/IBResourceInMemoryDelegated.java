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
package org.infrastructurebuilder.util.vertx.blobstore.impl;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import org.infrastructurebuilder.pathref.Checksum;
import org.infrastructurebuilder.pathref.fs.PathRefFileSystem;
import org.infrastructurebuilder.pathref.fs.PathRefPath;
import org.infrastructurebuilder.pathref.util.readdetect.model.v1_0.IBResourceModel;
import org.infrastructurebuilder.util.readdetect.api.IBResource;
import org.infrastructurebuilder.util.readdetect.base.impls.AbstractPathRefPathIBResourceBuilderFactory.AbstractIBResource;
import org.json.JSONObject;

import io.vertx.codegen.annotations.Nullable;

public class IBResourceInMemoryDelegated implements IBResource {


  public IBResourceInMemoryDelegated(byte[] bytes, String blobname, @Nullable String description, Instant instant,
      Instant instant2, Optional<Properties> requireNonNull)
  {
    // TODO Auto-generated constructor stub
  }

  @Override
  public PathRefPath get() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean validate(boolean hard) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getModelVersion() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Checksum getByteStreamChecksum() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Checksum getChecksum() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<Instant> getMostRecentReadTime() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public Optional<Instant> getCreateDate() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public Optional<Instant> getAcquireDate() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public Optional<Instant> getLastUpdateDate() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public URI getSourceURI() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<String> getSourceName() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public JSONObject getMetadata() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<Long> size() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public PathRefFileSystem getRelativeRoot() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IBResourceModel copyModel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<Object> asPlexusIOResource() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public Optional<String> getOwner() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public Optional<String> getGroup() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public Optional<String> getPermissionsAsString() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

}
