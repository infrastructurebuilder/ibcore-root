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
package org.infrastructurebuilder.util.readdetect;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.util.constants.IBConstants.APPLICATION_OCTET_STREAM;
import static org.infrastructurebuilder.util.constants.IBConstants.UNAVAILABLE_PATH;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import org.apache.tika.Tika;
import org.infrastructurebuilder.exceptions.IBException;
import org.infrastructurebuilder.util.core.Checksum;
import org.infrastructurebuilder.util.core.ChecksumBuilder;
import org.infrastructurebuilder.util.core.RelativeRoot;
import org.infrastructurebuilder.util.readdetect.impl.AbsolutePathIBResourceBuilder;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBMetadataModel.IBMetadataModelBuilderBase;
import org.infrastructurebuilder.util.readdetect.model.v1_0.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The IBResourceInMemoryDelegated is NOT a general-purpose IBResource implementation, as it stores the resource data
 * in-memory. If you try to use this as a general IBResource, you will QUICKLY run out of memory.
 *
 * @author mykel
 *
 */
public class IBResourceInMemoryDelegated implements IBResourceIS {
  private final static Tika tika = new Tika();

  private final static Logger log = LoggerFactory.getLogger(IBResourceInMemoryDelegated.class);

  private final IBResourceIS r;

  private final byte[] ba;

  public IBResourceInMemoryDelegated(byte[] bb, String blobname,

      String desc,

      Instant create, Instant updated,

      Optional<Properties> addlProps)
  {
    this.ba = bb.clone();
    IBResourceModel m = new IBResourceModel();
    m.setStreamChecksum(new Checksum(get().get()).toString());
    m.setStreamName(blobname);
    m.setDescription(desc);
    m.setLastUpdate(requireNonNull(updated));
    m.setCreated(requireNonNull(create));
    m.setPath("." + UUID.randomUUID());

    try {
      m.setStreamType(tika.detect(get().get()));
    } catch (IOException e) {
      m.setStreamType(APPLICATION_OCTET_STREAM);
    }

    IBMetadataModelBuilderBase b = IBMetadataModel.builder();
    requireNonNull(addlProps).ifPresent(p -> p.forEach((k, v) -> b.withAdditionalProperty(k.toString(), v.toString())));
    m.setMetadata(b.build());

    r = new AbsolutePathIBResourceBuilder().fromModel(m).build().get();
    log.debug("Built model {}", r.getChecksum().asUUID().get());
  }

  public Optional<Path> getPath() {
    throw new IBException(UNAVAILABLE_PATH);
  }

  @Override
  public Checksum getTChecksum() {
    return r.getTChecksum();
  }

  public Checksum getChecksum() {
    return asChecksum();
  }

  public String getType() {
    return r.getType();
  }

  public Optional<Instant> getMostRecentReadTime() {
    return r.getMostRecentReadTime();
  }

  public Optional<Instant> getCreateDate() {
    return r.getCreateDate();
  }

  @Override
  public Optional<Instant> getAcquireDate() {
    return r.getAcquireDate();
  }

  public Optional<Instant> getLastUpdateDate() {
    return r.getLastUpdateDate();
  }

  public Optional<InputStream> get() {
    return Optional.of(new ByteArrayInputStream(this.ba));
  }

  public Optional<URL> getSourceURL() {
    return empty();
  }

  public Optional<String> getSourceName() {
    return r.getSourceName();
  }

  public long size() {
    return this.ba.length;
  }

  public String getName() {
    return r.getName();
  }

  public Optional<String> getDescription() {
    return r.getDescription();
  }

  public JSONObject asJSON() {
    return r.asJSON();
  }

  public Path getOriginalPath() {
    return Paths.get(UUID.randomUUID().toString());
  }

  public Optional<BasicFileAttributes> getBasicFileAttributes() {
    return empty();
  }

  @Override
  public IBResourceModel copyModel() {
    return r.copyModel();
  }

  @Override
  public boolean validate(boolean hard) {
    Checksum s = this.getChecksum();
    IBResourceModel model = this.r.copyModel();
    if (!s.equals(new Checksum(model.getStreamChecksum())))
      return false;
    if (hard) {
      Checksum n = new Checksum(this.ba);
      if (!s.equals(n))
        return false;
    }
    return true;
  }

  @Override
  public ChecksumBuilder getChecksumBuilder() {
    return ChecksumBuilder.newInstance(this.getRelativeRoot().flatMap(RelativeRoot::getPath))
        .addChecksum(r.getChecksum());
  }

  @Override
  public String getModelVersion() {
    return r.getModelVersion();
  }

  @Override
  public JSONObject getMetadata() {
    return r.getMetadata();
  }
}
