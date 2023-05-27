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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.infrastructurebuilder.util.constants.IBConstants.*;

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
import org.infrastructurebuilder.util.readdetect.IBResource;
import org.infrastructurebuilder.util.readdetect.IBResourceFactory;
import org.infrastructurebuilder.util.readdetect.model.IBResourceModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The IMDelegatedIBResource is NOT a general-purpose IBResource implementation,
 * as it stores the resource data in-memory. If you try to use this as a general
 * resource, you will QUICKLY run out of memory.
 *
 * @author mykel
 *
 */
public class IMDelegatedIBResource implements IBResource {
  private final static Tika tika = new Tika();

  private final static Logger log = LoggerFactory.getLogger(IMDelegatedIBResource.class);

  private final IBResource r;

  private final byte[] ba;

  public IMDelegatedIBResource(byte[] bb, String blobname, String description2, Instant createDate, Instant lastUpdated,
      Optional<Properties> addlProps) {
    this.ba = bb.clone();
    IBResourceModel m = new IBResourceModel();
    m.setFileChecksum(new Checksum(get()).toString());
    m.setName(blobname);
    m.setDescription(description2);
    m.setLastUpdate(requireNonNull(lastUpdated).toString());
    m.setCreated(requireNonNull(createDate).toString());
    m.setFilePath("." + UUID.randomUUID().toString());
    try {
      m.setType(tika.detect(get()));
    } catch (IOException e) {
      m.setType(APPLICATION_OCTET_STREAM);
    }

    requireNonNull(addlProps).ifPresent(p -> p.forEach((k, v) -> m.addAdditionalProperty(k.toString(), v.toString())));

    r = IBResourceFactory.from(m);
  }

  public Path getPath() {
    throw new IBException(UNAVAILABLE_PATH);
  }

  public Checksum getChecksum() {
    return r.getChecksum();
  }

  public String getType() {
    return r.getType();
  }

  public IBResource moveTo(Path target) throws IOException {
    return r.moveTo(target);
  }

  public Instant getMostRecentReadTime() {
    return r.getMostRecentReadTime();
  }

  public Instant getCreateDate() {
    return r.getCreateDate();
  }

  public Instant getLastUpdateDate() {
    return r.getLastUpdateDate();
  }

  public InputStream get() {
    return new ByteArrayInputStream(this.ba);
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

  public Optional<String> getName() {
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
  public Optional<Properties> getAdditionalProperties() {
    return r.getAdditionalProperties();
  }

}
